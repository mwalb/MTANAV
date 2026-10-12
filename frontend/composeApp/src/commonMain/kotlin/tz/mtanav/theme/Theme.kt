package tz.mtanav.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Primary = Color(0xFF176B65)
val PrimaryDark = Color(0xFF124E4A)
val PrimaryLight = Color(0xFFE4F3F0)
val Action = Color(0xFF2D8A68)
val Accent = Color(0xFFE8AD4A)
val Background = Color(0xFFF7F8F5)
val Surface = Color(0xFFFFFFFF)
val TextPrimary = Color(0xFF24332F)
val TextSecondary = Color(0xFF687773)
val Border = Color(0xFFE1E8E4)
val Success = Color(0xFF247A50)
val Warning = Color(0xFF946313)
val Error = Color(0xFFB83C42)

val PrimaryDeepBlue = Primary
val PrimaryBlue = Color(0xFF1E88E5)
val CampnavDeepBlue = Color(0xFF1A237E)
val CampnavBlue = Color(0xFF3949AB)
val AppBackground = Background
val SurfaceWhite = Surface
val ActionTeal = Action
val HighlightGold = Accent
val SuccessGreen = Success
val ErrorRed = Error

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Surface,
    primaryContainer = PrimaryLight,
    onPrimaryContainer = PrimaryDark,
    secondary = Action,
    onSecondary = Surface,
    tertiary = Accent,
    onTertiary = TextPrimary,
    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    error = Error,
    onError = Surface
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryLight,
    onPrimary = PrimaryDark,
    primaryContainer = PrimaryDark,
    onPrimaryContainer = PrimaryLight,
    secondary = Action,
    onSecondary = Surface,
    tertiary = Accent,
    onTertiary = TextPrimary,
    background = Color(0xFF121B19),
    onBackground = Color(0xFFE4F3F0),
    surface = Color(0xFF1A2624),
    onSurface = Color(0xFFE4F3F0),
    error = Error,
    onError = Surface
)

@Composable
fun MtanavTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

