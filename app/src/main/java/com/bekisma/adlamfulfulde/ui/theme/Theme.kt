package com.bekisma.adlamfulfulde.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

private val BlueDarkColorScheme = darkColorScheme(
    primary = Blue80,
    secondary = BlueGrey80,
    tertiary = LightBlue80
)

private val BlueLightColorScheme = lightColorScheme(
    primary = Blue40,
    secondary = BlueGrey40,
    tertiary = LightBlue40
)

private val GreenDarkColorScheme = darkColorScheme(
    primary = Green80,
    secondary = GreenGrey80,
    tertiary = LightGreen80
)

private val GreenLightColorScheme = lightColorScheme(
    primary = Green40,
    secondary = GreenGrey40,
    tertiary = LightGreen40
)

private val OrangeDarkColorScheme = darkColorScheme(
    primary = Orange80,
    secondary = OrangeGrey80,
    tertiary = LightOrange80
)

private val OrangeLightColorScheme = lightColorScheme(
    primary = Orange40,
    secondary = OrangeGrey40,
    tertiary = LightOrange40
)

private val RedDarkColorScheme = darkColorScheme(
    primary = Red80,
    secondary = RedGrey80,
    tertiary = LightRed80
)

private val RedLightColorScheme = lightColorScheme(
    primary = Red40,
    secondary = RedGrey40,
    tertiary = LightRed40
)

enum class AppTheme {
    Default, Blue, Green, Orange, Red, Dynamic
}

@Composable
fun AdlamFulfuldeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    appTheme: AppTheme = AppTheme.Default,
    content: @Composable () -> Unit
) {
    val colorScheme = when (appTheme) {
        AppTheme.Default -> if (darkTheme) DarkColorScheme else LightColorScheme
        AppTheme.Blue -> if (darkTheme) BlueDarkColorScheme else BlueLightColorScheme
        AppTheme.Green -> if (darkTheme) GreenDarkColorScheme else GreenLightColorScheme
        AppTheme.Orange -> if (darkTheme) OrangeDarkColorScheme else OrangeLightColorScheme
        AppTheme.Red -> if (darkTheme) RedDarkColorScheme else RedLightColorScheme
        AppTheme.Dynamic -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}