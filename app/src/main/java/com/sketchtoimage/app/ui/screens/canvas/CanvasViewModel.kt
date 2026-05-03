package com.sketchtoimage.app.ui.screens.canvas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sketchtoimage.app.domain.model.AIArtStyle
import com.sketchtoimage.app.domain.model.BrushStroke
import com.sketchtoimage.app.domain.model.BrushType
import com.sketchtoimage.app.domain.model.Stroke
import com.sketchtoimage.app.domain.usecase.GenerateImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
    val isGenerating: Boolean = false,
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
        _uiState.update { it.copy(isDrawing = true) }
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
            
            _uiState.update {
                it.copy(
                    strokes = it.strokes.dropLast(if (currentStroke != null) 1 else 0) + updatedStroke
                )
            }
        }
    }
    
    fun onDrawEnd() {
        currentStroke?.let { stroke ->
            _uiState.update {
                it.copy(
                    strokes = it.strokes + stroke,
                    undoStack = it.undoStack + stroke,
                    isDrawing = false,
                    canUndo = it.undoStack.isNotEmpty()
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
            _uiState.update { it.copy(isGenerating = true) }
            
            try {
                val request = com.sketchtoimage.app.domain.model.GenerationRequest(
                    sketchData = "", // Would serialize strokes
                    style = _uiState.value.selectedStyle,
                    prompt = _uiState.value.prompt
                )
                
                val result = generateImageUseCase(request)
                result.fold(
                    onSuccess = { generationResult ->
                        _uiState.update {
                            it.copy(
                                isGenerating = false,
                                generatedImage = generationResult.imageUrl
                            )
                        }
                    },
                    onFailure = {
                        _uiState.update { it.copy(isGenerating = false) }
                    }
                )
            } catch (e: Exception) {
                _uiState.update { it.copy(isGenerating = false) }
            }
        }
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