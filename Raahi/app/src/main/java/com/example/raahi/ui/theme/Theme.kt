package com.example.raahi.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val RaahiLightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = White,
    primaryContainer = DarkGreen,
    onPrimaryContainer = White,
    secondary = AccentPurple,
    onSecondary = White,
    secondaryContainer = LightPurple,
    onSecondaryContainer = DarkPurple,
    tertiary = AccentPurple,
    onTertiary = White,
    tertiaryContainer = LightPurple,
    onTertiaryContainer = DarkPurple,
    error = ErrorRed,
    onError = White,
    background = BackgroundCream,
    onBackground = PrimaryTextDarkCharcoal,
    surface = SurfaceWhite,
    onSurface = PrimaryTextDarkCharcoal,
    surfaceVariant = LightGrey,
    onSurfaceVariant = SecondaryTextGrey,
    outline = SecondaryTextGrey

)


private val RaahiDarkColorScheme = darkColorScheme(
    primary = PrimaryGreen,
    onPrimary = Black,
    primaryContainer = DarkGreen,
    onPrimaryContainer = White,
    secondary = AccentPurple,
    onSecondary = Black,
    secondaryContainer = DarkPurple,
    onSecondaryContainer = LightPurple,
    tertiary = AccentPurple,
    onTertiary = Black,
    tertiaryContainer = DarkPurple,
    onTertiaryContainer = LightPurple,
    error = ErrorRed,
    onError = White,
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF2C2B2F),
    onSurface = White,
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = SecondaryTextGrey
)

@Composable
fun RaahiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),

    dynamicColor: Boolean = false, 
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> RaahiDarkColorScheme
        else -> RaahiLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
