package co.resume.domain.export

data class CoverLetterTemplateOption(val id: Int, val title: String, val assetFolder: String)

object CoverLetterTemplateCatalog {

    val templates = listOf(
        CoverLetterTemplateOption(1, "Classic", "classic"),
        CoverLetterTemplateOption(2, "Modern", "modern"),
        CoverLetterTemplateOption(3, "Minimal", "minimal"),
        CoverLetterTemplateOption(4, "Bold", "bold"),
        CoverLetterTemplateOption(5, "Elegant", "elegant"),
        CoverLetterTemplateOption(6, "Creative", "creative"),
        CoverLetterTemplateOption(7, "Executive", "executive"),
        CoverLetterTemplateOption(8, "Compact", "compact")
    )

    private val byId = templates.associateBy { it.id }

    fun resolve(templateId: Int): CoverLetterTemplateOption = byId[templateId] ?: templates.first()
}
