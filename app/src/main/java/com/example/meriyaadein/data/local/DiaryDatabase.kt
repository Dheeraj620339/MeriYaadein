package com.example.meriyaadein.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Room type converter for Mood enum
 */
class MoodConverter {
    @androidx.room.TypeConverter
    fun fromMood(mood: Mood): String = mood.name
    
    @androidx.room.TypeConverter
    fun toMood(name: String): Mood = Mood.valueOf(name)
}

/**
 * Room database for diary entries
 */
@Database(entities = [DiaryEntry::class], version = 1, exportSchema = false)
@TypeConverters(MoodConverter::class)
abstract class DiaryDatabase : RoomDatabase() {
    
    abstract fun diaryDao(): DiaryDao
    
    companion object {
        @Volatile
        private var INSTANCE: DiaryDatabase? = null
        
        fun getDatabase(context: Context): DiaryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DiaryDatabase::class.java,
                    "meri_yaadein_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
