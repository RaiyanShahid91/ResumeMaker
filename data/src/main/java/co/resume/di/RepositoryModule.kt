package co.resume.di

import co.resume.data.repository.AiChatRepository
import co.resume.data.repository.AiChatRepositoryImpl
import co.resume.data.repository.CoverLetterRepository
import co.resume.data.repository.CoverLetterRepositoryImpl
import co.resume.data.repository.ResumeRepository
import co.resume.data.repository.ResumeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindResumeRepository(impl: ResumeRepositoryImpl): ResumeRepository

    @Binds
    @Singleton
    abstract fun bindCoverLetterRepository(impl: CoverLetterRepositoryImpl): CoverLetterRepository

    @Binds
    @Singleton
    abstract fun bindAiChatRepository(impl: AiChatRepositoryImpl): AiChatRepository
}
