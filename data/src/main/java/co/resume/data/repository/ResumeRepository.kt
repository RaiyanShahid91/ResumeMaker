package co.resume.data.repository

import co.resume.data.local.entity.AchievementEntity
import co.resume.data.local.entity.EducationEntity
import co.resume.data.local.entity.HobbyEntity
import co.resume.data.local.entity.InterestEntity
import co.resume.data.local.entity.LanguageEntity
import co.resume.data.local.entity.ProjectEntity
import co.resume.data.local.entity.ResumeEntity
import co.resume.data.local.entity.ResumeWithDetails
import co.resume.data.local.entity.ScannedDocumentEntity
import co.resume.data.local.entity.SkillEntity
import co.resume.data.local.entity.WorkExperienceEntity
import kotlinx.coroutines.flow.Flow

interface ResumeRepository {
    fun observeAllResumes(): Flow<List<ResumeEntity>>
    fun observeAllResumesWithDetails(): Flow<List<ResumeWithDetails>>
    fun observeDeletedResumes(): Flow<List<ResumeEntity>>
    fun observeResume(id: Long): Flow<ResumeWithDetails?>

    suspend fun createResume(name: String, designation: String): Long
    suspend fun updateResume(resume: ResumeEntity)
    suspend fun reorderResumes(orderedIds: List<Long>)
    suspend fun duplicateResume(id: Long): Long
    suspend fun softDeleteResume(id: Long)
    suspend fun restoreResume(id: Long)
    suspend fun purgeOldDeletedResumes(olderThanMillis: Long)
    suspend fun getResumeWithDetails(id: Long): ResumeWithDetails?

    suspend fun setEducation(resumeId: Long, items: List<EducationEntity>)
    suspend fun setWorkExperience(resumeId: Long, items: List<WorkExperienceEntity>)
    suspend fun setSkills(resumeId: Long, items: List<SkillEntity>)
    suspend fun setProjects(resumeId: Long, items: List<ProjectEntity>)
    suspend fun setAchievements(resumeId: Long, items: List<AchievementEntity>)
    suspend fun setLanguages(resumeId: Long, items: List<LanguageEntity>)
    suspend fun setInterests(resumeId: Long, items: List<InterestEntity>)
    suspend fun setHobbies(resumeId: Long, items: List<HobbyEntity>)

    fun observeScannedDocuments(): Flow<List<ScannedDocumentEntity>>
    suspend fun saveScannedDocument(document: ScannedDocumentEntity): Long
    suspend fun deleteScannedDocument(id: Long)
}
