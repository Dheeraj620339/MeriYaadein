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

/**
 * Navigation routes
 */
object Routes {
    const val HOME = "home"
    const val CALENDAR = "calendar"
    const val FAVORITES = "favorites"
    const val SETTINGS = "settings"
    const val ADD_ENTRY = "add_entry"
    const val EDIT_ENTRY = "edit_entry/{entryId}"
    
    fun editEntry(entryId: Long) = "edit_entry/$entryId"
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
    val entryDates by viewModel.entryDates.collectAsState()
    val selectedEntry by viewModel.selectedEntry.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    
    var selectedCalendarDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    val entriesForSelectedDate by viewModel.getEntriesForDate(selectedCalendarDate).collectAsState(initial = emptyList())
    
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                entries = entries,
                onEntryClick = { entry ->
                    viewModel.loadEntryById(entry.id)
                    navController.navigate(Routes.editEntry(entry.id))
                },
                onFavoriteClick = { entry ->
                    viewModel.toggleFavorite(entry)
                },
                onAddClick = {
                    navController.navigate(Routes.ADD_ENTRY)
                },
                searchQuery = searchQuery,
                onSearchQueryChange = { viewModel.updateSearchQuery(it) }
            )
        }
        
        composable(Routes.CALENDAR) {
            CalendarScreen(
                entryDates = entryDates,
                entriesForSelectedDate = entriesForSelectedDate,
                onDateSelected = { date ->
                    selectedCalendarDate = date
                },
                onEntryClick = { entry ->
                    viewModel.loadEntryById(entry.id)
                    navController.navigate(Routes.editEntry(entry.id))
                },
                onFavoriteClick = { entry ->
                    viewModel.toggleFavorite(entry)
                }
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
        
        composable(Routes.ADD_ENTRY) {
            AddEditScreen(
                existingEntry = null,
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
