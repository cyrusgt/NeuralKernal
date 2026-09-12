package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.DocumentEntity
import com.example.engine.GenerationMetrics
import com.example.engine.ModelQuantProfile
import com.example.engine.SpeechRecognitionState
import com.example.engine.SystemHardwareTelemetry
import com.example.ui.components.AudioWaveformVisualizer
import com.example.ui.components.HardwareTelemetryBar
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberDarkBg
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.NeonRose
import com.example.ui.theme.TerminalCyan
import com.example.ui.theme.TerminalGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    messages: List<ChatMessageEntity>,
    documents: List<DocumentEntity>,
    attachedDocIds: Set<Long>,
    selectedModel: ModelQuantProfile,
    isInternetRetrievalEnabled: Boolean,
    isInferring: Boolean,
    streamingText: String,
    currentMetrics: GenerationMetrics,
    telemetry: SystemHardwareTelemetry,
    speechState: SpeechRecognitionState,
    onSendMessage: (String) -> Unit,
    onSelectModel: (ModelQuantProfile) -> Unit,
    onToggleInternetRetrieval: () -> Unit,
    onToggleDocAttachment: (Long) -> Unit,
    onQuickRamPurge: () -> Unit,
    onStartVoice: () -> Unit,
    onStopVoice: () -> Unit,
    onClearChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    var modelMenuExpanded by remember { mutableStateOf(false) }
    var docAttachSheetOpen by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    // Scroll to bottom on new message or streaming
    LaunchedEffect(messages.size, streamingText.length) {
        if (messages.isNotEmpty() || streamingText.isNotEmpty()) {
            val targetIndex = (messages.size + if (streamingText.isNotEmpty()) 1 else 0) - 1
            if (targetIndex >= 0) {
                listState.animateScrollToItem(targetIndex)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDarkBg)
    ) {
        // 1. Live Hardware Telemetry HUD
        HardwareTelemetryBar(
            telemetry = telemetry,
            currentTokensPerSec = if (isInferring) currentMetrics.tokensPerSecond else 0.0,
            isInferring = isInferring,
            onQuickPurgeClick = onQuickRamPurge,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )

        // 2. Model Quantization Profile & Privacy Shield Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Model Selector Dropdown Button
            Box {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberSurfaceVariant)
                        .border(1.dp, CyberCardBorder, RoundedCornerShape(8.dp))
                        .clickable { modelMenuExpanded = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = "Model Profile",
                        tint = NeonCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = selectedModel.displayName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                DropdownMenu(
                    expanded = modelMenuExpanded,
                    onDismissRequest = { modelMenuExpanded = false },
                    modifier = Modifier.background(CyberSurface)
                ) {
                    ModelQuantProfile.values().forEach { profile ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(profile.displayName, fontWeight = FontWeight.Bold, color = NeonCyan)
                                    Text("${profile.quantization} • ${profile.ramAllocatedMb}MB RAM • ${profile.powerEfficiencyScore}", fontSize = 10.sp, color = TextSecondary)
                                }
                            },
                            onClick = {
                                onSelectModel(profile)
                                modelMenuExpanded = false
                            }
                        )
                    }
                }
            }

            // Real-Time Internet Retrieval Toggle (Privacy Shield)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isInternetRetrievalEnabled) Color(0xFF1E1B4B) else Color(0xFF064E3B))
                    .border(1.dp, if (isInternetRetrievalEnabled) NeonIndigo else NeonEmerald, RoundedCornerShape(8.dp))
                    .clickable { onToggleInternetRetrieval() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = if (isInternetRetrievalEnabled) Icons.Default.Language else Icons.Default.Shield,
                    contentDescription = "Privacy Shield",
                    tint = if (isInternetRetrievalEnabled) NeonIndigo else NeonEmerald,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isInternetRetrievalEnabled) "Encrypted Web ON" else "Local Shield 100%",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isInternetRetrievalEnabled) Color(0xFFC7D2FE) else NeonEmerald
                )
            }

            // Clear Chat Action
            IconButton(
                onClick = onClearChat,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = "Clear Terminal History",
                    tint = TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // 3. Document Attachment Filter Row (Offline RAG)
        if (documents.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item {
                    Text(
                        text = "RAG Context:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
                items(documents) { doc ->
                    val isAttached = attachedDocIds.contains(doc.id)
                    FilterChip(
                        selected = isAttached,
                        onClick = { onToggleDocAttachment(doc.id) },
                        label = {
                            Text(
                                text = doc.title,
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonIndigo,
                            selectedLabelColor = Color.White,
                            containerColor = CyberSurfaceVariant,
                            labelColor = TextSecondary
                        )
                    )
                }
            }
        }

        // 4. Chat Messages Terminal Stream
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (messages.isEmpty() && streamingText.isEmpty()) {
                item {
                    EmptyChatTerminalBanner(
                        onQuickPrompt = { prompt ->
                            inputText = prompt
                            onSendMessage(prompt)
                        }
                    )
                }
            }

            items(messages, key = { it.id }) { msg ->
                ChatMessageItem(message = msg)
            }

            // Live streaming response item
            if (streamingText.isNotEmpty()) {
                item {
                    StreamingResponseCard(
                        text = streamingText,
                        metrics = currentMetrics,
                        modelProfile = selectedModel.displayName
                    )
                }
            }
        }

        // 5. Speech Recognition Waveform Banner (if active)
        AnimatedVisibility(visible = speechState.isListening) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E1B4B))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AudioWaveformVisualizer(isRecording = true, rmsLevel = speechState.rmsDb)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (speechState.recognizedText.isNotEmpty()) speechState.recognizedText else "Listening (Offline Whisper Engine)...",
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }

                IconButton(
                    onClick = onStopVoice,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(NeonRose)
                ) {
                    Icon(
                        imageVector = Icons.Default.MicOff,
                        contentDescription = "Stop Speech",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // 6. Input Control Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CyberSurface)
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Voice Dictation Button
            IconButton(
                onClick = {
                    if (speechState.isListening) onStopVoice() else onStartVoice()
                },
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (speechState.isListening) NeonRose else CyberSurfaceVariant)
                    .testTag("voice_dictation_button")
            ) {
                Icon(
                    imageVector = if (speechState.isListening) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = "Offline Voice to Text",
                    tint = if (speechState.isListening) Color.White else NeonCyan
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Main Text Input Field
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = {
                    Text(
                        "Command NeuralKernel, query local docs, or purge RAM...",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_field"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = CyberCardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = NeonCyan,
                    focusedContainerColor = CyberSurfaceVariant,
                    unfocusedContainerColor = CyberSurfaceVariant
                ),
                shape = RoundedCornerShape(20.dp),
                maxLines = 4
            )

            Spacer(modifier = Modifier.width(6.dp))

            // Send Prompt Button
            IconButton(
                onClick = {
                    if (inputText.isNotBlank() && !isInferring) {
                        val text = inputText
                        inputText = ""
                        onSendMessage(text)
                    }
                },
                enabled = inputText.isNotBlank() && !isInferring,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (inputText.isNotBlank() && !isInferring) NeonCyan else CyberSurfaceVariant)
                    .testTag("send_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send prompt",
                    tint = if (inputText.isNotBlank() && !isInferring) Color.Black else TextMuted
                )
            }
        }
    }
}

@Composable
fun ChatMessageItem(message: ChatMessageEntity) {
    val isUser = message.sender == "user"
    val timeStr = remember(message.timestamp) {
        SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(message.timestamp))
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        // Sender Badge & Timestamp
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Text(
                text = if (isUser) "OPERATOR (Local)" else "NEURAL KERNEL [${message.modelProfile}]",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = if (isUser) NeonIndigo else NeonCyan
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = timeStr,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                color = TextMuted
            )
        }

        // Message Bubble
        Card(
            modifier = Modifier
                .widthIn(max = 320.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                ),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) CyberSurfaceVariant else CyberSurface
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isUser) NeonIndigo.copy(alpha = 0.5f) else CyberCardBorder
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Executed Kernel Action Callout
                if (message.kernelActionsExecuted.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, NeonEmerald.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                            .padding(6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "Kernel Action",
                                tint = NeonEmerald,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = message.kernelActionsExecuted,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = NeonEmerald,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Message Text
                Text(
                    text = message.content,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = TextPrimary
                )

                // Document Citations / RAG sources
                if (message.documentCitations.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "📄 Local RAG Citation: ${message.documentCitations}",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = TerminalCyan
                    )
                }

                // Web Sources
                if (message.isWebRetrieved && message.sources.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "🌐 Encrypted Web Source: ${message.sources}",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFFC7D2FE)
                    )
                }

                // Assistant Performance Telemetry
                if (!isUser && message.tokensGenerated > 0) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${message.tokensGenerated} tokens • ${message.latencyMs}ms",
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            color = TextMuted
                        )
                        Text(
                            text = "${String.format(Locale.US, "%.1f", message.tokensPerSec)} tok/s",
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = NeonEmerald
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StreamingResponseCard(
    text: String,
    metrics: GenerationMetrics,
    modelProfile: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "NEURAL KERNEL INFERENCE [$modelProfile]",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = NeonCyan
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(NeonCyan)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Card(
            modifier = Modifier
                .widthIn(max = 320.dp)
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.7f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (metrics.executedKernelAction != null) {
                    Text(
                        text = "⚡ ${metrics.executedKernelAction}",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = NeonEmerald,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Text(
                    text = text + " █",
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Allocated: ${metrics.ramUsedMb}MB RAM",
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        color = TextMuted
                    )
                    Text(
                        text = "${String.format(Locale.US, "%.1f", metrics.tokensPerSecond)} tok/s",
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyChatTerminalBanner(
    onQuickPrompt: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Terminal,
                contentDescription = "Terminal",
                tint = NeonCyan,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "On-Device Neural OS Kernel",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Optimized for Vivo Y31 Pro (8GB RAM) & Android 16.\nZero cloud dependency • Local AES-256 Storage",
                fontSize = 11.sp,
                color = TextSecondary,
                lineHeight = 16.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Quick Execution Presets:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = NeonIndigo
            )
            Spacer(modifier = Modifier.height(8.dp))

            val presets = listOf(
                "Audit Vivo Y31 Pro memory and purge RAM",
                "Summarize confidential documents via local RAG",
                "Execute autonomous web research on Android 16",
                "Explain local INT4 quantization & battery governor"
            )

            presets.forEach { preset ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberSurfaceVariant)
                        .clickable { onQuickPrompt(preset) }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = preset,
                        fontSize = 11.sp,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}
