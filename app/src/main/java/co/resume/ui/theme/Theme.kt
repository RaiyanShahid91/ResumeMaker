package co.resume.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ResumeBuilderDarkColorScheme = darkColorScheme(
    primary = IndigoLight,
    onPrimary = SurfaceDark,
    primaryContainer = IndigoContainer,
    onPrimaryContainer = IndigoLight,
    secondary = Amber,
    onSecondary = SurfaceDark,
    background = SurfaceDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceContainerDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceContainerHighDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark,
    error = ErrorRed,
    onError = SurfaceDark
)

/**
 * The app is dark-mode only by design, so this always applies [ResumeBuilderDarkColorScheme]
 * regardless of the system theme setting.
 */
@Composable
fun ResumeBuilderTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ResumeBuilderDarkColorScheme,
        typography = ResumeBuilderTypography,
        shapes = ResumeBuilderShapes,
        content = content
    )
}
