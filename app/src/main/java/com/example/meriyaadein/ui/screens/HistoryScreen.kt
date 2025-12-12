package com.example.meriyaadein.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import com.example.meriyaadein.data.local.DiaryEntry
import com.example.meriyaadein.ui.components.DateHeader
import com.example.meriyaadein.ui.components.DiaryCard
import com.example.meriyaadein.ui.components.EmptyState
import com.example.meriyaadein.ui.components.PinDialog
import com.example.meriyaadein.ui.theme.*
import java.text.SimpleDateFormat
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import com.example.meriyaadein.viewmodel.DiaryViewModel
import java.util.*
import com.example.meriyaadein.data.local.Mood

/**
 * History screen - shows all past entries with premium design
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    entries: List<DiaryEntry>,
    onEntryClick: (DiaryEntry) -> Unit,
    onFavoriteClick: (DiaryEntry) -> Unit,
    onLockClick: (DiaryEntry) -> Unit,
    onDeleteClick: (DiaryEntry) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedTab: DiaryViewModel.HistoryTab,
    onTabSelected: (DiaryViewModel.HistoryTab) -> Unit,
    selectedVibe: Mood?,
    onVibeSelected: (Mood?) -> Unit,
    isPinSet: Boolean,
    onSetPin: (String) -> Unit,
    onValidatePin: (String) -> Boolean,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf<DiaryEntry?>(null) }
    var showViewModal by remember { mutableStateOf<DiaryEntry?>(null) }
    var showPinDialogForEntry by remember { mutableStateOf<DiaryEntry?>(null) }
    var showSetPinDialog by remember { mutableStateOf(false) }
    
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            GradientStart,
            GradientMid,
            GradientEnd.copy(alpha = 0.7f)
        )
    )
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(gradientBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // I. Upar Ka Fixed Area (The Command Center)
            // 1. Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "📚 Yaadon Ki Gallery",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = DeepPurple
                    )
                }
                IconButton(onClick = { showSetPinDialog = true }) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = DeepPurple
                    )
                }
            }
            
            // 2. Navigation Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                TabButton(
                    text = "📜 Yaadein",
                    isSelected = selectedTab == DiaryViewModel.HistoryTab.ALL,
                    onClick = { onTabSelected(DiaryViewModel.HistoryTab.ALL) }
                )
                Spacer(modifier = Modifier.width(12.dp))
                TabButton(
                    text = "❤️ Favorite",
                    isSelected = selectedTab == DiaryViewModel.HistoryTab.FAVORITES,
                    onClick = { onTabSelected(DiaryViewModel.HistoryTab.FAVORITES) }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // II. The Filter & Search Toolbox (Sticky Area)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GlassWhite.copy(alpha = 0.9f))
                    .padding(vertical = 12.dp)
            ) {
                // 1. Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search title, date, or S.No...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = DeepPurple) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DeepPurple,
                        unfocusedBorderColor = LavenderMid,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White.copy(alpha = 0.7f)
                    ),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // 2. Vibe Tags (Horizontal Scroll)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // "Sab Dekho" (Clear Filter)
                    FilterChip(
                        selected = selectedVibe == null,
                        onClick = { onVibeSelected(null) },
                        label = { Text("Sab Dekho") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DeepPurple,
                            selectedLabelColor = Color.White
                        )
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Mood.entries.forEach { mood ->
                        FilterChip(
                            selected = selectedVibe == mood,
                            onClick = { onVibeSelected(if (selectedVibe == mood) null else mood) },
                            label = { Text("${mood.emoji} ${mood.label}") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DeepPurple,
                                selectedLabelColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
            
            // III. The Memory Cards (Gallery)
            if (entries.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // EmptyState(
                    //     title = "Koi yaad nahi mili",
                    //     subtitle = "Filters change karke dekho ya kuch naya likho ✨"
                    // )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(entries.size) { i ->
                        val entry = entries[i]
                        val index = entries.size - i
                        
                        DiaryCard(
                            entry = entry,
                            index = index,
                            onClick = { 
                                if (entry.isLocked) {
                                    if (isPinSet) {
                                        showPinDialogForEntry = entry
                                    } else {
                                        showSetPinDialog = true
                                    }
                                } else {
                                    showViewModal = entry 
                                }
                            },
                            onFavoriteClick = { onFavoriteClick(entry) },
                            onLockClick = { onLockClick(entry) },
                            onDeleteClick = { showDeleteDialog = entry }
                        )
                    }
                }
            }
        }
    }
    
    // Delete Dialog
    if (showDeleteDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Memory?") },
            text = { Text("Pakka delete karna hai? Yeh wapas nahi aayegi.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteClick(showDeleteDialog!!)
                        showDeleteDialog = null
                    }
                ) {
                    Text("Yes, Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // View Modal (Full Story)
    if (showViewModal != null) {
        val entry = showViewModal!!
        ModalBottomSheet(
            onDismissRequest = { showViewModal = null },
            containerColor = GlassWhite
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = entry.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = DeepPurple
                    )
                    Text(
                        text = entry.mood.emoji,
                        fontSize = 32.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault()).format(Date(entry.date)),
                    style = MaterialTheme.typography.bodySmall,
                    color = GreyText
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = entry.content,
                    style = MaterialTheme.typography.bodyLarge,
                    color = CharcoalSlate
                )
                Spacer(modifier = Modifier.height(32.dp))
                
                val context = androidx.compose.ui.platform.LocalContext.current
                
                // PDF Buttons
                Button(
                    onClick = { 
                        com.example.meriyaadein.utils.PdfGenerator.generateDiaryPdf(context, entry)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepPurple)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Download PDF")
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { 
                        val file = com.example.meriyaadein.utils.PdfGenerator.generateDiaryPdf(context, entry)
                        if (file != null) {
                            val uri = androidx.core.content.FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.provider",
                                file
                            )
                            val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                type = "application/pdf"
                                putExtra(android.content.Intent.EXTRA_STREAM, uri)
                                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(android.content.Intent.createChooser(intent, "Share PDF"))
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share PDF")
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }


    // PIN Dialog for Entry Access
    var pinError by remember { mutableStateOf<String?>(null) }

    if (showPinDialogForEntry != null) {
        PinDialog(
            title = "Unlock Memory",
            onPinEntered = { pin ->
                if (onValidatePin(pin)) {
                    showViewModal = showPinDialogForEntry
                    showPinDialogForEntry = null
                    pinError = null
                } else {
                    pinError = "Incorrect PIN"
                }
            },
            onDismiss = { 
                showPinDialogForEntry = null 
                pinError = null
            },
            errorMessage = pinError
        )
    }
    
    // Set PIN Dialog
    if (showSetPinDialog) {
        PinDialog(
            title = if (isPinSet) "Change PIN" else "Set New PIN",
            onPinEntered = { pin ->
                onSetPin(pin)
                showSetPinDialog = false
            },
            onDismiss = { showSetPinDialog = false }
        )
    }
}

@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) DeepPurple else Color.Transparent)
            .border(1.dp, if (isSelected) DeepPurple else GreyText.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else GreyText,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
