package com.example.meriyaadein.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
 * Home screen - shows only TODAY's entry with greeting
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
    val currentDate = remember { 
        SimpleDateFormat("dd MMMM", Locale.getDefault()).format(Date()) 
    }
    val dayName = remember {
        SimpleDateFormat("EEEE", Locale.getDefault()).format(Date())
    }
    
    // Romantic pink gradient
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            GradientStart,
            GradientMid.copy(alpha = 0.5f),
            GradientEnd.copy(alpha = 0.3f)
        )
    )
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(gradientBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            
            // ========== GREETING SECTION ==========
            GreetingSection(currentDate = currentDate, dayName = dayName)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // ========== TODAY'S ENTRY CARD ==========
            TodayEntryCard(
                entry = todayEntry,
                onWriteClick = onWriteClick,
                onEditClick = { todayEntry?.let { onEditClick(it) } }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // ========== MOOD SELECTOR ==========
            MoodSelectorSection(
                selectedMood = todayEntry?.mood,
                onMoodSelected = onMoodSelected
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // ========== PROMPTS ==========
            PromptsSection()
            
            Spacer(modifier = Modifier.height(100.dp)) // Space for FAB
        }
        
        // ========== FLOATING ADD BUTTON ==========
        FloatingActionButton(
            onClick = onWriteClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = HoneyGold,
            contentColor = CreamWhite,
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Memory",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun GreetingSection(currentDate: String, dayName: String) {
    Column {
        Text(
            text = "Hello Dheeraj 🌸",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = DeepRose
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "Aaj $currentDate hai.",
            style = MaterialTheme.typography.bodyLarge,
            color = CharcoalSlate.copy(alpha = 0.7f)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Kaise ho? Aaj kya hua… likh do yahan ❤️",
            style = MaterialTheme.typography.bodyMedium,
            color = DeepRose.copy(alpha = 0.8f),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun TodayEntryCard(
    entry: DiaryEntry?,
    onWriteClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = DeepRose.copy(alpha = 0.15f),
                spotColor = DeepRose.copy(alpha = 0.15f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            CreamWhite,
                            BlushRose.copy(alpha = 0.4f),
                            SoftLavender.copy(alpha = 0.3f)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            if (entry != null) {
                // Show today's entry
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(BlushRose),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = entry.mood.emoji,
                                    fontSize = 22.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Aaj ki Yaad",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = DeepRose
                            )
                        }
                        
                        IconButton(onClick = onEditClick) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = DeepRose.copy(alpha = 0.6f)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = entry.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalSlate
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = entry.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = CharcoalSlate.copy(alpha = 0.7f),
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
                        text = "📝",
                        fontSize = 48.sp
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
                        color = CharcoalSlate.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Button(
                        onClick = onWriteClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DeepRose,
                            contentColor = CreamWhite
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "✍️ Likhna Shuru Karo",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MoodSelectorSection(
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
            text = "Aaj ka Mood",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = DeepRose
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            moods.forEach { mood ->
                val isSelected = selectedMood == mood
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) BlushRose else CardPink
                        )
                        .then(
                            if (isSelected) Modifier.shadow(4.dp, CircleShape) else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = { onMoodSelected(mood) }) {
                        Text(
                            text = mood.emoji,
                            fontSize = 26.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PromptsSection() {
    val prompts = listOf(
        "💭 Aaj kis baat ne muskuraya?",
        "✨ Koi khaas baat hui?",
        "💫 Kuch yaad rehne layak moment?"
    )
    
    Column {
        Text(
            text = "Kuch ideas...",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            color = CharcoalSlate.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        prompts.forEach { prompt ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CreamWhite.copy(alpha = 0.8f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = prompt,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = CharcoalSlate.copy(alpha = 0.7f)
                )
            }
        }
    }
}
