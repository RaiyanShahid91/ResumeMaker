package co.resume.ui.screen.onboarding

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.WavingHand
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import co.resumeai.R
import co.resume.ui.component.AppButton
import co.resume.ui.theme.ResumeBuilderTheme
import kotlinx.coroutines.launch

private data class OnboardingPage(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val highlight: String = ""
)

@Composable
private fun onboardingPages(): List<OnboardingPage> = listOf(
    OnboardingPage(
        icon = Icons.Filled.WavingHand,
        title = "Welcome to FileForge",
        description = "Your all-in-one toolkit for resumes, cover letters, and documents — build, scan, convert, and export, all from your phone.",
        highlight = "Free templates, AI-powered"
    ),
    OnboardingPage(
        icon = Icons.Filled.Description,
        title = "Build resumes & cover letters",
        description = "Pick a professional template, fill in your details, and let AI improve your wording. Drag to reorder any section, then export a polished PDF in minutes.",
        highlight = "AI writing help + drag to reorder"
    ),
    OnboardingPage(
        icon = Icons.Filled.DocumentScanner,
        title = "Scan, import & convert",
        description = "Scan paper documents with automatic edge detection, import an existing resume from a PDF or photo, or use the built-in tools to merge, compress, and convert PDFs.",
        highlight = "Document scanner + PDF toolkit"
    )
)

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val pages = onboardingPages()
    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == pages.lastIndex

    // Cycled by index % 3 rather than one fixed-length list per page — onboarding pages get added
    // over time (this pass alone added three), and a fixed list silently throws
    // IndexOutOfBoundsException the moment page count outgrows it instead of just repeating the
    // 3-color pattern like it obviously should.
    val pageColors = listOf(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.tertiaryContainer)
    val iconColors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.tertiary)

    val currentBg by animateColorAsState(
        targetValue = pageColors[pagerState.currentPage % pageColors.size],
        animationSpec = tween(400),
        label = "bg"
    )

    Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {

            // ── Top: Skip button ────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = !isLastPage,
                    enter = fadeIn(tween(200)),
                    exit = fadeOut(tween(200))
                ) {
                    TextButton(onClick = onFinish) {
                        Text(stringResource(R.string.onboard_skip), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // ── Pager ───────────────────────────────────────────────────────
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { index ->
                val page = pages[index]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp)
                        .graphicsLayer {
                            // Scales and fades each page's content in/out as it scrolls toward or
                            // away from center, instead of it just snapping fully in — a
                            // continuous value driven by drag/settle progress, not a one-shot
                            // entrance, so it also looks right on fast flicks between pages.
                            val distance = minOf(1f, kotlin.math.abs(pagerState.getOffsetDistanceInPages(index)))
                            val scale = lerp(0.82f, 1f, 1f - distance)
                            scaleX = scale
                            scaleY = scale
                            alpha = lerp(0.25f, 1f, 1f - distance)
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Illustration circle — a soft pulsing halo behind the active page's icon,
                    // plus a springy "pop" scale-in whenever a page becomes current (both on
                    // settle and on fast flicks), layered on top of the existing drag-driven
                    // scale/fade above so the pager has more than one kind of motion happening.
                    val iconColor = iconColors[index % iconColors.size]
                    val isCurrent = pagerState.currentPage == index
                    val pulse = rememberInfiniteTransition(label = "onboard_pulse")
                    val pulseScale by pulse.animateFloat(
                        initialValue = 1f,
                        targetValue = 1.18f,
                        animationSpec = infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
                        label = "pulse_scale"
                    )
                    val pulseAlpha by pulse.animateFloat(
                        initialValue = 0.35f,
                        targetValue = 0f,
                        animationSpec = infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
                        label = "pulse_alpha"
                    )
                    val iconPop = remember { Animatable(0.7f) }
                    LaunchedEffect(isCurrent) {
                        if (isCurrent) {
                            iconPop.snapTo(0.7f)
                            iconPop.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
                        }
                    }

                    Box(modifier = Modifier.size(140.dp), contentAlignment = Alignment.Center) {
                        if (isCurrent) {
                            Box(
                                modifier = Modifier
                                    .size(140.dp)
                                    .graphicsLayer { scaleX = pulseScale; scaleY = pulseScale; alpha = pulseAlpha }
                                    .clip(CircleShape)
                                    .background(iconColor)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .graphicsLayer { scaleX = iconPop.value; scaleY = iconPop.value }
                                .clip(CircleShape)
                                .background(currentBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                page.icon,
                                contentDescription = null,
                                tint = iconColor,
                                modifier = Modifier.size(68.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(36.dp))

                    Text(
                        page.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        page.description,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                    )

                    if (page.highlight.isNotBlank()) {
                        Spacer(Modifier.height(20.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(iconColor.copy(alpha = 0.12f))
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text(
                                page.highlight,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = iconColor
                            )
                        }
                    }
                }
            }

            // ── Bottom: dots + button ───────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 32.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Dot indicators
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(pages.size) { index ->
                        val isActive = index == pagerState.currentPage
                        val width by animateDpAsState(
                            targetValue = if (isActive) 24.dp else 8.dp,
                            animationSpec = tween(300),
                            label = "dot_width"
                        )
                        val color by animateColorAsState(
                            targetValue = if (isActive) MaterialTheme.colorScheme.primary
                                          else MaterialTheme.colorScheme.outlineVariant,
                            animationSpec = tween(300),
                            label = "dot_color"
                        )
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(width)
                                .clip(CircleShape)
                                .background(color)
                        )
                    }
                }

                Spacer(Modifier.height(28.dp))

                Crossfade(targetState = isLastPage, label = "onboard_button_label") { lastPage ->
                    AppButton(
                        text = if (lastPage) stringResource(R.string.onboard_get_started) else stringResource(R.string.onboard_next),
                        onClick = {
                            if (lastPage) {
                                onFinish()
                            } else {
                                scope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingScreenPreview() {
    ResumeBuilderTheme { OnboardingScreen(onFinish = {}) }
}
