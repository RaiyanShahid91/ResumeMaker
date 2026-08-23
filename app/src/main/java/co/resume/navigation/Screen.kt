package co.resume.navigation

sealed class Screen(val route: String) {
    data object Dashboard : Screen("dashboard")
    data object TemplateBrowse : Screen("template_browse")
    data object CoverLetterTemplateBrowse : Screen("cover_letter_template_browse")
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
    data object CoverLetterEditor : Screen("cover_letter_editor/{coverLetterId}") {
        fun createRoute(coverLetterId: Long) = "cover_letter_editor/$coverLetterId"
    }
    data object CoverLetterTemplatePicker : Screen("cover_letter_editor/{coverLetterId}/template") {
        fun createRoute(coverLetterId: Long) = "cover_letter_editor/$coverLetterId/template"
    }
    data object CoverLetterPreview : Screen("cover_letter_editor/{coverLetterId}/preview") {
        fun createRoute(coverLetterId: Long) = "cover_letter_editor/$coverLetterId/preview"
    }
    data object Privacy : Screen("privacy")
    data object About : Screen("about")
    data object Guide : Screen("guide")
    data object AiChat : Screen("ai_chat")
    data object Onboarding : Screen("onboarding")
    data object ScanResult : Screen("scan_result")
    data object Converter : Screen("converter/{tool}") {
        fun createRoute(tool: String) = "converter/$tool"
    }
    data object PdfViewer : Screen("pdf_viewer/{path}/{title}") {
        fun createRoute(path: String, title: String): String {
            val encodedPath = java.net.URLEncoder.encode(path, "UTF-8")
            val encodedTitle = java.net.URLEncoder.encode(title, "UTF-8")
            return "pdf_viewer/$encodedPath/$encodedTitle"
        }
    }
}
