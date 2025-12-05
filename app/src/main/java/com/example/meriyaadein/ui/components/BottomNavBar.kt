package com.example.meriyaadein.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.meriyaadein.ui.theme.*

/**
 * Bottom navigation destinations
 */
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Home : BottomNavItem(
        route = "home",
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )
    
    data object History : BottomNavItem(
        route = "history",
        title = "History",
        selectedIcon = Icons.Filled.History,
        unselectedIcon = Icons.Outlined.History
    )
    
    data object Favorites : BottomNavItem(
        route = "favorites",
        title = "Favorites",
        selectedIcon = Icons.Filled.Favorite,
        unselectedIcon = Icons.Outlined.FavoriteBorder
    )
    
    data object Settings : BottomNavItem(
        route = "settings",
        title = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )
}

/**
 * Bottom navigation bar component
 */
@Composable
fun BottomNavBar(
    currentRoute: String,
    onItemClick: (BottomNavItem) -> Unit
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.History,
        BottomNavItem.Favorites,
        BottomNavItem.Settings
    )
    
    NavigationBar(
        containerColor = CardPink,
        contentColor = DeepRose
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(text = item.title)
                },
                selected = isSelected,
                onClick = { onItemClick(item) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = DeepRose,
                    selectedTextColor = DeepRose,
                    unselectedIconColor = CharcoalSlate.copy(alpha = 0.5f),
                    unselectedTextColor = CharcoalSlate.copy(alpha = 0.5f),
                    indicatorColor = BlushRose.copy(alpha = 0.5f)
                )
            )
        }
    }
}

