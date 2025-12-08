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

        // Initialize Mood based on Today's Entry
        viewModelScope.launch {
            todayEntry.collect { entry ->
                val mood = entry?.mood ?: Mood.NEUTRAL
                _currentMood.value = mood
                _moodSuggestions.value = HomeData.getSuggestionsForMood(mood)
            }
        }
        
        searchResults = _searchQuery
            .debounce(300)
            .flatMapLatest { query ->
                if (query.isBlank()) {
                    flowOf(emptyList())
                } else {
                    repository.searchEntries(query)
                }
            }
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

        startTimers()
    }
    
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
            repository.toggleFavorite(entry.id, entry.isFavorite)
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
