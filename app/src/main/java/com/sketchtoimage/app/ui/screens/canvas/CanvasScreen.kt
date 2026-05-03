package com.sketchtoimage.app.ui.screens.canvas

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sketchtoimage.app.domain.model.AIArtStyle
import com.sketchtoimage.app.domain.model.BrushType
import com.sketchtoimage.app.domain.model.Stroke
import com.sketchtoimage.app.ui.theme.CyanSecondary
import com.sketchtoimage.app.ui.theme.NeonPink
import com.sketchtoimage.app.ui.theme.NeonPurple
import com.sketchtoimage.app.ui.theme.PurplePrimary
import com.sketchtoimage.app.ui.theme.SurfaceDark
import com.sketchtoimage.app.ui.theme.SurfaceVariant
import com.sketchtoimage.app.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanvasScreen(
    projectId: String,
    onNavigateBack: () -> Unit,
    viewModel: CanvasViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showColorPicker by remember { mutableStateOf(false) }
    var showBrushPicker by remember { mutableStateOf(false) }
    var showStylePicker by remember { mutableStateOf(false) }
    var showToolPicker by remember { mutableStateOf(false) }
    var showExportSheet by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Drawing Canvas
        DrawingCanvas(
            strokes = uiState.strokes,
            activeBrushType = uiState.activeBrushType,
            activeColor = uiState.activeColor,
            brushSize = uiState.brushSize,
            brushOpacity = uiState.brushOpacity,
            zoom = uiState.zoom,
            offset = uiState.offset,
            isDrawing = uiState.isDrawing,
            onDrawStart = { point -> viewModel.onDrawStart(point) },
            onDrawMove = { point -> viewModel.onDrawMove(point) },
            onDrawEnd = { viewModel.onDrawEnd() },
            onZoomPan = { zoom, offset -> viewModel.onZoomPan(zoom, offset) },
            modifier = Modifier.fillMaxSize()
        )
        
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Back button
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(48.dp)
                    .background(SurfaceDark.copy(alpha = 0.8f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }
            
            // Title / Prompt input
            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark.copy(alpha = 0.8f))
            ) {
                OutlinedTextField(
                    value = uiState.prompt,
                    onValueChange = { viewModel.updatePrompt(it) },
                    placeholder = {
                        Text(
                            text = "Add a prompt...",
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )
            }
            
            // Generate button
            Button(
                onClick = { viewModel.generateImage() },
                enabled = !uiState.isGenerating && uiState.strokes.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PurplePrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(24.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                if (uiState.isGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Generate",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Bring to Life")
                }
            }
        }
        
        // Bottom Tools Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter)
                .background(SurfaceDark.copy(alpha = 0.9f), RoundedCornerShape(32.dp))
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Undo
            ToolButton(
                icon = Icons.AutoMirrored.Filled.Undo,
                label = "Undo",
                enabled = uiState.canUndo,
                onClick = { viewModel.undo() }
            )
            
            // Redo
            ToolButton(
                icon = Icons.AutoMirrored.Filled.Redo,
                label = "Redo",
                enabled = uiState.canRedo,
                onClick = { viewModel.redo() }
            )
            
            // Brush/Color
            ToolButton(
                icon = Icons.Default.Brush,
                label = "Brush",
                onClick = { showBrushPicker = !showBrushPicker }
            )
            
            // Color Selector
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.White, CircleShape)
                    .background(uiState.activeColor)
                    .clickable { showColorPicker = !showColorPicker }
            )
            
            // Layers
            ToolButton(
                icon = Icons.Default.Layers,
                label = "Layers",
                onClick = { showToolPicker = !showToolPicker }
            )
            
            // Style
            ToolButton(
                icon = Icons.Default.Palette,
                label = "Style",
                onClick = { showStylePicker = !showStylePicker }
            )
            
            // Export
            ToolButton(
                icon = Icons.Default.Download,
                label = "Export",
                onClick = { showExportSheet = true }
            )
        }
        
        // Brush Picker Sheet
        if (showBrushPicker) {
            BrushPickerSheet(
                selectedBrush = uiState.activeBrushType,
                brushSize = uiState.brushSize,
                onBrushSelected = { brush -> viewModel.setBrushType(brush) },
                onBrushSizeChange = { viewModel.setBrushSize(it) },
                onDismiss = { showBrushPicker = false }
            )
        }
        
        // Style Picker Sheet
        if (showStylePicker) {
            StylePickerSheet(
                selectedStyle = uiState.selectedStyle,
                onStyleSelected = { style -> viewModel.setArtStyle(style) },
                onDismiss = { showStylePicker = false }
            )
        }
        
        // Color Picker Sheet
        if (showColorPicker) {
            ColorPickerSheet(
                selectedColor = uiState.activeColor,
                onColorSelected = { color -> viewModel.setColor(color) },
                onDismiss = { showColorPicker = false }
            )
        }
        
        // Export Sheet
        if (showExportSheet) {
            ExportSheet(
                onExportPNG = { viewModel.exportPNG() },
                onExportJPG = { viewModel.exportJPG() },
                onDismiss = { showExportSheet = false }
            )
        }
        
        // Generation Loading Overlay
        AnimatedVisibility(
            visible = uiState.isGenerating,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = PurplePrimary,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Transforming your sketch...",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "AI is creating your masterpiece",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }
        
        // Generated Image Preview
        uiState.generatedImage?.let { imageUrl ->
            ModalBottomSheet(
                onDismissRequest = { viewModel.clearGeneratedImage() },
                sheetState = sheetState
            ) {
                GeneratedImagePreview(
                    imageUrl = imageUrl,
                    onSave = { viewModel.saveProject() },
                    onRemix = { viewModel.generateImage() },
                    onShare = { /* Share logic */ }
                )
            }
        }
    }
}

@Composable
private fun ToolButton(
    icon: ImageVector,
    label: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (enabled) Color.White else Color.White.copy(alpha = 0.3f),
            modifier = Modifier.size(24.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BrushPickerSheet(
    selectedBrush: BrushType,
    brushSize: Float,
    onBrushSelected: (BrushType) -> Unit,
    onBrushSizeChange: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Brushes",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(BrushType.entries.filter { it != BrushType.ERASER }) { brush ->
                    BrushTypeItem(
                        brushType = brush,
                        isSelected = brush == selectedBrush,
                        onClick = { onBrushSelected(brush) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Brush Size: ${brushSize.toInt()}",
                color = Color.White
            )
            
            var sliderPosition by remember { mutableFloatStateOf(brushSize) }
            Slider(
                value = sliderPosition,
                onValueChange = { 
                    sliderPosition = it
                    onBrushSizeChange(it)
                },
                valueRange = 1f..50f,
                colors = SliderDefaults.colors(
                    thumbColor = PurplePrimary,
                    activeTrackColor = PurplePrimary
                )
            )
        }
    }
}

@Composable
private fun BrushTypeItem(
    brushType: BrushType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .background(
                if (isSelected) PurplePrimary.copy(alpha = 0.2f)
                else SurfaceVariant
            )
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) PurplePrimary else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Brush,
            contentDescription = brushType.name,
            tint = if (isSelected) PurplePrimary else Color.White,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = brushType.name.replace("_", " "),
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) PurplePrimary else Color.White.copy(alpha = 0.7f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StylePickerSheet(
    selectedStyle: AIArtStyle,
    onStyleSelected: (AIArtStyle) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "AI Art Styles",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(AIArtStyle.entries) { style ->
                    StyleItem(
                        artStyle = style,
                        isSelected = style == selectedStyle,
                        onClick = { onStyleSelected(style) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StyleItem(
    artStyle: AIArtStyle,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .background(
                if (isSelected) NeonPurple.copy(alpha = 0.2f)
                else SurfaceVariant
            )
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) NeonPurple else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Palette,
            contentDescription = artStyle.displayName,
            tint = if (isSelected) NeonPurple else Color.White,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = artStyle.displayName,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) NeonPurple else Color.White.copy(alpha = 0.7f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColorPickerSheet(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = listOf(
        Color.White, Color.Black, Color.Red, Color.Green, Color.Blue,
        Color.Yellow, PurplePrimary, CyanSecondary, NeonPink, NeonPurple,
        Color(0xFFFF6B6B), Color(0xFF4ECDC4), Color(0xFFFFE66D),
        Color(0xFF95E1D3), Color(0xFFF38181), Color(0xFFAA96DA)
    )
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Colors",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(colors) { color ->
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (color == selectedColor) 3.dp else 1.dp,
                                color = if (color == selectedColor) Color.White else Color.Gray,
                                shape = CircleShape
                            )
                            .clickable { onColorSelected(color) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExportSheet(
    onExportPNG: () -> Unit,
    onExportJPG: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Export",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onExportPNG,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceVariant)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Export as PNG")
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Button(
                onClick = onExportJPG,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceVariant)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Export as JPG")
            }
        }
    }
}

@Composable
private fun GeneratedImagePreview(
    imageUrl: String,
    onSave: () -> Unit,
    onRemix: () -> Unit,
    onShare: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Your Masterpiece",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Image preview placeholder - in real app would use Coil/AsyncImage
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Generated Image",
                color = Color.White.copy(alpha = 0.5f)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onSave,
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Save")
            }
            
            Button(
                onClick = onRemix,
                colors = ButtonDefaults.buttonColors(containerColor = CyanSecondary)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Remix")
            }
            
            Button(
                onClick = onShare,
                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
            ) {
                Icon(Icons.Default.Download, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Share")
            }
        }
    }
}