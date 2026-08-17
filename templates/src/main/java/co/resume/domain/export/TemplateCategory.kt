package co.resume.domain.export

import androidx.annotation.StringRes
import co.resume.templates.R

enum class TemplateCategory(@StringRes val labelRes: Int) {
    All(R.string.template_cat_all),
    Professional(R.string.template_cat_professional),
    Creative(R.string.template_cat_creative),
    Developer(R.string.template_cat_developer),
    Designer(R.string.template_cat_designer),
    Teacher(R.string.template_cat_teacher)
}
