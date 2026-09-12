package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.WallpaperEntity
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun WallpaperStudioScreen(
    downloadedWallpapers: List<WallpaperEntity>,
    onlineCollection: List<WallpaperEntity>,
    isGenerating: Boolean,
    onGenerateAiWallpaper: (String) -> Unit,
    onDownloadOnlineWallpaper: (WallpaperEntity) -> Unit,
    onSetActiveWallpaper: (Long) -> Unit,
    onDeleteWallpaper: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSubTab by remember { mutableStateOf(0) } // 0: AI Generator, 1: Online Collection, 2: My Wallpapers
    var aiPromptInput by remember { mutableStateOf("") }

    val tabs = listOf("AI Studio", "Curated Online", "My Gallery (${downloadedWallpapers.size})")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDarkBg)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Wallpaper,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Neural Wallpaper Studio",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Text(
                        text = "AI Generation & Cloud Curated Collection",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // Sub Tab Row
        ScrollableTabRow(
            selectedTabIndex = selectedSubTab,
            containerColor = CyberSurface,
            contentColor = NeonCyan,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                if (tabPositions.size > selectedSubTab) {
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedSubTab]),
                        color = NeonCyan,
                        height = 2.dp
                    )
                }
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedSubTab == index,
                    onClick = { selectedSubTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (selectedSubTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedSubTab == index) NeonCyan else TextMuted
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        when (selectedSubTab) {
            0 -> {
                // AI Wallpaper Generator Tab
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CyberSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = NeonCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Prompt-Driven AI Wallpaper Creator",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Describe any scene, aesthetic, or surreal environment. The AI generator will synthesize an ultra-high-definition mobile wallpaper for your Vivo Y31 Pro.",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = aiPromptInput,
                                onValueChange = { aiPromptInput = it },
                                placeholder = {
                                    Text(
                                        "E.g., 'A majestic cyberpunk samurai standing under neon cherry blossoms in Tokyo rain'...",
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("wallpaper_prompt_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonCyan,
                                    unfocusedBorderColor = CyberCardBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedContainerColor = CyberSurfaceVariant,
                                    unfocusedContainerColor = CyberSurfaceVariant
                                ),
                                shape = RoundedCornerShape(10.dp),
                                maxLines = 4
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    if (aiPromptInput.isNotBlank() && !isGenerating) {
                                        onGenerateAiWallpaper(aiPromptInput)
                                        aiPromptInput = ""
                                        selectedSubTab = 2 // Switch to My Gallery
                                    }
                                },
                                enabled = aiPromptInput.isNotBlank() && !isGenerating,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NeonCyan,
                                    contentColor = Color.Black
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("generate_wallpaper_button"),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                if (isGenerating) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        color = Color.Black,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Synthesizing AI Wallpaper...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                } else {
                                    Icon(imageVector = Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Generate AI Wallpaper", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Text(
                        text = "Quick AI Prompts",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    val quickPrompts = listOf(
                        "Cyberpunk neon street reflection in rainy puddles, cinematic",
                        "Deep space nebula with swirling violet dust and glowing stars",
                        "Minimalist zen mountain landscape at twilight, pastel gradients",
                        "Abstract geometric glowing glass prism in dark void, 3D render"
                    )

                    quickPrompts.forEach { prompt ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyberSurfaceVariant)
                                .clickable {
                                    onGenerateAiWallpaper(prompt)
                                    selectedSubTab = 2
                                }
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = prompt, fontSize = 11.sp, color = TextPrimary, modifier = Modifier.weight(1f))
                                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            1 -> {
                // Curated Online Collection Tab
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(onlineCollection, key = { it.title }) { wallpaper ->
                        OnlineWallpaperCard(
                            wallpaper = wallpaper,
                            onDownload = { onDownloadOnlineWallpaper(wallpaper) }
                        )
                    }
                }
            }

            2 -> {
                // My Gallery (Downloaded & Generated)
                if (downloadedWallpapers.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Default.Image, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No wallpapers in your gallery yet.", fontSize = 13.sp, color = TextSecondary)
                            Text("Generate with AI or download from the online collection.", fontSize = 11.sp, color = TextMuted)
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(downloadedWallpapers, key = { it.id }) { wallpaper ->
                            GalleryWallpaperCard(
                                wallpaper = wallpaper,
                                onSetActive = { onSetActiveWallpaper(wallpaper.id) },
                                onDelete = { onDeleteWallpaper(wallpaper.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OnlineWallpaperCard(
    wallpaper: WallpaperEntity,
    onDownload: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyberSurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Wallpaper,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(36.dp)
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = wallpaper.category, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = wallpaper.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1
            )
            Text(
                text = "By ${wallpaper.author}",
                fontSize = 10.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onDownload,
                colors = ButtonDefaults.buttonColors(containerColor = NeonIndigo, contentColor = Color.White),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(imageVector = Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Download", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun GalleryWallpaperCard(
    wallpaper: WallpaperEntity,
    onSetActive: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (wallpaper.isCurrentWallpaper) NeonCyan else CyberCardBorder
        )
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyberSurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    tint = if (wallpaper.isCurrentWallpaper) NeonCyan else TextMuted,
                    modifier = Modifier.size(36.dp)
                )

                if (wallpaper.isCurrentWallpaper) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(6.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(NeonCyan)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = "ACTIVE", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.Black)
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = wallpaper.category, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = NeonEmerald)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = wallpaper.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1
            )
            Text(
                text = wallpaper.prompt,
                fontSize = 10.sp,
                color = TextSecondary,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = onSetActive,
                    enabled = !wallpaper.isCurrentWallpaper,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (wallpaper.isCurrentWallpaper) CyberSurfaceVariant else NeonCyan,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(30.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = if (wallpaper.isCurrentWallpaper) "Active" else "Set Home",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(CyberSurfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = TextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
