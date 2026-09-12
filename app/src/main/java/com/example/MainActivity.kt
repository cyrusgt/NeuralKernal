package com.example

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.NeuralKernelViewModel
import com.example.ui.screens.AgentsScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.DocumentsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.KernelScreen
import com.example.ui.screens.SelfUpgradeScreen
import com.example.ui.screens.WallpaperStudioScreen
import com.example.ui.screens.WebResearchScreen
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

enum class AppNavigationTab(val label: String, val icon: ImageVector, val tag: String) {
    HOME("Home", Icons.Default.Home, "tab_home"),
    CHAT("Terminal", Icons.Default.Terminal, "tab_chat"),
    EVOLUTION("Evolve", Icons.Default.AutoAwesome, "tab_evolution"),
    WALLPAPER("Wallpapers", Icons.Default.Palette, "tab_wallpaper"),
    DOCUMENTS("RAG", Icons.Default.Description, "tab_docs"),
    AGENTS("Agents", Icons.Default.SmartToy, "tab_agents"),
    RESEARCH("Web", Icons.Default.Language, "tab_research"),
    KERNEL("OS", Icons.Default.Memory, "tab_kernel")
}

class MainActivity : ComponentActivity() {
    private val viewModel: NeuralKernelViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                NeuralKernelApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun NeuralKernelApp(viewModel: NeuralKernelViewModel) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(AppNavigationTab.HOME) }

    // Audio record permission launcher
    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
    }

    // Reactive State Collections
    val chatMessages by viewModel.chatMessages.collectAsState()
    val documents by viewModel.documents.collectAsState()
    val agents by viewModel.agents.collectAsState()
    val researchLogs by viewModel.researchLogs.collectAsState()
    val kernelLogs by viewModel.kernelLogs.collectAsState()
    val telemetry by viewModel.telemetry.collectAsState()
    val selectedModel by viewModel.selectedModel.collectAsState()
    val isInternetRetrievalEnabled by viewModel.isInternetRetrievalEnabled.collectAsState()
    val isInferring by viewModel.isInferring.collectAsState()
    val streamingText by viewModel.streamingText.collectAsState()
    val currentMetrics by viewModel.currentMetrics.collectAsState()
    val attachedDocIds by viewModel.attachedDocumentIds.collectAsState()
    val speechState by viewModel.speechState.collectAsState()
    val activeAgentSteps by viewModel.activeAgentRunSteps.collectAsState()
    val isAgentRunning by viewModel.isAgentRunning.collectAsState()
    val isSearchingWeb by viewModel.isSearchingWeb.collectAsState()
    val activeSynthesis by viewModel.activeResearchSynthesis.collectAsState()
    val installedApps by viewModel.installedApps.collectAsState()
    val launcherApps by viewModel.launcherApps.collectAsState()
    val pinnedPackages by viewModel.pinnedAppPackages.collectAsState()
    val upgradeModules by viewModel.upgradeModules.collectAsState()
    val upgradeRecommendations by viewModel.upgradeRecommendations.collectAsState()
    val isSynthesizingUpgrade by viewModel.isSynthesizingUpgrade.collectAsState()
    val activeSynthesisResult by viewModel.activeSynthesisResult.collectAsState()
    val downloadedWallpapers by viewModel.downloadedWallpapers.collectAsState()
    val onlineCollection by viewModel.onlineCollection.collectAsState()
    val isGeneratingWallpaper by viewModel.isGeneratingWallpaper.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = CyberSurface,
                modifier = Modifier
                    .height(68.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                tonalElevation = 8.dp
            ) {
                AppNavigationTab.values().forEach { tab ->
                    val isSelected = selectedTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.label,
                                fontSize = 9.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = NeonCyan,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted,
                            indicatorColor = NeonCyan
                        ),
                        modifier = Modifier.testTag(tab.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CyberDarkBg)
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                AppNavigationTab.HOME -> {
                    HomeScreen(
                        telemetry = telemetry,
                        launcherApps = launcherApps,
                        pinnedPackages = pinnedPackages,
                        selectedModel = selectedModel,
                        onLaunchApp = { pkg -> viewModel.launchApp(pkg) },
                        onTogglePinApp = { pkg -> viewModel.togglePinApp(pkg) },
                        onOpenAppDetails = { pkg -> viewModel.openAppDetails(pkg) },
                        onRequestUninstall = { pkg -> viewModel.requestUninstallApp(pkg) },
                        onOpenDefaultHomeSettings = { viewModel.openDefaultHomeSettings() },
                        onOpenWallpaperPicker = { selectedTab = AppNavigationTab.WALLPAPER },
                        onOpenSystemSettings = { viewModel.openSystemSettings() },
                        onQuickPurgeRam = { viewModel.executeRamPurge() },
                        onSelectGovernor = { gov -> viewModel.setPowerGovernor(gov) },
                        onQuickSearchQuery = { query ->
                            selectedTab = AppNavigationTab.CHAT
                            viewModel.sendMessage(query)
                        },
                        onStartVoice = {
                            if (hasAudioPermission) {
                                selectedTab = AppNavigationTab.CHAT
                                viewModel.startVoiceTranscription()
                            } else {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        },
                        onNavigateToTerminal = { selectedTab = AppNavigationTab.CHAT },
                        onNavigateToAgents = { selectedTab = AppNavigationTab.AGENTS },
                        onNavigateToEvolution = { selectedTab = AppNavigationTab.EVOLUTION },
                        onExitApp = { (context as? Activity)?.finish() }
                    )
                }

                AppNavigationTab.EVOLUTION -> {
                    SelfUpgradeScreen(
                        upgradeModules = upgradeModules,
                        recommendations = upgradeRecommendations,
                        telemetry = telemetry,
                        isSynthesizing = isSynthesizingUpgrade,
                        activeSynthesisResult = activeSynthesisResult,
                        onSynthesizeNeed = { need -> viewModel.synthesizeUpgrade(need) },
                        onToggleModule = { id, enabled -> viewModel.toggleUpgradeModule(id, enabled) },
                        onDeleteModule = { id -> viewModel.deleteUpgradeModule(id) },
                        onRefreshRecommendations = { viewModel.refreshRecommendations() }
                    )
                }

                AppNavigationTab.WALLPAPER -> {
                    WallpaperStudioScreen(
                        downloadedWallpapers = downloadedWallpapers,
                        onlineCollection = onlineCollection,
                        isGenerating = isGeneratingWallpaper,
                        onGenerateAiWallpaper = { prompt -> viewModel.generateAiWallpaper(prompt) },
                        onDownloadOnlineWallpaper = { wp -> viewModel.downloadOnlineWallpaper(wp) },
                        onSetActiveWallpaper = { id -> viewModel.setActiveWallpaper(id) },
                        onDeleteWallpaper = { id -> viewModel.deleteWallpaper(id) }
                    )
                }

                AppNavigationTab.CHAT -> {
                    ChatScreen(
                        messages = chatMessages,
                        documents = documents,
                        attachedDocIds = attachedDocIds,
                        selectedModel = selectedModel,
                        isInternetRetrievalEnabled = isInternetRetrievalEnabled,
                        isInferring = isInferring,
                        streamingText = streamingText,
                        currentMetrics = currentMetrics,
                        telemetry = telemetry,
                        speechState = speechState,
                        onSendMessage = { prompt -> viewModel.sendMessage(prompt) },
                        onSelectModel = { model -> viewModel.setModelProfile(model) },
                        onToggleInternetRetrieval = { viewModel.toggleInternetRetrieval() },
                        onToggleDocAttachment = { docId -> viewModel.toggleDocumentAttachment(docId) },
                        onQuickRamPurge = { viewModel.executeRamPurge() },
                        onStartVoice = {
                            if (hasAudioPermission) {
                                viewModel.startVoiceTranscription()
                            } else {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        },
                        onStopVoice = { viewModel.stopVoiceTranscription() },
                        onClearChat = { viewModel.clearChat() }
                    )
                }

                AppNavigationTab.DOCUMENTS -> {
                    DocumentsScreen(
                        documents = documents,
                        ragProcessor = viewModel.ragProcessor,
                        onAddDocument = { title, type, content ->
                            viewModel.addDocument(title, type, content)
                        },
                        onDeleteDocument = { id -> viewModel.deleteDocument(id) }
                    )
                }

                AppNavigationTab.AGENTS -> {
                    AgentsScreen(
                        agents = agents,
                        activeSteps = activeAgentSteps,
                        isAgentRunning = isAgentRunning,
                        onExecuteAgent = { agent, goal ->
                            viewModel.executeAgent(agent, goal)
                        },
                        onCreateAgent = { name, role, desc, prompt, tools ->
                            viewModel.createCustomAgent(name, role, desc, prompt, tools)
                        },
                        onDeleteAgent = { id -> viewModel.deleteAgent(id) }
                    )
                }

                AppNavigationTab.RESEARCH -> {
                    WebResearchScreen(
                        researchLogs = researchLogs,
                        activeSynthesis = activeSynthesis,
                        isSearching = isSearchingWeb,
                        onPerformResearch = { query -> viewModel.performAutonomousResearch(query) },
                        onDeleteLog = { id -> viewModel.deleteResearchLog(id) }
                    )
                }

                AppNavigationTab.KERNEL -> {
                    KernelScreen(
                        telemetry = telemetry,
                        installedApps = installedApps,
                        kernelLogs = kernelLogs,
                        onPurgeRam = { viewModel.executeRamPurge() },
                        onSelectGovernor = { gov -> viewModel.setPowerGovernor(gov) },
                        onLaunchApp = { pkg -> viewModel.launchApp(pkg) },
                        onOpenSettings = { viewModel.openSystemSettings() }
                    )
                }
            }
        }
    }
}

