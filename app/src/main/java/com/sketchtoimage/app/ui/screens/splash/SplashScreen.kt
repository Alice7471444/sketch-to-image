package com.sketchtoimage.app.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sketchtoimage.app.ui.theme.CyanSecondary
import com.sketchtoimage.app.ui.theme.NeonPink
import com.sketchtoimage.app.ui.theme.PurplePrimary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit
) {
    val animationProgress = remember { Animatable(0f) }
    
    // Sketch drawing animation
    LaunchedEffect(Unit) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing)
        )
        delay(1500)
        onNavigateToHome()
    }
    
    // Pulsing glow animation
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Animated Sketch Icon
            Canvas(
                modifier = Modifier.size(120.dp * pulseScale)
            ) {
                val brush = Brush.linearGradient(
                    colors = listOf(PurplePrimary, CyanSecondary, NeonPink)
                )
                
                // Glow effect
                drawCircle(
                    brush = brush,
                    radius = size.minDimension / 2,
                    alpha = glowAlpha * 0.3f
                )
                
                // Main sketch icon - pen/quill
                drawLine(
                    color = Color.White,
                    start = Offset(size.width * 0.2f, size.height * 0.8f),
                    end = Offset(size.width * 0.8f, size.height * 0.2f),
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round
                )
                
                // Sketch stroke
                drawLine(
                    color = PurplePrimary,
                    start = Offset(size.width * 0.25f, size.height * 0.7f),
                    end = Offset(size.width * 0.5f, size.height * 0.4f),
                    strokeWidth = 6.dp.toPx(),
                    cap = StrokeCap.Round
                )
                
                drawLine(
                    color = CyanSecondary,
                    start = Offset(size.width * 0.3f, size.height * 0.65f),
                    end = Offset(size.width * 0.7f, size.height * 0.3f),
                    strokeWidth = 6.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // App Name with gradient
            Text(
                text = "SKETCH TO IMAGE",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                ),
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Tagline
            Text(
                text = "Draw Dreams Into Reality",
                style = MaterialTheme.typography.bodyLarge,
                color = NeonPink.copy(alpha = animationProgress.value)
            )
        }
    }
}