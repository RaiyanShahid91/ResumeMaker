package co.resume.ui.screen.dashboard

import android.content.Context
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Drafts
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Transform
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import kotlinx.coroutines.launch
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.resumeai.R
import co.resume.ai.AiClient
import co.resume.domain.layout.CuratedTemplateCatalog
import co.resume.ui.component.AppCard
import co.resume.ui.component.BannerAdView
import co.resume.ui.component.ResumeTemplateThumbnail
import co.resume.ui.component.StaggeredEntrance
import co.resume.ui.component.TemplateThumbnailSkeleton
import co.resume.ui.component.rememberShimmerGate
import co.resume.ui.theme.ResumeBuilderTheme
import co.resume.ui.viewmodel.CoverLetterBrowseViewModel
import co.resume.ui.viewmodel.CoverLetterListViewModel
import co.resume.ui.viewmodel.CoverLetterTemplateUiModel
import co.resume.ui.viewmodel.TemplateBrowseViewModel
import co.resume.ui.viewmodel.TemplateUiModel

private data class HomeAction(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val onClick: () -> Unit,
    val gradient: List<Color>
)

@Composable
fun HomeTab(
    onCreateResume: () -> Unit,
    onViewResumes: () -> Unit,
    onViewCoverLetters: () -> Unit = {},
    onViewScannedDocuments: () -> Unit = {},
    onScanDocument: () -> Unit = {},
    onOpenConverter: () -> Unit = {},
    onBrowseTemplates: () -> Unit,
    onBrowseCoverLetterTemplates: () -> Unit = {},
    onOpenAiChat: () -> Unit = {},
    onCreateCoverLetter: (coverLetterId: Long) -> Unit = {},
    viewModel: TemplateBrowseViewModel = hiltViewModel(),
    coverLetterViewModel: CoverLetterListViewModel = hiltViewModel(),
    coverLetterBrowseViewModel: CoverLetterBrowseViewModel = hiltViewModel()
) {
    val templates by viewModel.templates.collectAsStateWithLifecycle()
    val templatesFetching = templates.isEmpty()
    val coverLetterTemplates by coverLetterBrowseViewModel.templates.collectAsStateWithLifecycle()
    val coverLetterTemplatesFetching by coverLetterBrowseViewModel.isLoading.collectAsStateWithLifecycle()
    HomeTabContent(
        templates = templates,
        templatesLoading = templatesFetching,
        coverLetterTemplates = coverLetterTemplates,
        coverLetterTemplatesLoading = coverLetterTemplatesFetching,
        onCreateResume = onCreateResume,
        onViewResumes = onViewResumes,
        onViewCoverLetters = onViewCoverLetters,
        onViewScannedDocuments = onViewScannedDocuments,
        onScanDocument = onScanDocument,
        onOpenConverter = onOpenConverter,
        onBrowseTemplates = onBrowseTemplates,
        onBrowseCoverLetterTemplates = onBrowseCoverLetterTemplates,
        onOpenAiChat = onOpenAiChat,
        onCreateCoverLetter = { coverLetterViewModel.createCoverLetter(onCreated = onCreateCoverLetter) },
        onCreateCoverLetterWithTemplate = { templateId ->
            coverLetterViewModel.createCoverLetter(templateId = templateId, onCreated = onCreateCoverLetter)
        },
        onRequestTemplatePreview = viewModel::renderPreview
    )
}

@Composable
private fun HomeTabContent(
    templates: List<TemplateUiModel>,
    templatesLoading: Boolean,
    coverLetterTemplates: List<CoverLetterTemplateUiModel> = emptyList(),
    coverLetterTemplatesLoading: Boolean = false,
    onCreateResume: () -> Unit,
    onViewResumes: () -> Unit,
    onViewCoverLetters: () -> Unit = {},
    onViewScannedDocuments: () -> Unit = {},
    onScanDocument: () -> Unit = {},
    onOpenConverter: () -> Unit = {},
    onBrowseTemplates: () -> Unit,
    onBrowseCoverLetterTemplates: () -> Unit = {},
    onOpenAiChat: () -> Unit = {},
    onCreateCoverLetter: () -> Unit = {},
    onCreateCoverLetterWithTemplate: (templateId: Int) -> Unit = {},
    onRequestTemplatePreview: (co.resume.domain.layout.CuratedTemplateOption) -> Unit = {},
    // Only ever passed non-null from @Preview functions — real callers always resolve this from
    // SharedPreferences (see below). Android Studio's static preview renderer doesn't run
    // animation frames, so the real value (false on a fresh install) left the AI bar's one-time
    // rise-in overlay stuck at frame zero instead of showing the settled bar — this lets the
    // preview skip straight to the settled state it'll actually be in almost all the time.
    previewForceAiIntroSettled: Boolean? = null
) {
    val shimmerGate = rememberShimmerGate()
    val isLoading = templatesLoading || shimmerGate

    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val tertiary = MaterialTheme.colorScheme.tertiary

    val actions = listOf(
        HomeAction(Icons.Filled.AddCircle,           stringResource(R.string.home_action_create),                stringResource(R.string.home_action_create_desc),                onCreateResume,        listOf(primary, tertiary)),
        HomeAction(Icons.Filled.Description,         stringResource(R.string.home_action_resumes),               stringResource(R.string.home_action_resumes_desc),               onViewResumes,         listOf(secondary, primary)),
        HomeAction(Icons.AutoMirrored.Filled.Article, stringResource(R.string.home_action_cover_letter),         stringResource(R.string.home_action_cover_letter_desc),          onCreateCoverLetter,   listOf(tertiary, secondary)),
        HomeAction(Icons.Filled.Drafts,               stringResource(R.string.home_action_my_cover_letters),      stringResource(R.string.home_action_my_cover_letters_desc),      onViewCoverLetters,    listOf(primary, secondary)),
        HomeAction(Icons.Filled.DocumentScanner,      stringResource(R.string.home_action_scan_document),         stringResource(R.string.home_action_scan_document_desc),         onScanDocument,        listOf(secondary, tertiary)),
        HomeAction(Icons.Filled.Folder,               stringResource(R.string.home_action_my_scanned_documents),  stringResource(R.string.home_action_my_scanned_documents_desc),  onViewScannedDocuments, listOf(tertiary, primary)),
        HomeAction(Icons.Filled.Transform,             stringResource(R.string.home_action_convert),               stringResource(R.string.home_action_convert_desc),               onOpenConverter,        listOf(primary, secondary))
    )

    var aiIntroAnimating by remember { mutableStateOf(false) }
    val context = LocalContext.current
    // Whether the bar has finished rising into place and is now shown in-flow inside the
    // LazyColumn — false only for the very first, never-before-seen play of the entrance.
    var aiIntroSettled by remember { mutableStateOf(previewForceAiIntroSettled ?: isAiIntroPlayed(context)) }
    var aiSlotOffset by remember { mutableStateOf(Offset.Zero) }
    var aiSlotWidthPx by remember { mutableStateOf(0) }
    var rootOffset by remember { mutableStateOf(Offset.Zero) }

    // No background fill here — the single app-wide gradient (painted once behind the nav
    // host in MainActivity) shows through this whole screen, glass cards included.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { rootOffset = it.positionInRoot() }
    ) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 16.dp),
            // Blurs everything BEHIND the AI bar while it rises — the bar itself is drawn as a
            // separate, unblurred overlay below (see aiIntroAnimating block) so it stays crisp
            // while the rest of the screen is blurred out from under it.
            modifier = if (aiIntroAnimating) Modifier.blur(20.dp) else Modifier
        ) {

            // ── Hero header ─────────────────────────────────────────────────
            item {
                // Picked once per fresh visit to Home (app launch, or navigating back to this
                // tab) rather than a fixed "Welcome back!" every time — remember keys off Unit
                // so it stays stable across recompositions within the same visit.
                val greetingTitles = stringArrayResource(R.array.home_greeting_titles)
                val greetingSubtitles = stringArrayResource(R.array.home_greeting_subtitles)
                val greetingIndex = remember { greetingTitles.indices.random() }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Text(
                        greetingTitles[greetingIndex],
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        greetingSubtitles.getOrElse(greetingIndex) { stringResource(R.string.home_subtitle) },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    if (aiIntroSettled) {
                        AiAssistantBar(onClick = onOpenAiChat, modifier = Modifier.fillMaxWidth())
                    } else {
                        // Reserves the bar's footprint in the scrolling layout (so content below
                        // doesn't jump once it settles) while the real, unblurred bar rises as an
                        // overlay positioned at this exact spot — see below.
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(68.dp)
                                .onGloballyPositioned { coords ->
                                    aiSlotOffset = coords.positionInRoot()
                                    aiSlotWidthPx = coords.size.width
                                }
                        )
                    }
                }
            }

            // ── Quick actions ────────────────────────────────────────────────
            item { SectionHeader(title = stringResource(R.string.home_quick_actions)) }

            val actionRows = actions.chunked(2)
            itemsIndexed(actionRows) { rowIndex, row ->
                StaggeredEntrance(index = rowIndex) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 5.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        row.forEach { action ->
                            QuickActionTile(
                                action = action,
                                isLoading = shimmerGate,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (row.size == 1) Box(modifier = Modifier.weight(1f))
                    }
                }
            }

            // ── Featured templates carousel ──────────────────────────────────
            if (isLoading || templates.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = stringResource(R.string.home_featured_templates),
                        actionLabel = stringResource(R.string.home_see_all),
                        onAction = onBrowseTemplates
                    )
                }
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (isLoading) {
                            items(4) { TemplateThumbnailSkeleton() }
                        } else {
                            val accents = listOf(primary, secondary, tertiary)
                            itemsIndexed(
                                templates.take(8),
                                key = { _, template -> template.option.id }
                            ) { index, template ->
                                TemplateThumbnailCard(
                                    template = template,
                                    accent = accents[index % accents.size],
                                    onClick = onBrowseTemplates,
                                    onVisible = { onRequestTemplatePreview(template.option) }
                                )
                            }
                        }
                    }
                }
            }

            // ── Featured cover letters carousel ──────────────────────────────
            // Gated by the same shimmerGate as the resume templates carousel above, so both
            // carousels hold their loading-skeleton for the same minimum duration and transition
            // to real content the same way — previously this one used only its own (near-instant)
            // loading flag, so it popped in abruptly instead of animating like the resume one.
            val coverLetterIsLoading = coverLetterTemplatesLoading || shimmerGate
            if (coverLetterIsLoading || coverLetterTemplates.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = stringResource(R.string.home_featured_cover_letters),
                        actionLabel = stringResource(R.string.home_see_all),
                        onAction = onBrowseCoverLetterTemplates
                    )
                }
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (coverLetterIsLoading) {
                            items(4) { TemplateThumbnailSkeleton() }
                        } else {
                            val accents = listOf(secondary, tertiary, primary)
                            itemsIndexed(
                                coverLetterTemplates,
                                key = { _, template -> template.option.id }
                            ) { index, template ->
                                CoverLetterThumbnailCard(
                                    template = template,
                                    accent = accents[index % accents.size],
                                    onClick = { onCreateCoverLetterWithTemplate(template.option.id) }
                                )
                            }
                        }
                    }
                }
            }

            // ── Banner ad ────────────────────────────────────────────────────
            item { BannerAdView(modifier = Modifier.padding(top = 8.dp)) }
        }

        // Blocks all taps while the AI assistant's one-time intro animation plays, so nothing
        // underneath the blurred content can be accidentally triggered mid-entrance.
        if (aiIntroAnimating) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {}
            )
        }

        if (!aiIntroSettled && aiSlotWidthPx > 0) {
            AiAssistantIntroOverlay(
                onClick = onOpenAiChat,
                anchorXPx = aiSlotOffset.x - rootOffset.x,
                anchorYPx = aiSlotOffset.y - rootOffset.y,
                widthPx = aiSlotWidthPx,
                onAnimatingChanged = { aiIntroAnimating = it },
                onFinished = {
                    markAiIntroPlayed(context)
                    aiIntroSettled = true
                }
            )
        }
    }
}

private const val AiIntroPrefs = "coach_marks"
private const val AiIntroKey = "ai_assistant_intro_played"

private fun isAiIntroPlayed(context: Context): Boolean =
    context.getSharedPreferences(AiIntroPrefs, Context.MODE_PRIVATE).getBoolean(AiIntroKey, false)

private fun markAiIntroPlayed(context: Context) {
    context.getSharedPreferences(AiIntroPrefs, Context.MODE_PRIVATE).edit().putBoolean(AiIntroKey, true).apply()
}

/**
 * Renders the AI assistant bar rising into place, exactly once ever, as a screen-level overlay
 * positioned at ([anchorXPx], [anchorYPx]) — i.e. the exact spot the reserved slot in the header
 * measured itself at — rather than in-flow inside the (blurred) LazyColumn, so the bar itself
 * stays crisp and unblurred while everything behind/below it is blurred out. [onFinished] tells
 * the caller to swap over to rendering [AiAssistantBar] in-flow from then on (same visual spot,
 * so the swap is imperceptible), and to persist that this has now played.
 */
@Composable
private fun AiAssistantIntroOverlay(
    onClick: () -> Unit,
    anchorXPx: Float,
    anchorYPx: Float,
    widthPx: Int,
    onAnimatingChanged: (Boolean) -> Unit,
    onFinished: () -> Unit
) {
    val density = LocalDensity.current
    val offsetY = remember { Animatable(560f) }
    val scale = remember { Animatable(0.35f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        onAnimatingChanged(true)
        launch { alpha.animateTo(1f, tween(durationMillis = 700)) }
        launch { scale.animateTo(1f, tween(durationMillis = 1800, easing = FastOutSlowInEasing)) }
        offsetY.animateTo(0f, tween(durationMillis = 1800, easing = FastOutSlowInEasing))
        onAnimatingChanged(false)
        onFinished()
    }

    val widthDp = with(density) { widthPx.toDp() }
    Box(
        modifier = Modifier
            .offset { IntOffset(anchorXPx.roundToInt(), anchorYPx.roundToInt()) }
            .width(widthDp)
            .graphicsLayer {
                translationY = offsetY.value.dp.toPx()
                scaleX = scale.value
                scaleY = scale.value
                this.alpha = alpha.value
                transformOrigin = TransformOrigin(0.5f, 1f)
            }
    ) {
        AiAssistantBar(onClick = onClick, modifier = Modifier.fillMaxWidth())
    }
}

/**
 * A pill-shaped, search-bar-styled entry point into the AI chat screen: an opaque surface card
 * (so it reads as a distinct, tappable element rather than a faint tint) with a gradient icon
 * badge, and a short bright segment that continuously travels around the border's perimeter
 * (starting from a corner and looping forever) instead of a busier full-fill highlight sweep.
 */
@Composable
private fun AiAssistantBar(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "ai_border_travel")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(durationMillis = 2600, easing = LinearEasing)),
        label = "ai_border_progress"
    )
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val shape = RoundedCornerShape(50)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .shadow(elevation = 8.dp, shape = shape, ambientColor = primary.copy(alpha = 0.45f), spotColor = primary.copy(alpha = 0.45f))
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .drawWithCache {
                val outline = Path().apply {
                    addRoundRect(RoundRect(0f, 0f, size.width, size.height, CornerRadius(size.height / 2f)))
                }
                val measure = PathMeasure().apply { setPath(outline, false) }
                val length = measure.length
                val segmentLength = length * 0.16f
                onDrawWithContent {
                    drawContent()
                    if (length > 0f) {
                        val start = progress * length
                        val end = start + segmentLength
                        val segment = Path()
                        if (end <= length) {
                            measure.getSegment(start, end, segment, true)
                        } else {
                            measure.getSegment(start, length, segment, true)
                            val wrapped = Path()
                            measure.getSegment(0f, end - length, wrapped, true)
                            segment.addPath(wrapped)
                        }
                        drawPath(
                            path = segment,
                            brush = Brush.linearGradient(colors = listOf(primary, tertiary)),
                            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }
            }
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(colors = listOf(primary, tertiary))),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
        }
        Column(modifier = Modifier.padding(start = 14.dp).weight(1f)) {
            Text(
                stringResource(R.string.home_ai_search_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                stringResource(R.string.home_ai_search_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 8.dp, top = 22.dp, bottom = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        if (actionLabel != null && onAction != null) {
            TextButton(onClick = onAction) {
                Text(actionLabel, style = MaterialTheme.typography.labelMedium)
                Icon(Icons.Filled.ChevronRight, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun QuickActionTile(action: HomeAction, isLoading: Boolean, modifier: Modifier = Modifier) {
    AppCard(
        onClick = if (isLoading) null else action.onClick,
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        // Was 0.10f — nearly transparent, so the floating background blobs (see
        // FloatingBlobsBackground) showed straight through the card instead of behind it.
        containerColor = androidx.compose.ui.graphics.lerp(
            MaterialTheme.colorScheme.surface,
            action.gradient.first(),
            0.12f
        ).copy(alpha = 0.94f),
        restingElevation = 0.dp
    ) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth().height(104.dp))
        } else {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Brush.linearGradient(action.gradient)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        action.icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Text(
                    action.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    modifier = Modifier.padding(top = 12.dp)
                )
                Text(
                    action.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun TemplateThumbnailCard(template: TemplateUiModel, accent: Color, onClick: () -> Unit, onVisible: () -> Unit = {}) {
    LaunchedEffect(template.option.id) { onVisible() }
    Column(
        modifier = Modifier
            .width(184.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.White.copy(alpha = 0.30f), Color.White.copy(alpha = 0.12f))
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White.copy(alpha = 0.55f), accent.copy(alpha = 0.25f))
                ),
                shape = RoundedCornerShape(26.dp)
            )
            .clickable(onClick = onClick)
            .padding(10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.72f)
                .clip(RoundedCornerShape(18.dp))
        ) {
            ResumeTemplateThumbnail(html = template.previewHtml, modifier = Modifier.fillMaxSize())

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.Black.copy(alpha = 0.4f),
                modifier = Modifier.align(Alignment.TopStart).padding(8.dp)
            ) {
                Text(
                    stringResource(template.option.category.labelRes),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(accent)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                template.option.title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun CoverLetterThumbnailCard(template: CoverLetterTemplateUiModel, accent: Color, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(184.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.White.copy(alpha = 0.30f), Color.White.copy(alpha = 0.12f))
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White.copy(alpha = 0.55f), accent.copy(alpha = 0.25f))
                ),
                shape = RoundedCornerShape(26.dp)
            )
            .clickable(onClick = onClick)
            .padding(10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.72f)
                .clip(RoundedCornerShape(18.dp))
        ) {
            ResumeTemplateThumbnail(html = template.previewHtml, modifier = Modifier.fillMaxSize())
        }

        Spacer(Modifier.height(10.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(accent)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                template.option.title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private val previewTemplates = CuratedTemplateCatalog.templates.take(4).map { TemplateUiModel(option = it, previewHtml = "") }

@Preview(showBackground = true)
@Composable
private fun HomeTabPreview() {
    ResumeBuilderTheme {
        HomeTabContent(
            templates = previewTemplates,
            templatesLoading = false,
            onCreateResume = {},
            onViewResumes = {},
            onBrowseTemplates = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 160)
@Composable
private fun TemplateThumbnailCardPreview() {
    ResumeBuilderTheme {
        TemplateThumbnailCard(
            template = TemplateUiModel(option = CuratedTemplateCatalog.templates.first(), previewHtml = ""),
            accent = MaterialTheme.colorScheme.primary,
            onClick = {}
        )
    }
}
