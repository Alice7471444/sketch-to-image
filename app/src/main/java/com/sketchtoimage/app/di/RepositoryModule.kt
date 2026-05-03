package com.sketchtoimage.app.di

import com.sketchtoimage.app.data.repository.GenerationRepositoryImpl
import com.sketchtoimage.app.data.repository.ProjectRepositoryImpl
import com.sketchtoimage.app.domain.repository.GenerationRepository
import com.sketchtoimage.app.domain.repository.ProjectRepository
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
    abstract fun bindProjectRepository(
        impl: ProjectRepositoryImpl
    ): ProjectRepository
    
    @Binds
    @Singleton
    abstract fun bindGenerationRepository(
        impl: GenerationRepositoryImpl
    ): GenerationRepository
}