package com.example.meriyaadein.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.meriyaadein.data.local.Mood

object ThemeExtensions {
    fun getMoodGradient(mood: Mood): Brush {
        return when (mood) {
            Mood.HAPPY, Mood.EXCITED -> Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFFFF8E1), // Very light amber
                    Color(0xFFFFE082), // Light amber
                    Color(0xFFFFD54F).copy(alpha = 0.6f) // Amber 200
                )
            )
            Mood.SAD, Mood.ANXIOUS -> Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFE3F2FD), // Very light blue
                    Color(0xFF90CAF9), // Blue 200
                    Color(0xFF64B5F6).copy(alpha = 0.6f) // Blue 300
                )
            )
            Mood.ROMANTIC, Mood.GRATEFUL -> Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFFCE4EC), // Very light pink
                    Color(0xFFF8BBD9), // Pink 100
                    Color(0xFFF48FB1).copy(alpha = 0.6f) // Pink 200
                )
            )
            Mood.PEACEFUL -> Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFE8F5E9), // Very light green
                    Color(0xFFA5D6A7), // Green 100
                    Color(0xFF81C784).copy(alpha = 0.6f) // Green 300
                )
            )
            Mood.ANGRY -> Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFFFEBEE), // Very light red
                    Color(0xFFFFCDD2), // Red 100
                    Color(0xFFEF9A9A).copy(alpha = 0.6f) // Red 200
                )
            )
            else -> Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFF3F0FF), // Lavender light (Custom)
                    Color(0xFFE6E1F5), // Lavender mid
                    Color(0xFFD1C4E9).copy(alpha = 0.7f) // Deep purple light
                )
            )
        }
    }
    
    fun getMoodSuggestionTitle(mood: Mood): String {
        return when (mood) {
            Mood.HAPPY, Mood.EXCITED -> "Share your joy!"
            Mood.SAD, Mood.ANXIOUS -> "Let it out..."
            Mood.ROMANTIC, Mood.GRATEFUL -> "Express your love"
            Mood.ANGRY -> "Vent it here"
            Mood.PEACEFUL -> "Capture the calm"
            else -> "AI Suggestions"
        }
    }

    fun getMoodPrimaryColor(mood: Mood): Color {
        return when (mood) {
            Mood.HAPPY, Mood.EXCITED -> Color(0xFFFFB300) // Amber 600
            Mood.SAD, Mood.ANXIOUS -> Color(0xFF1E88E5) // Blue 600
            Mood.ROMANTIC, Mood.GRATEFUL -> Color(0xFFD81B60) // Pink 600
            Mood.ANGRY -> Color(0xFFE53935) // Red 600
            Mood.PEACEFUL -> Color(0xFF43A047) // Green 600
            else -> Color(0xFF5D1424) // DeepPurple
        }
    }
}
