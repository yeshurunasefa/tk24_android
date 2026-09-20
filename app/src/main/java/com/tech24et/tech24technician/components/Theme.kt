package com.tech24et.tech24technician.components

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

/**
 * Design tokens taken from the mockups.
 * We keep our own palette (instead of only Material's ColorScheme) because the design
 * uses a lot of "soft" tinted backgrounds (orangeSoft, redSoft, amberSoft ...).
 */
@Immutable
data class AppColors(
    val background: Color,
    val surface: Color,
    val border: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    // Dark hero cards (profile card, active queue, map). Stay dark in both themes.
    val navy: Color,
    val navyRaised: Color,
    val onNavy: Color,
    val onNavyMuted: Color,
    // Brand
    val orange: Color,
    val orangeSoft: Color,
    val onOrangeSoft: Color,
    // Status
    val red: Color,
    val redSoft: Color,
    val amber: Color,
    val amberSoft: Color,
    val blue: Color,
    val blueSoft: Color,
    val green: Color,
    val greenSoft: Color,
    val switchOff: Color,
)

val LightColors = AppColors(
    background = Color(0xFFF7F8FA),
    surface = Color(0xFFFFFFFF),
    border = Color(0xFFE8EAEE),
    textPrimary = Color(0xFF111827),
    textSecondary = Color(0xFF6B7280),
    navy = Color(0xFF111827),
    navyRaised = Color(0xFF232B38),
    onNavy = Color(0xFFFFFFFF),
    onNavyMuted = Color(0xFFB4BAC5),
    orange = Color(0xFFFF5A1F),
    orangeSoft = Color(0xFFFFF0EB),
    onOrangeSoft = Color(0xFF9A3412),
    red = Color(0xFFC24B45),
    redSoft = Color(0xFFFCE9E7),
    amber = Color(0xFFC98A1B),
    amberSoft = Color(0xFFFEF3D9),
    blue = Color(0xFF3D8EB9),
    blueSoft = Color(0xFFE4F0F7),
    green = Color(0xFF2E9E6B),
    greenSoft = Color(0xFFE3F5EC),
    switchOff = Color(0xFFE5E7EB),
)

val DarkColors = AppColors(
    background = Color(0xFF0D1117),
    surface = Color(0xFF161B22),
    border = Color(0xFF262D38),
    textPrimary = Color(0xFFF3F4F6),
    textSecondary = Color(0xFF9CA3AF),
    navy = Color(0xFF1B2432),
    navyRaised = Color(0xFF2A3446),
    onNavy = Color(0xFFFFFFFF),
    onNavyMuted = Color(0xFFB4BAC5),
    orange = Color(0xFFFF6B35),
    orangeSoft = Color(0xFF2E1B13),
    onOrangeSoft = Color(0xFFFDBA9B),
    red = Color(0xFFE0625B),
    redSoft = Color(0xFF3A1F1E),
    amber = Color(0xFFE3A93C),
    amberSoft = Color(0xFF33290F),
    blue = Color(0xFF5FB0DC),
    blueSoft = Color(0xFF15262F),
    green = Color(0xFF45C08A),
    greenSoft = Color(0xFF12291F),
    switchOff = Color(0xFF374151),
)

val LocalAppColors = staticCompositionLocalOf { LightColors }

object AppTheme {
    val colors: AppColors
        @Composable get() = LocalAppColors.current
}

/**
 * The mockups use the "Outfit" typeface. To use it:
 *  1. Download Outfit from Google Fonts and drop the files in res/font/
 *     (outfit_regular.ttf, outfit_medium.ttf, outfit_semibold.ttf)
 *  2. Replace the line below with:
 *     val AppFont = FontFamily(
 *         Font(R.font.outfit_regular, FontWeight.Normal),
 *         Font(R.font.outfit_medium, FontWeight.Medium),
 *         Font(R.font.outfit_semibold, FontWeight.SemiBold),
 *     )
 */
val AppFont: FontFamily = FontFamily.SansSerif

val AppTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = AppFont, fontWeight = FontWeight.Medium,
        fontSize = 34.sp, lineHeight = 40.sp, letterSpacing = (-0.5).sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = AppFont, fontWeight = FontWeight.Medium,
        fontSize = 28.sp, lineHeight = 34.sp, letterSpacing = (-0.3).sp,
    ),
    titleLarge = TextStyle(
        fontFamily = AppFont, fontWeight = FontWeight.Medium,
        fontSize = 22.sp, lineHeight = 28.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = AppFont, fontWeight = FontWeight.Medium,
        fontSize = 17.sp, lineHeight = 24.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = AppFont, fontWeight = FontWeight.Normal,
        fontSize = 16.sp, lineHeight = 22.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = AppFont, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 20.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = AppFont, fontWeight = FontWeight.Medium,
        fontSize = 16.sp, lineHeight = 22.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = AppFont, fontWeight = FontWeight.Medium,
        fontSize = 13.sp, lineHeight = 18.sp,
    ),
    // Small tracked-out labels: "ACTIVE CASE", "URGENT", "YOUR ACTIVE QUEUE"
    labelSmall = TextStyle(
        fontFamily = AppFont, fontWeight = FontWeight.Medium,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 1.6.sp,
    ),
)

@Composable
fun TechnicianTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkColors else LightColors

    val materialScheme = if (darkTheme) {
        darkColorScheme(
            primary = colors.orange, onPrimary = Color.White,
            background = colors.background, onBackground = colors.textPrimary,
            surface = colors.surface, onSurface = colors.textPrimary,
            outline = colors.border, error = colors.red,
        )
    } else {
        lightColorScheme(
            primary = colors.orange, onPrimary = Color.White,
            background = colors.background, onBackground = colors.textPrimary,
            surface = colors.surface, onSurface = colors.textPrimary,
            outline = colors.border, error = colors.red,
        )
    }

    // Status bar icons: dark on light theme, light on dark theme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(LocalAppColors provides colors) {
        MaterialTheme(
            colorScheme = materialScheme,
            typography = AppTypography,
            content = content,
        )
    }
}
