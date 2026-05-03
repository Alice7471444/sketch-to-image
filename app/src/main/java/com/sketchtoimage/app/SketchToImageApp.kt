package com.sketchtoimage.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SketchToImageApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}