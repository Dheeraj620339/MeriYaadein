package com.example.meriyaadein

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.meriyaadein.navigation.DiaryNavHost
import com.example.meriyaadein.navigation.Routes
import com.example.meriyaadein.ui.components.BottomNavBar
import com.example.meriyaadein.ui.components.BottomNavItem
import com.example.meriyaadein.ui.theme.MeriYaadeinTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MeriYaadeinTheme {
                DiaryApp()
            }
        }
    }
}

@Composable
fun DiaryApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Routes.HOME
    
    // Hoist ViewModel to handle bottom nav actions
    val viewModel: com.example.meriyaadein.viewmodel.DiaryViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    
    // Routes where bottom nav should be visible
    // Routes where bottom nav should be visible
    // Home has its own bottom nav now
    val bottomNavRoutes = listOf(Routes.HISTORY, Routes.FAVORITES, Routes.SETTINGS)
    val showBottomNav = currentRoute in bottomNavRoutes
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // We show bottom bar on main screens
            // Note: Favorites is now a tab in History, so we might not need a route for it in bottom nav check if we removed it from nav bar
            // But keeping it safe.
            if (showBottomNav) {
                BottomNavBar(
                    currentRoute = currentRoute,
                    onItemClick = { item ->
                        if (item.route == "write_memory") {
                            // If on Home, Save. Else, go to Home.
                            if (currentRoute == Routes.HOME) {
                                viewModel.saveDraft()
                            } else {
                                navController.navigate(Routes.HOME) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        } else {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        DiaryNavHost(
            navController = navController,
            viewModel = viewModel,
            modifier = Modifier.padding(innerPadding)
        )
    }
}