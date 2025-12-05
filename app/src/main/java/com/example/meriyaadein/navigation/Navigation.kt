package com.example.meriyaadein.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.meriyaadein.ui.screens.*
import com.example.meriyaadein.viewmodel.DiaryViewModel
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Navigation routes
 */
object Routes {
    const val HOME = "home"
    const val HISTORY = "history"
    const val FAVORITES = "favorites"
    const val SETTINGS = "settings"
    const val ADD_ENTRY = "add_entry"
    const val ADD_ENTRY_WITH_PROMPT = "add_entry_prompt/{prompt}"
    const val EDIT_ENTRY = "edit_entry/{entryId}"
    
    fun editEntry(entryId: Long) = "edit_entry/$entryId"
    fun addEntryWithPrompt(prompt: String): String {
        val encoded = URLEncoder.encode(prompt, StandardCharsets.UTF_8.toString())
        return "add_entry_prompt/$encoded"
    }
}

/**
 * Main navigation host
 */
@Composable
fun DiaryNavHost(
    navController: NavHostController,
    viewModel: DiaryViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val entries by viewModel.allEntries.collectAsState()
    val favoriteEntries by viewModel.favoriteEntries.collectAsState()
    val todayEntry by viewModel.todayEntry.collectAsState()
    val selectedEntry by viewModel.selectedEntry.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier
    ) {
        // Home - shows today's entry only
        composable(Routes.HOME) {
            HomeScreen(
                todayEntry = todayEntry,
                onWriteClick = {
                    navController.navigate(Routes.ADD_ENTRY)
                },
                onWriteWithPrompt = { prompt ->
                    // Navigate with pre-filled prompt
                    navController.navigate(Routes.addEntryWithPrompt(prompt))
                },
                onEditClick = { entry ->
                    viewModel.loadEntryById(entry.id)
                    navController.navigate(Routes.editEntry(entry.id))
                },
                onMoodSelected = { mood ->
                    // Just update mood in ViewModel (for theme persistence)
                    // Does NOT navigate anywhere
                    viewModel.updateTodayMood(mood)
                }
            )
        }
        
        // History - shows all past entries
        composable(Routes.HISTORY) {
            HistoryScreen(
                entries = entries,
                onEntryClick = { entry ->
                    viewModel.loadEntryById(entry.id)
                    navController.navigate(Routes.editEntry(entry.id))
                },
                onFavoriteClick = { entry ->
                    viewModel.toggleFavorite(entry)
                },
                searchQuery = searchQuery,
                onSearchQueryChange = { viewModel.updateSearchQuery(it) }
            )
        }
        
        composable(Routes.FAVORITES) {
            FavoritesScreen(
                favoriteEntries = favoriteEntries,
                onEntryClick = { entry ->
                    viewModel.loadEntryById(entry.id)
                    navController.navigate(Routes.editEntry(entry.id))
                },
                onFavoriteClick = { entry ->
                    viewModel.toggleFavorite(entry)
                }
            )
        }
        
        composable(Routes.SETTINGS) {
            SettingsScreen()
        }
        
        // Add entry without prompt
        composable(Routes.ADD_ENTRY) {
            AddEditScreen(
                existingEntry = null,
                preFilledPrompt = null,
                onSave = { title, content, date, mood ->
                    viewModel.saveEntry(title, content, date, mood)
                    navController.popBackStack()
                },
                onDelete = null,
                onBack = { navController.popBackStack() }
            )
        }
        
        // Add entry WITH pre-filled prompt (from AI suggestions)
        composable(
            route = Routes.ADD_ENTRY_WITH_PROMPT,
            arguments = listOf(navArgument("prompt") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedPrompt = backStackEntry.arguments?.getString("prompt") ?: ""
            val prompt = URLDecoder.decode(encodedPrompt, StandardCharsets.UTF_8.toString())
            
            AddEditScreen(
                existingEntry = null,
                preFilledPrompt = prompt,  // Pass the prompt to pre-fill
                onSave = { title, content, date, mood ->
                    viewModel.saveEntry(title, content, date, mood)
                    navController.popBackStack()
                },
                onDelete = null,
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Routes.EDIT_ENTRY,
            arguments = listOf(navArgument("entryId") { type = NavType.LongType })
        ) { backStackEntry ->
            val entryId = backStackEntry.arguments?.getLong("entryId") ?: 0L
            
            LaunchedEffect(entryId) {
                viewModel.loadEntryById(entryId)
            }
            
            AddEditScreen(
                existingEntry = selectedEntry,
                preFilledPrompt = null,
                onSave = { title, content, date, mood ->
                    viewModel.saveEntry(title, content, date, mood, entryId)
                    navController.popBackStack()
                },
                onDelete = {
                    selectedEntry?.let { viewModel.deleteEntry(it) }
                    navController.popBackStack()
                },
                onBack = {
                    viewModel.clearSelectedEntry()
                    navController.popBackStack()
                }
            )
        }
    }
}
