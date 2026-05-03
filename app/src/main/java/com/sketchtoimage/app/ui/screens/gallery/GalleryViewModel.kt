package com.sketchtoimage.app.ui.screens.gallery

import androidx.lifecycle.ViewModel
import com.sketchtoimage.app.domain.model.Project
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor() : ViewModel() {
    
    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects.asStateFlow()
    
    // Will be connected to repository later
    init {
        // Sample data for UI preview
        _projects.value = listOf(
            Project(
                id = "1",
                name = "Dragon Sketch",
                createdAt = System.currentTimeMillis()
            ),
            Project(
                id = "2",
                name = "City Skyline",
                createdAt = System.currentTimeMillis() - 86400000
            ),
            Project(
                id = "3",
                name = "Character Art",
                createdAt = System.currentTimeMillis() - 172800000
            ),
            Project(
                id = "4",
                name = "Fantasy Landscape",
                createdAt = System.currentTimeMillis() - 259200000
            )
        )
    }
}