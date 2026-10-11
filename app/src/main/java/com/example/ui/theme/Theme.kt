package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = Color(0xFF1F1600),
    primaryContainer = GoldContainer,
    onPrimaryContainer = OnGoldContainer,
    secondary = AfricanBronze,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF3B2615),
    onSecondaryContainer = Color(0xFFFFDCC2),
    tertiary = TerracottaAccent,
    onTertiary = Color.White,
    tertiaryContainer = TerracottaContainer,
    onTertiaryContainer = Color(0xFFFFDAD4),
    background = ObsidianDark,
    onBackground = Color(0xFFEDE0D4),
    surface = ObsidianSurface,
    onSurface = Color(0xFFEDE0D4),
    surfaceVariant = ObsidianElevated,
    onSurfaceVariant = Color(0xFFD4C4B5),
    outline = ObsidianBorder
)

private val LightColorScheme = lightColorScheme(
    primary = GoldDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFBE7A1),
    onPrimaryContainer = Color(0xFF261A00),
    secondary = AfricanBronze,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE0CF),
    onSecondaryContainer = Color(0xFF331400),
    tertiary = TerracottaAccent,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDAD4),
    onTertiaryContainer = Color(0xFF410002),
    background = CreamBackground,
    onBackground = DarkText,
    surface = CreamSurface,
    onSurface = DarkText,
    surfaceVariant = Color(0xFFF2ECE1),
    onSurfaceVariant = MutedText,
    outline = CreamBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to rich luxury lounge dark theme
    dynamicColor: Boolean = false, // Keep brand aesthetic
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
