package com.sketchtoimage.app.data.repository

import com.sketchtoimage.app.domain.model.AIArtStyle
import com.sketchtoimage.app.domain.model.GenerationRequest
import com.sketchtoimage.app.domain.model.GenerationResult
import com.sketchtoimage.app.domain.model.Project
import com.sketchtoimage.app.domain.repository.GenerationRepository
import com.sketchtoimage.app.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRepositoryImpl @Inject constructor() : ProjectRepository {
    
    private val projects = MutableStateFlow<List<Project>>(emptyList())
    
    override fun getAllProjects(): Flow<List<Project>> = projects.asStateFlow()
    
    override fun getProjectById(id: String): Flow<Project?> = flow {
        projects.value.find { it.id == id }
    }
    
    override suspend fun saveProject(project: Project) {
        projects.value = projects.value + project
    }
    
    override suspend fun deleteProject(id: String) {
        projects.value = projects.value.filter { it.id != id }
    }
    
    override suspend fun updateProject(project: Project) {
        projects.value = projects.value.map {
            if (it.id == project.id) project else it
        }
    }
}

@Singleton
class GenerationRepositoryImpl @Inject constructor() : GenerationRepository {
    
    private val history = MutableStateFlow<List<GenerationResult>>(emptyList())
    
    override suspend fun generateImage(request: GenerationRequest): Result<GenerationResult> {
        // In a real app, this would call an AI API
        // For now, we return a mock result
        return Result.success(
            GenerationResult(
                id = UUID.randomUUID().toString(),
                imageUrl = "", // Would be populated by AI API
                style = request.style,
                prompt = request.prompt
            )
        )
    }
    
    override suspend fun getGeneratedImages(): List<GenerationResult> {
        return history.value
    }
    
    override suspend fun deleteGeneratedImage(id: String) {
        history.value = history.value.filter { it.id != id }
    }
    
    override fun getGenerationHistory(): Flow<List<GenerationResult>> = history.asStateFlow()
}