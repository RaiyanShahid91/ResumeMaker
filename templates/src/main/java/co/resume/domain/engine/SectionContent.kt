package co.resume.domain.engine

import co.resume.data.local.entity.ResumeWithDetails
import co.resume.domain.export.RichBlock
import co.resume.domain.export.RichTextRenderer
import co.resume.domain.layout.SectionType

/** One flowable, atomic unit within a section — never split across a page break except as a last resort. */
data class SectionEntry(
    val id: String,
    val titleLine: String? = null,
    val subtitleLine: String? = null,
    val dateLine: String? = null,
    val body: List<RichBlock> = emptyList(),
)

data class ResumeSection(val type: SectionType, val heading: String, val entries: List<SectionEntry>)

/** Flattens ResumeWithDetails into the ordered, filtered section list the engine flows onto pages. */
object SectionContentBuilder {

    fun build(resume: ResumeWithDetails): List<ResumeSection> {
        val hidden = resume.resume.hiddenSections.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
        val order = resume.resume.sectionOrder.split(",").map { it.trim() }.filter { it.isNotEmpty() }

        val sections = order.mapNotNull { key -> sectionFor(key, resume) }
        return sections.filter { it.type.name.lowercase() !in hidden && it.entries.isNotEmpty() }
    }

    private fun sectionFor(key: String, r: ResumeWithDetails): ResumeSection? = when (key) {
        "objective" -> objectiveSection(r)
        "education" -> educationSection(r)
        "work_experience" -> workExperienceSection(r)
        "skills" -> skillsSection(r)
        "projects" -> projectsSection(r)
        "achievements" -> achievementsSection(r)
        "languages" -> languagesSection(r)
        "interests" -> interestsSection(r)
        "hobbies" -> hobbiesSection(r)
        "declaration" -> declarationSection(r)
        else -> null
    }

    private fun objectiveSection(r: ResumeWithDetails): ResumeSection? {
        if (r.resume.objective.isBlank()) return null
        return ResumeSection(
            SectionType.OBJECTIVE, "Summary",
            listOf(SectionEntry(id = "objective", body = RichTextRenderer.parse(r.resume.objective))),
        )
    }

    private fun educationSection(r: ResumeWithDetails): ResumeSection? {
        if (r.education.isEmpty()) return null
        val entries = r.education.sortedBy { it.orderIndex }.map {
            SectionEntry(
                id = "education:${it.id}",
                titleLine = it.course,
                subtitleLine = it.university,
                dateLine = dateRange(it.durationFrom, it.durationTo),
                body = if (it.grade.isNotBlank()) RichTextRenderer.parse("Grade: ${it.grade}") else emptyList(),
            )
        }
        return ResumeSection(SectionType.EDUCATION, "Education", entries)
    }

    private fun workExperienceSection(r: ResumeWithDetails): ResumeSection? {
        if (r.workExperience.isEmpty()) return null
        val entries = r.workExperience.sortedBy { it.orderIndex }.map {
            SectionEntry(
                id = "workExperience:${it.id}",
                titleLine = it.jobTitle,
                subtitleLine = it.company,
                dateLine = dateRange(it.durationFrom, it.durationTo),
                body = RichTextRenderer.parse(it.description),
            )
        }
        return ResumeSection(SectionType.WORK_EXPERIENCE, "Work Experience", entries)
    }

    private fun projectsSection(r: ResumeWithDetails): ResumeSection? {
        if (r.projects.isEmpty()) return null
        val entries = r.projects.sortedBy { it.orderIndex }.map {
            SectionEntry(
                id = "project:${it.id}",
                titleLine = it.projectName,
                subtitleLine = it.projectLink.takeIf { link -> link.isNotBlank() },
                dateLine = dateRange(it.durationFrom, it.durationTo),
                body = RichTextRenderer.parse(it.description),
            )
        }
        return ResumeSection(SectionType.PROJECTS, "Projects", entries)
    }

    private fun skillsSection(r: ResumeWithDetails): ResumeSection? {
        if (r.skills.isEmpty()) return null
        val line = r.skills.sortedBy { it.orderIndex }.joinToString(" • ") { it.skillName }
        return ResumeSection(SectionType.SKILLS, "Skills", listOf(SectionEntry(id = "skills", body = RichTextRenderer.parse(line))))
    }

    private fun achievementsSection(r: ResumeWithDetails): ResumeSection? {
        if (r.achievements.isEmpty()) return null
        val entries = r.achievements.sortedBy { it.orderIndex }
            .map { SectionEntry(id = "achievement:${it.id}", body = RichTextRenderer.parse("- ${it.achievementName}")) }
        return ResumeSection(SectionType.ACHIEVEMENTS, "Achievements", entries)
    }

    private fun languagesSection(r: ResumeWithDetails): ResumeSection? {
        if (r.languages.isEmpty()) return null
        val line = r.languages.sortedBy { it.orderIndex }.joinToString(" • ") { it.languageName }
        return ResumeSection(SectionType.LANGUAGES, "Languages", listOf(SectionEntry(id = "languages", body = RichTextRenderer.parse(line))))
    }

    private fun interestsSection(r: ResumeWithDetails): ResumeSection? {
        if (r.interests.isEmpty()) return null
        val line = r.interests.sortedBy { it.orderIndex }.joinToString(" • ") { it.interestName }
        return ResumeSection(SectionType.INTERESTS, "Interests", listOf(SectionEntry(id = "interests", body = RichTextRenderer.parse(line))))
    }

    private fun hobbiesSection(r: ResumeWithDetails): ResumeSection? {
        if (r.hobbies.isEmpty()) return null
        val line = r.hobbies.sortedBy { it.orderIndex }.joinToString(" • ") { it.hobbyName }
        return ResumeSection(SectionType.HOBBIES, "Hobbies", listOf(SectionEntry(id = "hobbies", body = RichTextRenderer.parse(line))))
    }

    private fun declarationSection(r: ResumeWithDetails): ResumeSection? {
        if (r.resume.declaration.isBlank()) return null
        val signature = listOfNotNull(
            r.resume.declarationPlace.takeIf { it.isNotBlank() },
            r.resume.declarationDate.takeIf { it.isNotBlank() },
        ).joinToString(" • ")
        val body = RichTextRenderer.parse(r.resume.declaration) +
            (if (signature.isNotBlank()) RichTextRenderer.parse(signature) else emptyList())
        return ResumeSection(SectionType.DECLARATION, "Declaration", listOf(SectionEntry(id = "declaration", body = body)))
    }

    private fun dateRange(from: String, to: String): String? {
        if (from.isBlank() && to.isBlank()) return null
        return listOf(from, to).filter { it.isNotBlank() }.joinToString(" – ")
    }
}
