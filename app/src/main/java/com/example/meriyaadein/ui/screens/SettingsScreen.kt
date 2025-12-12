package com.example.meriyaadein.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.Message
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.meriyaadein.ui.components.PinDialog

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.meriyaadein.ui.theme.*

/**
 * Premium Settings Screen - Modern, Clean, Professional UI
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    isPinSet: Boolean = false,
    onSetPin: (String) -> Unit = {},
    onValidatePin: (String) -> Boolean = { true },
    modifier: Modifier = Modifier
) {
    var showSetPinDialog by remember { mutableStateOf(false) }
    var showVerifyPinDialog by remember { mutableStateOf(false) }
    val isDark = isSystemInDarkTheme()
    
    // Theme-aware colors
    val backgroundColor = if (isDark) DarkDeepPurple else GradientStart
    val cardBackground = if (isDark) DarkCard.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.85f)
    val sectionTitleColor = if (isDark) LavenderMid else DeepPurple
    val itemTitleColor = if (isDark) CreamWhite else CharcoalSlate
    val itemSubtitleColor = if (isDark) CreamWhite.copy(alpha = 0.6f) else CharcoalSlate.copy(alpha = 0.55f)
    val iconTint = if (isDark) TealAccent else DeepPurple
    val dividerColor = if (isDark) CreamWhite.copy(alpha = 0.08f) else CharcoalSlate.copy(alpha = 0.08f)
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) CreamWhite else DeepPurple
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = backgroundColor,
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // ═══════════════════════════════════════════════════════════
            // 👤 YOUR PROFILE SECTION
            // ═══════════════════════════════════════════════════════════
            item {
                SettingsSection(
                    title = "Your Profile",
                    emoji = "👤",
                    sectionTitleColor = sectionTitleColor,
                    cardBackground = cardBackground,
                    dividerColor = dividerColor
                ) {
                    SettingsItem(
                        icon = Icons.Outlined.Edit,
                        title = "Edit Name",
                        iconTint = iconTint,
                        titleColor = itemTitleColor,
                        subtitleColor = itemSubtitleColor
                    )
                    SettingsDivider(dividerColor)
                    SettingsItem(
                        icon = Icons.Outlined.Person,
                        title = "About You",
                        iconTint = iconTint,
                        titleColor = itemTitleColor,
                        subtitleColor = itemSubtitleColor
                    )
                }
            }
            
            // ═══════════════════════════════════════════════════════════
            // 🎨 APPEARANCE SECTION
            // ═══════════════════════════════════════════════════════════
            item {
                SettingsSection(
                    title = "Appearance",
                    emoji = "🎨",
                    sectionTitleColor = sectionTitleColor,
                    cardBackground = cardBackground,
                    dividerColor = dividerColor
                ) {
                    SettingsItem(
                        icon = Icons.Outlined.Palette,
                        title = "Theme & Colors",
                        iconTint = iconTint,
                        titleColor = itemTitleColor,
                        subtitleColor = itemSubtitleColor
                    )
                    SettingsDivider(dividerColor)
                    SettingsItem(
                        icon = Icons.Outlined.TextFormat,
                        title = "Font & Text Style",
                        iconTint = iconTint,
                        titleColor = itemTitleColor,
                        subtitleColor = itemSubtitleColor
                    )
                    SettingsDivider(dividerColor)
                    SettingsItem(
                        icon = Icons.Outlined.Language,
                        title = "App Language",
                        iconTint = iconTint,
                        titleColor = itemTitleColor,
                        subtitleColor = itemSubtitleColor
                    )
                }
            }
            
            // ═══════════════════════════════════════════════════════════
            // ✨ PERSONALIZATION SECTION
            // ═══════════════════════════════════════════════════════════
            item {
                SettingsSection(
                    title = "Personalization",
                    emoji = "✨",
                    sectionTitleColor = sectionTitleColor,
                    cardBackground = cardBackground,
                    dividerColor = dividerColor
                ) {
                    SettingsItem(
                        icon = Icons.Outlined.Notifications,
                        title = "Daily Reminder",
                        iconTint = iconTint,
                        titleColor = itemTitleColor,
                        subtitleColor = itemSubtitleColor
                    )
                    SettingsDivider(dividerColor)
                    SettingsItem(
                        icon = Icons.Outlined.Mood,
                        title = "Mood Tracking",
                        iconTint = iconTint,
                        titleColor = itemTitleColor,
                        subtitleColor = itemSubtitleColor
                    )
                    SettingsDivider(dividerColor)
                    SettingsItem(
                        icon = Icons.Outlined.AutoAwesome,
                        title = "AI Suggestions",
                        iconTint = iconTint,
                        titleColor = itemTitleColor,
                        subtitleColor = itemSubtitleColor
                    )
                    SettingsDivider(dividerColor)
                    SettingsItem(
                        icon = Icons.Outlined.WavingHand,
                        title = "Home Greeting Style",
                        iconTint = iconTint,
                        titleColor = itemTitleColor,
                        subtitleColor = itemSubtitleColor
                    )
                }
            }
            
            // ═══════════════════════════════════════════════════════════
            // 💾 MEMORY MANAGEMENT SECTION
            // ═══════════════════════════════════════════════════════════
            item {
                SettingsSection(
                    title = "Memory Management",
                    emoji = "💾",
                    sectionTitleColor = sectionTitleColor,
                    cardBackground = cardBackground,
                    dividerColor = dividerColor
                ) {
                    SettingsItem(
                        icon = Icons.Outlined.CloudUpload,
                        title = "Backup to Drive",
                        iconTint = iconTint,
                        titleColor = itemTitleColor,
                        subtitleColor = itemSubtitleColor
                    )
                    SettingsDivider(dividerColor)
                    SettingsItem(
                        icon = Icons.Outlined.CloudDownload,
                        title = "Restore Backup",
                        iconTint = iconTint,
                        titleColor = itemTitleColor,
                        subtitleColor = itemSubtitleColor
                    )
                    SettingsDivider(dividerColor)
                    SettingsItem(
                        icon = Icons.Outlined.FileDownload,
                        title = "Export Memories",
                        iconTint = iconTint,
                        titleColor = itemTitleColor,
                        subtitleColor = itemSubtitleColor
                    )
                    SettingsDivider(dividerColor)
                    SettingsItem(
                        icon = Icons.Outlined.CleaningServices,
                        title = "Storage Cleanup",
                        iconTint = iconTint,
                        titleColor = itemTitleColor,
                        subtitleColor = itemSubtitleColor
                    )
                }
            }
            
            // ═══════════════════════════════════════════════════════════
            // 🔒 PRIVACY CONTROL SECTION
            // ═══════════════════════════════════════════════════════════
            item {
                SettingsSection(
                    title = "Privacy Control",
                    emoji = "🔒",
                    sectionTitleColor = sectionTitleColor,
                    cardBackground = cardBackground,
                    dividerColor = dividerColor
                ) {
                    SettingsItem(
                        icon = Icons.Outlined.Lock,
                        title = if (isPinSet) "Change PIN" else "Set App Lock",
                        iconTint = iconTint,
                        titleColor = itemTitleColor,
                        subtitleColor = itemSubtitleColor,
                        onClick = {
                            if (isPinSet) {
                                showVerifyPinDialog = true
                            } else {
                                showSetPinDialog = true
                            }
                        }
                    )
                    SettingsDivider(dividerColor)
                    SettingsItem(
                        icon = Icons.Outlined.VisibilityOff,
                        title = "Private Notes",
                        iconTint = iconTint,
                        titleColor = itemTitleColor,
                        subtitleColor = itemSubtitleColor
                    )
                }
            }
            
            // ═══════════════════════════════════════════════════════════
            // ℹ️ SUPPORT & INFO SECTION
            // ═══════════════════════════════════════════════════════════
            item {
                SettingsSection(
                    title = "Support & Info",
                    emoji = "ℹ️",
                    sectionTitleColor = sectionTitleColor,
                    cardBackground = cardBackground,
                    dividerColor = dividerColor
                ) {
                    SettingsItem(
                        icon = Icons.AutoMirrored.Outlined.HelpOutline,
                        title = "Help Center",
                        iconTint = iconTint,
                        titleColor = itemTitleColor,
                        subtitleColor = itemSubtitleColor
                    )
                    SettingsDivider(dividerColor)
                    SettingsItem(
                        icon = Icons.AutoMirrored.Outlined.Message,
                        title = "Feedback",
                        iconTint = iconTint,
                        titleColor = itemTitleColor,
                        subtitleColor = itemSubtitleColor
                    )
                    SettingsDivider(dividerColor)
                    SettingsItem(
                        icon = Icons.Outlined.Policy,
                        title = "Terms & Privacy",
                        iconTint = iconTint,
                        titleColor = itemTitleColor,
                        subtitleColor = itemSubtitleColor
                    )
                    SettingsDivider(dividerColor)
                    SettingsItemWithValue(
                        icon = Icons.Outlined.Info,
                        title = "App Version",
                        value = "v1.0.0",
                        iconTint = iconTint,
                        titleColor = itemTitleColor,
                        valueColor = itemSubtitleColor
                    )
                }
            }
            
            // App Branding Footer
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "✨ Meri Yaadein",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) LavenderMid else DeepPurple
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Made with ❤️ for your memories",
                        style = MaterialTheme.typography.bodySmall,
                        color = itemSubtitleColor
                    )
                }
            }
        }
    }
    
    // Dialogs

}

// ═══════════════════════════════════════════════════════════════════════════════
// REUSABLE COMPONENTS
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun SettingsSection(
    title: String,
    emoji: String,
    sectionTitleColor: Color,
    cardBackground: Color,
    dividerColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        // Section Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 10.dp, start = 4.dp)
        ) {
            Text(
                text = emoji,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = sectionTitleColor,
                letterSpacing = 0.3.sp
            )
        }
        
        // Section Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = cardBackground),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    iconTint: Color,
    titleColor: Color,
    subtitleColor: Color,
    onClick: (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent,
        onClick = onClick ?: {}
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon with subtle background
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(14.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = titleColor,
                modifier = Modifier.weight(1f)
            )
            
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = subtitleColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SettingsItemWithValue(
    icon: ImageVector,
    title: String,
    value: String,
    iconTint: Color,
    titleColor: Color,
    valueColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon with subtle background
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(14.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = titleColor,
            modifier = Modifier.weight(1f)
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Normal,
            color = valueColor
        )
    }
}

@Composable
private fun SettingsDivider(color: Color) {
    HorizontalDivider(
        modifier = Modifier.padding(start = 66.dp, end = 16.dp),
        thickness = 0.5.dp,
        color = color
    )
}
