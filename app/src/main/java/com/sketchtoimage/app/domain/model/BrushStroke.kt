package com.sketchtoimage.app.domain.model

import androidx.compose.ui.graphics.Color

data class BrushStroke(
    val id: String = "",
    val color: Color = Color.White,
    val size: Float = 10f,
    val type: BrushType = BrushType.PENCIL,
    val opacity: Float = 1f
)

data class DrawingPath(
    val id: String,
    val points: List<DrawPoint>,
    val brushStroke: BrushStroke
)