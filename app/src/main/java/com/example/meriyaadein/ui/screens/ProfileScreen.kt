package com.example.meriyaadein.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.meriyaadein.ui.theme.*

/**
 * Premium Profile Screen
 * - Clean, minimal design
 * - Name input with save button
 * - Color picker for accent color
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    currentName: String,
    currentAccentColor: String,
    onSaveName: (String) -> Unit,
    onSaveColor: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var nameInput by remember { mutableStateOf(currentName) }
    var selectedColor by remember { mutableStateOf(currentAccentColor) }
    var showSaveSuccess by remember { mutableStateOf(false) }
    
    // Animation for save success
    LaunchedEffect(showSaveSuccess) {
        if (showSaveSuccess) {
            kotlinx.coroutines.delay(1500)
            showSaveSuccess = false
        }
    }
    
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
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "👤 Profile",
                            fontWeight = FontWeight.SemiBold,
                            color = DeepPurple
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = DeepPurple
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = GlassWhite
                    )
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                
                // Profile Avatar
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    parseColor(selectedColor),
                                    parseColor(selectedColor).copy(alpha = 0.7f)
                                )
                            )
                        )
                        .border(4.dp, GlassWhite, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (nameInput.isNotBlank()) nameInput.first().uppercase() else "👤",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = CreamWhite
                    )
                }
                
                Spacer(modifier = Modifier.height(40.dp))
                
                // Name Input Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = GlassWhite)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Apna Naam Likho ✨",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = CharcoalSlate
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            placeholder = { Text("Your name...") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = parseColor(selectedColor),
                                focusedLabelColor = parseColor(selectedColor),
                                cursorColor = parseColor(selectedColor),
                                unfocusedContainerColor = CreamWhite,
                                focusedContainerColor = CreamWhite
                            ),
                            textStyle = MaterialTheme.typography.titleLarge.copy(
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Save Button
                        Button(
                            onClick = {
                                if (nameInput.isNotBlank()) {
                                    onSaveName(nameInput)
                                    showSaveSuccess = true
                                }
                            },
                            enabled = nameInput.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = parseColor(selectedColor),
                                contentColor = CreamWhite
                            )
                        ) {
                            AnimatedVisibility(
                                visible = showSaveSuccess,
                                enter = scaleIn() + fadeIn(),
                                exit = scaleOut() + fadeOut()
                            ) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            AnimatedVisibility(
                                visible = !showSaveSuccess,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                Text(
                                    text = "💾 Naam Save Karo",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Color Picker Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = GlassWhite)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🎨 Theme Color Choose Karo",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = CharcoalSlate
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        ColorPalette(
                            selectedColor = selectedColor,
                            onColorSelected = { color ->
                                selectedColor = color
                                onSaveColor(color)
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Color Palette with premium color swatches
 */
@Composable
fun ColorPalette(
    selectedColor: String,
    onColorSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = listOf(
        "#5D1424" to "Velvet Burgundy",
        "#7B2D3E" to "Warm Burgundy",
        "#B76E79" to "Rose Gold",
        "#D4A574" to "Soft Gold",
        "#C5A059" to "Antique Gold",
        "#8B4513" to "Saddle Brown",
        "#2C6B4F" to "Forest Green",
        "#1E5F74" to "Deep Teal",
        "#2E4A62" to "Navy Blue",
        "#4A3B8C" to "Deep Purple",
        "#6B5B95" to "Ultra Violet",
        "#88304E" to "Wine Red",
        "#522B5B" to "Plum",
        "#1A1A2E" to "Midnight",
        "#16213E" to "Dark Navy"
    )
    
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(colors) { (hex, name) ->
            ColorSwatch(
                color = hex,
                colorName = name,
                isSelected = selectedColor == hex,
                onClick = { onColorSelected(hex) }
            )
        }
    }
}

@Composable
private fun ColorSwatch(
    color: String,
    colorName: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "scale"
    )
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .scale(scale)
                .size(48.dp)
                .clip(CircleShape)
                .background(parseColor(color))
                .border(
                    width = if (isSelected) 3.dp else 1.dp,
                    color = if (isSelected) CreamWhite else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = CreamWhite,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = colorName.split(" ").first(),
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) CharcoalSlate else GreyText,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}

/**
 * Parse hex color string to Compose Color
 */
fun parseColor(hexColor: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hexColor))
    } catch (e: Exception) {
        Color(0xFF5D1424) // Default to Velvet Burgundy
    }
}
