package com.example.meriyaadein.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.meriyaadein.data.local.DiaryDatabase
import com.example.meriyaadein.data.local.DiaryEntry
import com.example.meriyaadein.data.local.Mood
import com.example.meriyaadein.data.repository.DiaryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

/**
 * ViewModel for diary operations
 */
class DiaryViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: DiaryRepository
    
    val allEntries: StateFlow<List<DiaryEntry>>
    val favoriteEntries: StateFlow<List<DiaryEntry>>
    val todayEntry: StateFlow<DiaryEntry?>
    
    private val _selectedEntry = MutableStateFlow<DiaryEntry?>(null)
    val selectedEntry: StateFlow<DiaryEntry?> = _selectedEntry.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    val searchResults: StateFlow<List<DiaryEntry>>
    
    init {
        val database = DiaryDatabase.getDatabase(application)
        repository = DiaryRepository(database.diaryDao())
        
        allEntries = repository.allEntries
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        
        favoriteEntries = repository.favoriteEntries
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        
        // Get today's entry only
        val todayStart = getTodayStartMillis()
        val todayEnd = todayStart + 24 * 60 * 60 * 1000
        
        todayEntry = repository.getEntriesByDateRange(todayStart, todayEnd)
            .map { entries -> entries.firstOrNull() }
            .stateIn(viewModelScope, SharingStarted.Lazily, null)
        
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
        }
    }
    
    fun updateTodayMood(mood: Mood) {
        viewModelScope.launch {
            val entry = todayEntry.value
            if (entry != null) {
                repository.updateEntry(entry.copy(mood = mood))
            }
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
}
