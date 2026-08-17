package co.resume.domain.score

import co.resume.data.local.entity.ResumeWithDetails
import co.resume.ui.screen.editor.EditorSectionType
import kotlin.math.roundToInt

/**
 * Resume-completeness meter (not a job-description match score): the percentage of editor
 * sections that have at least the minimum content a resume needs, used to nudge users to fill
 * in more before exporting.
 */
object AtsScoreCalculator {

    private val trackedSections = listOf(
        EditorSectionType.PERSONAL,
        EditorSectionType.PHOTO,
        EditorSectionType.OBJECTIVE,
        EditorSectionType.EDUCATION,
        EditorSectionType.WORK_EXPERIENCE,
        EditorSectionType.SKILLS,
        EditorSectionType.PROJECTS,
        EditorSectionType.ACHIEVEMENTS,
        EditorSectionType.LANGUAGES,
        EditorSectionType.DECLARATION
    )

    fun sectionCompletion(details: ResumeWithDetails): Map<EditorSectionType, Boolean> =
        trackedSections.associateWith { isComplete(it, details) }

    fun score(details: ResumeWithDetails): Int {
        val completion = sectionCompletion(details)
        val completeCount = completion.values.count { it }
        return ((completeCount.toDouble() / completion.size) * 100).roundToInt()
    }

    fun missingSections(details: ResumeWithDetails): List<EditorSectionType> =
        sectionCompletion(details).filterValues { !it }.keys.toList()

    private fun isComplete(type: EditorSectionType, details: ResumeWithDetails): Boolean = when (type) {
        EditorSectionType.PERSONAL -> details.resume.name.isNotBlank() && details.resume.email.isNotBlank() &&
            details.resume.phone.isNotBlank() && details.resume.address.isNotBlank()
        EditorSectionType.PHOTO -> !details.resume.profilePhotoPath.isNullOrBlank()
        EditorSectionType.OBJECTIVE -> details.resume.objective.isNotBlank()
        EditorSectionType.EDUCATION -> details.education.isNotEmpty()
        EditorSectionType.WORK_EXPERIENCE -> details.workExperience.isNotEmpty()
        EditorSectionType.SKILLS -> details.skills.isNotEmpty()
        EditorSectionType.PROJECTS -> details.projects.isNotEmpty()
        EditorSectionType.ACHIEVEMENTS -> details.achievements.isNotEmpty()
        EditorSectionType.LANGUAGES -> details.languages.isNotEmpty()
        EditorSectionType.DECLARATION -> details.resume.declaration.isNotBlank()
    }
}
