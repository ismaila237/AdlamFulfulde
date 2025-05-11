package com.bekisma.adlamfulfulde.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography // Ensure Typography is imported or defined
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

// --- ADD Imports for the Enums from their single source of truth ---
import com.bekisma.adlamfulfulde.ThemeMode
import com.bekisma.adlamfulfulde.ColorTheme
// --- End ADD Imports ---

import com.bekisma.adlamfulfulde.ColorSchemes
import com.bekisma.adlamfulfulde.ThemeManager


@Composable
fun AdlamFulfuldeTheme(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    // Remember the ThemeManager at the top level of the theme composable
    val themeManager = remember { ThemeManager(context) }

    // Collect the theme states from DataStore.
    // These states changing will trigger recomposition of AdlamFulfuldeTheme
    val themeMode by themeManager.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
    // Use the imported ColorTheme directly
    val colorTheme by themeManager.colorTheme.collectAsState(initial = ColorTheme.DEFAULT)

    // Determine if dark theme should be used based on the collected themeMode
    val shouldUseDarkTheme = when(themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        // --- ADD the else branch here ---
        else -> isSystemInDarkTheme() // Fallback for unexpected enum values
        // --- End ADD ---
    }

    // Select the appropriate color scheme based on the color theme and dark mode
    val colorScheme = when {
        shouldUseDarkTheme -> when(colorTheme) {
            // Use the imported ColorTheme enum constants directly
            ColorTheme.GREEN -> ColorSchemes.GreenDarkColors
            ColorTheme.BLUE -> ColorSchemes.BlueDarkColors
            ColorTheme.PURple -> ColorSchemes.PurpleDarkColors
            ColorTheme.ORANGE -> ColorSchemes.OrangeDarkColors
            else -> ColorSchemes.DefaultDarkColors // Default case for color theme
        }
        else -> when(colorTheme) {
            // Use the imported ColorTheme enum constants directly
            ColorTheme.GREEN -> ColorSchemes.GreenLightColors
            ColorTheme.BLUE -> ColorSchemes.BlueLightColors
            ColorTheme.PURPLE -> ColorSchemes.PurpleLightColors
            ColorTheme.ORANGE -> ColorSchemes.OrangeLightColors
            else -> ColorSchemes.DefaultLightColors // Default case for color theme
        }
    }

    // Apply the selected color scheme using MaterialTheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Use the imported Typography or your defined one
        content = content // Render the rest of the app's UI tree
    )
}

// Assuming you have a Typography object defined in this package or a related one
// If not, use the default or define your own like this:
val Typography = androidx.compose.material3.Typography() // Example default Typography