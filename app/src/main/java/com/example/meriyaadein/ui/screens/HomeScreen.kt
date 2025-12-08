package com.example.meriyaadein.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.meriyaadein.data.local.DiaryEntry
import com.example.meriyaadein.data.local.Mood
import com.example.meriyaadein.ui.theme.*
import com.example.meriyaadein.ui.theme.ThemeExtensions
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.sin
import kotlin.random.Random

/**
 * Professional Home Screen with:
 * - Top Rotating Sentences
 * - Dynamic Greeting + Real-time Clock
 * - Recent Memories Slider
 * - Mood Section with AI Suggestions
 * - Floating Write Button
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    todayEntry: DiaryEntry?,
    recentEntries: List<DiaryEntry>, // Passed from ViewModel
    onWriteClick: () -> Unit,
    onWriteWithPrompt: (String) -> Unit,
    onEditClick: (DiaryEntry) -> Unit,
    onMoodSelected: (Mood) -> Unit, // Callback to update ViewModel
    currentSentence: String, // From ViewModel
    currentTimeMillis: Long, // From ViewModel
    currentMood: Mood, // From ViewModel
    moodSuggestions: List<String>, // From ViewModel
    onProfileClick: () -> Unit = {},
    userName: String = "Friend",
    modifier: Modifier = Modifier
) {
    // Get mood-based gradient from Extension
    val moodGradient = ThemeExtensions.getMoodGradient(currentMood)
    
    // Animation states
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }
    
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
        MoodAnimationOverlay(selectedMood = currentMood)
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // ========== TOP ROTATING SENTENCES ==========
            RotatingSentencesView(sentence = currentSentence)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                // ========== GREETING & CLOCK ==========
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(animationSpec = tween(600)) + slideInVertically(
                        initialOffsetY = { -40 },
                        animationSpec = tween(600)
                    )
                ) {
                    GreetingView(
                        userName = userName,
                        currentTimeMillis = currentTimeMillis,
                        onProfileClick = onProfileClick
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                // ========== RECENT MEMORIES SLIDER ==========
                Text(
                    text = "Recent Memories",
                    style = MaterialTheme.typography.titleMedium,
                    color = CharcoalSlate.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                RecentMemoriesSlider(
                    entries = recentEntries,
                    onEntryClick = onEditClick
                )

                Spacer(modifier = Modifier.height(32.dp))

                // ========== MOOD SECTION & AI SUGGESTIONS ==========
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(animationSpec = tween(600, delayMillis = 600))
                ) {
                    MoodSection(
                        selectedMood = currentMood,
                        onMoodSelected = onMoodSelected,
                        suggestions = moodSuggestions,
                        onSuggestionClick = onWriteWithPrompt
                    )
                }

                Spacer(modifier = Modifier.height(100.dp)) // Space for FAB
            }
        }
        
        // ========== FLOATING ACTION BUTTON ==========
        FloatingWriteButton(
            onClick = onWriteClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}

// -----------------------------------------------------------------------------
// SUB-COMPONENTS
// -----------------------------------------------------------------------------

@Composable
fun RotatingSentencesView(sentence: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = sentence,
            transitionSpec = {
                fadeIn(animationSpec = tween(1000)) towards fadeOut(animationSpec = tween(1000))
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
    currentTimeMillis: Long,
    onProfileClick: () -> Unit
) {
    val calendar = Calendar.getInstance().apply { timeInMillis = currentTimeMillis }
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
    
    val timeString = timeFormat.format(calendar.time)
    val dateString = dateFormat.format(calendar.time)
    
    val (greeting, emoji) = when {
        hour in 5..11 -> "Good Morning" to "👋"
        hour in 12..16 -> "Good Afternoon" to "☀️"
        hour in 17..20 -> "Good Evening" to "🌙"
        else -> "Good Night" to "⭐"
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "$emoji $greeting",
                    style = MaterialTheme.typography.titleMedium,
                    color = GreyText
                )
                Text(
                    text = userName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = DeepPurple
                )
            }
            
            IconButton(
                onClick = onProfileClick,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(GlassWhite)
                    .border(1.dp, LavenderMid.copy(alpha=0.5f), CircleShape)
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = DeepPurple        
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Date & Time Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(GlassWhite.copy(alpha=0.6f))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📆 $dateString",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = CharcoalSlate
            )
            
            Text(
                text = "⏱ $timeString",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = DeepPurple
            )
        }
    }
}

@Composable
fun RecentMemoriesSlider(
    entries: List<DiaryEntry>,
    onEntryClick: (DiaryEntry) -> Unit
) {
    if (entries.isEmpty()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            colors = CardDefaults.cardColors(containerColor = GlassWhite.copy(alpha=0.5f)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "😢", fontSize = 32.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Koi yaad abhi likhi nahi!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = GreyText
                )
            }
        }
    } else {
        val pagerState = rememberPagerState(
            initialPage = 0,
            pageCount = { entries.size.coerceAtMost(50) }
        )
        
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 32.dp),
            pageSpacing = 16.dp
        ) { page ->
            val entry = entries[page]
            val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
            
            // Parallax/Scale Effect
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .graphicsLayer {
                        val scale = 1f - (pageOffset.absoluteValue * 0.1f)
                        scaleX = scale
                        scaleY = scale
                        alpha = 1f - (pageOffset.absoluteValue * 0.3f)
                    }
                    .clickable { onEntryClick(entry) },
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = GlassWhite)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = computeTimeAgo(entry.date),
                            style = MaterialTheme.typography.labelSmall,
                            color = DeepPurple,
                            fontWeight = FontWeight.Bold
                        )
                        Text(text = entry.mood.emoji, fontSize = 20.sp)
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = entry.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalSlate,
                        maxLines = 1
                    )
                    
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    Text(
                        text = entry.content,
                        style = MaterialTheme.typography.bodySmall,
                        color = CharcoalSlate.copy(alpha = 0.7f),
                        maxLines = 3,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

private fun computeTimeAgo(dateMillis: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - dateMillis
    val days = diff / (1000 * 60 * 60 * 24)
    
    return when {
        days == 0L -> "Today"
        days == 1L -> "Yesterday"
        days < 7L -> "$days Days Ago"
        days < 30L -> "${days / 7} Weeks Ago"
        else -> "Long time ago"
    }
}

@Composable
fun MoodSection(
    selectedMood: Mood,
    onMoodSelected: (Mood) -> Unit,
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit
) {
    Column {
        // Mood Selector
        Text(
            text = "How are you feeling?",
            style = MaterialTheme.typography.titleSmall,
            color = CharcoalSlate,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Mood.entries.take(6).forEach { mood ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onMoodSelected(mood) }
                        .padding(4.dp)
                ) {
                    Text(
                        text = mood.emoji, 
                        fontSize = if(mood == selectedMood) 32.sp else 24.sp,
                        modifier = Modifier.scale(if(mood == selectedMood) 1.2f else 1f)
                    )
                    if (mood == selectedMood) {
                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(DeepPurple)
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // AI Suggestions Carousel
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = GlassWhite.copy(alpha = 0.7f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "✨ AI Suggestions for you",
                    style = MaterialTheme.typography.labelMedium,
                    color = DeepPurple,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                // Limiting to 3 visible at a time
                suggestions.take(3).forEach { suggestion ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onSuggestionClick(suggestion) }
                            .background(Color.White.copy(alpha=0.5f))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "💡", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = suggestion,
                            style = MaterialTheme.typography.bodyMedium,
                            color = CharcoalSlate.copy(alpha=0.8f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FloatingWriteButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fabPulse"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(16.dp, CircleShape, spotColor = DeepPurple)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(DeepPurple, PurpleAccent)
                )
            )
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Icon(
            Icons.Default.Edit,
            contentDescription = "Write Memory",
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
    }
}

// Reuse existing MoodAnimationOverlay logic (simplified)
@Composable
private fun MoodAnimationOverlay(selectedMood: Mood) {
    // Basic implementations for brevity, reusing concepts from previous version
    val infiniteTransition = rememberInfiniteTransition(label = "moodAnim")
    
    when (selectedMood) {
        Mood.HAPPY, Mood.EXCITED -> SparklingParticles() // Reuse
        Mood.SAD, Mood.ANXIOUS -> RainAnimation() // Reuse
        Mood.ROMANTIC, Mood.GRATEFUL -> FloatingHeartsAnimation() // Reuse
        Mood.ANGRY -> PulseAnimation(color = Color(0x22FF5252)) // Reuse
        Mood.PEACEFUL -> FloatingOrbsAnimation() // Reuse
        else -> {} 
    }
}

// Copying animation components strictly as they were valid
@Composable 
private fun SparklingParticles() {
    val particles = remember { List(20) { Particle() } }
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
    val raindrops = remember { List(30) { Raindrop() } }
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
    val hearts = remember { List(10) { FloatingHeart() } }
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
                center = Offset(
                    x = (heart.x + sway) * size.width,
                    y = y * size.height
                )
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
    val orbs = remember { List(8) { Orb() } }
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
