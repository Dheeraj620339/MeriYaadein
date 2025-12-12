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
import com.example.meriyaadein.data.local.Mood

/**
 * Navigation routes
 */
object Routes {
    const val HOME = "home"
    const val HISTORY = "history"
    const val FAVORITES = "favorites"
    const val SETTINGS = "settings"
    const val PROFILE = "profile"
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
    
    // User Preferences
    val userName by viewModel.userName.collectAsState()
    val accentColor by viewModel.accentColor.collectAsState()

    // Home Screen Dynamic Data
    val currentSentence by viewModel.currentSentence.collectAsState()
    val currentTime by viewModel.currentTime.collectAsState()
    val currentMood by viewModel.currentMood.collectAsState()
    val moodSuggestions by viewModel.moodSuggestions.collectAsState()
    
    // Draft Data
    val draftTitle by viewModel.draftTitle.collectAsState()
    val draftContent by viewModel.draftContent.collectAsState()
    
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier
    ) {
        // Home - shows today's entry only
        composable(Routes.HOME) {
            HomeScreen(
                todayEntry = todayEntry,
                recentEntries = entries, // Passing all entries for recent slider
                onWriteClick = {
                    navController.navigate(Routes.ADD_ENTRY)
                },
                onWriteWithPrompt = { prompt ->
                    navController.navigate(Routes.addEntryWithPrompt(prompt))
                },
                onEditClick = { entry ->
                    viewModel.loadEntryById(entry.id)
                    navController.navigate(Routes.editEntry(entry.id))
                },
                onMoodSelected = { mood ->
                    viewModel.updateCurrentMood(mood)
                },
                currentSentence = currentSentence,
                currentTimeMillis = currentTime,
                currentMood = currentMood,
                moodSuggestions = moodSuggestions,
                onProfileClick = {
                    navController.navigate(Routes.PROFILE)
                },
                userName = userName,
                draftContent = draftContent,
                onTitleChange = { viewModel.updateDraftTitle(it) },
                onContentChange = { viewModel.updateDraftContent(it) },
                onNavigateToHistory = { navController.navigate(Routes.HISTORY) },
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }
        
        // History - shows all past entries
        composable(Routes.HISTORY) {
            val historyEntries by viewModel.filteredHistoryEntries.collectAsState()
            val selectedTab by viewModel.selectedHistoryTab.collectAsState()
            val selectedVibe by viewModel.selectedVibeFilter.collectAsState()
            
            HistoryScreen(
                entries = historyEntries,
                onEntryClick = { entry ->
                    viewModel.loadEntryById(entry.id)
                    navController.navigate(Routes.editEntry(entry.id))
                },
                onFavoriteClick = { entry ->
                    viewModel.toggleFavorite(entry)
                },
                onLockClick = { entry ->
                    viewModel.toggleLock(entry)
                },
                onDeleteClick = { entry ->
                    viewModel.deleteEntry(entry)
                },
                searchQuery = searchQuery,
                onSearchQueryChange = { query ->
                    viewModel.updateSearchQuery(query)
                },
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    viewModel.setHistoryTab(tab)
                },
                selectedVibe = selectedVibe,
                onVibeSelected = { vibe ->
                    viewModel.setVibeFilter(vibe)
                },
                isPinSet = viewModel.isPinSet.collectAsState().value,
                onSetPin = { pin -> viewModel.setPin(pin) },
                onValidatePin = { pin -> viewModel.validatePin(pin) }
            )
        }
        
        composable(Routes.SETTINGS) {
            SettingsScreen(
                isPinSet = viewModel.isPinSet.collectAsState().value,
                onSetPin = { pin -> viewModel.setPin(pin) },
                onValidatePin = { pin -> viewModel.validatePin(pin) }
            )
        }
        
        // Profile Screen
        composable(Routes.PROFILE) {
            ProfileScreen(
                currentName = userName,
                currentAccentColor = accentColor,
                onSaveName = { name ->
                    viewModel.saveUserName(name)
                },
                onSaveColor = { color ->
                    viewModel.saveAccentColor(color)
                },
                onBack = { navController.popBackStack() }
            )
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
