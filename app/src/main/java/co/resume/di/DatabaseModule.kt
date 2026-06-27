package co.resume.di

import android.content.Context
import androidx.room.Room
import co.resume.data.local.AppDatabase
import co.resume.data.local.dao.AchievementDao
import co.resume.data.local.dao.EducationDao
import co.resume.data.local.dao.HobbyDao
import co.resume.data.local.dao.InterestDao
import co.resume.data.local.dao.LanguageDao
import co.resume.data.local.dao.ProjectDao
import co.resume.data.local.dao.ResumeDao
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
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME).build()

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
}
