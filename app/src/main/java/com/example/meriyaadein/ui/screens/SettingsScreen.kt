package com.example.meriyaadein.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.meriyaadein.ui.theme.*

/**
 * Settings screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = VelvetBurgundy
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CreamPaper
                )
            )
        },
        containerColor = CreamPaper,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // About Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BlushMist.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "✨ Meri Yaadein",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = VelvetBurgundy
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your personal diary for cherished memories",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CharcoalSlate.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Version 1.0",
                        style = MaterialTheme.typography.labelSmall,
                        color = CharcoalSlate.copy(alpha = 0.5f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Preferences",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = CharcoalSlate
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Settings Items
            SettingsItem(
                icon = Icons.Default.Notifications,
                title = "Reminders",
                subtitle = "Set daily writing reminders"
            )
            
            SettingsItem(
                icon = Icons.Default.Lock,
                title = "Privacy",
                subtitle = "App lock and security"
            )
            
            SettingsItem(
                icon = Icons.Default.CloudUpload,
                title = "Backup",
                subtitle = "Backup your memories"
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "About",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = CharcoalSlate
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            SettingsItem(
                icon = Icons.Default.Info,
                title = "About App",
                subtitle = "Learn more about Meri Yaadein"
            )
            
            SettingsItem(
                icon = Icons.Default.Star,
                title = "Rate Us",
                subtitle = "Love the app? Rate us!"
            )
            
            SettingsItem(
                icon = Icons.Default.Share,
                title = "Share App",
                subtitle = "Share with friends and family"
            )
        }
    }
}

@Composable
private fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = CreamPaper),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = VelvetBurgundy,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = CharcoalSlate
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = CharcoalSlate.copy(alpha = 0.6f)
                )
            }
            
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = CharcoalSlate.copy(alpha = 0.4f)
            )
        }
    }
}
