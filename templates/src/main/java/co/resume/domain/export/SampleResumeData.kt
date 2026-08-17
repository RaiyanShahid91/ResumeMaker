package co.resume.domain.export

import android.content.Context
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
import co.resume.templates.R
import java.io.File

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
                course = "M.S. in Human-Computer Interaction", university = "Carnegie Mellon University",
                grade = "3.9 GPA", durationFrom = "2018", durationTo = "2020"
            ),
            EducationEntity(
                resumeId = SAMPLE_RESUME_ID, orderIndex = 1,
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
                    "by 22%. Built and maintained the company's design system, partnering closely " +
                    "with engineering and research to ship a consistent experience across web and " +
                    "mobile surfaces."
            ),
            WorkExperienceEntity(
                resumeId = SAMPLE_RESUME_ID, orderIndex = 1,
                jobTitle = "Product Designer", company = "Brightside Co.",
                durationFrom = "2018", durationTo = "2021",
                description = "Designed mobile and web features end-to-end with product and " +
                    "engineering, from research through shipped UI. Ran usability studies that " +
                    "informed a checkout redesign, lifting conversion by 14%."
            ),
            WorkExperienceEntity(
                resumeId = SAMPLE_RESUME_ID, orderIndex = 2,
                jobTitle = "UX Designer", company = "Fieldstone Interactive",
                durationFrom = "2016", durationTo = "2018",
                description = "Owned the design of an internal analytics dashboard used by 40+ " +
                    "account managers daily, cutting average task completion time by a third."
            ),
            WorkExperienceEntity(
                resumeId = SAMPLE_RESUME_ID, orderIndex = 3,
                jobTitle = "Junior Designer", company = "Ashgrove Studio",
                durationFrom = "2014", durationTo = "2016",
                description = "Supported senior designers on branding and marketing site work for " +
                    "a roster of small-business clients, from wireframes through final assets."
            )
        ),
        skills = listOf(
            SkillEntity(resumeId = SAMPLE_RESUME_ID, orderIndex = 0, skillName = "UI/UX Design", skillLevel = "Expert"),
            SkillEntity(resumeId = SAMPLE_RESUME_ID, orderIndex = 1, skillName = "Figma", skillLevel = "Expert"),
            SkillEntity(resumeId = SAMPLE_RESUME_ID, orderIndex = 2, skillName = "Design Systems", skillLevel = "Advanced"),
            SkillEntity(resumeId = SAMPLE_RESUME_ID, orderIndex = 3, skillName = "Prototyping", skillLevel = "Advanced"),
            SkillEntity(resumeId = SAMPLE_RESUME_ID, orderIndex = 4, skillName = "User Research", skillLevel = "Advanced"),
            SkillEntity(resumeId = SAMPLE_RESUME_ID, orderIndex = 5, skillName = "Interaction Design", skillLevel = "Expert"),
            SkillEntity(resumeId = SAMPLE_RESUME_ID, orderIndex = 6, skillName = "Design Systems Governance", skillLevel = "Advanced"),
            SkillEntity(resumeId = SAMPLE_RESUME_ID, orderIndex = 7, skillName = "Accessibility (WCAG)", skillLevel = "Intermediate"),
            SkillEntity(resumeId = SAMPLE_RESUME_ID, orderIndex = 8, skillName = "Front-End Prototyping (HTML/CSS)", skillLevel = "Intermediate")
        ),
        projects = listOf(
            ProjectEntity(
                resumeId = SAMPLE_RESUME_ID, orderIndex = 0,
                projectName = "Design System Revamp", description = "Rebuilt the component " +
                    "library used across 12 product teams.",
                durationFrom = "2022", durationTo = "2023", projectLink = "northwind.design"
            ),
            ProjectEntity(
                resumeId = SAMPLE_RESUME_ID, orderIndex = 1,
                projectName = "Onboarding Funnel Redesign", description = "Re-architected the " +
                    "first-run experience end to end, reducing drop-off at the second step by 18%.",
                durationFrom = "2021", durationTo = "2022", projectLink = "northwind.design/onboarding"
            )
        ),
        achievements = listOf(
            AchievementEntity(resumeId = SAMPLE_RESUME_ID, orderIndex = 0, achievementName = "Speaker, Design+Research 2023"),
            AchievementEntity(resumeId = SAMPLE_RESUME_ID, orderIndex = 1, achievementName = "Mentor of the Year, Northwind Labs 2022")
        ),
        languages = listOf(
            LanguageEntity(resumeId = SAMPLE_RESUME_ID, orderIndex = 0, languageName = "English"),
            LanguageEntity(resumeId = SAMPLE_RESUME_ID, orderIndex = 1, languageName = "Spanish"),
            LanguageEntity(resumeId = SAMPLE_RESUME_ID, orderIndex = 2, languageName = "Portuguese")
        ),
        interests = listOf(
            InterestEntity(resumeId = SAMPLE_RESUME_ID, orderIndex = 0, interestName = "Typography")
        ),
        hobbies = listOf(
            HobbyEntity(resumeId = SAMPLE_RESUME_ID, orderIndex = 0, hobbyName = "Photography"),
            HobbyEntity(resumeId = SAMPLE_RESUME_ID, orderIndex = 1, hobbyName = "Hiking")
        )
    )

    @Volatile private var cachedPhotoPath: String? = null

    /** Copies the bundled placeholder headshot into the app's cache dir once and reuses that file's
     * path on every later call — [profilePhotoPath][co.resume.data.local.entity.ResumeEntity.profilePhotoPath]
     * has to be a real file:// path (the same contract a picked photo would satisfy), not a drawable
     * resource id, since both the preview WebView and the PDF/DOCX exporters read it straight off disk. */
    private fun samplePhotoPath(context: Context): String {
        cachedPhotoPath?.let { return it }
        synchronized(this) {
            cachedPhotoPath?.let { return it }
            val file = File(context.cacheDir, "sample_resume_photo.png")
            if (!file.exists()) {
                context.resources.openRawResource(R.drawable.icon_resume_profile_photo).use { input ->
                    file.outputStream().use { output -> input.copyTo(output) }
                }
            }
            return file.absolutePath.also { cachedPhotoPath = it }
        }
    }

    fun forLayoutAndTheme(context: Context, layoutId: String, themeId: String): ResumeWithDetails =
        details.copy(resume = details.resume.copy(layoutId = layoutId, themeId = themeId, profilePhotoPath = samplePhotoPath(context)))
}
