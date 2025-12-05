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
    
    // Routes where bottom nav should be visible
    val bottomNavRoutes = listOf(Routes.HOME, Routes.CALENDAR, Routes.FAVORITES, Routes.SETTINGS)
    val showBottomNav = currentRoute in bottomNavRoutes
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomNav) {
                BottomNavBar(
                    currentRoute = currentRoute,
                    onItemClick = { item ->
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        DiaryNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}