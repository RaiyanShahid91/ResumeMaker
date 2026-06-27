package co.resume.di

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
}
