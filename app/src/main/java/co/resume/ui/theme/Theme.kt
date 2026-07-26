package co.resume.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush

private val ResumeBuilderLightColorScheme = lightColorScheme(
    primary = InkPrimary,
    onPrimary = OnInkPrimary,
    primaryContainer = InkPrimaryContainer,
    onPrimaryContainer = OnInkPrimaryContainer,
    secondary = SkyBlue,
    onSecondary = SurfaceWhite,
    secondaryContainer = SkyBlueContainer,
    onSecondaryContainer = OnSkyBlueContainer,
    tertiary = Amber,
    onTertiary = SurfaceWhite,
    tertiaryContainer = AmberContainer,
    onTertiaryContainer = OnAmberContainer,
    background = BackgroundLight,
    onBackground = OnSurfaceInk,
    surface = SurfaceRow,
    onSurface = OnSurfaceInk,
    surfaceVariant = SurfaceHigh,
    onSurfaceVariant = OnSurfaceMuted,
    surfaceContainerLowest = SurfaceWhite,
    surfaceContainerLow = SurfaceLow,
    surfaceContainer = SurfaceRow,
    surfaceContainerHigh = SurfaceHigh,
    surfaceContainerHighest = SurfaceHighest,
    outline = OutlineLight,
    outlineVariant = OutlineVariantLight,
    error = ErrorRed,
    onError = SurfaceWhite,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer
)

/**
 * Soft sky-blue-to-white gradient used as the single continuous background behind every
 * screen — painted once behind the nav host so it never scrolls or repeats per-screen.
 */
val AppBackgroundGradient = Brush.verticalGradient(listOf(GradientStart, GradientEnd))

/**
 * Bottom-sheet background — a fully opaque radial gradient anchored in the top left corner
 * that gradually spreads across the sheet, so there is no transparency edge case where
 * content behind the sheet can show through, while still matching the app's gradient look.
 */
val SheetBackgroundGradient = Brush.radialGradient(
    colors = listOf(GradientStart, GradientEnd),
    center = Offset.Zero
)

/**
 * Rezume-inspired light theme: white/near-white backgrounds, light-gray rounded rows,
 * near-black pill buttons, and a soft blue accent for AI features.
 */
@Composable
fun ResumeBuilderTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ResumeBuilderLightColorScheme,
        typography = ResumeBuilderTypography,
        shapes = ResumeBuilderShapes,
        content = content
    )
}
