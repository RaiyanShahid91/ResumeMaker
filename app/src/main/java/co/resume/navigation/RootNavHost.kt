package co.resume.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import co.resume.ui.screen.ai.AiChatScreen
import co.resume.ui.screen.dashboard.DashboardScreen
import co.resume.ui.screen.editor.ResumeEditorScreen
import co.resume.ui.screen.editor.SectionEditorScreen
import co.resume.ui.screen.editor.TemplatePickerScreen
import co.resume.ui.screen.info.AboutScreen
import co.resume.ui.screen.info.PrivacyPolicyScreen
import co.resume.ui.screen.info.UserGuideScreen
import co.resume.ui.screen.onboarding.OnboardingScreen
import co.resume.ui.screen.preview.ResumePreviewScreen
import co.resume.ui.screen.templates.TemplateBrowseScreen

private const val TransitionDurationMs = 260

@Composable
fun RootNavHost(
    startDestination: String,
    onLanguageSelected: (String) -> Unit,
    onOnboardingFinished: () -> Unit = {},
    navController: NavHostController = rememberNavController()
) {
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
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onOpenResumeEditor = { resumeId ->
                    navController.navigate(Screen.ResumeEditor.createRoute(resumeId))
                },
                onBrowseTemplates = { navController.navigate(Screen.TemplateBrowse.route) },
                onLanguageSelected = onLanguageSelected,
                onOpenPrivacy = { navController.navigate(Screen.Privacy.route) },
                onOpenAbout = { navController.navigate(Screen.About.route) },
                onOpenGuide = { navController.navigate(Screen.Guide.route) },
                onOpenAiChat = { navController.navigate(Screen.AiChat.route) }
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
            ResumePreviewScreen(onBack = { navController.popBackStack() })
        }
    }
}
