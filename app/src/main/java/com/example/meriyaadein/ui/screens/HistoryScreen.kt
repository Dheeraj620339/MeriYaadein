package com.example.meriyaadein.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.example.meriyaadein.data.local.DiaryEntry
import com.example.meriyaadein.ui.components.DateHeader
import com.example.meriyaadein.ui.components.DiaryCard
import com.example.meriyaadein.ui.components.EmptyState
import com.example.meriyaadein.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * History screen - shows all past entries with premium design
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    entries: List<DiaryEntry>,
    onEntryClick: (DiaryEntry) -> Unit,
    onFavoriteClick: (DiaryEntry) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isSearchVisible by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) { isVisible = true }
    
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
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        AnimatedVisibility(
                            visible = !isSearchVisible,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Column {
                                Text(
                                    text = "📜 History",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepPurple
                                )
                                Text(
                                    text = "Puraani yaadein",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GreyText
                                )
                            }
                        }
                        AnimatedVisibility(
                            visible = isSearchVisible,
                            enter = fadeIn() + expandHorizontally(),
                            exit = fadeOut() + shrinkHorizontally()
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = onSearchQueryChange,
                                placeholder = { Text("Search memories...") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = DeepPurple,
                                    cursorColor = DeepPurple
                                )
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { isSearchVisible = !isSearchVisible }) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = DeepPurple
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
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(500))
            ) {
                if (entries.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyState(
                            title = "Koi puraani yaad nahi",
                            subtitle = "Jab tum likhoge, sab yahan dikhega ✨"
                        )
                    }
                } else {
                    val groupedEntries = remember(entries) {
                        entries.groupBy { entry ->
                            val sdf = SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault())
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
}
