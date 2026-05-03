package com.sketchtoimage.app.domain.usecase

import com.sketchtoimage.app.domain.model.GenerationRequest
import com.sketchtoimage.app.domain.model.GenerationResult
import com.sketchtoimage.app.domain.repository.GenerationRepository
import javax.inject.Inject

class GenerateImageUseCase @Inject constructor(
    private val repository: GenerationRepository
) {
    suspend operator fun invoke(request: GenerationRequest): Result<GenerationResult> {
        return repository.generateImage(request)
    }
}

class GetGenerationHistoryUseCase @Inject constructor(
    private val repository: GenerationRepository
) {
    operator fun invoke() = repository.getGenerationHistory()
}