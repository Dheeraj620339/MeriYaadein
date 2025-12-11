package com.example.meriyaadein.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.meriyaadein.data.local.DiaryDatabase
import com.example.meriyaadein.data.local.DiaryEntry
import com.example.meriyaadein.data.local.Mood
import com.example.meriyaadein.data.local.UserPreferences
import com.example.meriyaadein.data.repository.DiaryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.util.*
import com.example.meriyaadein.data.HomeData

/**
 * ViewModel for diary operations
 */
class DiaryViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: DiaryRepository
    private val userPreferences: UserPreferences
    
    val allEntries: StateFlow<List<DiaryEntry>>
    val favoriteEntries: StateFlow<List<DiaryEntry>>
    val todayEntry: StateFlow<DiaryEntry?>
    
    // User Preferences
    val userName: StateFlow<String>
    val accentColor: StateFlow<String>
    
    private val _selectedEntry = MutableStateFlow<DiaryEntry?>(null)
    val selectedEntry: StateFlow<DiaryEntry?> = _selectedEntry.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    val searchResults: StateFlow<List<DiaryEntry>>
    
    // Rotating Sentences
    private val _currentSentence = MutableStateFlow(HomeData.rotatingSentences.random())
    val currentSentence: StateFlow<String> = _currentSentence.asStateFlow()

    // Real-time Clock
    private val _currentTime = MutableStateFlow(System.currentTimeMillis())
    val currentTime: StateFlow<Long> = _currentTime.asStateFlow()

    // Mood & Suggestions
    private val _currentMood = MutableStateFlow(Mood.NEUTRAL)
    val currentMood: StateFlow<Mood> = _currentMood.asStateFlow()

    private val _moodSuggestions = MutableStateFlow(HomeData.getSuggestionsForMood(Mood.NEUTRAL))
    val moodSuggestions: StateFlow<List<String>> = _moodSuggestions.asStateFlow()

    // Draft State for Home Screen
    private val _draftTitle = MutableStateFlow("")
    val draftTitle: StateFlow<String> = _draftTitle.asStateFlow()

    private val _draftContent = MutableStateFlow("")
    val draftContent: StateFlow<String> = _draftContent.asStateFlow()

    fun updateDraftTitle(title: String) { _draftTitle.value = title }
    fun updateDraftContent(content: String) { _draftContent.value = content }

    // History Screen Filters
    enum class HistoryTab { ALL, FAVORITES }
    private val _selectedHistoryTab = MutableStateFlow(HistoryTab.ALL)
    val selectedHistoryTab: StateFlow<HistoryTab> = _selectedHistoryTab.asStateFlow()

    private val _selectedVibeFilter = MutableStateFlow<Mood?>(null)
    val selectedVibeFilter: StateFlow<Mood?> = _selectedVibeFilter.asStateFlow()

    val filteredHistoryEntries: StateFlow<List<DiaryEntry>>

    init {
        val database = DiaryDatabase.getDatabase(application)
        repository = DiaryRepository(database.diaryDao())
        userPreferences = UserPreferences(application)
        
        allEntries = repository.allEntries
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        
        favoriteEntries = repository.favoriteEntries
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        
        // User Preferences flows
        userName = userPreferences.userName
            .stateIn(viewModelScope, SharingStarted.Eagerly, UserPreferences.DEFAULT_USER_NAME)
        
        accentColor = userPreferences.accentColor
            .stateIn(viewModelScope, SharingStarted.Eagerly, UserPreferences.DEFAULT_ACCENT_COLOR)
        
        // Get today's entry only
        val todayStart = getTodayStartMillis()
        val todayEnd = todayStart + 24 * 60 * 60 * 1000
        
        todayEntry = repository.getEntriesByDateRange(todayStart, todayEnd)
            .map { entries -> entries.firstOrNull() }
            .stateIn(viewModelScope, SharingStarted.Lazily, null)

        // Initialize Mood and Draft based on Today's Entry
        viewModelScope.launch {
            todayEntry.collect { entry ->
                val mood = entry?.mood ?: Mood.NEUTRAL
                _currentMood.value = mood
                _moodSuggestions.value = HomeData.getSuggestionsForMood(mood)
                
                // Only update draft if it's empty (first load) to avoid overwriting user typing
                if (_draftTitle.value.isEmpty() && _draftContent.value.isEmpty()) {
                    _draftTitle.value = entry?.title ?: ""
                    _draftContent.value = entry?.content ?: ""
                }
            }
        }
        
        // Complex Filtering for History
        filteredHistoryEntries = combine(
            allEntries,
            _searchQuery,
            _selectedHistoryTab,
            _selectedVibeFilter
        ) { entries, query, tab, vibe ->
            var result = entries
            
            // 1. Tab Filter
            if (tab == HistoryTab.FAVORITES) {
                result = result.filter { it.isFavorite }
            }
            
            // 2. Vibe Filter
            if (vibe != null) {
                result = result.filter { it.mood == vibe }
            }
            
            // 3. Search Filter
            if (query.isNotBlank()) {
                result = result.filter { 
                    it.title.contains(query, ignoreCase = true) || 
                    it.content.contains(query, ignoreCase = true) 
                    // Note: Date search is implicit if user types date string, but specific date parsing is complex.
                    // S.No search would require index, which is dynamic.
                }
            }
            
            result
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        
        // searchResults is now redundant or can alias to filteredHistoryEntries
        searchResults = filteredHistoryEntries

        startTimers()
    }

    fun setHistoryTab(tab: HistoryTab) { _selectedHistoryTab.value = tab }
    fun setVibeFilter(mood: Mood?) { _selectedVibeFilter.value = mood }
    
    private fun startTimers() {
        // Rotate Sentence every 3 minutes (random 1-5 min is avg 3)
        viewModelScope.launch {
            while(true) {
                delay(180_000) // 3 minutes
                _currentSentence.value = HomeData.rotatingSentences.random()
            }
        }

        // Update Time every minute
        viewModelScope.launch {
            while(true) {
                _currentTime.value = System.currentTimeMillis()
                // Wait until next minute starts to align roughly
                val calendar = Calendar.getInstance()
                val seconds = calendar.get(Calendar.SECOND)
                delay((60 - seconds) * 1000L)
            }
        }

        // Rotate suggestions every 2 minutes
        viewModelScope.launch {
            while(true) {
                delay(120_000)
                _moodSuggestions.value = HomeData.getSuggestionsForMood(_currentMood.value).shuffled()
            }
        }
    }

    fun updateCurrentMood(mood: Mood) {
        _currentMood.value = mood
        _moodSuggestions.value = HomeData.getSuggestionsForMood(mood)
    }
    
    private fun getTodayStartMillis(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }
    
    fun saveDraft() {
        val title = _draftTitle.value
        val content = _draftContent.value
        val mood = _currentMood.value
        val date = System.currentTimeMillis()
        val existingId = todayEntry.value?.id
        
        if (title.isNotBlank() && content.isNotBlank()) {
            saveEntry(title, content, date, mood, existingId)
        }
    }
    
    fun saveEntry(title: String, content: String, date: Long, mood: Mood, entryId: Long? = null) {
        viewModelScope.launch {
            if (entryId != null && entryId > 0) {
                val existingEntry = repository.getEntryById(entryId)
                if (existingEntry != null) {
                    repository.updateEntry(
                        existingEntry.copy(
                            title = title,
                            content = content,
                            date = date,
                            mood = mood
                        )
                    )
                }
            } else {
                repository.insertEntry(
                    DiaryEntry(
                        title = title,
                        content = content,
                        date = date,
                        mood = mood
                    )
                )
            }
            // Update current mood if the saved entry is for today
             if (isDateToday(date)) {
                 updateCurrentMood(mood)
             }
        }
    }

    private fun isDateToday(dateInMillis: Long): Boolean {
        val calendar = Calendar.getInstance()
        val todayYear = calendar.get(Calendar.YEAR)
        val todayDay = calendar.get(Calendar.DAY_OF_YEAR)

        calendar.timeInMillis = dateInMillis
        return calendar.get(Calendar.YEAR) == todayYear && 
               calendar.get(Calendar.DAY_OF_YEAR) == todayDay
    }
    
    fun updateTodayMood(mood: Mood) {
        viewModelScope.launch {
            val entry = todayEntry.value
            if (entry != null) {
                repository.updateEntry(entry.copy(mood = mood))
            }
            updateCurrentMood(mood)
        }
    }
    
    fun deleteEntry(entry: DiaryEntry) {
        viewModelScope.launch {
            repository.deleteEntry(entry)
        }
    }
    
    fun toggleFavorite(entry: DiaryEntry) {
        viewModelScope.launch {
            repository.updateEntry(entry.copy(isFavorite = !entry.isFavorite))
        }
    }
    
    fun toggleLock(entry: DiaryEntry) {
        viewModelScope.launch {
            repository.updateEntry(entry.copy(isLocked = !entry.isLocked))
        }
    }
    
    fun loadEntryById(id: Long) {
        viewModelScope.launch {
            _selectedEntry.value = repository.getEntryById(id)
        }
    }
    
    fun clearSelectedEntry() {
        _selectedEntry.value = null
    }
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun getEntriesForDate(date: Long): Flow<List<DiaryEntry>> {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val endOfDay = calendar.timeInMillis
        
        return repository.getEntriesByDateRange(startOfDay, endOfDay)
    }
    
    // ==================== User Preferences ====================
    
    fun saveUserName(name: String) {
        viewModelScope.launch {
            userPreferences.saveUserName(name)
        }
    }
    
    fun saveAccentColor(color: String) {
        viewModelScope.launch {
            userPreferences.saveAccentColor(color)
        }
    }
}
