package com.example.meriyaadein.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
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

/**
 * Professional Home Screen with:
 * - Dynamic Smart Greeting
 * - Glassmorphism Cards
 * - Interactive Mood Selector
 * - AI-Smart Suggestions
 * - 2025 Premium Design
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    todayEntry: DiaryEntry?,
    onWriteClick: () -> Unit,
    onEditClick: (DiaryEntry) -> Unit,
    onMoodSelected: (Mood) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentHour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    val currentDate = remember { 
        SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()).format(Date()) 
    }
    
    // Premium dual gradient background
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            GradientStart,
            GradientMid,
            GradientEnd.copy(alpha = 0.7f)
        )
    )
    
    // Animation states
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(gradientBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            
            // ========== DYNAMIC SMART GREETING ==========
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(600)) + slideInVertically(
                    initialOffsetY = { -40 },
                    animationSpec = tween(600)
                )
            ) {
                SmartGreetingSection(currentHour = currentHour)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // ========== SMART CONTEXT LINE ==========
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(800, delayMillis = 200))
            ) {
                SmartContextLine(currentDate = currentDate)
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
                    selectedMood = todayEntry?.mood,
                    onMoodSelected = onMoodSelected
                )
            }
            
            Spacer(modifier = Modifier.height(28.dp))
            
            // ========== AI-SMART SUGGESTIONS ==========
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 800))
            ) {
                AiSmartSuggestions()
            }
            
            Spacer(modifier = Modifier.height(100.dp))
        }
        
        // ========== FLOATING ACTION BUTTON 2.0 ==========
        PremiumFloatingActionButton(
            onClick = onWriteClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        )
    }
}

@Composable
private fun SmartGreetingSection(currentHour: Int) {
    val (greeting, emoji, subtext) = when {
        currentHour in 5..11 -> Triple(
            "Good Morning, Dheeraj!",
            "☀️",
            "Let's start fresh today."
        )
        currentHour in 12..16 -> Triple(
            "Good Afternoon, Champ!",
            "😎",
            "Hope your day is going well."
        )
        currentHour in 17..20 -> Triple(
            "Good Evening, Buddy!",
            "🌅",
            "Time to reflect on today."
        )
        else -> Triple(
            "Good Night, Thinker!",
            "🌙",
            "Aaj kya seekha?"
        )
    }
    
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = greeting,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = DeepPurple
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = emoji, fontSize = 28.sp)
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = subtext,
            style = MaterialTheme.typography.bodyLarge,
            color = CharcoalSlate.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun SmartContextLine(currentDate: String) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "📅", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = currentDate,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = CharcoalSlate.copy(alpha = 0.8f)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Thought of the day
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(LavenderLight.copy(alpha = 0.5f))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "💡", fontSize = 18.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "\"Success loves discipline.\"",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                color = DeepPurple
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
                        colors = listOf(
                            GlassWhite,
                            GlassLight,
                            GlassFrost
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.8f),
                            Color.White.copy(alpha = 0.3f)
                        )
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(24.dp)
        ) {
            if (entry != null) {
                // Today's entry exists
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
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(LavenderMid, PurpleAccent)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = entry.mood.emoji,
                                    fontSize = 24.sp
                                )
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
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(LavenderLight)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = DeepPurple
                            )
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
                // Empty state - prompt to write
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "✍️",
                        fontSize = 56.sp
                    )
                    
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
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DeepPurple,
                            contentColor = CreamWhite
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.height(52.dp)
                    ) {
                        Icon(Icons.Default.Create, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Likhna Shuru Karo",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InteractiveMoodSelector(
    selectedMood: Mood?,
    onMoodSelected: (Mood) -> Unit
) {
    val moods = listOf(
        Mood.HAPPY,      // 🙂
        Mood.PEACEFUL,   // 😌
        Mood.ROMANTIC,   // 😍
        Mood.SAD,        // 😢
        Mood.NEUTRAL,    // 😐
        Mood.ANGRY       // 😠
    )
    
    Column {
        Text(
            text = "🎭 Aaj ka Mood",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = DeepPurple
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            moods.forEach { mood ->
                InteractiveMoodChip(
                    mood = mood,
                    isSelected = selectedMood == mood,
                    onClick = { onMoodSelected(mood) }
                )
            }
        }
    }
}

@Composable
private fun InteractiveMoodChip(
    mood: Mood,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Bounce animation
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.85f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "bounce"
    )
    
    // Neon ring animation
    val infiniteTransition = rememberInfiniteTransition(label = "neon")
    val neonAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "neonAlpha"
    )
    
    Box(
        modifier = Modifier
            .size(56.dp)
            .scale(scale)
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 3.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                NeonPurple.copy(alpha = neonAlpha),
                                NeonBlue.copy(alpha = neonAlpha),
                                NeonPink.copy(alpha = neonAlpha)
                            )
                        ),
                        shape = CircleShape
                    )
                } else Modifier
            )
            .clip(CircleShape)
            .background(
                if (isSelected) LavenderMid.copy(alpha = 0.8f) 
                else GlassLight
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                pressed = true
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = mood.emoji,
            fontSize = 28.sp
        )
    }
    
    LaunchedEffect(pressed) {
        if (pressed) {
            kotlinx.coroutines.delay(150)
            pressed = false
        }
    }
}

@Composable
private fun AiSmartSuggestions() {
    val suggestions = listOf(
        "💭 Kal ke goals kitne complete hue?",
        "🌟 Kis baat ne aaj proud feel karaya?",
        "💕 Aaj kisi ne accha behave kiya? Mention karo.",
        "📸 Koi special moment capture karna chahte ho?"
    )
    
    Column {
        Text(
            text = "✨ AI Suggestions",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            color = GreyText
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        suggestions.forEach { suggestion ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = GlassWhite
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = suggestion,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = CharcoalSlate.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun PremiumFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 3D shadow effect
    val elevation by animateFloatAsState(
        targetValue = 12f,
        animationSpec = spring(),
        label = "elevation"
    )
    
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier
            .shadow(
                elevation = elevation.dp,
                shape = CircleShape,
                ambientColor = DeepPurple.copy(alpha = 0.4f),
                spotColor = PurpleAccent.copy(alpha = 0.4f)
            )
            .size(64.dp),
        shape = CircleShape,
        containerColor = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(PurpleAccent, DeepPurple)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Memory",
                modifier = Modifier.size(32.dp),
                tint = CreamWhite
            )
        }
    }
}
