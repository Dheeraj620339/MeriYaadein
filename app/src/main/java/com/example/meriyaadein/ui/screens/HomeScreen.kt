package com.example.meriyaadein.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.meriyaadein.data.local.DiaryEntry
import com.example.meriyaadein.data.local.Mood
import com.example.meriyaadein.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.sin
import kotlin.random.Random

/**
 * Professional Home Screen with:
 * - Dynamic Greeting with User Profile
 * - Date + Weather Display
 * - Mood-based Animations
 * - Dynamic AI Suggestions based on Mood
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    todayEntry: DiaryEntry?,
    recentEntries: List<DiaryEntry> = emptyList(), // Added ensuring signature match
    onWriteClick: () -> Unit,
    onWriteWithPrompt: (String) -> Unit,
    onEditClick: (DiaryEntry) -> Unit,
    onMoodSelected: (Mood) -> Unit,
    onProfileClick: () -> Unit = {},
    currentSentence: String = "", // Added for signature match 
    currentTimeMillis: Long = System.currentTimeMillis(), // Added for signature match
    currentMood: Mood = Mood.NEUTRAL, // Added for signature match
    moodSuggestions: List<String> = emptyList(), // Added for signature match
    userName: String = "Friend",
    accentColor: String = "#5D1424", // Optional
    modifier: Modifier = Modifier
) {
    // We use the passed currentMood for theming, falling back to todayEntry's mood or internal state
    // To avoid conflict, we prefer the ViewModel's state (currentMood).
    val effectiveMood = currentMood 
    
    // Get mood-based gradient
    val moodGradient = getMoodGradient(effectiveMood)
    
    // Animation states
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }
    
    // Derived date/time strings from passed parameters or local calculation
    val calendar = Calendar.getInstance().apply { timeInMillis = currentTimeMillis }
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
    
    val timeString = timeFormat.format(calendar.time)
    val dateString = dateFormat.format(calendar.time)

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Background with mood gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(moodGradient)
        )
        
        // Mood-based animation overlay
        MoodAnimationOverlay(selectedMood = effectiveMood)
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            
            // Rotating Sentence using passed param
            if (currentSentence.isNotEmpty()) {
                RotatingSentencesView(sentence = currentSentence)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // ========== HEADER WITH PROFILE ==========
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(600)) + slideInVertically(
                    initialOffsetY = { -40 },
                    animationSpec = tween(600)
                )
            ) {
                GreetingView(
                    userName = userName,
                    greetingTime = hour,
                    timeString = timeString,
                    dateString = dateString,
                    onProfileClick = onProfileClick
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // ========== GLASSMORPHISM DAILY SNAPSHOT CARD ==========
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 400)) + 
                        scaleIn(initialScale = 0.9f, animationSpec = tween(600, delayMillis = 400))
            ) {
                GlassmorphismDailyCard(
                    entry = todayEntry,
                    onWriteClick = onWriteClick,
                    onEditClick = { todayEntry?.let { onEditClick(it) } }
                )
            }
            
            Spacer(modifier = Modifier.height(28.dp))
            
            // ========== INTERACTIVE MOOD SELECTOR ==========
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 600))
            ) {
                InteractiveMoodSelector(
                    selectedMood = effectiveMood,
                    onMoodSelected = onMoodSelected
                )
            }
            
            Spacer(modifier = Modifier.height(28.dp))
            
            // ========== DYNAMIC AI SUGGESTIONS (MOOD-BASED) ==========
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 800))
            ) {
                DynamicAiSuggestions(
                    selectedMood = effectiveMood,
                    suggestions = moodSuggestions,
                    onSuggestionClick = onWriteWithPrompt
                )
            }
            
            Spacer(modifier = Modifier.height(100.dp))
        }
        
        // ========== FLOATING ACTION BUTTON ==========
        PremiumFloatingActionButton(
            onClick = onWriteClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        )
    }
}

// ==================== SUB-COMPONENTS ====================

@Composable
fun RotatingSentencesView(sentence: String) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = sentence,
            transitionSpec = {
                fadeIn(animationSpec = tween(1000)) togetherWith fadeOut(animationSpec = tween(1000))
            },
            label = "sentenceAnim"
        ) { targetSentence ->
            Text(
                text = "💬 \"$targetSentence\"",
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                color = CharcoalSlate.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}

@Composable
fun GreetingView(
    userName: String,
    greetingTime: Int,
    timeString: String,
    dateString: String,
    onProfileClick: () -> Unit
) {
    val (greeting, emoji) = when {
        greetingTime in 5..11 -> "Good Morning" to "👋"
        greetingTime in 12..16 -> "Good Afternoon" to "☀️"
        greetingTime in 17..20 -> "Good Evening" to "🌙"
        else -> "Good Night" to "⭐"
    }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$greeting, $userName",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = DeepPurple
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = emoji, fontSize = 28.sp)
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "$dateString • $timeString",
                style = MaterialTheme.typography.bodyMedium,
                color = GreyText
            )
        }
        
        IconButton(
            onClick = onProfileClick,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(GlassWhite)
                .border(2.dp, LavenderMid, CircleShape)
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = "Profile",
                tint = DeepPurple,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun GlassmorphismDailyCard(
    entry: DiaryEntry?,
    onWriteClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = DeepPurple.copy(alpha = 0.2f),
                spotColor = DeepPurple.copy(alpha = 0.15f)
            ),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(GlassWhite, GlassLight, GlassFrost)
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(Color.White.copy(alpha = 0.8f), Color.White.copy(alpha = 0.3f))
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(24.dp)
        ) {
            if (entry != null) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Brush.radialGradient(colors = listOf(LavenderMid, PurpleAccent))),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = entry.mood.emoji, fontSize = 24.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "📝 Aaj ki Yaad",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = DeepPurple
                                )
                                Text(
                                    text = "Daily Snapshot",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GreyText
                                )
                            }
                        }
                        IconButton(
                            onClick = onEditClick,
                            modifier = Modifier.clip(CircleShape).background(LavenderLight)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = DeepPurple)
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = entry.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalSlate
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = entry.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = CharcoalSlate.copy(alpha = 0.75f),
                        maxLines = 4
                    )
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "✍️", fontSize = 56.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Aaj kuch likha nahi...",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = CharcoalSlate
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Apni aaj ki yaad yahan save karo ✨",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GreyText,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onWriteClick,
                        colors = ButtonDefaults.buttonColors(containerColor = DeepPurple, contentColor = CreamWhite),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.height(52.dp)
                    ) {
                        Icon(Icons.Default.Create, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Likhna Shuru Karo", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun InteractiveMoodSelector(
    selectedMood: Mood,
    onMoodSelected: (Mood) -> Unit
) {
    Column {
        Text(
            text = "How are you feeling?",
            style = MaterialTheme.typography.titleSmall,
            color = CharcoalSlate
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Mood.entries.forEach { mood ->
                MoodChip(
                    mood = mood,
                    isSelected = mood == selectedMood,
                    onClick = { onMoodSelected(mood) }
                )
            }
        }
    }
}

@Composable
private fun MoodChip(
    mood: Mood,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(if (isSelected) DustyRose else BlushMist.copy(alpha = 0.5f))
                .border(
                    width = if (isSelected) 3.dp else 0.dp,
                    color = if (isSelected) VelvetBurgundy else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = mood.emoji, fontSize = 28.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = mood.label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) VelvetBurgundy else CharcoalSlate.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun DynamicAiSuggestions(
    selectedMood: Mood,
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit
) {
    val suggestionsToDisplay = if (suggestions.isNotEmpty()) suggestions else getMoodBasedSuggestions(selectedMood).map { it.second }
    
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "✨ ${getMoodSuggestionTitle(selectedMood)}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = GreyText
            )
            Text(
                text = "Tap to write",
                style = MaterialTheme.typography.labelSmall,
                color = DeepPurple.copy(alpha = 0.7f)
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        suggestionsToDisplay.take(3).forEach { suggestion ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onSuggestionClick(suggestion) },
                colors = CardDefaults.cardColors(containerColor = GlassWhite),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "💡", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = suggestion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = CharcoalSlate.copy(alpha = 0.8f),
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = "Write",
                        tint = DeepPurple.copy(alpha = 0.5f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PremiumFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = DeepPurple,
        contentColor = CreamWhite,
        shape = CircleShape,
        elevation = FloatingActionButtonDefaults.elevation(8.dp),
        modifier = modifier.size(64.dp)
    ) {
        Icon(Icons.Default.Add, contentDescription = "Add Entry", modifier = Modifier.size(32.dp))
    }
}

// ==================== MOOD ANIMATION OVERLAY ====================
@Composable
private fun MoodAnimationOverlay(selectedMood: Mood) {
    when (selectedMood) {
        Mood.HAPPY, Mood.EXCITED -> SparklingParticles()
        Mood.SAD, Mood.ANXIOUS -> RainAnimation()
        Mood.ROMANTIC, Mood.GRATEFUL -> FloatingHeartsAnimation()
        Mood.ANGRY -> PulseAnimation(color = Color(0x33FF5252))
        Mood.PEACEFUL -> FloatingOrbsAnimation()
        else -> { /* No animation for neutral */ }
    }
}

@Composable
private fun SparklingParticles() {
    val particles: List<Particle> = remember { List(20) { Particle() } }
    val infiniteTransition = rememberInfiniteTransition(label = "sparkle")
    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sparkleProgress"
    )
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val alpha = (sin(animProgress * 2 * Math.PI + particle.phase) * 0.5f + 0.5f).toFloat()
            drawCircle(
                color = Color(0xFFFFD700).copy(alpha = alpha * 0.4f),
                radius = particle.size,
                center = Offset(
                    x = particle.x * size.width,
                    y = particle.y * size.height
                )
            )
        }
    }
}

@Composable
private fun RainAnimation() {
    val raindrops: List<Raindrop> = remember { List(30) { Raindrop() } }
    val infiniteTransition = rememberInfiniteTransition(label = "rain")
    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rainProgress"
    )
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        raindrops.forEach { drop ->
            val y = ((drop.startY + animProgress * drop.speed) % 1.2f)
            drawLine(
                color = Color(0xFF64B5F6).copy(alpha = 0.3f),
                start = Offset(drop.x * size.width, y * size.height),
                end = Offset(drop.x * size.width, (y + 0.02f) * size.height),
                strokeWidth = 2f
            )
        }
    }
}

@Composable
private fun FloatingHeartsAnimation() {
    val hearts: List<FloatingHeart> = remember { List(10) { FloatingHeart() } }
    val infiniteTransition = rememberInfiniteTransition(label = "hearts")
    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "heartProgress"
    )
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        hearts.forEach { heart ->
            val y = 1f - ((heart.startY + animProgress * heart.speed) % 1.2f)
            val alpha = if (y > 0.8f) (1f - y) * 5 else if (y < 0.2f) y * 5 else 1f
            val sway = sin(animProgress * 4 * Math.PI + heart.phase).toFloat() * 0.02f
            drawCircle(
                color = Color(0xFFFF69B4).copy(alpha = alpha * 0.4f),
                radius = heart.size,
                center = Offset((heart.x + sway) * size.width, y * size.height)
            )
        }
    }
}

@Composable
private fun PulseAnimation(color: Color) { 
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    Box(modifier = Modifier.fillMaxSize().scale(scale).background(color))
}

@Composable
private fun FloatingOrbsAnimation() {
    val orbs: List<Orb> = remember { List(8) { Orb() } }
    val infiniteTransition = rememberInfiniteTransition(label = "orbs")
    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbProgress"
    )
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        orbs.forEach { orb ->
            val x = orb.x + sin(animProgress * 2 * Math.PI * orb.speedX + orb.phase).toFloat() * 0.05f
            val y = orb.y + sin(animProgress * 2 * Math.PI * orb.speedY + orb.phase).toFloat() * 0.03f
            drawCircle(
                color = Color(0xFF81C784).copy(alpha = 0.2f),
                radius = orb.size,
                center = Offset(x * size.width, y * size.height)
            )
        }
    }
}

// Helpers 
private fun getMoodSuggestionTitle(mood: Mood): String {
    return when (mood) {
        Mood.HAPPY, Mood.EXCITED -> "Share your joy!"
        Mood.SAD, Mood.ANXIOUS -> "Let it out..."
        Mood.ROMANTIC, Mood.GRATEFUL -> "Express your love"
        Mood.ANGRY -> "Vent it here"
        Mood.PEACEFUL -> "Capture the calm"
        else -> "AI Suggestions"
    }
}

private fun getMoodBasedSuggestions(mood: Mood): List<Pair<String, String>> {
    return when (mood) {
        Mood.HAPPY, Mood.EXCITED -> listOf(
            "😊" to "Aaj kis baat se smile aayi?",
            "🏆" to "Kis achievement pe proud feel hua?",
            "🎉" to "Koi celebration worthy moment?",
            "💪" to "Aaj kya accha kiya tune?"
        )
        Mood.SAD, Mood.ANXIOUS -> listOf(
            "💭" to "Kya baat ne aaj disturb kiya?",
            "🗣️" to "Kisi se baat karni hai?",
            "😔" to "Kya dil mein hai jo bahar nahi aa raha?",
            "🤗" to "Kya cheez comfort deti hai tujhe?"
        )
        Mood.ROMANTIC, Mood.GRATEFUL -> listOf(
            "💕" to "Kisi special ke baare mein likho",
            "💝" to "Aaj kis baat ke liye grateful ho?",
            "😍" to "Kya cheez dil ko chu gayi aaj?",
            "🌹" to "Pyaar ka koi moment share karo"
        )
        Mood.ANGRY -> listOf(
            "😤" to "Kis baat pe gussa aaya?",
            "🔥" to "Kya cheez frustrate kar rahi hai?",
            "💢" to "Kisko bolna chahte ho jo nahi bol paaye?",
            "🤯" to "Kya expectation break hui?"
        )
        Mood.PEACEFUL -> listOf(
            "🧘" to "Aaj kya cheez peaceful lagi?",
            "🌿" to "Kis moment mein sukoon mila?",
            "☕" to "Kya simple cheez ne khush kiya?",
            "🌅" to "Koi beautiful moment capture karo"
        )
        else -> listOf(
            "💭" to "Aaj kya chal raha hai?",
            "✨" to "Koi interesting baat hui?",
            "📝" to "Random thoughts likho",
            "🎯" to "Kal ke liye kya plan hai?"
        )
    }
}

@Composable
private fun getMoodGradient(mood: Mood): Brush {
    return when (mood) {
        Mood.HAPPY, Mood.EXCITED -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFFF8E1),
                Color(0xFFFFE082),
                Color(0xFFFFD54F).copy(alpha = 0.6f)
            )
        )
        Mood.SAD, Mood.ANXIOUS -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFFE3F2FD),
                Color(0xFF90CAF9),
                Color(0xFF64B5F6).copy(alpha = 0.6f)
            )
        )
        Mood.ROMANTIC, Mood.GRATEFUL -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFCE4EC),
                Color(0xFFF8BBD9),
                Color(0xFFF48FB1).copy(alpha = 0.6f)
            )
        )
        Mood.PEACEFUL -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFFE8F5E9),
                Color(0xFFA5D6A7),
                Color(0xFF81C784).copy(alpha = 0.6f)
            )
        )
        Mood.ANGRY -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFFEBEE),
                Color(0xFFFFCDD2),
                Color(0xFFEF9A9A).copy(alpha = 0.6f)
            )
        )
        else -> Brush.verticalGradient(
            colors = listOf(
                GradientStart,
                GradientMid,
                GradientEnd.copy(alpha = 0.7f)
            )
        )
    }
}

// Data Classes for Animations
private data class Particle(
    val x: Float = Random.nextFloat(),
    val y: Float = Random.nextFloat(),
    val size: Float = Random.nextFloat() * 6 + 2,
    val phase: Double = Random.nextDouble() * Math.PI * 2
)

private data class Raindrop(
    val x: Float = Random.nextFloat(),
    val startY: Float = Random.nextFloat(),
    val speed: Float = Random.nextFloat() * 0.5f + 0.5f
)

private data class FloatingHeart(
    val x: Float = Random.nextFloat(),
    val startY: Float = Random.nextFloat(),
    val size: Float = Random.nextFloat() * 8 + 4,
    val speed: Float = Random.nextFloat() * 0.3f + 0.2f,
    val phase: Double = Random.nextDouble() * Math.PI * 2
)

private data class Orb(
    val x: Float = Random.nextFloat(),
    val y: Float = Random.nextFloat(),
    val size: Float = Random.nextFloat() * 40 + 20,
    val speedX: Float = Random.nextFloat() * 0.5f + 0.5f,
    val speedY: Float = Random.nextFloat() * 0.5f + 0.5f,
    val phase: Double = Random.nextDouble() * Math.PI * 2
)
