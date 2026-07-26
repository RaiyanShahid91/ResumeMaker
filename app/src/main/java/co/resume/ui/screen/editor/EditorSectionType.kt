package co.resume.ui.screen.editor

import androidx.annotation.StringRes
import co.resumeai.R

enum class EditorSectionType(val key: String, @StringRes val titleRes: Int) {
    PERSONAL("personal",          R.string.section_personal),
    PHOTO("photo",                R.string.section_photo),
    OBJECTIVE("objective",        R.string.section_objective),
    EDUCATION("education",        R.string.section_education),
    WORK_EXPERIENCE("work_experience", R.string.section_work_experience),
    SKILLS("skills",              R.string.section_skills),
    PROJECTS("projects",          R.string.section_projects),
    ACHIEVEMENTS("achievements",  R.string.section_achievements),
    LANGUAGES("languages",        R.string.section_languages),
    DECLARATION("declaration",    R.string.section_declaration);

    companion object {
        fun fromKey(key: String): EditorSectionType = entries.first { it.key == key }
    }
}
