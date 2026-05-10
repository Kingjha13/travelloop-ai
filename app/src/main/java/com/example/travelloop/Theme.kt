package com.example.traveloop

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
val Amber400     = Color(0xFFFBBF24)
val Amber500     = Color(0xFFF59E0B)
val Teal600      = Color(0xFF0D9488)
val Teal700      = Color(0xFF0F766E)
val Slate900     = Color(0xFF0F172A)
val Slate800     = Color(0xFF1E293B)
val Slate700     = Color(0xFF334155)
val Slate600     = Color(0xFF475569)
val Slate200     = Color(0xFFE2E8F0)
val Slate100     = Color(0xFFF1F5F9)
val White        = Color(0xFFFFFFFF)
val ErrorRed     = Color(0xFFEF4444)
val SuccessGreen = Color(0xFF22C55E)

private val DarkColorScheme = darkColorScheme(
    primary          = Amber400,
    onPrimary        = Slate900,
    primaryContainer = Amber500.copy(alpha = 0.2f),
    secondary        = Teal600,
    onSecondary      = White,
    background       = Slate900,
    onBackground     = White,
    surface          = Slate800,
    onSurface        = White,
    surfaceVariant   = Slate700,
    onSurfaceVariant = Slate200,
    error            = ErrorRed,
    outline          = Slate600
)

private val LightColorScheme = lightColorScheme(
    primary          = Teal700,
    onPrimary        = White,
    primaryContainer = Teal600.copy(alpha = 0.1f),
    secondary        = Amber500,
    onSecondary      = Slate900,
    background       = Slate100,
    onBackground     = Slate900,
    surface          = White,
    onSurface        = Slate900,
    surfaceVariant   = Slate200,
    onSurfaceVariant = Slate700,
    error            = ErrorRed,
    outline          = Slate200
)

val TraveloopTypography = Typography(
    displayLarge   = TextStyle(fontWeight = FontWeight.Black,    fontSize = 57.sp, lineHeight = 64.sp),
    displayMedium  = TextStyle(fontWeight = FontWeight.Bold,     fontSize = 45.sp, lineHeight = 52.sp),
    headlineLarge  = TextStyle(fontWeight = FontWeight.Bold,     fontSize = 32.sp, lineHeight = 40.sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 28.sp, lineHeight = 36.sp),
    headlineSmall  = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 24.sp, lineHeight = 32.sp),
    titleLarge     = TextStyle(fontWeight = FontWeight.Bold,     fontSize = 22.sp, lineHeight = 28.sp),
    titleMedium    = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 24.sp),
    titleSmall     = TextStyle(fontWeight = FontWeight.Medium,   fontSize = 14.sp, lineHeight = 20.sp),
    bodyLarge      = TextStyle(fontWeight = FontWeight.Normal,   fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium     = TextStyle(fontWeight = FontWeight.Normal,   fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall      = TextStyle(fontWeight = FontWeight.Normal,   fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge     = TextStyle(fontWeight = FontWeight.Medium,   fontSize = 14.sp, lineHeight = 20.sp),
    labelMedium    = TextStyle(fontWeight = FontWeight.Medium,   fontSize = 12.sp, lineHeight = 16.sp),
    labelSmall     = TextStyle(fontWeight = FontWeight.Medium,   fontSize = 11.sp, lineHeight = 16.sp)
)

@Composable
fun TraveloopTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography  = TraveloopTypography,
        content     = content
    )
}