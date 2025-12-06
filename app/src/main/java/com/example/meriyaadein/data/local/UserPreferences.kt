package com.example.meriyaadein.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * DataStore-based preferences for user settings
 * Stores: userName, accentColor
 */

// Extension property for DataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

object UserPreferencesKeys {
    val USER_NAME = stringPreferencesKey("user_name")
    val ACCENT_COLOR = stringPreferencesKey("accent_color")
}

class UserPreferences(private val context: Context) {
    
    // Default accent color (Deep Purple from brand identity)
    companion object {
        const val DEFAULT_ACCENT_COLOR = "#5D1424"
        const val DEFAULT_USER_NAME = "Friend"
    }
    
    /**
     * Get user name as Flow
     */
    val userName: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[UserPreferencesKeys.USER_NAME] ?: DEFAULT_USER_NAME
    }
    
    /**
     * Get accent color as Flow (hex string)
     */
    val accentColor: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[UserPreferencesKeys.ACCENT_COLOR] ?: DEFAULT_ACCENT_COLOR
    }
    
    /**
     * Save user name
     */
    suspend fun saveUserName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.USER_NAME] = name.trim()
        }
    }
    
    /**
     * Save accent color (hex string)
     */
    suspend fun saveAccentColor(color: String) {
        context.dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.ACCENT_COLOR] = color
        }
    }
}
