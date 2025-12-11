package com.example.meriyaadein.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
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
    
    data object WriteMemory : BottomNavItem(
        route = "write_memory",
        title = "Write",
        selectedIcon = Icons.Filled.Create,
        unselectedIcon = Icons.Outlined.Create
    )

    data object History : BottomNavItem(
        route = "history",
        title = "Memories",
        selectedIcon = Icons.Filled.History,
        unselectedIcon = Icons.Outlined.History
    )
    
    data object Settings : BottomNavItem(
        route = "settings",
        title = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )
}

/**
 * Professional Bottom Navigation Bar
 * - Glassmorphism background
 * - Animated icons
 * - Active tab highlight
 */
@Composable
fun BottomNavBar(
    currentRoute: String,
    onItemClick: (BottomNavItem) -> Unit
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.WriteMemory,
        BottomNavItem.History,
        BottomNavItem.Settings
    )
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        GlassWhite,
                        CardLavender.copy(alpha = 0.95f)
                    )
                )
            )
    ) {
        NavigationBar(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            contentColor = DeepPurple,
            tonalElevation = 0.dp
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route
                
                // Bounce animation for selected item
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.1f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "navScale"
                )
                
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.title,
                            modifier = Modifier.scale(scale)
                        )
                    },
                    label = {
                        Text(text = item.title)
                    },
                    selected = isSelected,
                    onClick = { onItemClick(item) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DeepPurple,
                        selectedTextColor = DeepPurple,
                        unselectedIconColor = GreyText,
                        unselectedTextColor = GreyText,
                        indicatorColor = LavenderMid.copy(alpha = 0.4f)
                    )
                )
            }
        }
    }
}
