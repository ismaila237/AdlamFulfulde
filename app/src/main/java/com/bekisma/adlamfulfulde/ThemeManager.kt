package com.bekisma.adlamfulfulde

import android.content.Context
import androidx.compose.material3.darkColorScheme // Import necessary color functions
import androidx.compose.material3.lightColorScheme // Import necessary color functions
import androidx.compose.ui.graphics.Color
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension function for DataStore (Keep this)
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

// --- Define Enums HERE as the single source of truth ---
enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

enum class ColorTheme {
    DEFAULT, GREEN, BLUE, PURPLE, ORANGE, PURple
}
// --- End Enum Definitions ---


class ThemeManager(private val context: Context) {

    // Keys for DataStore (Keep this)
    companion object {
        val THEME_MODE_KEY = intPreferencesKey("theme_mode")
        val COLOR_THEME_KEY = intPreferencesKey("color_theme")
    }

    // Get the saved theme mode (Keep this)
    val themeMode: Flow<ThemeMode> = context.dataStore.data
        .map { preferences ->
            // Handle potential null or unknown values by defaulting
            when(preferences[THEME_MODE_KEY]) {
                ThemeMode.LIGHT.ordinal -> ThemeMode.LIGHT
                ThemeMode.DARK.ordinal -> ThemeMode.DARK
                else -> ThemeMode.SYSTEM // Default to SYSTEM
            }
        }

    // Get the saved color theme (Keep this)
    val colorTheme: Flow<ColorTheme> = context.dataStore.data
        .map { preferences ->
            // Handle potential null or unknown values by defaulting
            when(preferences[COLOR_THEME_KEY]) {
                ColorTheme.GREEN.ordinal -> ColorTheme.GREEN
                ColorTheme.BLUE.ordinal -> ColorTheme.BLUE
                ColorTheme.PURPLE.ordinal -> ColorTheme.PURPLE
                ColorTheme.ORANGE.ordinal -> ColorTheme.ORANGE
                else -> ColorTheme.DEFAULT // Default to DEFAULT
            }
        }

    // Save theme mode (Keep this)
    suspend fun saveThemeMode(themeMode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = themeMode.ordinal
        }
    }

    // Save color theme (Keep this)
    suspend fun saveColorTheme(colorTheme: ColorTheme) {
        context.dataStore.edit { preferences ->
            preferences[COLOR_THEME_KEY] = colorTheme.ordinal
        }
    }
}

// Custom color schemes for each theme (Keep this)
object ColorSchemes {
    // Default Purple theme
    val DefaultLightColors = lightColorScheme(
        primary = Color(0xFF6750A4), onPrimary = Color.White, primaryContainer = Color(0xFFEADDFF), onPrimaryContainer = Color(0xFF21005E),
        secondary = Color(0xFF625B71), secondaryContainer = Color(0xFFE8DEF8), tertiary = Color(0xFF7D5260), tertiaryContainer = Color(0xFFFFD8E4),
        background = Color(0xFFFEF7FF), onBackground = Color(0xFF1D1B20), surface = Color(0xFFFEF7FF), onSurface = Color(0xFF1D1B20),
        surfaceVariant = Color(0xFFE7E0EC), onSurfaceVariant = Color(0xFF49454F)
    )

    val DefaultDarkColors = darkColorScheme(
        primary = Color(0xFFD0BCFF), onPrimary = Color(0xFF381E72), primaryContainer = Color(0xFF4F378B), onPrimaryContainer = Color(0xFFEADDFF),
        secondary = Color(0xFFCCC2DC), secondaryContainer = Color(0xFF4A4458), tertiary = Color(0xFFEFB8C8), tertiaryContainer = Color(0xFF633B48),
        background = Color(0xFF1D1B20), onBackground = Color(0xFFE6E1E5), surface = Color(0xFF1D1B20), onSurface = Color(0xFFE6E1E5),
        surfaceVariant = Color(0xFF49454F), onSurfaceVariant = Color(0xFFCCC4CF)
    )

    // Green theme
    val GreenLightColors = lightColorScheme(
        primary = Color(0xFF1B863E), onPrimary = Color.White, primaryContainer = Color(0xFFB5F5BC), onPrimaryContainer = Color(0xFF002109),
        secondary = Color(0xFF516350), secondaryContainer = Color(0xFFD3E8D0), tertiary = Color(0xFF38656A), tertiaryContainer = Color(0xFFBCEBF0),
        background = Color(0xFFF7FCF5), onBackground = Color(0xFF1A1C19), surface = Color(0xFFF7FCF5), onSurface = Color(0xFF1A1C19),
        surfaceVariant = Color(0xFFDEE5DA), onSurfaceVariant = Color(0xFF424940)
    )

    val GreenDarkColors = darkColorScheme(
        primary = Color(0xFF9ADB9E), onPrimary = Color(0xFF003919), primaryContainer = Color(0xFF00522B), onPrimaryContainer = Color(0xFFB5F5BC),
        secondary = Color(0xFFB8CCB5), secondaryContainer = Color(0xFF394B38), tertiary = Color(0xFFA0CFD4), tertiaryContainer = Color(0xFF1E4D51),
        background = Color(0xFF1A1C19), onBackground = Color(0xFFE3E3DF), surface = Color(0xFF1A1C19), onSurface = Color(0xFFE3E3DF),
        surfaceVariant = Color(0xFF424940), onSurfaceVariant = Color(0xFFC2C8BD)
    )

    // Blue theme
    val BlueLightColors = lightColorScheme(
        primary = Color(0xFF1976D2), onPrimary = Color.White, primaryContainer = Color(0xFFD1E4FF), onPrimaryContainer = Color(0xFF001D36),
        secondary = Color(0xFF535F70), secondaryContainer = Color(0xFFD6E3F7), tertiary = Color(0xFF6B5778), tertiaryContainer = Color(0xFFF2DAFF),
        background = Color(0xFFFDFBFF), onBackground = Color(0xFF1A1C1E), surface = Color(0xFFFDFBFF), onSurface = Color(0xFF1A1C1E),
        surfaceVariant = Color(0xFFDFE2EB), onSurfaceVariant = Color(0xFF43474E)
    )

    val BlueDarkColors = darkColorScheme(
        primary = Color(0xFF9FCAFF), onPrimary = Color(0xFF003258), primaryContainer = Color(0xFF004881), onPrimaryContainer = Color(0xFFD1E4FF),
        secondary = Color(0xFFBAC8DB), secondaryContainer = Color(0xFF3B4858), tertiary = Color(0xFFD6BEE5), tertiaryContainer = Color(0xFF523F5F),
        background = Color(0xFF1A1C1E), onBackground = Color(0xFFE3E2E6), surface = Color(0xFF1A1C1E), onSurface = Color(0xFFE3E2E6),
        surfaceVariant = Color(0xFF43474E), onSurfaceVariant = Color(0xFFC3C6CC)
    )

    // Purple theme (Adjusted slightly to be distinct from Default)
    val PurpleLightColors = lightColorScheme(
        primary = Color(0xFF9C27B0), onPrimary = Color.White, primaryContainer = Color(0xFFF8D7FF), onPrimaryContainer = Color(0xFF34003D),
        secondary = Color(0xFF65587A), secondaryContainer = Color(0xFFEBDCFF), tertiary = Color(0xFF815689), tertiaryContainer = Color(0xFFF8D8FF),
        background = Color(0xFFFFF7FE), onBackground = Color(0xFF1E1A20), surface = Color(0xFFFFF7FE), onSurface = Color(0xFF1E1A20),
        surfaceVariant = Color(0xFFEADFEA), onSurfaceVariant = Color(0xFF4B454D)
    )

    val PurpleDarkColors = darkColorScheme(
        primary = Color(0xFFE9B2FF), onPrimary = Color(0xFF560A6C), primaryContainer = Color(0xFF721F8F), onPrimaryContainer = Color(0xFFF8D7FF),
        secondary = Color(0xFFCFC0E8), secondaryContainer = Color(0xFF4D4161), tertiary = Color(0xFFDEBCDF), tertiaryContainer = Color(0xFF683E6F),
        background = Color(0xFF1E1A20), onBackground = Color(0xFFEAE1E8), surface = Color(0xFF1E1A20), onSurface = Color(0xFFEAE1E8),
        surfaceVariant = Color(0xFF4B454D), onSurfaceVariant = Color(0xCDC4CF) // Corrected alpha for dark variant onSurfaceVariant
    )

    // Orange theme
    val OrangeLightColors = lightColorScheme(
        primary = Color(0xFFE65100), onPrimary = Color.White, primaryContainer = Color(0xFFFFDBCA), onPrimaryContainer = Color(0xFF331200),
        secondary = Color(0xFF765849), secondaryContainer = Color(0xFFFFDBCA), tertiary = Color(0xFF795B00), tertiaryContainer = Color(0xFFFFDF9E),
        background = Color(0xFFFFFBFF), onBackground = Color(0xFF201A17), surface = Color(0xFFFFFBFF), onSurface = Color(0xFF201A17),
        surfaceVariant = Color(0xFFF4DED5), onSurfaceVariant = Color(0xFF52443C)
    )

    val OrangeDarkColors = darkColorScheme(
        primary = Color(0xFFFFB68C), onPrimary = Color(0xFF552000), primaryContainer = Color(0xFF793100), onPrimaryContainer = Color(0xFFFFDBCA),
        secondary = Color(0xFFE7BFA9), secondaryContainer = Color(0xFF5C4032), tertiary = Color(0xFFEBC248), tertiaryContainer = Color(0xFF5A4300),
        background = Color(0xFF201A17), onBackground = Color(0xFFEDE0DC), surface = Color(0xFF201A17), onSurface = Color(0xFFEDE0DC),
        surfaceVariant = Color(0xFF52443C), onSurfaceVariant = Color(0xD6D3C8) // Corrected alpha for dark variant onSurfaceVariant
    )
}