package com.sketchtoimage.app.domain.repository

import com.sketchtoimage.app.domain.model.CommunityPost
import com.sketchtoimage.app.domain.model.DailyChallenge
import com.sketchtoimage.app.domain.model.GenerationRequest
import com.sketchtoimage.app.domain.model.GenerationResult
import com.sketchtoimage.app.domain.model.Project
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    fun getAllProjects(): Flow<List<Project>>
    fun getProjectById(id: String): Flow<Project?>
    suspend fun saveProject(project: Project)
    suspend fun deleteProject(id: String)
    suspend fun updateProject(project: Project)
}

interface GenerationRepository {
    suspend fun generateImage(request: GenerationRequest): Result<GenerationResult>
    suspend fun getGeneratedImages(): List<GenerationResult>
    suspend fun deleteGeneratedImage(id: String)
    fun getGenerationHistory(): Flow<List<GenerationResult>>
}

interface CommunityRepository {
    fun getTrendingPosts(): Flow<List<CommunityPost>>
    fun getDailyChallenge(): Flow<DailyChallenge?>
    suspend fun likePost(postId: String): Result<Boolean>
    suspend fun unlikePost(postId: String): Result<Boolean>
}