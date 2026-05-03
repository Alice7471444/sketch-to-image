package com.sketchtoimage.app.domain.model

enum class AIArtStyle(val displayName: String, val prompt: String) {
    REALISTIC("Realistic", " photorealistic, detailed, professional photography"),
    ANIME("Anime", " anime style, manga, vibrant colors, japanese animation"),
    PIXAR("Pixar", " Pixar style, 3D animation, cute, disney"),
    FANTASY("Fantasy", " fantasy art, magical, ethereal, detailed fantasy"),
    CYBERPUNK("Cyberpunk", " cyberpunk, neon lights, futuristic, dystopian"),
    GHIBLI("Ghibli", " Studio Ghibli style, handpainted, whimsical, Miyazaki"),
    COMIC("Comic", " comic book style, bold lines, vibrant, superhero"),
    OIL_PAINTING("Oil Painting", " classical oil painting, realistic, masterpiece"),
    WATERCOLOR("Watercolor", " watercolor painting, soft, flowing, artistic"),
    RENDER_3D("3D Render", " 3D render, cinema4d, octane, realistic"),
    CLAY_RENDER("Clay", " claymation style, 3D, cute, stop motion"),
    SCIFI("Sci-Fi", " sci-fi, cinematic, space, futuristic")
}

enum class AIImageGenerator(val apiName: String, val description: String) {
    STABLE_DIFFUSION("Stable Diffusion", "High quality image generation"),
    DALL_E("DALL-E", "OpenAI's image generation"),
    GEMINI("Gemini", "Google's multimodal AI")
}

data class GenerationRequest(
    val sketchData: String,
    val style: AIArtStyle = AIArtStyle.REALISTIC,
    val prompt: String = "",
    val negativePrompt: String = "",
    val generator: AIImageGenerator = AIImageGenerator.STABLE_DIFFUSION,
    val seed: Int = -1
)

data class GenerationResult(
    val id: String,
    val imageUrl: String,
    val localPath: String? = null,
    val style: AIArtStyle,
    val prompt: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)

data class AITool(
    val id: String,
    val name: String,
    val description: String,
    val icon: String
)

object AITools {
    val all = listOf(
        AITool("auto_colorize", "Auto Colorize", "Automatically colorize your sketch", "palette"),
        AITool("background_gen", "Background Generator", "Generate a matching background", "landscape"),
        AITool("scene_expand", "Scene Expansion", "Expand your canvas with AI", "zoom_in"),
        AITool("cleanup", "AI Cleanup", "Clean up and refine your sketch", "auto_fix_high"),
        AITool("shadows", "Smart Shadows", "Add realistic shadows", "contrast")
    )
}