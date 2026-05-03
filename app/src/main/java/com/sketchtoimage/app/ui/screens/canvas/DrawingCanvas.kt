package com.sketchtoimage.app.ui.screens.canvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.sketchtoimage.app.domain.model.BrushType
import com.sketchtoimage.app.domain.model.Stroke as DrawStroke

@Composable
fun DrawingCanvas(
    strokes: List<DrawStroke>,
    activeBrushType: BrushType,
    activeColor: Color,
    brushSize: Float,
    brushOpacity: Float,
    zoom: Float,
    offset: Offset,
    isDrawing: Boolean,
    onDrawStart: (Offset) -> Unit,
    onDrawMove: (Offset) -> Unit,
    onDrawEnd: () -> Unit,
    onZoomPan: (Float, Offset) -> Unit,
    modifier: Modifier = Modifier
) {
    var localZoom by remember { mutableFloatStateOf(zoom) }
    var localOffset by remember { mutableStateOf(offset) }
    
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .graphicsLayer {
                scaleX = localZoom
                scaleY = localZoom
                translationX = localOffset.x
                translationY = localOffset.y
            }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    onDrawStart(offset)
                    onDrawEnd()
                }
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        onDrawStart(offset)
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        onDrawMove(change.position)
                    },
                    onDragEnd = {
                        onDrawEnd()
                    }
                )
            }
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoomChange, _ ->
                    localZoom = (localZoom * zoomChange).coerceIn(0.5f, 4f)
                    localOffset = Offset(
                        x = localOffset.x + pan.x,
                        y = localOffset.y + pan.y
                    )
                    onZoomPan(localZoom, localOffset)
                }
            }
    ) {
        // Draw grid background
        drawGrid()
        
        // Draw all strokes
        strokes.forEach { stroke ->
            drawStroke(stroke)
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawGrid() {
    val gridSize = 40.dp.toPx()
    val gridColor = Color.White.copy(alpha = 0.05f)
    
    var x = 0f
    while (x < size.width * 2) {
        drawLine(
            color = gridColor,
            start = Offset(x, 0f),
            end = Offset(x, size.height * 2),
            strokeWidth = 1.dp.toPx()
        )
        x += gridSize
    }
    
    var y = 0f
    while (y < size.height * 2) {
        drawLine(
            color = gridColor,
            start = Offset(0f, y),
            end = Offset(size.width * 2, y),
            strokeWidth = 1.dp.toPx()
        )
        y += gridSize
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawStroke(stroke: DrawStroke) {
    if (stroke.points.size < 2) return
    
    val path = Path()
    val points = stroke.points
    
    path.moveTo(points.first().offset.x, points.first().offset.y)
    
    // Smooth curve through points
    for (i in 1 until points.size) {
        val prev = points[i - 1].offset
        val curr = points[i].offset
        val midX = (prev.x + curr.x) / 2
        val midY = (prev.y + curr.y) / 2
        
        if (i == 1) {
            path.lineTo(midX, midY)
        } else {
            path.quadraticBezierTo(prev.x, prev.y, midX, midY)
        }
    }
    
    // Last point
    path.lineTo(points.last().offset.x, points.last().offset.y)
    
    // Set up stroke style based on brush type
    val strokeStyle = Stroke(
        cap = StrokeCap.Round,
        join = StrokeJoin.Round,
        width = stroke.brushSize.dp.toPx()
    )
    
    when (stroke.brushType) {
        BrushType.ERASER -> {
            drawPath(
                path = path,
                color = Color.Transparent,
                style = strokeStyle.copy(brushSize = stroke.brushSize.dp.toPx() * 2)
            )
        }
        BrushType.NEON -> {
            // Glow effect
            drawPath(
                path = path,
                color = stroke.color.copy(alpha = 0.3f),
                style = strokeStyle.copy(width = stroke.brushSize.dp.toPx() * 3)
            )
            drawPath(
                path = path,
                color = stroke.color,
                style = strokeStyle
            )
        }
        BrushType.GLOW -> {
            // Multiple passes for glow
            repeat(3) { i ->
                drawPath(
                    path = path,
                    color = stroke.color.copy(alpha = (1f - i * 0.25f) * 0.5f),
                    style = strokeStyle.copy(width = stroke.brushSize.dp.toPx() * (1 + i * 0.5f))
                )
            }
            drawPath(
                path = path,
                color = stroke.color,
                style = strokeStyle
            )
        }
        BrushType.WATERCOLOR -> {
            drawPath(
                path = path,
                color = stroke.color.copy(alpha = stroke.opacity * 0.4f),
                style = strokeStyle.copy(width = stroke.brushSize.dp.toPx() * 1.5f)
            )
            drawPath(
                path = path,
                color = stroke.color.copy(alpha = stroke.opacity * 0.6f),
                style = strokeStyle
            )
        }
        BrushType.MARKER -> {
            drawPath(
                path = path,
                color = stroke.color.copy(alpha = stroke.opacity * 0.8f),
                style = strokeStyle.copy(
                    cap = StrokeCap.Square,
                    width = stroke.brushSize.dp.toPx()
                )
            )
        }
        else -> {
            drawPath(
                path = path,
                color = stroke.color.copy(alpha = stroke.opacity),
                style = strokeStyle
            )
        }
    }
}