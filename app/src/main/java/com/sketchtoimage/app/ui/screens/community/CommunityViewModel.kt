package com.sketchtoimage.app.ui.screens.community

import androidx.lifecycle.ViewModel
import com.sketchtoimage.app.domain.model.CommunityPost
import com.sketchtoimage.app.domain.model.DailyChallenge
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor() : ViewModel() {
    
    private val _trendingPosts = MutableStateFlow<List<CommunityPost>>(emptyList())
    val trendingPosts: StateFlow<List<CommunityPost>> = _trendingPosts.asStateFlow()
    
    private val _dailyChallenge = MutableStateFlow<DailyChallenge?>(null)
    val dailyChallenge: StateFlow<DailyChallenge?> = _dailyChallenge.asStateFlow()
    
    private var likedPosts = mutableSetOf<String>()
    
    init {
        // Sample data
        _dailyChallenge.value = DailyChallenge(
            id = "1",
            title = "Future City",
            description = "Create a futuristic cityscape with neon lights",
            prompt = "futuristic cyberpunk city, neon lights, tall buildings",
            points = 500,
            participants = 1234,
            expiresAt = System.currentTimeMillis() + 86400000
        )
        
        _trendingPosts.value = listOf(
            CommunityPost(
                id = "1",
                userId = "user1",
                username = "ArtistJane",
                imageUrl = "",
                prompt = "Majestic dragon in a fantasy forest",
                likes = 1234,
                comments = 89,
                createdAt = System.currentTimeMillis() - 3600000
            ),
            CommunityPost(
                id = "2",
                userId = "user2",
                username = "DigitalArtPro",
                imageUrl = "",
                prompt = "Cyberpunk street food vendor",
                likes = 892,
                comments = 45,
                createdAt = System.currentTimeMillis() - 7200000
            ),
            CommunityPost(
                id = "3",
                userId = "user3",
                username = "AIDreams",
                imageUrl = "",
                prompt = "Ghibli-style cottage in the meadow",
                likes = 2105,
                comments = 156,
                createdAt = System.currentTimeMillis() - 10800000
            ),
            CommunityPost(
                id = "4",
                userId = "user4",
                username = "CreativeMind",
                imageUrl = "",
                prompt = "3D clay render cute robot",
                likes = 567,
                comments = 34,
                createdAt = System.currentTimeMillis() - 14400000
            )
        )
    }
    
    fun toggleLike(postId: String) {
        if (likedPosts.contains(postId)) {
            likedPosts.remove(postId)
        } else {
            likedPosts.add(postId)
        }
        
        _trendingPosts.value = _trendingPosts.value.map { post ->
            if (post.id == postId) {
                post.copy(
                    isLiked = likedPosts.contains(postId),
                    likes = if (likedPosts.contains(postId)) post.likes + 1 else post.likes - 1
                )
            } else post
        }
    }
}