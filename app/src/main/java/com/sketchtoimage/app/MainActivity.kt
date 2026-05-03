package com.sketchtoimage.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.sketchtoimage.app.ui.navigation.SketchToImageNavHost
import com.sketchtoimage.app.ui.theme.SketchToImageTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            SketchToImageTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    SketchToImageNavHost()
                }
            }
        }
    }
}