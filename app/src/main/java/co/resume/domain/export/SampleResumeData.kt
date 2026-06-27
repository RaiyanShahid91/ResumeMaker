package co.resume.domain.export

import co.resume.data.local.entity.AchievementEntity
import co.resume.data.local.entity.EducationEntity
import co.resume.data.local.entity.HobbyEntity
import co.resume.data.local.entity.InterestEntity
import co.resume.data.local.entity.LanguageEntity
import co.resume.data.local.entity.ProjectEntity
import co.resume.data.local.entity.ResumeEntity
import co.resume.data.local.entity.ResumeWithDetails
import co.resume.data.local.entity.SkillEntity
import co.resume.data.local.entity.WorkExperienceEntity

/** Placeholder resume content used only to render template previews — never persisted. */
object SampleResumeData {

    private const val SAMPLE_RESUME_ID = -1L

    val details: ResumeWithDetails = ResumeWithDetails(
        resume = ResumeEntity(
            id = SAMPLE_RESUME_ID,
            name = "Alex Morgan",
            designation = "Product Designer",
            email = "alex.morgan@email.com",
            phone = "+1 555 010 2030",
            address = "San Francisco, CA",
            objective = "Product designer with 6+ years crafting user-centered web and mobile " +
                "experiences. Passionate about design systems, accessibility, and shipping work " +
                "that measurably improves engagement.",
            declaration = "I hereby declare that the information provided above is true to the " +
                "best of my knowledge.",
            declarationPlace = "San Francisco",
            declarationDate = "01/01/2026"
        ),
        education = listOf(
            EducationEntity(
                resumeId = SAMPLE_RESUME_ID, orderIndex = 0,
                course = "B.S. in Design", university = "Stanford University",
                grade = "3.8 GPA", durationFrom = "2014", durationTo = "2018"
            )
        ),
        workExperience = listOf(
            WorkExperienceEntity(
                resumeId = SAMPLE_RESUME_ID, orderIndex = 0,
                jobTitle = "Senior Product Designer", company = "Northwind Labs",
                durationFrom = "2021", durationTo = "Present",
                description = "Led design for the core onboarding flow, increasing activation " +
                    "by 22%. Built and maintained the company's design system."
            ),
            WorkExperienceEntity(
                resumeId = SAMPLE_RESUME_ID, orderIndex = 1,
                jobTitle = "Product Designer", company = "Brightside Co.",
                durationFrom = "2018", durationTo = "2021",
                description = "Designed mobile and web features end-to-end with product and " +
                    "engineering, from research through shipped UI."
            )
        ),
        skills = listOf(
            SkillEntity(resumeId = SAMPLE_RESUME_ID, orderIndex = 0, skillName = "UI/UX Design", skillLevel = "Expert"),
            SkillEntity(resumeId = SAMPLE_RESUME_ID, orderIndex = 1, skillName = "Figma", skillLevel = "Expert"),
            SkillEntity(resumeId = SAMPLE_RESUME_ID, orderIndex = 2, skillName = "Design Systems", skillLevel = "Advanced"),
            SkillEntity(resumeId = SAMPLE_RESUME_ID, orderIndex = 3, skillName = "Prototyping", skillLevel = "Advanced")
        ),
        projects = listOf(
            ProjectEntity(
                resumeId = SAMPLE_RESUME_ID, orderIndex = 0,
                projectName = "Design System Revamp", description = "Rebuilt the component " +
                    "library used across 12 product teams.",
                durationFrom = "2022", durationTo = "2023", projectLink = "northwind.design"
            )
        ),
        achievements = listOf(
            AchievementEntity(resumeId = SAMPLE_RESUME_ID, orderIndex = 0, achievementName = "Speaker, Design+Research 2023")
        ),
        languages = listOf(
            LanguageEntity(resumeId = SAMPLE_RESUME_ID, orderIndex = 0, languageName = "English"),
            LanguageEntity(resumeId = SAMPLE_RESUME_ID, orderIndex = 1, languageName = "Spanish")
        ),
        interests = listOf(
            InterestEntity(resumeId = SAMPLE_RESUME_ID, orderIndex = 0, interestName = "Typography")
        ),
        hobbies = listOf(
            HobbyEntity(resumeId = SAMPLE_RESUME_ID, orderIndex = 0, hobbyName = "Photography"),
            HobbyEntity(resumeId = SAMPLE_RESUME_ID, orderIndex = 1, hobbyName = "Hiking")
        )
    )

    fun forTemplate(templateId: Int): ResumeWithDetails =
        details.copy(resume = details.resume.copy(templateId = templateId))
}
