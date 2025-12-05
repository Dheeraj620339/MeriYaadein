package com.example.meriyaadein.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.meriyaadein.data.local.DiaryEntry
import com.example.meriyaadein.data.local.Mood
import com.example.meriyaadein.ui.components.MoodSelector
import com.example.meriyaadein.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Screen for adding or editing a diary entry
 * 
 * @param preFilledPrompt - Optional prompt from AI suggestions to pre-fill content
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    existingEntry: DiaryEntry?,
    preFilledPrompt: String?,  // NEW: Pre-filled prompt from AI suggestions
    onSave: (String, String, Long, Mood) -> Unit,
    onDelete: (() -> Unit)?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf(existingEntry?.title ?: "") }
    // If preFilledPrompt is provided, use it as initial content
    var content by remember { 
        mutableStateOf(existingEntry?.content ?: preFilledPrompt?.let { "💭 $it\n\n" } ?: "") 
    }
    var selectedMood by remember { mutableStateOf(existingEntry?.mood ?: Mood.HAPPY) }
    var selectedDate by remember { mutableLongStateOf(existingEntry?.date ?: System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    val dateFormat = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())
    val isEditing = existingEntry != null
    
    // Premium gradient background
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            GradientStart,
            GradientMid.copy(alpha = 0.5f),
            GradientEnd.copy(alpha = 0.3f)
        )
    )
    
    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            selectedDate = it
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    
    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Memory?") },
            text = { Text("This action cannot be undone. Are you sure you want to delete this memory?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete?.invoke()
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
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
                            text = if (isEditing) "✏️ Edit Memory" else "✍️ New Memory",
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
                    actions = {
                        if (isEditing && onDelete != null) {
                            IconButton(onClick = { showDeleteDialog = true }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                        IconButton(
                            onClick = {
                                if (title.isNotBlank() && content.isNotBlank()) {
                                    onSave(title, content, selectedDate, selectedMood)
                                }
                            },
                            enabled = title.isNotBlank() && content.isNotBlank()
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Save",
                                tint = if (title.isNotBlank() && content.isNotBlank()) 
                                    DeepPurple else GreyText.copy(alpha = 0.3f)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = GlassWhite
                    )
                )
            },
            containerColor = androidx.compose.ui.graphics.Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Date Selector
                OutlinedCard(
                    onClick = { showDatePicker = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = GlassWhite
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = dateFormat.format(Date(selectedDate)),
                            style = MaterialTheme.typography.bodyLarge,
                            color = CharcoalSlate
                        )
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "Select Date",
                            tint = DeepPurple
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Mood Selector
                MoodSelector(
                    selectedMood = selectedMood,
                    onMoodSelected = { selectedMood = it }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    placeholder = { Text("Give your memory a title...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DeepPurple,
                        focusedLabelColor = DeepPurple,
                        cursorColor = DeepPurple,
                        unfocusedContainerColor = GlassWhite,
                        focusedContainerColor = GlassWhite
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Content Input
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Your Memory") },
                    placeholder = { Text("Write your thoughts here...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DeepPurple,
                        focusedLabelColor = DeepPurple,
                        cursorColor = DeepPurple,
                        unfocusedContainerColor = GlassWhite,
                        focusedContainerColor = GlassWhite
                    )
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Save Button
                Button(
                    onClick = {
                        if (title.isNotBlank() && content.isNotBlank()) {
                            onSave(title, content, selectedDate, selectedMood)
                        }
                    },
                    enabled = title.isNotBlank() && content.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DeepPurple,
                        contentColor = CreamWhite
                    )
                ) {
                    Text(
                        text = if (isEditing) "Update Memory" else "Save Memory",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
