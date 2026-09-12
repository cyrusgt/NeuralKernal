package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.AppCategory
import com.example.engine.LauncherAppItem
import com.example.engine.ModelQuantProfile
import com.example.engine.PowerGovernor
import com.example.engine.SystemHardwareTelemetry
import com.example.ui.theme.BatteryGood
import com.example.ui.theme.BatteryWarning
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRose
import com.example.ui.theme.TerminalCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    telemetry: SystemHardwareTelemetry,
    launcherApps: List<LauncherAppItem>,
    pinnedPackages: Set<String>,
    selectedModel: ModelQuantProfile,
    onLaunchApp: (String) -> Unit,
    onTogglePinApp: (String) -> Unit,
    onOpenAppDetails: (String) -> Unit,
    onRequestUninstall: (String) -> Unit,
    onOpenDefaultHomeSettings: () -> Unit,
    onOpenWallpaperPicker: () -> Unit,
    onOpenSystemSettings: () -> Unit,
    onQuickPurgeRam: () -> Unit,
    onSelectGovernor: (PowerGovernor) -> Unit,
    onQuickSearchQuery: (String) -> Unit,
    onStartVoice: () -> Unit,
    onNavigateToTerminal: () -> Unit,
    onNavigateToAgents: () -> Unit,
    onNavigateToEvolution: () -> Unit,
    onExitApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(AppCategory.ALL) }
    var showAppDrawer by remember { mutableStateOf(false) }
    var selectedAppForModal by remember { mutableStateOf<LauncherAppItem?>(null) }

    // Live Clock State
    var currentTimeStr by remember { mutableStateOf("") }
    var currentDateStr by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while (true) {
            val now = Date()
            currentTimeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(now)
            currentDateStr = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(now)
            delay(1000)
        }
    }

    val pinnedApps = remember(launcherApps, pinnedPackages) {
        launcherApps.filter { it.packageName in pinnedPackages || it.isPinned }
    }

    val filteredApps = remember(launcherApps, searchQuery, selectedCategory) {
        launcherApps.filter { app ->
            val matchesCategory = (selectedCategory == AppCategory.ALL) || (app.category == selectedCategory)
            val matchesSearch = searchQuery.isBlank() ||
                    app.appName.contains(searchQuery, ignoreCase = true) ||
                    app.packageName.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDarkBg)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp)
        ) {
            // 1. Top Glance & Digital Clock Header
            item {
                HomeGlanceHeader(
                    timeStr = currentTimeStr,
                    dateStr = currentDateStr,
                    telemetry = telemetry,
                    onOpenSettings = onOpenSystemSettings,
                    onOpenWallpaper = onOpenWallpaperPicker,
                    onSetDefaultHome = onOpenDefaultHomeSettings,
                    onExitApp = onExitApp
                )
            }

            // 2. Default Home App Switcher Banner
            item {
                DefaultHomeAppPromptCard(
                    onSetDefaultHome = onOpenDefaultHomeSettings
                )
            }

            // 3. Unified Search Bar (Routes to Apps or Neural LLM)
            item {
                HomeUnifiedSearchBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    onExecuteSearch = { query ->
                        if (query.isNotBlank()) {
                            onQuickSearchQuery(query)
                        }
                    },
                    onStartVoice = onStartVoice
                )
            }

            // 4. Vivo Y31 Pro Kernel & RAM Smart Widget
            item {
                VivoHardwareSmartWidget(
                    telemetry = telemetry,
                    onPurgeRam = onQuickPurgeRam,
                    onSelectGovernor = onSelectGovernor
                )
            }

            // 5. Self-Upgrade & Autonomous Evolution Smart Widget
            item {
                SelfEvolutionHomeWidget(
                    onEvolve = onNavigateToEvolution
                )
            }

            // 6. Autonomous AI Agents Quick-Action Bar
            item {
                AutonomousAgentsHomeWidget(
                    onRunAgent = { onNavigateToAgents() }
                )
            }

            // 6. Pinned Favorite Apps Dock Grid
            item {
                PinnedAppsDockSection(
                    pinnedApps = pinnedApps,
                    onLaunchApp = onLaunchApp,
                    onAppLongClick = { app -> selectedAppForModal = app },
                    onOpenAllApps = { showAppDrawer = true }
                )
            }

            // 7. Full App Drawer (Categorized Application Grid)
            item {
                AppDrawerSection(
                    apps = filteredApps,
                    selectedCategory = selectedCategory,
                    onSelectCategory = { selectedCategory = it },
                    onLaunchApp = onLaunchApp,
                    onAppLongClick = { app -> selectedAppForModal = app }
                )
            }
        }

        // App Options Modal (Launch, Pin/Unpin, App Info, Uninstall)
        if (selectedAppForModal != null) {
            val app = selectedAppForModal!!
            val isPinned = app.packageName in pinnedPackages || app.isPinned

            AppOptionsDialog(
                app = app,
                isPinned = isPinned,
                onDismiss = { selectedAppForModal = null },
                onLaunch = {
                    onLaunchApp(app.packageName)
                    selectedAppForModal = null
                },
                onTogglePin = {
                    onTogglePinApp(app.packageName)
                    selectedAppForModal = null
                },
                onAppInfo = {
                    onOpenAppDetails(app.packageName)
                    selectedAppForModal = null
                },
                onUninstall = {
                    onRequestUninstall(app.packageName)
                    selectedAppForModal = null
                }
            )
        }
    }
}

@Composable
fun HomeGlanceHeader(
    timeStr: String,
    dateStr: String,
    telemetry: SystemHardwareTelemetry,
    onOpenSettings: () -> Unit,
    onOpenWallpaper: () -> Unit,
    onSetDefaultHome: () -> Unit,
    onExitApp: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Controls Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home Status Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF064E3B))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home App",
                        tint = NeonEmerald,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "NEURAL LAUNCHER",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = NeonEmerald
                    )
                }

                // Quick Header Actions
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onOpenWallpaper,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Wallpaper,
                            contentDescription = "Change Wallpaper",
                            tint = NeonCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Device Settings",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = onExitApp,
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("exit_launcher_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PowerSettingsNew,
                            contentDescription = "Exit Launcher",
                            tint = NeonRose,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Big Digital Clock
            Text(
                text = if (timeStr.isNotEmpty()) timeStr else "12:00",
                fontSize = 44.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                color = TextPrimary,
                letterSpacing = (-1).sp
            )

            // Date & Platform
            Text(
                text = if (dateStr.isNotEmpty()) dateStr else "Friday, September 11, 2026",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = NeonCyan
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Vivo Y31 Pro Mini Telemetry Status Line
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Memory,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "RAM: ${telemetry.usedRamMb}/8192 MB",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = TextSecondary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (telemetry.isCharging) Icons.Default.BatteryChargingFull else Icons.Default.Bolt,
                        contentDescription = null,
                        tint = if (telemetry.batteryPercent > 20) BatteryGood else BatteryWarning,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${telemetry.batteryPercent}% • ${telemetry.batteryTempCelsius}°C",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun DefaultHomeAppPromptCard(
    onSetDefaultHome: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSetDefaultHome() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
        border = androidx.compose.foundation.BorderStroke(1.dp, NeonIndigo)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Set as Default Home App",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Enable system-level launcher integration & gestures",
                        fontSize = 10.sp,
                        color = Color(0xFFC7D2FE)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(NeonCyan)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "CONFIGURE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun HomeUnifiedSearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onExecuteSearch: (String) -> Unit,
    onStartVoice: () -> Unit
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        placeholder = {
            Text("Search apps or ask on-device LLM...", fontSize = 12.sp, color = TextMuted)
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = NeonCyan,
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = {
            IconButton(onClick = onStartVoice) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice Search",
                    tint = NeonCyan,
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .testTag("home_search_bar"),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonCyan,
            unfocusedBorderColor = CyberCardBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedContainerColor = CyberSurface,
            unfocusedContainerColor = CyberSurface
        ),
        shape = RoundedCornerShape(16.dp),
        singleLine = true
    )
}

@Composable
fun VivoHardwareSmartWidget(
    telemetry: SystemHardwareTelemetry,
    onPurgeRam: () -> Unit,
    onSelectGovernor: (PowerGovernor) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Memory,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Vivo Y31 Pro Kernel Smart Monitor",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Text(
                    text = "${telemetry.powerGovernorMode.label}",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = NeonEmerald
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { telemetry.ramUsagePercent },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = NeonCyan,
                trackColor = CyberSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Free: ${telemetry.freeRamMb} MB headroom",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TextSecondary
                )

                Button(
                    onClick = onPurgeRam,
                    colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceVariant, contentColor = NeonCyan),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(28.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                ) {
                    Icon(imageVector = Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Purge RAM", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AutonomousAgentsHomeWidget(
    onRunAgent: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = null,
                        tint = NeonIndigo,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Autonomous AI Agent Shortcuts",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Text(
                    text = "View All →",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan,
                    modifier = Modifier.clickable { onRunAgent() }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Pair("Battery Guard", "Optimal mA"),
                    Pair("Web Research", "Zero-Track"),
                    Pair("Doc Auditor", "Offline RAG")
                ).forEach { item ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberSurfaceVariant)
                            .clickable { onRunAgent() }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = item.first,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = item.second,
                                fontSize = 9.sp,
                                color = NeonCyan
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PinnedAppsDockSection(
    pinnedApps: List<LauncherAppItem>,
    onLaunchApp: (String) -> Unit,
    onAppLongClick: (LauncherAppItem) -> Unit,
    onOpenAllApps: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Pinned Home Dock",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Text(
                    text = "${pinnedApps.size} Pinned",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 4-Column Grid for Pinned Apps
            val displayList = (pinnedApps.take(7) + listOf(
                LauncherAppItem("All Apps", "launcher.drawer", true, AppCategory.ALL, isPinned = true)
            ))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                items(displayList) { app ->
                    val isAllAppsBtn = app.packageName == "launcher.drawer"
                    HomeAppIconItem(
                        app = app,
                        isAllApps = isAllAppsBtn,
                        onClick = {
                            if (isAllAppsBtn) {
                                onOpenAllApps()
                            } else {
                                onLaunchApp(app.packageName)
                            }
                        },
                        onLongClick = {
                            if (!isAllAppsBtn) {
                                onAppLongClick(app)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AppDrawerSection(
    apps: List<LauncherAppItem>,
    selectedCategory: AppCategory,
    onSelectCategory: (AppCategory) -> Unit,
    onLaunchApp: (String) -> Unit,
    onAppLongClick: (LauncherAppItem) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Apps,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Installed App Drawer",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Text(
                    text = "${apps.size} Apps",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = NeonEmerald
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Category Filter Pills
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(AppCategory.values()) { cat ->
                    val isSelected = selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectCategory(cat) },
                        label = {
                            Text(
                                text = cat.label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonCyan,
                            selectedLabelColor = Color.Black,
                            containerColor = CyberSurfaceVariant,
                            labelColor = TextSecondary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Apps Grid
            val chunked = apps.chunked(4)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                chunked.forEach { rowApps ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        rowApps.forEach { app ->
                            HomeAppIconItem(
                                app = app,
                                isAllApps = false,
                                onClick = { onLaunchApp(app.packageName) },
                                onLongClick = { onAppLongClick(app) }
                            )
                        }
                        // Fill empty spacers if row < 4
                        for (i in 0 until (4 - rowApps.size)) {
                            Spacer(modifier = Modifier.width(64.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeAppIconItem(
    app: LauncherAppItem,
    isAllApps: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val iconVector = when {
        isAllApps -> Icons.Default.Apps
        app.packageName.contains("dialer") || app.packageName.contains("phone") -> Icons.Default.Call
        app.packageName.contains("camera") -> Icons.Default.CameraAlt
        app.packageName.contains("chrome") || app.packageName.contains("browser") -> Icons.Default.Language
        app.packageName.contains("photo") || app.packageName.contains("gallery") -> Icons.Default.PhotoLibrary
        app.packageName.contains("document") || app.packageName.contains("files") -> Icons.Default.Folder
        app.packageName.contains("settings") -> Icons.Default.Settings
        app.packageName.contains("neural") -> Icons.Default.Terminal
        else -> Icons.Default.SmartToy
    }

    val iconBgColor = when {
        isAllApps -> NeonIndigo
        app.category == AppCategory.AI_NEURAL -> Color(0xFF0F766E)
        app.category == AppCategory.COMMUNICATION -> Color(0xFF1D4ED8)
        app.category == AppCategory.MEDIA -> Color(0xFF9D174D)
        app.category == AppCategory.PRODUCTIVITY -> Color(0xFF047857)
        app.category == AppCategory.SYSTEM -> Color(0xFF334155)
        else -> Color(0xFF1E293B)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(68.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBgColor)
                .border(1.dp, CyberCardBorder, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = iconVector,
                contentDescription = app.appName,
                tint = if (isAllApps) NeonCyan else Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = app.appName,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SelfEvolutionHomeWidget(
    onEvolve: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onEvolve() },
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, NeonIndigo)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Autonomous Self-Upgrade Engine",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(NeonIndigo)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "EVOLVE →",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "The OS kernel synthesizes its own code adapters, model quantization layers, and automation tools on demand.",
                fontSize = 11.sp,
                color = TextSecondary,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    Pair("ZRAM Compactor", "+35% RAM"),
                    Pair("Dense Hybrid RAG", "+52% Q&A"),
                    Pair("CodeCraft AST", "+38% Coding")
                ).forEach { item ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(CyberSurfaceVariant)
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = item.first, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1)
                            Text(text = item.second, fontSize = 8.sp, color = NeonEmerald, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppOptionsDialog(
    app: LauncherAppItem,
    isPinned: Boolean,
    onDismiss: () -> Unit,
    onLaunch: () -> Unit,
    onTogglePin: () -> Unit,
    onAppInfo: () -> Unit,
    onUninstall: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CyberSurface,
        title = {
            Column {
                Text(app.appName, color = NeonCyan, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(app.packageName, color = TextSecondary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Launch Action
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberSurfaceVariant)
                        .clickable { onLaunch() }
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Launch Application", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }

                // Pin / Unpin Action
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberSurfaceVariant)
                        .clickable { onTogglePin() }
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.PushPin, contentDescription = null, tint = NeonIndigo, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isPinned) "Unpin from Home Dock" else "Pin to Home Dock", fontSize = 12.sp, color = TextPrimary)
                }

                // App Details Action
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberSurfaceVariant)
                        .clickable { onAppInfo() }
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("App Info & Permissions", fontSize = 12.sp, color = TextPrimary)
                }

                // Uninstall Action (if not system app)
                if (!app.isSystemApp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF450A0A))
                            .clickable { onUninstall() }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = NeonRose, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Uninstall Application", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeonRose)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = NeonCyan)
            }
        }
    )
}
