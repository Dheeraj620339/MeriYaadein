package com.example.meriyaadein.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.meriyaadein.data.local.DiaryEntry
import com.example.meriyaadein.ui.components.DiaryCard
import com.example.meriyaadein.ui.components.EmptyState
import com.example.meriyaadein.ui.theme.*

/**
 * Screen showing favorite diary entries
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    favoriteEntries: List<DiaryEntry>,
    onEntryClick: (DiaryEntry) -> Unit,
    onFavoriteClick: (DiaryEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Favorites",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = VelvetBurgundy
                        )
                        Text(
                            text = "${favoriteEntries.size} treasured memories",
                            style = MaterialTheme.typography.bodySmall,
                            color = CharcoalSlate.copy(alpha = 0.6f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CreamPaper
                )
            )
        },
        containerColor = CreamPaper,
        modifier = modifier
    ) { paddingValues ->
        if (favoriteEntries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(
                    title = "No favorites yet",
                    subtitle = "Tap the heart icon on any memory to add it here"
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(favoriteEntries, key = { it.id }) { entry ->
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
