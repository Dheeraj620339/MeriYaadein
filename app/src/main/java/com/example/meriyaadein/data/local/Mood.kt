package com.example.meriyaadein.data.local

/**
 * Mood options for diary entries
 */
enum class Mood(val emoji: String, val label: String) {
    HAPPY("😊", "Happy"),
    SAD("😢", "Sad"),
    NEUTRAL("😐", "Neutral"),
    EXCITED("🎉", "Excited"),
    ROMANTIC("💕", "Romantic"),
    GRATEFUL("🙏", "Grateful"),
    ANGRY("😠", "Angry"),
    ANXIOUS("😰", "Anxious"),
    PEACEFUL("😌", "Peaceful")
}
