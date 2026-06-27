package co.resume.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
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

@Database(
    entities = [
        ResumeEntity::class,
        EducationEntity::class,
        WorkExperienceEntity::class,
        SkillEntity::class,
        ProjectEntity::class,
        AchievementEntity::class,
        LanguageEntity::class,
        InterestEntity::class,
        HobbyEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun resumeDao(): ResumeDao
    abstract fun educationDao(): EducationDao
    abstract fun workExperienceDao(): WorkExperienceDao
    abstract fun skillDao(): SkillDao
    abstract fun projectDao(): ProjectDao
    abstract fun achievementDao(): AchievementDao
    abstract fun languageDao(): LanguageDao
    abstract fun interestDao(): InterestDao
    abstract fun hobbyDao(): HobbyDao

    companion object {
        const val DATABASE_NAME = "resume_maker.db"
    }
}
