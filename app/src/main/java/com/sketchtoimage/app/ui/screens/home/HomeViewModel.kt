package com.sketchtoimage.app.ui.screens.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val recentProjects: List<ProjectItem> = emptyList(),
    val trendingStyles: List<String> = emptyList(),
    val dailyChallenge: DailyChallengeInfo? = null
)

data class ProjectItem(
    val id: String,
    val name: String,
    val thumbnailUrl: String?
)

data class DailyChallengeInfo(
    val title: String,
    val description: String,
    val points: Int
)

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        // Load initial data
        _uiState.update {
            it.copy(
                recentProjects = listOf(
                    ProjectItem("1", "Dragon Sketch", null),
                    ProjectItem("2", "City Skyline", null),
                    ProjectItem("3", "Character Art", null)
                ),
                trendingStyles = listOf("Cyberpunk", "Anime", "Fantasy", "Realistic"),
                dailyChallenge = DailyChallengeInfo(
                    title = "Future City",
                    description = "Design a futuristic cityscape",
                    points = 500
                )
            )
        }
    }
}