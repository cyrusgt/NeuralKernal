package com.example.data.repository

import com.example.data.local.CryptoUtils
import com.example.data.local.dao.AgentDao
import com.example.data.local.dao.ChatDao
import com.example.data.local.dao.DocumentDao
import com.example.data.local.dao.KernelLogDao
import com.example.data.local.dao.ResearchDao
import com.example.data.local.dao.UpgradeDao
import com.example.data.local.dao.WallpaperDao
import com.example.data.local.entity.AgentEntity
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.DocumentEntity
import com.example.data.local.entity.KernelLogEntity
import com.example.data.local.entity.ResearchLogEntity
import com.example.data.local.entity.UpgradeModuleEntity
import com.example.data.local.entity.WallpaperEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NeuralRepository(
    private val chatDao: ChatDao,
    private val documentDao: DocumentDao,
    private val agentDao: AgentDao,
    private val researchDao: ResearchDao,
    private val kernelLogDao: KernelLogDao,
    private val upgradeDao: UpgradeDao,
    private val wallpaperDao: WallpaperDao
) {
    // Chat stream with automatic on-device decryption
    val allMessages: Flow<List<ChatMessageEntity>> = chatDao.getAllMessages().map { list ->
        list.map { msg ->
            if (msg.isEncrypted) {
                msg.copy(content = CryptoUtils.decrypt(msg.content))
            } else {
                msg
            }
        }
    }

    suspend fun saveMessage(
        sender: String,
        content: String,
        modelProfile: String,
        tokens: Int = 0,
        latencyMs: Long = 0,
        tokensPerSec: Double = 0.0,
        isWebRetrieved: Boolean = false,
        sources: String = "",
        documentCitations: String = "",
        kernelActions: String = ""
    ): Long {
        val encryptedContent = CryptoUtils.encrypt(content)
        val entity = ChatMessageEntity(
            sender = sender,
            content = encryptedContent,
            isEncrypted = true,
            modelProfile = modelProfile,
            tokensGenerated = tokens,
            latencyMs = latencyMs,
            tokensPerSec = tokensPerSec,
            isWebRetrieved = isWebRetrieved,
            sources = sources,
            documentCitations = documentCitations,
            kernelActionsExecuted = kernelActions
        )
        return chatDao.insertMessage(entity)
    }

    suspend fun clearChatHistory() = chatDao.clearHistory()
    suspend fun deleteMessage(id: Long) = chatDao.deleteMessageById(id)

    // Documents
    val allDocuments: Flow<List<DocumentEntity>> = documentDao.getAllDocuments().map { list ->
        list.map { doc ->
            if (doc.isEncrypted) {
                doc.copy(
                    content = CryptoUtils.decrypt(doc.content),
                    summary = CryptoUtils.decrypt(doc.summary)
                )
            } else {
                doc
            }
        }
    }

    suspend fun saveDocument(
        title: String,
        fileType: String,
        rawContent: String,
        summary: String,
        chunks: List<String>
    ): Long {
        val encryptedContent = CryptoUtils.encrypt(rawContent)
        val encryptedSummary = CryptoUtils.encrypt(summary)
        // Store chunks serialized
        val chunksJson = "[" + chunks.joinToString(",") { "\"" + it.replace("\"", "\\\"").replace("\n", "\\n") + "\"" } + "]"
        val entity = DocumentEntity(
            title = title,
            fileType = fileType,
            content = encryptedContent,
            summary = encryptedSummary,
            chunkCount = chunks.size,
            chunksJson = chunksJson,
            wordCount = rawContent.split("\\s+".toRegex()).size,
            isEncrypted = true
        )
        return documentDao.insertDocument(entity)
    }

    suspend fun deleteDocument(id: Long) = documentDao.deleteDocumentById(id)
    suspend fun getDocumentCount(): Int = documentDao.getDocumentCount()

    // Agents
    val allAgents: Flow<List<AgentEntity>> = agentDao.getAllAgents()

    suspend fun saveAgent(agent: AgentEntity) = agentDao.insertAgent(agent)
    suspend fun updateAgent(agent: AgentEntity) = agentDao.updateAgent(agent)
    suspend fun deleteAgent(id: Long) = agentDao.deleteAgentById(id)
    suspend fun getAgentCount(): Int = agentDao.getAgentCount()
    suspend fun insertDefaultAgents(agents: List<AgentEntity>) = agentDao.insertAll(agents)

    // Research logs
    val allResearchLogs: Flow<List<ResearchLogEntity>> = researchDao.getAllResearchLogs().map { list ->
        list.map { log ->
            if (log.isEncrypted) {
                log.copy(summary = CryptoUtils.decrypt(log.summary))
            } else {
                log
            }
        }
    }

    suspend fun saveResearchLog(
        query: String,
        summary: String,
        sourcesJson: String,
        keyTakeawaysJson: String,
        tokenUsage: Int,
        latencyMs: Long
    ): Long {
        val entity = ResearchLogEntity(
            query = query,
            summary = CryptoUtils.encrypt(summary),
            sourceUrlsJson = sourcesJson,
            keyTakeawaysJson = keyTakeawaysJson,
            isEncrypted = true,
            tokenUsage = tokenUsage,
            retrievalLatencyMs = latencyMs
        )
        return researchDao.insertResearchLog(entity)
    }

    suspend fun deleteResearchLog(id: Long) = researchDao.deleteLogById(id)
    suspend fun clearResearchLogs() = researchDao.clearAll()

    // Kernel logs
    val allKernelLogs: Flow<List<KernelLogEntity>> = kernelLogDao.getAllKernelLogs()

    suspend fun logKernelAction(
        actionType: String,
        description: String,
        ramDeltaMb: Int = 0,
        batteryImpactWatts: Double = 0.0,
        status: String = "SUCCESS"
    ): Long {
        val entity = KernelLogEntity(
            actionType = actionType,
            description = description,
            ramDeltaMb = ramDeltaMb,
            batteryImpactEstimateMilliWatts = batteryImpactWatts,
            status = status
        )
        return kernelLogDao.insertLog(entity)
    }

    suspend fun clearKernelLogs() = kernelLogDao.clearLogs()

    // ==================== SELF-UPGRADE & DYNAMIC EXTENSIONS ====================

    val allUpgradeModules: Flow<List<UpgradeModuleEntity>> = upgradeDao.getAllUpgradeModules().map { list ->
        list.map { mod ->
            mod.copy(codeOrConfigPayload = CryptoUtils.decrypt(mod.codeOrConfigPayload))
        }
    }

    val activeUpgradeModules: Flow<List<UpgradeModuleEntity>> = upgradeDao.getActiveUpgradeModules().map { list ->
        list.map { mod ->
            mod.copy(codeOrConfigPayload = CryptoUtils.decrypt(mod.codeOrConfigPayload))
        }
    }

    suspend fun saveUpgradeModule(
        title: String,
        description: String,
        moduleType: String,
        targetNeed: String,
        payload: String,
        version: String,
        ramImpactMb: Int,
        performanceGainPercent: Int,
        isAutoSynthesized: Boolean = true
    ): Long {
        val encryptedPayload = CryptoUtils.encrypt(payload)
        val entity = UpgradeModuleEntity(
            title = title,
            description = description,
            moduleType = moduleType,
            targetNeed = targetNeed,
            codeOrConfigPayload = encryptedPayload,
            version = version,
            ramImpactMb = ramImpactMb,
            performanceGainPercent = performanceGainPercent,
            isEnabled = true,
            isAutoSynthesized = isAutoSynthesized
        )
        return upgradeDao.insertUpgradeModule(entity)
    }

    suspend fun toggleUpgradeModule(id: Long, isEnabled: Boolean) = upgradeDao.toggleUpgradeModule(id, isEnabled)
    suspend fun deleteUpgradeModule(id: Long) = upgradeDao.deleteUpgradeModule(id)
    suspend fun getModuleCount(): Int = upgradeDao.getModuleCount()

    // ==================== WALLPAPERS ====================

    val allWallpapers: Flow<List<WallpaperEntity>> = wallpaperDao.getAllWallpapers()
    val downloadedWallpapers: Flow<List<WallpaperEntity>> = wallpaperDao.getDownloadedWallpapers()

    suspend fun saveWallpaper(
        title: String,
        prompt: String,
        imageUrl: String,
        category: String,
        author: String,
        isDownloaded: Boolean = true
    ): Long {
        val entity = WallpaperEntity(
            title = title,
            prompt = prompt,
            imageUrl = imageUrl,
            category = category,
            author = author,
            isDownloaded = isDownloaded
        )
        return wallpaperDao.insertWallpaper(entity)
    }

    suspend fun setActiveWallpaper(id: Long) {
        wallpaperDao.clearActiveWallpapers()
        wallpaperDao.setActiveWallpaper(id)
    }

    suspend fun deleteWallpaper(id: Long) = wallpaperDao.deleteWallpaper(id)
    suspend fun getWallpaperCount(): Int = wallpaperDao.getWallpaperCount()
}
