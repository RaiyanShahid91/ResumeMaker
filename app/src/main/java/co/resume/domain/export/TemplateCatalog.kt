package co.resume.domain.export

import androidx.annotation.StringRes
import co.resumeai.R

enum class TemplateCategory(@StringRes val labelRes: Int) {
    All(R.string.template_cat_all),
    Professional(R.string.template_cat_professional),
    Creative(R.string.template_cat_creative),
    Developer(R.string.template_cat_developer),
    Designer(R.string.template_cat_designer),
    Teacher(R.string.template_cat_teacher)
}

data class TemplateOption(
    val id: Int,
    val title: String,
    val assetFolder: String,
    val category: TemplateCategory = TemplateCategory.Professional
)

object TemplateCatalog {

    val templates = listOf(
        TemplateOption(1,  "Modern",     "modern",     TemplateCategory.Professional),
        TemplateOption(2,  "Minimal",    "minimal",    TemplateCategory.Professional),
        TemplateOption(3,  "Classic",    "classic",    TemplateCategory.Professional),
        TemplateOption(4,  "Creative",   "creative",   TemplateCategory.Creative),
        TemplateOption(5,  "Two-Column", "two_column", TemplateCategory.Professional),
        TemplateOption(6,  "Bold",       "bold",       TemplateCategory.Creative),
        TemplateOption(7,  "Elegant",    "elegant",    TemplateCategory.Professional),
        TemplateOption(8,  "Compact",    "compact",    TemplateCategory.Professional),
        TemplateOption(9,  "Developer",  "developer",  TemplateCategory.Developer),
        TemplateOption(10, "Code Dark",  "code_dark",  TemplateCategory.Developer),
        TemplateOption(11, "Designer",   "designer",   TemplateCategory.Designer),
        TemplateOption(12, "Portfolio",  "portfolio",  TemplateCategory.Designer),
        TemplateOption(13, "Teacher",    "teacher",    TemplateCategory.Teacher),
        TemplateOption(14, "Academic",   "academic",   TemplateCategory.Teacher)
    )

    val categories: List<TemplateCategory> = TemplateCategory.entries.toList()

    private val byId = templates.associateBy { it.id }

    fun resolve(templateId: Int): TemplateOption = byId[templateId] ?: templates.first()
}
