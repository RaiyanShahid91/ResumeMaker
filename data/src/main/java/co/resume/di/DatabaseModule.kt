package co.resume.di

import android.content.Context
import androidx.room.Room
import co.resume.data.local.AppDatabase
import co.resume.data.local.dao.AchievementDao
import co.resume.data.local.dao.AiChatDao
import co.resume.data.local.dao.CoverLetterDao
import co.resume.data.local.dao.EducationDao
import co.resume.data.local.dao.HobbyDao
import co.resume.data.local.dao.InterestDao
import co.resume.data.local.dao.LanguageDao
import co.resume.data.local.dao.ProjectDao
import co.resume.data.local.dao.ResumeDao
import co.resume.data.local.dao.ScannedDocumentDao
import co.resume.data.local.dao.SkillDao
import co.resume.data.local.dao.WorkExperienceDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            .addMigrations(
                AppDatabase.MIGRATION_1_2, AppDatabase.MIGRATION_2_3, AppDatabase.MIGRATION_3_4,
                AppDatabase.MIGRATION_4_5, AppDatabase.MIGRATION_5_6, AppDatabase.MIGRATION_6_7,
                AppDatabase.MIGRATION_7_8, AppDatabase.MIGRATION_8_9
            )
            .build()

    @Provides
    fun provideResumeDao(db: AppDatabase): ResumeDao = db.resumeDao()

    @Provides
    fun provideEducationDao(db: AppDatabase): EducationDao = db.educationDao()

    @Provides
    fun provideWorkExperienceDao(db: AppDatabase): WorkExperienceDao = db.workExperienceDao()

    @Provides
    fun provideSkillDao(db: AppDatabase): SkillDao = db.skillDao()

    @Provides
    fun provideProjectDao(db: AppDatabase): ProjectDao = db.projectDao()

    @Provides
    fun provideAchievementDao(db: AppDatabase): AchievementDao = db.achievementDao()

    @Provides
    fun provideLanguageDao(db: AppDatabase): LanguageDao = db.languageDao()

    @Provides
    fun provideInterestDao(db: AppDatabase): InterestDao = db.interestDao()

    @Provides
    fun provideHobbyDao(db: AppDatabase): HobbyDao = db.hobbyDao()

    @Provides
    fun provideScannedDocumentDao(db: AppDatabase): ScannedDocumentDao = db.scannedDocumentDao()

    @Provides
    fun provideCoverLetterDao(db: AppDatabase): CoverLetterDao = db.coverLetterDao()

    @Provides
    fun provideAiChatDao(db: AppDatabase): AiChatDao = db.aiChatDao()
}
