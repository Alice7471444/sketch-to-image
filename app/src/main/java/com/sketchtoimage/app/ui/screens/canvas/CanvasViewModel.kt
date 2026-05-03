package com.sketchtoimage.app.ui.screens.canvas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sketchtoimage.app.domain.model.AIArtStyle
import com.sketchtoimage.app.domain.model.AIImageGenerator
import com.sketchtoimage.app.domain.model.BrushStroke
import com.sketchtoimage.app.domain.model.BrushType
import com.sketchtoimage.app.domain.model.Stroke
import com.sketchtoimage.app.domain.usecase.GenerateImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class CanvasUiState(
    val strokes: List<Stroke> = emptyList(),
    val activeBrushType: BrushType = BrushType.PENCIL,
    val activeColor: Color = Color.White,
    val brushSize: Float = 10f,
    val brushOpacity: Float = 1f,
    val zoom: Float = 1f,
    val offset: Offset = Offset.Zero,
    val isDrawing: Boolean = false,
    val prompt: String = "",
    val selectedStyle: AIArtStyle = AIArtStyle.REALISTIC,
    val selectedGenerator: AIImageGenerator = AIImageGenerator.STABLE_DIFFUSION,
    val isGenerating: Boolean = false,
    val isLoading: Boolean = false,
    val loadingMessage: String = "Interpreting sketch...",
    val generatedImage: String? = null,
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val undoStack: List<Stroke> = emptyList(),
    val redoStack: List<Stroke> = emptyList()
)

@HiltViewModel
class CanvasViewModel @Inject constructor(
    private val generateImageUseCase: GenerateImageUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CanvasUiState())
    val uiState: StateFlow<CanvasUiState> = _uiState.asStateFlow()
    
    private var currentStroke: Stroke? = null
    
    fun onDrawStart(point: Offset) {
        currentStroke = Stroke(
            points = listOf(
                com.sketchtoimage.app.domain.model.DrawPoint(
                    offset = point,
                    pressure = 1f
                )
            ),
            color = _uiState.value.activeColor,
            brushSize = _uiState.value.brushSize,
            brushType = _uiState.value.activeBrushType,
            opacity = _uiState.value.brushOpacity
        )
        // Add current stroke to list immediately for real-time rendering
        _uiState.update { 
            it.copy(
                isDrawing = true,
                strokes = it.strokes + currentStroke
            ) 
        }
    }
    
    fun onDrawMove(point: Offset) {
        currentStroke?.let { stroke ->
            val updatedStroke = stroke.copy(
                points = stroke.points + com.sketchtoimage.app.domain.model.DrawPoint(
                    offset = point,
                    pressure = 1f
                )
            )
            currentStroke = updatedStroke
            
            // Update the stroke in real-time
            _uiState.update {
                val strokesWithoutLast = if (it.strokes.isNotEmpty()) it.strokes.dropLast(1) else it.strokes
                it.copy(strokes = strokesWithoutLast + updatedStroke)
            }
        }
    }
    
    fun onDrawEnd() {
        currentStroke?.let { stroke ->
            // Final stroke is already in the list from onDrawStart/onDrawMove
            _uiState.update {
                it.copy(
                    undoStack = it.undoStack + stroke,
                    isDrawing = false,
                    canUndo = true
                )
            }
        }
        currentStroke = null
    }
    
    fun onZoomPan(zoom: Float, offset: Offset) {
        _uiState.update {
            it.copy(zoom = zoom.coerceIn(0.5f, 4f), offset = offset)
        }
    }
    
    fun updatePrompt(prompt: String) {
        _uiState.update { it.copy(prompt = prompt) }
    }
    
    fun setBrushType(brush: BrushType) {
        _uiState.update { it.copy(activeBrushType = brush) }
    }
    
    fun setColor(color: Color) {
        _uiState.update { it.copy(activeColor = color) }
    }
    
    fun setBrushSize(size: Float) {
        _uiState.update { it.copy(brushSize = size) }
    }
    
    fun setOpacity(opacity: Float) {
        _uiState.update { it.copy(brushOpacity = opacity) }
    }
    
    fun setArtStyle(style: AIArtStyle) {
        _uiState.update { it.copy(selectedStyle = style) }
    }
    
    fun undo() {
        val state = _uiState.value
        if (state.canUndo && state.undoStack.isNotEmpty()) {
            val lastStroke = state.undoStack.last()
            _uiState.update {
                it.copy(
                    strokes = it.strokes.filter { s -> s != lastStroke },
                    undoStack = it.undoStack.dropLast(1),
                    redoStack = it.redoStack + lastStroke,
                    canUndo = it.undoStack.size > 1,
                    canRedo = true
                )
            }
        }
    }
    
    fun redo() {
        val state = _uiState.value
        if (state.canRedo && state.redoStack.isNotEmpty()) {
            val strokeToRedo = state.redoStack.last()
            _uiState.update {
                it.copy(
                    strokes = it.strokes + strokeToRedo,
                    undoStack = it.undoStack + strokeToRedo,
                    redoStack = it.redoStack.dropLast(1),
                    canUndo = true,
                    canRedo = it.redoStack.size > 1
                )
            }
        }
    }
    
    fun generateImage() {
        viewModelScope.launch {
            // Start loading phase
            _uiState.update { 
                it.copy(
                    isGenerating = true,
                    isLoading = true,
                    loadingMessage = "Interpreting sketch..."
                ) 
            }
            
            // Animate through loading messages
            val loadingMessages = listOf(
                "Interpreting sketch...",
                "Analyzing strokes...",
                "Generating details...",
                "Adding textures...",
                "Refining edges...",
                "Bringing imagination to life..."
            )
            
            try {
                // Show progressive loading messages
                for (i in loadingMessages.indices) {
                    delay(600)
                    _uiState.update { it.copy(loadingMessage = loadingMessages[i]) }
                }
                
                val request = com.sketchtoimage.app.domain.model.GenerationRequest(
                    sketchData = "sketch_${System.currentTimeMillis()}",
                    style = _uiState.value.selectedStyle,
                    prompt = _uiState.value.prompt.ifEmpty { _uiState.value.selectedStyle.prompt },
                    generator = _uiState.value.selectedGenerator
                )
                
                val result = generateImageUseCase(request)
                result.fold(
                    onSuccess = { generationResult ->
                        _uiState.update {
                            it.copy(
                                isGenerating = false,
                                isLoading = false,
                                generatedImage = generationResult.imageUrl
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update { 
                            it.copy(
                                isGenerating = false, 
                                isLoading = false,
                                loadingMessage = "Generation failed. Please try again."
                            ) 
                        }
                    }
                )
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isGenerating = false, 
                        isLoading = false,
                        loadingMessage = "Error: ${e.message ?: "Unknown error"}"
                    ) 
                }
            }
        }
    }
    
    fun setGenerator(generator: AIImageGenerator) {
        _uiState.update { it.copy(selectedGenerator = generator) }
    }
    
    fun retryGeneration() {
        generateImage()
    }
    
    fun clearGeneratedImage() {
        _uiState.update { it.copy(generatedImage = null) }
    }
    
    fun saveProject() {
        // Save project logic
    }
    
    fun exportPNG() {
        // PNG export logic
    }
    
    fun exportJPG() {
        // JPG export logic
    }
    
    fun clearCanvas() {
        _uiState.update {
            it.copy(
                strokes = emptyList(),
                undoStack = emptyList(),
                redoStack = emptyList(),
                canUndo = false,
                canRedo = false
            )
        }
    }
}