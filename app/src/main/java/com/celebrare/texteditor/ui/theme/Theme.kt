package com.celebrare.texteditor.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Define the new green color scheme
private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = White,
    primaryContainer = LightGreen,
    secondary = DarkGreen,
    background = LightGreen, // Use our soft green for the main background
    surface = White, // Keep cards and dialogs clean and white
    onBackground = DarkGray,
    onSurface = DarkGray,
    error = AccentRed
)

@Composable
fun TextEditorTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = LightColorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = LightColorScheme, // Apply the new green color scheme
        typography = Typography,
        content = content
    )
}
