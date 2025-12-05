package com.example.meriyaadein.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = LavenderMid,
    onPrimary = DarkDeepPurple,
    primaryContainer = DeepPurple,
    onPrimaryContainer = LavenderLight,
    secondary = TealAccent,
    onSecondary = DarkDeepPurple,
    secondaryContainer = DarkCard,
    onSecondaryContainer = TealAccent,
    tertiary = HoneyGold,
    onTertiary = DarkDeepPurple,
    background = DarkDeepPurple,
    onBackground = CreamWhite,
    surface = DarkSurface,
    onSurface = CreamWhite,
    surfaceVariant = DarkCard,
    onSurfaceVariant = LavenderLight
)

private val LightColorScheme = lightColorScheme(
    primary = DeepPurple,
    onPrimary = CreamWhite,
    primaryContainer = LavenderLight,
    onPrimaryContainer = DeepPurple,
    secondary = TealAccent,
    onSecondary = CharcoalSlate,
    secondaryContainer = TealGradientStart,
    onSecondaryContainer = CharcoalSlate,
    tertiary = HoneyGold,
    onTertiary = CharcoalSlate,
    background = GradientStart,
    onBackground = CharcoalSlate,
    surface = CardLavender,
    onSurface = CharcoalSlate,
    surfaceVariant = LavenderLight,
    onSurfaceVariant = CharcoalSlate
)

@Composable
fun MeriYaadeinTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}