package com.example.meriyaadein.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.meriyaadein.data.local.DiaryEntry
import com.example.meriyaadein.ui.components.*
import com.example.meriyaadein.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Home screen with timeline view of diary entries
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    entries: List<DiaryEntry>,
    onEntryClick: (DiaryEntry) -> Unit,
    onFavoriteClick: (DiaryEntry) -> Unit,
    onAddClick: () -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isSearchVisible by remember { mutableStateOf(false) }
    
    // Romantic pink gradient
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            GradientStart,
            GradientMid.copy(alpha = 0.6f),
            GradientEnd.copy(alpha = 0.4f)
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
                        if (isSearchVisible) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = onSearchQueryChange,
                                placeholder = { Text("Search memories...") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = DeepRose,
                                    cursorColor = DeepRose
                                )
                            )
                        } else {
                            Column {
                                Text(
                                    text = "✨ Meri Yaadein",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepRose
                                )
                                Text(
                                    text = "Your personal memories",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CharcoalSlate.copy(alpha = 0.6f)
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { isSearchVisible = !isSearchVisible }) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = DeepRose
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = GradientStart.copy(alpha = 0.95f)
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onAddClick,
                    containerColor = HoneyGold,
                    contentColor = CreamWhite
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Entry")
                }
            },
            containerColor = androidx.compose.ui.graphics.Color.Transparent
        ) { paddingValues ->
            if (entries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyState(
                        title = "No memories yet",
                        subtitle = "Start writing your first memory by tapping the + button"
                    )
                }
            } else {
                val groupedEntries = remember(entries) {
                    entries.groupBy { entry ->
                        val sdf = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
                        sdf.format(Date(entry.date))
                    }
                }
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    groupedEntries.forEach { (date, entriesForDate) ->
                        item {
                            DateHeader(dateText = date)
                        }
                        
                        items(entriesForDate, key = { it.id }) { entry ->
                            DiaryCard(
                                entry = entry,
                                onClick = { onEntryClick(entry) },
                                onFavoriteClick = { onFavoriteClick(entry) }
                            )
                        }
                    }
                }
            }
        }
    }
}
