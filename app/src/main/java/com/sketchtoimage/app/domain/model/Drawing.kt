package com.sketchtoimage.app.domain.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class DrawPoint(
    val offset: Offset,
    val pressure: Float = 1f
)

data class Stroke(
    val points: List<DrawPoint> = emptyList(),
    val color: Color = Color.White,
    val brushSize: Float = 10f,
    val brushType: BrushType = BrushType.PENCIL,
    val opacity: Float = 1f,
    val layerId: String = ""
)

enum class BrushType {
    PENCIL,
    INK_PEN,
    MARKER,
    NEON,
    GLOW,
    WATERCOLOR,
    PIXEL,
    ERASER
}

data class DrawLayer(
    val id: String,
    val name: String,
    val strokes: List<Stroke> = emptyList(),
    val isVisible: Boolean = true,
    val opacity: Float = 1f,
    val isLocked: Boolean = false
)

data class CanvasState(
    val layers: List<DrawLayer> = listOf(DrawLayer(id = "layer_1", name = "Layer 1")),
    val activeLayerId: String = "layer_1",
    val activeBrushType: BrushType = BrushType.PENCIL,
    val activeColor: Color = Color.White,
    val brushSize: Float = 10f,
    val brushOpacity: Float = 1f,
    val zoom: Float = 1f,
    val offset: Offset = Offset.Zero,
    val undoStack: List<CanvasAction> = emptyList(),
    val redoStack: List<CanvasAction> = emptyList()
)

sealed class CanvasAction {
    data class AddStroke(val stroke: Stroke, val layerId: String) : CanvasAction()
    data class RemoveStroke(val stroke: Stroke, val layerId: String) : CanvasAction()
    data class AddLayer(val layer: DrawLayer) : CanvasAction()
    data class RemoveLayer(val layerId: String) : CanvasAction()
}