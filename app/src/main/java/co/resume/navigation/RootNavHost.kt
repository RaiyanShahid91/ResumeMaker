package co.resume.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import co.resume.analytics.Analytics
import co.resume.ui.screen.ai.AiChatScreen
import co.resume.ui.screen.dashboard.DashboardScreen
import co.resume.ui.screen.editor.CoverLetterEditorScreen
import co.resume.ui.screen.editor.CoverLetterTemplatePickerScreen
import co.resume.ui.screen.editor.ResumeEditorScreen
import co.resume.ui.screen.editor.SectionEditorScreen
import co.resume.ui.screen.editor.TemplatePickerScreen
import co.resume.ui.screen.info.AboutScreen
import co.resume.ui.screen.info.PrivacyPolicyScreen
import co.resume.ui.screen.info.UserGuideScreen
import co.resume.ui.screen.onboarding.OnboardingScreen
import co.resume.ui.screen.convert.ConverterToolScreen
import co.resume.ui.screen.pdf.PdfViewerScreen
import co.resume.ui.screen.preview.CoverLetterPreviewScreen
import co.resume.ui.screen.preview.ResumePreviewScreen
import co.resume.ui.screen.scan.ScanResultScreen
import co.resume.ui.screen.templates.CoverLetterTemplateBrowseScreen
import co.resume.ui.screen.templates.TemplateBrowseScreen
import co.resume.ui.viewmodel.ConverterTool
import co.resume.ui.viewmodel.ScannerViewModel

private const val TransitionDurationMs = 260

@Composable
fun RootNavHost(
    startDestination: String,
    onLanguageSelected: (String) -> Unit,
    onOnboardingFinished: () -> Unit = {},
    navController: NavHostController = rememberNavController()
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(currentBackStackEntry) {
        currentBackStackEntry?.destination?.route?.let { route ->
            Analytics.logScreenView(route)
        }
    }
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(TransitionDurationMs))
        },
        exitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(TransitionDurationMs))
        },
        popEnterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(TransitionDurationMs))
        },
        popExitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(TransitionDurationMs))
        }
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    onOnboardingFinished()
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Dashboard.route) { backStackEntry ->
            val scannerViewModel: ScannerViewModel = hiltViewModel(backStackEntry)
            DashboardScreen(
                scannerViewModel = scannerViewModel,
                onOpenResumeEditor = { resumeId ->
                    navController.navigate(Screen.ResumeEditor.createRoute(resumeId))
                },
                onOpenCoverLetterEditor = { coverLetterId ->
                    navController.navigate(Screen.CoverLetterEditor.createRoute(coverLetterId))
                },
                onBrowseTemplates = { navController.navigate(Screen.TemplateBrowse.route) },
                onBrowseCoverLetterTemplates = { navController.navigate(Screen.CoverLetterTemplateBrowse.route) },
                onLanguageSelected = onLanguageSelected,
                onOpenPrivacy = { navController.navigate(Screen.Privacy.route) },
                onOpenAbout = { navController.navigate(Screen.About.route) },
                onOpenGuide = { navController.navigate(Screen.Guide.route) },
                onOpenAiChat = { navController.navigate(Screen.AiChat.route) },
                onScanReady = { navController.navigate(Screen.ScanResult.route) },
                onOpenPdfViewer = { path, title ->
                    navController.navigate(Screen.PdfViewer.createRoute(path, title))
                },
                onOpenConverterTool = { tool -> navController.navigate(Screen.Converter.createRoute(tool.name)) }
            )
        }
        composable(
            route = Screen.Converter.route,
            arguments = listOf(navArgument("tool") { type = NavType.StringType })
        ) { backStackEntry ->
            val toolName = backStackEntry.arguments?.getString("tool") ?: return@composable
            val tool = runCatching { ConverterTool.valueOf(toolName) }.getOrNull() ?: return@composable
            ConverterToolScreen(
                tool = tool,
                onBack = { navController.popBackStack() },
                onGoToDocuments = {
                    navController.popBackStack(Screen.Dashboard.route, false)
                }
            )
        }
        composable(
            route = Screen.PdfViewer.route,
            arguments = listOf(
                navArgument("path") { type = NavType.StringType },
                navArgument("title") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val path = backStackEntry.arguments?.getString("path")
                ?.let { java.net.URLDecoder.decode(it, "UTF-8") } ?: return@composable
            val title = backStackEntry.arguments?.getString("title")
                ?.let { java.net.URLDecoder.decode(it, "UTF-8") }.orEmpty()
            PdfViewerScreen(
                pdfPath = path,
                title = title,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.ScanResult.route) {
            val dashboardEntry = remember(navController) { navController.getBackStackEntry(Screen.Dashboard.route) }
            val scannerViewModel: ScannerViewModel = hiltViewModel(dashboardEntry)
            ScanResultScreen(
                viewModel = scannerViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.AiChat.route) {
            AiChatScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.TemplateBrowse.route) {
            TemplateBrowseScreen(
                onBack = { navController.popBackStack() },
                onOpenEditor = { resumeId ->
                    navController.navigate(Screen.ResumeEditor.createRoute(resumeId))
                }
            )
        }
        composable(Screen.CoverLetterTemplateBrowse.route) {
            CoverLetterTemplateBrowseScreen(
                onBack = { navController.popBackStack() },
                onOpenEditor = { coverLetterId ->
                    navController.navigate(Screen.CoverLetterEditor.createRoute(coverLetterId))
                }
            )
        }
        composable(Screen.Privacy.route) {
            PrivacyPolicyScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.About.route) {
            AboutScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Guide.route) {
            UserGuideScreen(onBack = { navController.popBackStack() })
        }
        composable(
            route = Screen.ResumeEditor.route,
            arguments = listOf(navArgument("resumeId") { type = NavType.LongType })
        ) { backStackEntry ->
            val resumeId = backStackEntry.arguments?.getLong("resumeId") ?: return@composable
            ResumeEditorScreen(
                resumeId = resumeId,
                onBack = { navController.popBackStack() },
                onOpenSection = { sectionKey ->
                    navController.navigate(Screen.EditorSection.createRoute(resumeId, sectionKey))
                },
                onOpenTemplatePicker = { navController.navigate(Screen.TemplatePicker.createRoute(resumeId)) },
                onOpenPreview = { navController.navigate(Screen.ResumePreview.createRoute(resumeId)) }
            )
        }
        composable(
            route = Screen.EditorSection.route,
            arguments = listOf(
                navArgument("resumeId") { type = NavType.LongType },
                navArgument("sectionKey") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val sectionKey = backStackEntry.arguments?.getString("sectionKey") ?: return@composable
            SectionEditorScreen(sectionKey = sectionKey, onBack = { navController.popBackStack() })
        }
        composable(
            route = Screen.TemplatePicker.route,
            arguments = listOf(navArgument("resumeId") { type = NavType.LongType })
        ) {
            TemplatePickerScreen(onBack = { navController.popBackStack() })
        }
        composable(
            route = Screen.ResumePreview.route,
            arguments = listOf(navArgument("resumeId") { type = NavType.LongType })
        ) {
            ResumePreviewScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.CoverLetterEditor.route,
            arguments = listOf(navArgument("coverLetterId") { type = NavType.LongType })
        ) { backStackEntry ->
            val coverLetterId = backStackEntry.arguments?.getLong("coverLetterId") ?: return@composable
            CoverLetterEditorScreen(
                coverLetterId = coverLetterId,
                onBack = { navController.popBackStack() },
                onOpenTemplatePicker = { navController.navigate(Screen.CoverLetterTemplatePicker.createRoute(coverLetterId)) },
                onOpenPreview = { navController.navigate(Screen.CoverLetterPreview.createRoute(coverLetterId)) }
            )
        }
        composable(
            route = Screen.CoverLetterTemplatePicker.route,
            arguments = listOf(navArgument("coverLetterId") { type = NavType.LongType })
        ) {
            CoverLetterTemplatePickerScreen(onBack = { navController.popBackStack() })
        }
        composable(
            route = Screen.CoverLetterPreview.route,
            arguments = listOf(navArgument("coverLetterId") { type = NavType.LongType })
        ) {
            CoverLetterPreviewScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
