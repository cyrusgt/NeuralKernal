package com.example.engine

import com.example.data.local.entity.WallpaperEntity

class WallpaperEngine {

    fun getCuratedOnlineCollection(): List<WallpaperEntity> {
        return listOf(
            WallpaperEntity(
                title = "Cyber Neon Horizon",
                prompt = "Cyberpunk neon city skyline at night with glowing purple and cyan holographic lights",
                imageUrl = "img_wallpaper_cyber_1789189957353",
                category = "Cyberpunk",
                author = "NeuralStudio AI",
                isDownloaded = false
            ),
            WallpaperEntity(
                title = "Cosmic Nebula Deep Space",
                prompt = "Breathtaking cosmic nebula in deep space with vibrant star clusters and glowing purple and teal interstellar dust",
                imageUrl = "img_wallpaper_nebula_1789189972753",
                category = "Cosmic",
                author = "DeepSpace 9",
                isDownloaded = false
            ),
            WallpaperEntity(
                title = "Minimalist Zen Minimalist",
                prompt = "Minimalist zen garden with serene water ripple and smooth quartz stone, soft warm lighting",
                imageUrl = "img_wallpaper_cyber_1789189957353", // Fallback placeholder reference
                category = "Minimalist",
                author = "Zenith",
                isDownloaded = false
            ),
            WallpaperEntity(
                title = "Ethereal Aurora Borealis",
                prompt = "Vibrant green and violet aurora borealis dancing across arctic night sky reflecting on glassy fjord water",
                imageUrl = "img_wallpaper_nebula_1789189972753",
                category = "Nature",
                author = "NordicCraft",
                isDownloaded = false
            )
        )
    }
}
