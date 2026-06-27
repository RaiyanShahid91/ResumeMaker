package co.resume.navigation

sealed class Screen(val route: String) {
    data object Dashboard : Screen("dashboard")
    data object TemplateBrowse : Screen("template_browse")
    data object ResumeEditor : Screen("resume_editor/{resumeId}") {
        fun createRoute(resumeId: Long) = "resume_editor/$resumeId"
    }
    data object EditorSection : Screen("resume_editor/{resumeId}/section/{sectionKey}") {
        fun createRoute(resumeId: Long, sectionKey: String) = "resume_editor/$resumeId/section/$sectionKey"
    }
    data object TemplatePicker : Screen("resume_editor/{resumeId}/template") {
        fun createRoute(resumeId: Long) = "resume_editor/$resumeId/template"
    }
    data object ResumePreview : Screen("resume_editor/{resumeId}/preview") {
        fun createRoute(resumeId: Long) = "resume_editor/$resumeId/preview"
    }
    data object Privacy : Screen("privacy")
    data object About : Screen("about")
    data object Guide : Screen("guide")
    data object AiChat : Screen("ai_chat")
    data object Onboarding : Screen("onboarding")
}
