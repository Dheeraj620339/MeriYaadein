package com.example.meriyaadein.data.repository

import com.example.meriyaadein.data.local.DiaryDao
import com.example.meriyaadein.data.local.DiaryEntry
import kotlinx.coroutines.flow.Flow

/**
 * Repository for diary entries
 */
class DiaryRepository(private val diaryDao: DiaryDao) {
    
    val allEntries: Flow<List<DiaryEntry>> = diaryDao.getAllEntries()
    
    val favoriteEntries: Flow<List<DiaryEntry>> = diaryDao.getFavoriteEntries()
    
    fun getEntriesByDateRange(startDate: Long, endDate: Long): Flow<List<DiaryEntry>> {
        return diaryDao.getEntriesByDateRange(startDate, endDate)
    }
    
    suspend fun getEntryById(id: Long): DiaryEntry? {
        return diaryDao.getEntryById(id)
    }
    
    suspend fun getEntryByDate(startOfDay: Long, endOfDay: Long): DiaryEntry? {
        return diaryDao.getEntryByDate(startOfDay, endOfDay)
    }
    
    fun getAllEntryDates(): Flow<List<Long>> {
        return diaryDao.getAllEntryDates()
    }
    
    suspend fun insertEntry(entry: DiaryEntry): Long {
        return diaryDao.insertEntry(entry)
    }
    
    suspend fun updateEntry(entry: DiaryEntry) {
        diaryDao.updateEntry(entry.copy(updatedAt = System.currentTimeMillis()))
    }
    
    suspend fun deleteEntry(entry: DiaryEntry) {
        diaryDao.deleteEntry(entry)
    }
    
    suspend fun toggleFavorite(id: Long, currentStatus: Boolean) {
        diaryDao.updateFavoriteStatus(id, !currentStatus)
    }
    
    fun searchEntries(query: String): Flow<List<DiaryEntry>> {
        return diaryDao.searchEntries(query)
    }
}
