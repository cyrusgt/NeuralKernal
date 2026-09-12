package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.AgentEntity
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.DocumentEntity
import com.example.data.local.entity.KernelLogEntity
import com.example.data.local.entity.ResearchLogEntity
import com.example.data.repository.NeuralRepository
import com.example.engine.AgentExecutionStep
import com.example.engine.AutonomousAgentEngine
import com.example.engine.DeviceKernelBridge
import com.example.engine.DocumentRAGProcessor
import com.example.engine.GenerationMetrics
import com.example.engine.InstalledAppInfo
import com.example.engine.LocalInferenceEngine
import com.example.engine.ModelQuantProfile
import com.example.engine.OfflineSpeechTranscriber
import com.example.engine.PowerGovernor
import com.example.engine.ResearchSynthesis
import com.example.engine.SpeechRecognitionState
import com.example.engine.SystemHardwareTelemetry
import com.example.engine.WebResearchEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import com.example.data.local.entity.UpgradeModuleEntity
import com.example.data.local.entity.WallpaperEntity
import com.example.engine.NeedRecommendation
import com.example.engine.SelfUpgradeEngine
import com.example.engine.UpgradeSynthesisResult
import com.example.engine.WallpaperEngine

class NeuralKernelViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = NeuralRepository(
        chatDao = db.chatDao(),
        documentDao = db.documentDao(),
        agentDao = db.agentDao(),
        researchDao = db.researchDao(),
        kernelLogDao = db.kernelLogDao(),
        upgradeDao = db.upgradeDao(),
        wallpaperDao = db.wallpaperDao()
    )

    val kernelBridge = DeviceKernelBridge(application)
    val ragProcessor = DocumentRAGProcessor()
    val webResearchEngine = WebResearchEngine()
    val inferenceEngine = LocalInferenceEngine(kernelBridge, ragProcessor, webResearchEngine)
    val agentEngine = AutonomousAgentEngine(kernelBridge, ragProcessor, webResearchEngine)
    val speechTranscriber = OfflineSpeechTranscriber(application)
    val selfUpgradeEngine = SelfUpgradeEngine()
    val wallpaperEngine = WallpaperEngine()

    // Room reactive streams
    val chatMessages: StateFlow<List<ChatMessageEntity>> = repository.allMessages.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val documents: StateFlow<List<DocumentEntity>> = repository.allDocuments.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val agents: StateFlow<List<AgentEntity>> = repository.allAgents.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val researchLogs: StateFlow<List<ResearchLogEntity>> = repository.allResearchLogs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val kernelLogs: StateFlow<List<KernelLogEntity>> = repository.allKernelLogs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val upgradeModules: StateFlow<List<UpgradeModuleEntity>> = repository.allUpgradeModules.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val downloadedWallpapers: StateFlow<List<WallpaperEntity>> = repository.downloadedWallpapers.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _onlineCollection = MutableStateFlow<List<WallpaperEntity>>(wallpaperEngine.getCuratedOnlineCollection())
    val onlineCollection: StateFlow<List<WallpaperEntity>> = _onlineCollection.asStateFlow()

    private val _isGeneratingWallpaper = MutableStateFlow(false)
    val isGeneratingWallpaper: StateFlow<Boolean> = _isGeneratingWallpaper.asStateFlow()

    // Self-upgrade synthesis state
    private val _upgradeRecommendations = MutableStateFlow<List<NeedRecommendation>>(emptyList())
    val upgradeRecommendations: StateFlow<List<NeedRecommendation>> = _upgradeRecommendations.asStateFlow()

    private val _isSynthesizingUpgrade = MutableStateFlow(false)
    val isSynthesizingUpgrade: StateFlow<Boolean> = _isSynthesizingUpgrade.asStateFlow()

    private val _activeSynthesisResult = MutableStateFlow<UpgradeSynthesisResult?>(null)
    val activeSynthesisResult: StateFlow<UpgradeSynthesisResult?> = _activeSynthesisResult.asStateFlow()

    // Speech recognition state
    val speechState: StateFlow<SpeechRecognitionState> = speechTranscriber.state

    // Inference State
    private val _selectedModel = MutableStateFlow(ModelQuantProfile.VIVO_NANO_1_8B)
    val selectedModel: StateFlow<ModelQuantProfile> = _selectedModel.asStateFlow()

    private val _isInternetRetrievalEnabled = MutableStateFlow(false)
    val isInternetRetrievalEnabled: StateFlow<Boolean> = _isInternetRetrievalEnabled.asStateFlow()

    private val _isInferring = MutableStateFlow(false)
    val isInferring: StateFlow<Boolean> = _isInferring.asStateFlow()

    private val _streamingText = MutableStateFlow("")
    val streamingText: StateFlow<String> = _streamingText.asStateFlow()

    private val _currentMetrics = MutableStateFlow(GenerationMetrics())
    val currentMetrics: StateFlow<GenerationMetrics> = _currentMetrics.asStateFlow()

    private val _attachedDocumentIds = MutableStateFlow<Set<Long>>(emptySet())
    val attachedDocumentIds: StateFlow<Set<Long>> = _attachedDocumentIds.asStateFlow()

    // Hardware Telemetry
    private val _telemetry = MutableStateFlow(kernelBridge.getRealHardwareTelemetry())
    val telemetry: StateFlow<SystemHardwareTelemetry> = _telemetry.asStateFlow()

    // Autonomous Agent Run State
    private val _activeAgentRunSteps = MutableStateFlow<List<AgentExecutionStep>>(emptyList())
    val activeAgentRunSteps: StateFlow<List<AgentExecutionStep>> = _activeAgentRunSteps.asStateFlow()

    private val _isAgentRunning = MutableStateFlow(false)
    val isAgentRunning: StateFlow<Boolean> = _isAgentRunning.asStateFlow()

    private val _selectedAgentForRun = MutableStateFlow<AgentEntity?>(null)
    val selectedAgentForRun: StateFlow<AgentEntity?> = _selectedAgentForRun.asStateFlow()

    // Web Research State
    private val _isSearchingWeb = MutableStateFlow(false)
    val isSearchingWeb: StateFlow<Boolean> = _isSearchingWeb.asStateFlow()

    private val _activeResearchSynthesis = MutableStateFlow<ResearchSynthesis?>(null)
    val activeResearchSynthesis: StateFlow<ResearchSynthesis?> = _activeResearchSynthesis.asStateFlow()

    // Installed Launcher Apps
    private val _launcherApps = MutableStateFlow<List<com.example.engine.LauncherAppItem>>(emptyList())
    val launcherApps: StateFlow<List<com.example.engine.LauncherAppItem>> = _launcherApps.asStateFlow()

    private val _pinnedAppPackages = MutableStateFlow<Set<String>>(
        setOf(
            "com.google.android.dialer",
            "com.google.android.apps.messaging",
            "com.android.chrome",
            "com.android.camera",
            "com.android.settings"
        )
    )
    val pinnedAppPackages: StateFlow<Set<String>> = _pinnedAppPackages.asStateFlow()

    private val _installedApps = MutableStateFlow<List<InstalledAppInfo>>(emptyList())
    val installedApps: StateFlow<List<InstalledAppInfo>> = _installedApps.asStateFlow()

    private var inferenceJob: Job? = null
    private var telemetryPollingJob: Job? = null

    init {
        // Seed default agents and preloaded sample documentation if empty
        viewModelScope.launch(Dispatchers.IO) {
            if (repository.getAgentCount() == 0) {
                repository.insertDefaultAgents(AutonomousAgentEngine.getDefaultAgents())
            }
            if (repository.getDocumentCount() == 0) {
                seedInitialDocuments()
            }
            if (repository.getModuleCount() == 0) {
                val defaults = selfUpgradeEngine.getDefaultModules()
                for (mod in defaults) {
                    repository.saveUpgradeModule(
                        title = mod.title,
                        description = mod.description,
                        moduleType = mod.moduleType,
                        targetNeed = mod.targetNeed,
                        payload = mod.codeOrConfigPayload,
                        version = mod.version,
                        ramImpactMb = mod.ramImpactMb,
                        performanceGainPercent = mod.performanceGainPercent,
                        isAutoSynthesized = mod.isAutoSynthesized
                    )
                }
            }
            if (repository.getWallpaperCount() == 0) {
                repository.saveWallpaper(
                    title = "Cyber Neon Horizon",
                    prompt = "Cyberpunk neon city skyline at night with glowing purple and cyan holographic lights",
                    imageUrl = "img_wallpaper_cyber_1789189957353",
                    category = "Cyberpunk",
                    author = "NeuralStudio AI",
                    isDownloaded = true
                )
                repository.saveWallpaper(
                    title = "Cosmic Nebula Deep Space",
                    prompt = "Breathtaking cosmic nebula in deep space with vibrant star clusters and glowing purple and teal interstellar dust",
                    imageUrl = "img_wallpaper_nebula_1789189972753",
                    category = "Cosmic",
                    author = "DeepSpace 9",
                    isDownloaded = true
                )
            }
            loadLauncherApps()
            refreshRecommendations()
        }

        // Periodic telemetry polling
        telemetryPollingJob = viewModelScope.launch(Dispatchers.Default) {
            while (true) {
                delay(3000)
                _telemetry.value = kernelBridge.getRealHardwareTelemetry(_telemetry.value.powerGovernorMode)
            }
        }
    }

    fun loadLauncherApps() {
        val list = kernelBridge.getLauncherAppList()
        _launcherApps.value = list
        _installedApps.value = list.map { InstalledAppInfo(it.appName, it.packageName, it.isSystemApp) }
    }

    fun togglePinApp(packageName: String) {
        val current = _pinnedAppPackages.value.toMutableSet()
        if (current.contains(packageName)) {
            current.remove(packageName)
        } else {
            current.add(packageName)
        }
        _pinnedAppPackages.value = current
        kernelBridge.triggerHapticFeedback()
    }

    fun openAppDetails(packageName: String) {
        kernelBridge.openAppDetails(packageName)
    }

    fun requestUninstallApp(packageName: String) {
        kernelBridge.requestUninstallApp(packageName)
    }

    fun openDefaultHomeSettings() {
        kernelBridge.openDefaultHomeSettings()
    }

    fun openWallpaperPicker() {
        kernelBridge.openWallpaperPicker()
    }

    private suspend fun seedInitialDocuments() {
        val vivoManual = """
            Vivo Y31 Pro Hardware and Neural Engine Specifications:
            The Vivo Y31 Pro features 8 GB LPDDR4X physical RAM with dynamic Extended RAM 3.0 support.
            It is powered by an octa-core CPU with specialized big.LITTLE core pinning for on-device AI acceleration.
            The display is a 6.58-inch FHD+ IPS LCD with 90Hz adaptive refresh rate.
            Battery capacity is 5000 mAh with 18W Fast Charging and ultra-low standby sleep mode consuming <0.8% per 8 hours.
            Running Android 16 with Funtouch OS 16, it supports NNAPI 1.4 hardware tensor acceleration,
            allowing quantized local language models (INT4/INT8) to execute up to 38 tokens per second entirely on-device.
        """.trimIndent()

        val ragChunks1 = ragProcessor.chunkDocument(vivoManual)
        val summary1 = ragProcessor.generateOfflineSummary("Vivo Y31 Pro Technical Specs", vivoManual)
        repository.saveDocument(
            title = "Vivo Y31 Pro Hardware Manual",
            fileType = "MANUAL",
            rawContent = vivoManual,
            summary = summary1,
            chunks = ragChunks1
        )

        val securityDoc = """
            NeuralKernel Privacy & Encryption Architecture:
            All local knowledge base entries, conversations, and search logs are protected using AES-256-GCM hardware-backed cryptography.
            When in Zero-Telemetry Mode, the application cuts all socket connectivity, verifying that zero network packets leave the device.
            When Real-Time Retrieval is active, queries are routed through an anonymizing privacy shield that strips identifiers, cookies, and IP metadata.
            No user biometric or acoustic voice data is ever transmitted to external servers.
        """.trimIndent()

        val ragChunks2 = ragProcessor.chunkDocument(securityDoc)
        val summary2 = ragProcessor.generateOfflineSummary("NeuralKernel Security Whitepaper", securityDoc)
        repository.saveDocument(
            title = "Privacy & Encryption Whitepaper",
            fileType = "MD",
            rawContent = securityDoc,
            summary = summary2,
            chunks = ragChunks2
        )
    }

    // ==================== CHAT ACTIONS ====================

    fun sendMessage(prompt: String) {
        if (prompt.isBlank() || _isInferring.value) return

        val userPrompt = prompt.trim()
        viewModelScope.launch(Dispatchers.IO) {
            // Save User message
            repository.saveMessage(
                sender = "user",
                content = userPrompt,
                modelProfile = _selectedModel.value.displayName
            )

            // Gather attached documents for RAG
            val allDocs = documents.value
            val attachedIds = _attachedDocumentIds.value
            val targetDocs = if (attachedIds.isNotEmpty()) {
                allDocs.filter { it.id in attachedIds }
            } else {
                allDocs
            }

            val docChunks = targetDocs.map { doc ->
                val chunks = ragProcessor.chunkDocument(doc.content)
                Pair(doc.title, chunks)
            }

            _isInferring.value = true
            _streamingText.value = ""

            var lastMetrics = GenerationMetrics()
            inferenceJob = launch {
                inferenceEngine.streamInference(
                    prompt = userPrompt,
                    profile = _selectedModel.value,
                    isInternetRetrievalEnabled = _isInternetRetrievalEnabled.value,
                    attachedDocuments = docChunks
                ).collect { chunk ->
                    _streamingText.value = chunk.accumulatedText
                    _currentMetrics.value = chunk.metrics
                    lastMetrics = chunk.metrics

                    if (chunk.isDone) {
                        // Persist Assistant response
                        repository.saveMessage(
                            sender = "assistant",
                            content = chunk.accumulatedText,
                            modelProfile = _selectedModel.value.displayName,
                            tokens = chunk.metrics.totalTokens,
                            latencyMs = chunk.metrics.elapsedMs,
                            tokensPerSec = chunk.metrics.tokensPerSecond,
                            isWebRetrieved = chunk.metrics.isWebRetrieved,
                            sources = chunk.metrics.sources.joinToString(", "),
                            documentCitations = chunk.metrics.documentCitations.joinToString(" | "),
                            kernelActions = chunk.metrics.executedKernelAction ?: ""
                        )
                        _isInferring.value = false
                        _streamingText.value = ""
                    }
                }
            }
        }
    }

    fun setModelProfile(profile: ModelQuantProfile) {
        _selectedModel.value = profile
        kernelBridge.triggerHapticFeedback()
    }

    fun toggleInternetRetrieval() {
        _isInternetRetrievalEnabled.value = !_isInternetRetrievalEnabled.value
        kernelBridge.triggerHapticFeedback()
    }

    fun toggleDocumentAttachment(docId: Long) {
        val current = _attachedDocumentIds.value.toMutableSet()
        if (current.contains(docId)) {
            current.remove(docId)
        } else {
            current.add(docId)
        }
        _attachedDocumentIds.value = current
    }

    fun clearChat() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearChatHistory()
        }
    }

    // ==================== DOCUMENT RAG ACTIONS ====================

    fun addDocument(title: String, fileType: String, content: String) {
        if (title.isBlank() || content.isBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            val chunks = ragProcessor.chunkDocument(content)
            val summary = ragProcessor.generateOfflineSummary(title, content)
            repository.saveDocument(
                title = title.trim(),
                fileType = fileType.uppercase().trim(),
                rawContent = content.trim(),
                summary = summary,
                chunks = chunks
            )
            repository.logKernelAction(
                actionType = "LOCAL_RAG",
                description = "Indexed document '$title' into ${chunks.size} offline vector chunks."
            )
        }
    }

    fun deleteDocument(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteDocument(id)
        }
    }

    // ==================== AGENT ACTIONS ====================

    fun selectAgentForRun(agent: AgentEntity) {
        _selectedAgentForRun.value = agent
    }

    fun executeAgent(agent: AgentEntity, goal: String) {
        if (goal.isBlank() || _isAgentRunning.value) return
        _isAgentRunning.value = true
        _activeAgentRunSteps.value = emptyList()

        viewModelScope.launch(Dispatchers.IO) {
            val allDocs = documents.value.map { doc ->
                Pair(doc.title, ragProcessor.chunkDocument(doc.content))
            }

            val stepsList = mutableListOf<AgentExecutionStep>()
            agentEngine.executeAgentStream(agent, goal, allDocs).collect { step ->
                stepsList.add(step)
                _activeAgentRunSteps.value = stepsList.toList()
                if (step.stepType == com.example.engine.AgentStepType.RESULT) {
                    // Update agent entity stats
                    repository.updateAgent(
                        agent.copy(
                            runCount = agent.runCount + 1,
                            lastRunTimestamp = System.currentTimeMillis(),
                            lastOutput = step.message
                        )
                    )
                    repository.logKernelAction(
                        actionType = "AGENT_EXECUTION",
                        description = "Agent '${agent.name}' completed autonomous workflow for: '$goal'"
                    )
                    _isAgentRunning.value = false
                }
            }
        }
    }

    fun createCustomAgent(
        name: String,
        role: String,
        description: String,
        systemPrompt: String,
        tools: List<String>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val toolsJson = "[" + tools.joinToString(",") { "\"$it\"" } + "]"
            val newAgent = AgentEntity(
                name = name,
                role = role,
                description = description,
                systemPrompt = systemPrompt,
                toolsJson = toolsJson,
                executionMode = "AUTONOMOUS",
                iconName = "smart_toy"
            )
            repository.saveAgent(newAgent)
        }
    }

    fun deleteAgent(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAgent(id)
        }
    }

    // ==================== WEB RESEARCH & ENCRYPTION VAULT ====================

    fun performAutonomousResearch(query: String) {
        if (query.isBlank() || _isSearchingWeb.value) return
        _isSearchingWeb.value = true
        _activeResearchSynthesis.value = null

        viewModelScope.launch(Dispatchers.IO) {
            val result = webResearchEngine.performAutonomousResearch(query)
            _activeResearchSynthesis.value = result
            _isSearchingWeb.value = false

            val sourcesJson = "[" + result.sources.joinToString(",") { "\"${it.title}\"" } + "]"
            val takeawaysJson = "[" + result.keyTakeaways.joinToString(",") { "\"$it\"" } + "]"

            repository.saveResearchLog(
                query = query,
                summary = result.summary,
                sourcesJson = sourcesJson,
                keyTakeawaysJson = takeawaysJson,
                tokenUsage = result.tokensUsed,
                latencyMs = result.latencyMs
            )
            repository.logKernelAction(
                actionType = "WEB_RESEARCH",
                description = "Autonomous research on '$query' completed. Encrypted in AES-256 vault."
            )
        }
    }

    fun deleteResearchLog(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteResearchLog(id)
        }
    }

    // ==================== KERNEL & HARDWARE CONTROL ====================

    fun setPowerGovernor(mode: PowerGovernor) {
        _telemetry.value = _telemetry.value.copy(powerGovernorMode = mode)
        kernelBridge.triggerHapticFeedback()
        viewModelScope.launch(Dispatchers.IO) {
            repository.logKernelAction(
                actionType = "POWER_PROFILE",
                description = "Switched to ${mode.label} (${mode.description})."
            )
        }
    }

    fun executeRamPurge() {
        viewModelScope.launch(Dispatchers.IO) {
            val freed = kernelBridge.purgeRamMemory()
            kernelBridge.triggerHapticFeedback()
            _telemetry.value = kernelBridge.getRealHardwareTelemetry(_telemetry.value.powerGovernorMode)
            repository.logKernelAction(
                actionType = "RAM_PURGE",
                description = "Manual RAM Purge: Freed ${freed}MB RAM. Restored heap headroom.",
                ramDeltaMb = freed
            )
        }
    }

    fun launchApp(pkg: String) {
        kernelBridge.launchAppByPackage(pkg)
        viewModelScope.launch(Dispatchers.IO) {
            repository.logKernelAction(
                actionType = "APP_LAUNCH",
                description = "Kernel bridge launched package: $pkg"
            )
        }
    }

    fun openSystemSettings() {
        kernelBridge.openSystemSettings()
    }

    // ==================== VOICE TRANSCRIPTION ====================

    fun startVoiceTranscription() {
        speechTranscriber.startListening { text ->
            if (text.isNotBlank()) {
                sendMessage(text)
            }
        }
    }

    fun stopVoiceTranscription() {
        speechTranscriber.stopListening()
    }

    // ==================== SELF-UPGRADE & DYNAMIC ADAPTATION ====================

    fun synthesizeUpgrade(userNeed: String) {
        if (userNeed.isBlank() || _isSynthesizingUpgrade.value) return
        _isSynthesizingUpgrade.value = true
        _activeSynthesisResult.value = null

        viewModelScope.launch(Dispatchers.IO) {
            val result = selfUpgradeEngine.synthesizeUpgradeForNeed(userNeed, _telemetry.value)
            _activeSynthesisResult.value = result
            _isSynthesizingUpgrade.value = false

            repository.saveUpgradeModule(
                title = result.title,
                description = result.description,
                moduleType = result.moduleType,
                targetNeed = result.targetNeed,
                payload = result.codeOrConfigPayload,
                version = result.version,
                ramImpactMb = result.ramImpactMb,
                performanceGainPercent = result.performanceGainPercent,
                isAutoSynthesized = true
            )

            kernelBridge.triggerHapticFeedback()

            repository.logKernelAction(
                actionType = "KERNEL_UPGRADE",
                description = "Self-Upgrade compiled & hot-patched: '${result.title}' (${result.version}). Gain: +${result.performanceGainPercent}%."
            )

            refreshRecommendations()
        }
    }

    fun toggleUpgradeModule(id: Long, isEnabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleUpgradeModule(id, isEnabled)
            kernelBridge.triggerHapticFeedback()
            repository.logKernelAction(
                actionType = "MODULE_TOGGLE",
                description = "Upgrade module id=$id set to isEnabled=$isEnabled."
            )
        }
    }

    fun deleteUpgradeModule(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteUpgradeModule(id)
            kernelBridge.triggerHapticFeedback()
            repository.logKernelAction(
                actionType = "MODULE_DELETE",
                description = "Rollback upgrade module id=$id."
            )
            refreshRecommendations()
        }
    }

    fun refreshRecommendations() {
        _upgradeRecommendations.value = selfUpgradeEngine.analyzeUsageAndSuggestUpgrades(_telemetry.value)
    }

    // ==================== WALLPAPER STUDIO ====================

    fun generateAiWallpaper(prompt: String) {
        if (prompt.isBlank() || _isGeneratingWallpaper.value) return
        _isGeneratingWallpaper.value = true

        viewModelScope.launch(Dispatchers.IO) {
            delay(1200) // Simulate AI generation processing
            repository.saveWallpaper(
                title = "AI: ${prompt.take(20)}...",
                prompt = prompt,
                imageUrl = "img_wallpaper_cyber_1789189957353",
                category = "AI Generated",
                author = "You (Vivo NPU)",
                isDownloaded = true
            )
            _isGeneratingWallpaper.value = false
            kernelBridge.triggerHapticFeedback()
            repository.logKernelAction(
                actionType = "AI_WALLPAPER",
                description = "Generated AI wallpaper for prompt: '$prompt'"
            )
        }
    }

    fun downloadOnlineWallpaper(wallpaper: WallpaperEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveWallpaper(
                title = wallpaper.title,
                prompt = wallpaper.prompt,
                imageUrl = wallpaper.imageUrl,
                category = wallpaper.category,
                author = wallpaper.author,
                isDownloaded = true
            )
            kernelBridge.triggerHapticFeedback()
            repository.logKernelAction(
                actionType = "WALLPAPER_DOWNLOAD",
                description = "Downloaded online wallpaper: '${wallpaper.title}' by ${wallpaper.author}"
            )
        }
    }

    fun setActiveWallpaper(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setActiveWallpaper(id)
            kernelBridge.triggerHapticFeedback()
            repository.logKernelAction(
                actionType = "WALLPAPER_SET",
                description = "Set active home screen wallpaper (id=$id)."
            )
        }
    }

    fun deleteWallpaper(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteWallpaper(id)
            kernelBridge.triggerHapticFeedback()
        }
    }

    override fun onCleared() {
        super.onCleared()
        speechTranscriber.release()
        telemetryPollingJob?.cancel()
        inferenceJob?.cancel()
    }
}
