package co.resume.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ResumeWithDetails(
    @Embedded val resume: ResumeEntity,
    @Relation(parentColumn = "id", entityColumn = "resumeId") val education: List<EducationEntity> = emptyList(),
    @Relation(parentColumn = "id", entityColumn = "resumeId") val workExperience: List<WorkExperienceEntity> = emptyList(),
    @Relation(parentColumn = "id", entityColumn = "resumeId") val skills: List<SkillEntity> = emptyList(),
    @Relation(parentColumn = "id", entityColumn = "resumeId") val projects: List<ProjectEntity> = emptyList(),
    @Relation(parentColumn = "id", entityColumn = "resumeId") val achievements: List<AchievementEntity> = emptyList(),
    @Relation(parentColumn = "id", entityColumn = "resumeId") val languages: List<LanguageEntity> = emptyList(),
    @Relation(parentColumn = "id", entityColumn = "resumeId") val interests: List<InterestEntity> = emptyList(),
    @Relation(parentColumn = "id", entityColumn = "resumeId") val hobbies: List<HobbyEntity> = emptyList()
)
