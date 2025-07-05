package com.vkolisnichenko.babybirthday.presentation.theme

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Blue500,
    onPrimary = Color.White,
    primaryContainer = Blue100,
    onPrimaryContainer = Blue600,

    secondary = Gray600,
    onSecondary = Color.White,
    secondaryContainer = Gray100,
    onSecondaryContainer = Gray800,

    tertiary = Gray500,
    onTertiary = Color.White,

    background = Gray50,
    onBackground = Gray900,

    surface = Color.White,
    onSurface = Gray900,
    surfaceVariant = Gray100,
    onSurfaceVariant = Gray700,

    outline = Gray300,
    outlineVariant = Gray200,

    error = Red500,
    onError = Color.White,

    inverseSurface = Gray800,
    inverseOnSurface = Gray100
)

private val DarkColorScheme = darkColorScheme(
    primary = BlueDark,
    onPrimary = GrayDark50,
    primaryContainer = Blue600,
    onPrimaryContainer = Blue100,

    secondary = Gray400,
    onSecondary = GrayDark50,
    secondaryContainer = GrayDark100,
    onSecondaryContainer = Gray200,

    tertiary = Gray400,
    onTertiary = GrayDark50,

    background = GrayDark50,
    onBackground = Gray100,

    surface = GrayDark100,
    onSurface = Gray100,
    surfaceVariant = GrayDark200,
    onSurfaceVariant = Gray300,

    outline = Gray600,
    outlineVariant = Gray700,

    error = Red500,
    onError = Color.White,

    inverseSurface = Gray100,
    inverseOnSurface = Gray800
)

@Composable
fun BabyBirthdayAppTheme(
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
            WindowCompat.setDecorFitsSystemWindows(window, false)
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}