package com.example.meriyaadein.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for diary entries
 */
@Dao
interface DiaryDao {
    
    @Query("SELECT * FROM diary_entries ORDER BY date DESC")
    fun getAllEntries(): Flow<List<DiaryEntry>>
    
    @Query("SELECT * FROM diary_entries WHERE isFavorite = 1 ORDER BY date DESC")
    fun getFavoriteEntries(): Flow<List<DiaryEntry>>
    
    @Query("SELECT * FROM diary_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getEntriesByDateRange(startDate: Long, endDate: Long): Flow<List<DiaryEntry>>
    
    @Query("SELECT * FROM diary_entries WHERE date >= :startOfDay AND date < :endOfDay LIMIT 1")
    suspend fun getEntryByDate(startOfDay: Long, endOfDay: Long): DiaryEntry?
    
    @Query("SELECT * FROM diary_entries WHERE id = :id")
    suspend fun getEntryById(id: Long): DiaryEntry?
    
    @Query("SELECT DISTINCT date FROM diary_entries")
    fun getAllEntryDates(): Flow<List<Long>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: DiaryEntry): Long
    
    @Update
    suspend fun updateEntry(entry: DiaryEntry)
    
    @Delete
    suspend fun deleteEntry(entry: DiaryEntry)
    
    @Query("UPDATE diary_entries SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean)
    
    @Query("SELECT * FROM diary_entries WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' ORDER BY date DESC")
    fun searchEntries(query: String): Flow<List<DiaryEntry>>
}
