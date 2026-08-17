package co.resume.data.repository

import co.resume.data.local.dao.AchievementDao
import co.resume.data.local.dao.EducationDao
import co.resume.data.local.dao.HobbyDao
import co.resume.data.local.dao.InterestDao
import co.resume.data.local.dao.LanguageDao
import co.resume.data.local.dao.ProjectDao
import co.resume.data.local.dao.ResumeDao
import co.resume.data.local.dao.ScannedDocumentDao
import co.resume.data.local.dao.SkillDao
import co.resume.data.local.dao.WorkExperienceDao
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
import javax.inject.Inject

class ResumeRepositoryImpl @Inject constructor(
    private val resumeDao: ResumeDao,
    private val educationDao: EducationDao,
    private val workExperienceDao: WorkExperienceDao,
    private val skillDao: SkillDao,
    private val projectDao: ProjectDao,
    private val achievementDao: AchievementDao,
    private val languageDao: LanguageDao,
    private val interestDao: InterestDao,
    private val hobbyDao: HobbyDao,
    private val scannedDocumentDao: ScannedDocumentDao
) : ResumeRepository {

    override fun observeAllResumes(): Flow<List<ResumeEntity>> = resumeDao.observeAll()

    override fun observeAllResumesWithDetails(): Flow<List<ResumeWithDetails>> = resumeDao.observeAllWithDetails()

    override fun observeDeletedResumes(): Flow<List<ResumeEntity>> = resumeDao.observeDeleted()

    override fun observeResume(id: Long): Flow<ResumeWithDetails?> = resumeDao.observeWithDetails(id)

    override suspend fun createResume(name: String, designation: String): Long =
        resumeDao.insert(ResumeEntity(name = name, designation = designation))

    override suspend fun updateResume(resume: ResumeEntity) {
        resumeDao.update(resume.copy(updatedAt = System.currentTimeMillis()))
    }

    override suspend fun reorderResumes(orderedIds: List<Long>) {
        resumeDao.reorder(orderedIds)
    }

    override suspend fun duplicateResume(id: Long): Long {
        val original = resumeDao.getWithDetails(id) ?: return -1
        val now = System.currentTimeMillis()
        val newId = resumeDao.insert(
            original.resume.copy(
                id = 0,
                legacyResumeId = null,
                name = "${original.resume.name} (Copy)",
                createdAt = now,
                updatedAt = now
            )
        )
        educationDao.insertAll(original.education.map { it.copy(id = 0, resumeId = newId) })
        workExperienceDao.insertAll(original.workExperience.map { it.copy(id = 0, resumeId = newId) })
        skillDao.insertAll(original.skills.map { it.copy(id = 0, resumeId = newId) })
        projectDao.insertAll(original.projects.map { it.copy(id = 0, resumeId = newId) })
        achievementDao.insertAll(original.achievements.map { it.copy(id = 0, resumeId = newId) })
        languageDao.insertAll(original.languages.map { it.copy(id = 0, resumeId = newId) })
        interestDao.insertAll(original.interests.map { it.copy(id = 0, resumeId = newId) })
        hobbyDao.insertAll(original.hobbies.map { it.copy(id = 0, resumeId = newId) })
        return newId
    }

    override suspend fun softDeleteResume(id: Long) = resumeDao.softDelete(id)

    override suspend fun restoreResume(id: Long) = resumeDao.restore(id)

    override suspend fun purgeOldDeletedResumes(olderThanMillis: Long) =
        resumeDao.purgeDeletedOlderThan(olderThanMillis)

    override suspend fun getResumeWithDetails(id: Long): ResumeWithDetails? = resumeDao.getWithDetails(id)

    override suspend fun setEducation(resumeId: Long, items: List<EducationEntity>) =
        educationDao.replaceAll(resumeId, items)

    override suspend fun setWorkExperience(resumeId: Long, items: List<WorkExperienceEntity>) =
        workExperienceDao.replaceAll(resumeId, items)

    override suspend fun setSkills(resumeId: Long, items: List<SkillEntity>) =
        skillDao.replaceAll(resumeId, items)

    override suspend fun setProjects(resumeId: Long, items: List<ProjectEntity>) =
        projectDao.replaceAll(resumeId, items)

    override suspend fun setAchievements(resumeId: Long, items: List<AchievementEntity>) =
        achievementDao.replaceAll(resumeId, items)

    override suspend fun setLanguages(resumeId: Long, items: List<LanguageEntity>) =
        languageDao.replaceAll(resumeId, items)

    override suspend fun setInterests(resumeId: Long, items: List<InterestEntity>) =
        interestDao.replaceAll(resumeId, items)

    override suspend fun setHobbies(resumeId: Long, items: List<HobbyEntity>) =
        hobbyDao.replaceAll(resumeId, items)

    override fun observeScannedDocuments(): Flow<List<ScannedDocumentEntity>> =
        scannedDocumentDao.observeAll()

    override suspend fun saveScannedDocument(document: ScannedDocumentEntity): Long =
        scannedDocumentDao.insert(document)

    override suspend fun deleteScannedDocument(id: Long) = scannedDocumentDao.delete(id)
}
