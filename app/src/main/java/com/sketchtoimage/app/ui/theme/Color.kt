package com.sketchtoimage.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Primary colors
val PurplePrimary = Color(0xFF6366F1)
val PurplePrimaryVariant = Color(0xFF4F46E5)
val CyanSecondary = Color(0xFF06B6D4)
val CyanSecondaryVariant = Color(0xFF0891B2)

// Background colors
val BackgroundDark = Color(0xFF0A0A0F)
val SurfaceDark = Color(0xFF1A1A2E)
val SurfaceVariant = Color(0xFF252542)

// Accent colors
val NeonPink = Color(0xFFEC4899)
val NeonPurple = Color(0xFFA855F7)
val NeonBlue = Color(0xFF3B82F6)

// Text colors
val TextPrimary = Color.White
val TextSecondary = Color(0xB3FFFFFF)
val TextHint = Color(0x66FFFFFF)

// Status colors
val ErrorColor = Color(0xFFEF4444)
val SuccessColor = Color(0xFF22C55E)

// Transparency helpers
val White10 = Color(0x1AFFFFFF)
val White20 = Color(0x33FFFFFF)
val White50 = Color(0x80FFFFFF)

private val DarkColorScheme = darkColorScheme(
    primary = PurplePrimary,
    onPrimary = TextPrimary,
    primaryContainer = PurplePrimaryVariant,
    onPrimaryContainer = TextPrimary,
    secondary = CyanSecondary,
    onSecondary = TextPrimary,
    secondaryContainer = CyanSecondaryVariant,
    onSecondaryContainer = TextPrimary,
    tertiary = NeonPink,
    onTertiary = TextPrimary,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,
    error = ErrorColor,
    onError = TextPrimary
)

@Composable
fun SketchToImageTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = BackgroundDark.toArgb()
            window.navigationBarColor = BackgroundDark.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}