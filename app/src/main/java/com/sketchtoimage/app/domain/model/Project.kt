package com.sketchtoimage.app.domain.model

data class Project(
    val id: String,
    val name: String,
    val thumbnailPath: String? = null,
    val canvasData: String? = null,
    val generatedImages: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val style: AIArtStyle? = null
)

data class CommunityPost(
    val id: String,
    val userId: String,
    val username: String,
    val userAvatar: String? = null,
    val imageUrl: String,
    val prompt: String? = null,
    val likes: Int = 0,
    val comments: Int = 0,
    val isLiked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class DailyChallenge(
    val id: String,
    val title: String,
    val description: String,
    val prompt: String,
    val points: Int,
    val participants: Int = 0,
    val expiresAt: Long
)

enum class ExportFormat(val extension: String, val mimeType: String) {
    PNG("png", "image/png"),
    JPG("jpg", "image/jpeg"),
    TRANSPARENT_PNG("png", "image/png")
}

enum class ExportQuality(val displayName: String, val scaleFactor: Float) {
    STANDARD("Standard", 1f),
    HD("HD", 2f),
    UHD_4K("4K", 4f)
}