package co.resume.data.migration

import android.content.Context
import android.util.Base64
import co.resume.data.local.dao.AchievementDao
import co.resume.data.local.dao.EducationDao
import co.resume.data.local.dao.HobbyDao
import co.resume.data.local.dao.InterestDao
import co.resume.data.local.dao.LanguageDao
import co.resume.data.local.dao.ProjectDao
import co.resume.data.local.dao.ResumeDao
import co.resume.data.local.dao.SkillDao
import co.resume.data.local.dao.WorkExperienceDao
import co.resume.data.local.entity.AchievementEntity
import co.resume.data.local.entity.EducationEntity
import co.resume.data.local.entity.HobbyEntity
import co.resume.data.local.entity.InterestEntity
import co.resume.data.local.entity.LanguageEntity
import co.resume.data.local.entity.ProjectEntity
import co.resume.data.local.entity.ResumeEntity
import co.resume.data.local.entity.SkillEntity
import co.resume.data.local.entity.WorkExperienceEntity
import co.resume.utils.Constants
import co.resume.utils.SharedPref
import co.resume.data.TinyDB
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

/**
 * One-time migration of legacy TinyDB (SharedPreferences) resume data into Room.
 * Gated by [Constants.TINY_DB_IMPORTED] so it only ever runs once per install.
 * The original SharedPreferences are left untouched as a backup.
 */
class TinyDbImporter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedPref: SharedPref,
    private val resumeDao: ResumeDao,
    private val educationDao: EducationDao,
    private val workExperienceDao: WorkExperienceDao,
    private val skillDao: SkillDao,
    private val projectDao: ProjectDao,
    private val achievementDao: AchievementDao,
    private val languageDao: LanguageDao,
    private val interestDao: InterestDao,
    private val hobbyDao: HobbyDao
) {
    private val legacyIdFormat = SimpleDateFormat(Constants.YEAR_MONTH_DAY_SEC, Locale.US)

    suspend fun importIfNeeded() {
        if (sharedPref.getBoolean(Constants.TINY_DB_IMPORTED, false)) return

        val tinyDb = TinyDB(context)
        val legacyIds = tinyDb.getListString(Constants.ALL_RESUME).filter { it.isNotBlank() }
        legacyIds.forEach { legacyId -> importOneResume(tinyDb, legacyId) }

        sharedPref.saveBoolean(Constants.TINY_DB_IMPORTED, true)
    }

    private suspend fun importOneResume(tinyDb: TinyDB, legacyId: String) {
        val createdAt = runCatching { legacyIdFormat.parse(legacyId)?.time }.getOrNull()
            ?: System.currentTimeMillis()

        val resume = ResumeEntity(
            legacyResumeId = legacyId,
            name = tinyDb.getString("$legacyId:name"),
            designation = tinyDb.getString("$legacyId:descriptionDesignation"),
            email = tinyDb.getString("$legacyId:email"),
            phone = tinyDb.getString("$legacyId:phone"),
            address = tinyDb.getString("$legacyId:address"),
            objective = tinyDb.getString("$legacyId:objective"),
            declaration = tinyDb.getString("$legacyId:declaration"),
            declarationPlace = tinyDb.getString("$legacyId:declaration_place"),
            declarationDate = tinyDb.getString("$legacyId:declaration_date"),
            profilePhotoPath = decodeBase64ToFileOrNull(
                tinyDb.getString("$legacyId:profile_photo"), "photo_$legacyId.png"
            ),
            signaturePath = decodeBase64ToFileOrNull(
                tinyDb.getString("$legacyId:signature"), "signature_$legacyId.png"
            ),
            templateId = tinyDb.getString("$legacyId:resume_template").toIntOrNull() ?: 1,
            fontFamilyIndex = tinyDb.getInt("$legacyId:font_family"),
            colorOneIndex = tinyDb.getInt("$legacyId:color_one"),
            colorTwoIndex = tinyDb.getInt("$legacyId:color_two"),
            createdAt = createdAt,
            updatedAt = createdAt
        )
        val newResumeId = resumeDao.insert(resume)

        importSection(tinyDb, "$legacyId:educational_details") { row, index ->
            EducationEntity(
                resumeId = newResumeId,
                orderIndex = index,
                course = row.getOrElse(0) { "" },
                university = row.getOrElse(1) { "" },
                grade = row.getOrElse(2) { "" },
                durationFrom = row.getOrElse(3) { "" },
                durationTo = row.getOrElse(4) { "" }
            )
        }.let { educationDao.insertAll(it) }

        importSection(tinyDb, "$legacyId:work_experience") { row, index ->
            WorkExperienceEntity(
                resumeId = newResumeId,
                orderIndex = index,
                jobTitle = row.getOrElse(0) { "" },
                company = row.getOrElse(1) { "" },
                durationFrom = row.getOrElse(2) { "" },
                durationTo = row.getOrElse(3) { "" },
                description = row.getOrElse(4) { "" }
            )
        }.let { workExperienceDao.insertAll(it) }

        importSection(tinyDb, "$legacyId:skills_details") { row, index ->
            SkillEntity(
                resumeId = newResumeId,
                orderIndex = index,
                skillName = row.getOrElse(0) { "" },
                skillLevel = row.getOrElse(1) { "" }
            )
        }.let { skillDao.insertAll(it) }

        importSection(tinyDb, "$legacyId:projects_details") { row, index ->
            ProjectEntity(
                resumeId = newResumeId,
                orderIndex = index,
                projectName = row.getOrElse(0) { "" },
                description = row.getOrElse(1) { "" },
                durationFrom = row.getOrElse(2) { "" },
                durationTo = row.getOrElse(3) { "" },
                projectLink = row.getOrElse(4) { "" }
            )
        }.let { projectDao.insertAll(it) }

        importSection(tinyDb, "$legacyId:achievements_details") { row, index ->
            AchievementEntity(resumeId = newResumeId, orderIndex = index, achievementName = row.getOrElse(0) { "" })
        }.let { achievementDao.insertAll(it) }

        importSection(tinyDb, "$legacyId:languages_details") { row, index ->
            LanguageEntity(resumeId = newResumeId, orderIndex = index, languageName = row.getOrElse(0) { "" })
        }.let { languageDao.insertAll(it) }

        importSection(tinyDb, "$legacyId:interests_details") { row, index ->
            InterestEntity(resumeId = newResumeId, orderIndex = index, interestName = row.getOrElse(0) { "" })
        }.let { interestDao.insertAll(it) }

        importSection(tinyDb, "$legacyId:hobbies_details") { row, index ->
            HobbyEntity(resumeId = newResumeId, orderIndex = index, hobbyName = row.getOrElse(0) { "" })
        }.let { hobbyDao.insertAll(it) }
    }

    private fun <T> importSection(tinyDb: TinyDB, listKey: String, map: (List<String>, Int) -> T): List<T> {
        val subKeys = tinyDb.getListString(listKey).filter { it.isNotBlank() }
        return subKeys.mapIndexed { index, subKey -> map(tinyDb.getListString(subKey), index) }
    }

    private fun decodeBase64ToFileOrNull(base64: String, fileName: String): String? {
        if (base64.isBlank()) return null
        return runCatching {
            val bytes = Base64.decode(base64, Base64.DEFAULT)
            val dir = File(context.filesDir, "imported_images").apply { mkdirs() }
            val file = File(dir, fileName)
            FileOutputStream(file).use { it.write(bytes) }
            file.absolutePath
        }.getOrNull()
    }
}
