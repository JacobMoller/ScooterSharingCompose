package dk.itu.moapd.scootersharing.jacj.ui.theme

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = ElectricGreen,
    secondary = ElectricGreenDark,
    tertiary = Color.Blue,

    /* Other default colors to override */
    background = DarkGreen,
    surface = ElectricGreen,
    onPrimary = Color.White,
    onSecondary = Color.Red,
    onTertiary = Color.Red,
    onBackground = ElectricGreen,
    onSurface = DarkGreen,
)

private val LightColorScheme = lightColorScheme(
    /*primary = White,
    secondary = PurpleGrey40,
    tertiary = Pink40,

    /* Other default colors to override */
    background = LimeGreen,
    surface = White,
    onPrimary = LimeGreen,
    onSecondary = Color.Red,
    onTertiary = Color.Red,
    onBackground = White,
    onSurface = LimeGreen,*/
    primary = LimeGreen,
    secondary = LimeGreenLight,
    tertiary = Pink40,

    /* Other default colors to override */
    background = White,
    surface = LimeGreen,
    onPrimary = Color.Black,
    onSecondary = Color.Red,
    onTertiary = Color.Red,
    onBackground = LimeGreen,
    onSurface = White,
)

@Composable
fun ScooterSharingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled dynamic colors to fit Figma
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
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}