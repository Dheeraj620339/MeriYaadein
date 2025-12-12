package com.example.meriyaadein.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.graphics.vector.ImageVector
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
 * Premium Home Screen Redesign
 * - Top Quote Section
 * - Greeting + Date + Time
 * - Mood Selector (Theme Changer)
 * - AI Suggestion Box (Auto-scroll)
 * - Bottom Navigation with Hero Write Button
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    todayEntry: DiaryEntry?,
    recentEntries: List<DiaryEntry> = emptyList(),
    onWriteClick: () -> Unit,
    onWriteWithPrompt: (String) -> Unit,
    onEditClick: (DiaryEntry) -> Unit,
    onMoodSelected: (Mood) -> Unit,
    onProfileClick: () -> Unit = {},
    currentSentence: String = "",
    currentTimeMillis: Long = System.currentTimeMillis(),
    currentMood: Mood = Mood.NEUTRAL,
    moodSuggestions: List<String> = emptyList(),
    userName: String = "Friend",
    accentColor: String = "#5D1424",
    draftTitle: String = "",
    draftContent: String = "",
    onTitleChange: (String) -> Unit = {},
    onContentChange: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    onNavigateToHistory: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val effectiveMood = currentMood 
    val moodGradient = getMoodGradient(effectiveMood)
    
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }
    
    val calendar = Calendar.getInstance().apply { timeInMillis = currentTimeMillis }
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
    
    val timeString = timeFormat.format(calendar.time)
    val dateString = dateFormat.format(calendar.time)

    Scaffold(
        bottomBar = {
            HomeBottomNavigation(
                onHomeClick = { /* Already on Home */ },
                onWriteClick = onWriteClick,
                onHistoryClick = onNavigateToHistory,
                onSettingsClick = onNavigateToSettings
            )
        },
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
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
            ) {
                // 1. TOP QUOTE SECTION
                QuoteHeader(quote = currentSentence)
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 2. GREETING + DATE + TIME
                GreetingSection(
                    userName = userName,
                    greetingTime = hour,
                    timeString = timeString,
                    dateString = dateString
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // 3. MOOD SELECTOR
                MoodSelector(
                    selectedMood = effectiveMood,
                    onMoodSelected = onMoodSelected
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // 4. AI SUGGESTION BOX
                AiSuggestionCard(
                    suggestions = moodSuggestions,
                    onSuggestionClick = onWriteWithPrompt
                )
                
                Spacer(modifier = Modifier.height(100.dp)) // Bottom padding for nav bar
            }
        }
    }
}

// 1. TOP QUOTE SECTION
@Composable
fun QuoteHeader(quote: String) {
    var visible by remember { mutableStateOf(true) }
    var currentQuote by remember { mutableStateOf(quote) }

    LaunchedEffect(quote) {
        visible = false
        kotlinx.coroutines.delay(500)
        currentQuote = quote
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(800)),
        exit = fadeOut(animationSpec = tween(500))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 24.dp, end = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "\"$currentQuote\"",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    lineHeight = 40.sp
                ),
                color = DeepPurple,
                textAlign = TextAlign.Center,
                fontSize = 28.sp
            )
        }
    }
}

// 2. GREETING + DATE + TIME
@Composable
fun GreetingSection(
    userName: String,
    greetingTime: Int,
    timeString: String,
    dateString: String
) {
    val greeting = when (greetingTime) {
        in 5..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        in 17..21 -> "Good Evening"
        else -> "Good Night"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "👋 $greeting, $userName",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = CharcoalSlate
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "📆 $dateString",
            style = MaterialTheme.typography.bodyMedium,
            color = GreyText
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "⏱ $timeString",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = DeepPurple
        )
    }
}

// 3. MOOD SELECTOR
@Composable
fun MoodSelector(
    selectedMood: Mood,
    onMoodSelected: (Mood) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "How are you feeling?",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = CharcoalSlate,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Mood.entries.forEach { mood ->
                MoodEmojiItem(
                    mood = mood,
                    isSelected = mood == selectedMood,
                    onClick = { onMoodSelected(mood) }
                )
            }
        }
    }
}

@Composable
fun MoodEmojiItem(
    mood: Mood,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.2f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .scale(scale)
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Text(
            text = mood.emoji,
            fontSize = 32.sp
        )
    }
}

// 4. AI SUGGESTION BOX
@Composable
fun AiSuggestionCard(
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit
) {
    val listState = rememberScrollState()
    
    // Auto-scroll logic
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(50)
            if (listState.value < listState.maxValue) {
                listState.animateScrollTo(listState.value + 2)
            } else {
                listState.scrollTo(0)
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .padding(horizontal = 24.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = DeepPurple.copy(alpha = 0.1f),
                spotColor = DeepPurple.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = GlassWhite.copy(alpha = 0.9f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "✨ AI SUGGESTION",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = DeepPurple,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(listState)
            ) {
                suggestions.forEach { suggestion ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { onSuggestionClick(suggestion) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(DeepPurple.copy(alpha = 0.5f), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = suggestion,
                            style = MaterialTheme.typography.bodyMedium,
                            color = CharcoalSlate.copy(alpha = 0.8f),
                            lineHeight = 20.sp
                        )
                    }
                    HorizontalDivider(color = LavenderLight.copy(alpha = 0.3f))
                }
                // Duplicate list for infinite scroll illusion
                suggestions.forEach { suggestion ->
                     Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { onSuggestionClick(suggestion) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(DeepPurple.copy(alpha = 0.5f), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = suggestion,
                            style = MaterialTheme.typography.bodyMedium,
                            color = CharcoalSlate.copy(alpha = 0.8f),
                            lineHeight = 20.sp
                        )
                    }
                    HorizontalDivider(color = LavenderLight.copy(alpha = 0.3f))
                }
            }
        }
    }
}

// 5. BOTTOM NAVIGATION
@Composable
fun HomeBottomNavigation(
    onHomeClick: () -> Unit,
    onWriteClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Background Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
            color = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home Button
                NavButton(
                    icon = Icons.Default.Home,
                    label = "Home",
                    isSelected = true,
                    onClick = onHomeClick
                )
                
                Spacer(modifier = Modifier.width(48.dp)) // Space for Write Button
                
                // History Button
                NavButton(
                    icon = Icons.Default.History,
                    label = "History",
                    isSelected = false,
                    onClick = onHistoryClick
                )
                
                // Settings Button
                NavButton(
                    icon = Icons.Default.Settings,
                    label = "Settings",
                    isSelected = false,
                    onClick = onSettingsClick
                )
            }
        }
        
        // Hero Write Button
        Box(
            modifier = Modifier
                .padding(bottom = 30.dp)
                .size(72.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = CircleShape,
                    spotColor = DeepPurple.copy(alpha = 0.5f)
                )
                .clip(CircleShape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(DeepPurple, Color(0xFF9575CD))
                    )
                )
                .clickable(onClick = onWriteClick),
            contentAlignment = Alignment.Center
        ) {
            // Pulse Animation
            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
            val scale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulse"
            )
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(scale)
                    .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape)
            )
            
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Write",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun NavButton(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) DeepPurple else GreyText,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) DeepPurple else GreyText,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

// ==================== MOOD ANIMATION OVERLAY (Reused) ====================
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
