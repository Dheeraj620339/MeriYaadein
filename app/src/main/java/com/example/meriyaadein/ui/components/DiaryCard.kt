package com.example.meriyaadein.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.meriyaadein.data.local.DiaryEntry
import com.example.meriyaadein.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Premium Diary Card with:
 * - Glassmorphism effect
 * - Gradient backgrounds
 * - Smooth animations
 * - Professional 2025 design
 */
@Composable
fun DiaryCard(
    entry: DiaryEntry,
    index: Int,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onLockClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    
    // Hover/press animation
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "cardScale"
    )
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = DeepPurple.copy(alpha = 0.15f),
                spotColor = PurpleAccent.copy(alpha = 0.1f)
            )
            .clickable {
                pressed = true
                onClick()
            },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            GlassWhite,
                            CardLavender.copy(alpha = 0.8f),
                            LavenderLight.copy(alpha = 0.6f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.6f),
                            Color.White.copy(alpha = 0.2f)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(18.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // S.No and Title
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "#$index",
                            style = MaterialTheme.typography.labelSmall,
                            color = GreyText.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (entry.isLocked) "🔒 Locked Memory" else entry.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = CharcoalSlate,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Mood emoji
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        LavenderMid,
                                        PurpleAccent.copy(alpha = 0.7f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = entry.mood.emoji,
                            fontSize = 20.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Content preview (blur if locked)
                Text(
                    text = if (entry.isLocked) "This memory is locked. Tap to unlock." else entry.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = CharcoalSlate.copy(alpha = 0.7f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(14.dp))
                
                // Footer: Date and Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dateFormat.format(Date(entry.date)),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = DeepPurple
                    )
                    
                    // Actions
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Favorite
                        IconButton(
                            onClick = onFavoriteClick,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (entry.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (entry.isFavorite) PurpleAccent else GreyText,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        
                        // Lock
                        IconButton(
                            onClick = onLockClick,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (entry.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                                contentDescription = "Lock",
                                tint = if (entry.isLocked) DeepPurple else GreyText,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        
                        // Delete
                        IconButton(
                            onClick = onDeleteClick,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
    
    LaunchedEffect(pressed) {
        if (pressed) {
            kotlinx.coroutines.delay(100)
            pressed = false
        }
    }
}
