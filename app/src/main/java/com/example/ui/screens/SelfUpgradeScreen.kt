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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.UpgradeModuleEntity
import com.example.engine.NeedRecommendation
import com.example.engine.SystemHardwareTelemetry
import com.example.engine.UpgradeSynthesisResult
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

@Composable
fun SelfUpgradeScreen(
    upgradeModules: List<UpgradeModuleEntity>,
    recommendations: List<NeedRecommendation>,
    telemetry: SystemHardwareTelemetry,
    isSynthesizing: Boolean,
    activeSynthesisResult: UpgradeSynthesisResult?,
    onSynthesizeNeed: (String) -> Unit,
    onToggleModule: (Long, Boolean) -> Unit,
    onDeleteModule: (Long) -> Unit,
    onRefreshRecommendations: () -> Unit,
    modifier: Modifier = Modifier
) {
    var userNeedInput by remember { mutableStateOf("") }

    val totalActiveGain = remember(upgradeModules) {
        upgradeModules.filter { it.isEnabled }.sumOf { it.performanceGainPercent }
    }

    val totalActiveRamImpact = remember(upgradeModules) {
        upgradeModules.filter { it.isEnabled }.sumOf { it.ramImpactMb }
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
            contentPadding = PaddingValues(top = 10.dp, bottom = 28.dp)
        ) {
            // 1. Evolution Header
            item {
                EvolutionHeaderCard(
                    activeCount = upgradeModules.count { it.isEnabled },
                    totalModules = upgradeModules.size,
                    totalGain = totalActiveGain,
                    ramImpact = totalActiveRamImpact,
                    telemetry = telemetry
                )
            }

            // 2. Prompt-Driven Self-Upgrade Builder
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = NeonCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Autonomous Self-Upgrade Engine",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF064E3B))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "ZERO-CLOUD",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonEmerald
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Describe what capability, optimization, or skill you need. The OS kernel will synthesize, sandbox, compile, and hot-patch itself locally.",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = userNeedInput,
                            onValueChange = { userNeedInput = it },
                            placeholder = {
                                Text(
                                    "E.g., 'Optimize battery for long gaming', 'Add offline Python AST compiler', 'Parse SMS receipts into expenses'...",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("upgrade_need_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = CyberCardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = CyberSurfaceVariant,
                                unfocusedContainerColor = CyberSurfaceVariant
                            ),
                            shape = RoundedCornerShape(10.dp),
                            maxLines = 3
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                if (userNeedInput.isNotBlank() && !isSynthesizing) {
                                    onSynthesizeNeed(userNeedInput)
                                    userNeedInput = ""
                                }
                            },
                            enabled = userNeedInput.isNotBlank() && !isSynthesizing,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonCyan,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .testTag("synthesize_upgrade_button"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            if (isSynthesizing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = Color.Black,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Synthesizing & Compiling Module...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Icon(imageVector = Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Synthesize & Upgrade Kernel", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // 3. Real-time Compilation Logs
            if (activeSynthesisResult != null && activeSynthesisResult.synthesisLogs.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF030712)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonEmerald)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Compilation Terminal",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonEmerald
                                )
                                Text(
                                    text = "${activeSynthesisResult.version}",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = TextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            activeSynthesisResult.synthesisLogs.forEach { logLine ->
                                Text(
                                    text = logLine,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = TerminalCyan,
                                    modifier = Modifier.padding(vertical = 1.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 4. AI-Recommended Evolutions for Current Telemetry
            item {
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
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = null,
                                    tint = NeonIndigo,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "AI-Diagnosed Upgrade Recommendations",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }

                            IconButton(
                                onClick = onRefreshRecommendations,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(recommendations) { rec ->
                                RecommendationChipCard(
                                    recommendation = rec,
                                    onInstall = {
                                        onSynthesizeNeed(rec.userNeedPrompt)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // 5. Installed Modules List
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Active Kernel Extensions & Adapters (${upgradeModules.size})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            items(upgradeModules, key = { it.id }) { module ->
                UpgradeModuleItemCard(
                    module = module,
                    onToggle = { isEnabled -> onToggleModule(module.id, isEnabled) },
                    onDelete = { onDeleteModule(module.id) }
                )
            }
        }
    }
}

@Composable
fun EvolutionHeaderCard(
    activeCount: Int,
    totalModules: Int,
    totalGain: Int,
    ramImpact: Int,
    telemetry: SystemHardwareTelemetry
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
                Column {
                    Text(
                        text = "NeuralKernel OS Evolution",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Text(
                        text = "Self-Optimizing Architecture for Vivo Y31 Pro",
                        fontSize = 11.sp,
                        color = NeonCyan
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E1B4B))
                        .border(1.dp, NeonIndigo, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "v2.4-AUTONOMOUS",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFA5B4FC)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Metrics Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EvolutionStatBox(
                    title = "Active Modules",
                    value = "$activeCount / $totalModules",
                    subValue = "Hot-Patched",
                    modifier = Modifier.weight(1f)
                )
                EvolutionStatBox(
                    title = "Speedup Gain",
                    value = "+$totalGain%",
                    subValue = "Kernel Boost",
                    modifier = Modifier.weight(1f)
                )
                EvolutionStatBox(
                    title = "Memory Delta",
                    value = if (ramImpact <= 0) "${ramImpact} MB" else "+${ramImpact} MB",
                    subValue = if (ramImpact <= 0) "RAM Reclaimed" else "Allocated",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun EvolutionStatBox(
    title: String,
    value: String,
    subValue: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(CyberSurfaceVariant)
            .padding(8.dp)
    ) {
        Column {
            Text(text = title, fontSize = 9.sp, color = TextMuted)
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = NeonCyan
            )
            Text(text = subValue, fontSize = 8.sp, color = TextSecondary)
        }
    }
}

@Composable
fun RecommendationChipCard(
    recommendation: NeedRecommendation,
    onInstall: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(200.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(CyberSurfaceVariant)
            .border(1.dp, CyberCardBorder, RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = recommendation.title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1
                )
                Text(
                    text = "+${recommendation.estimatedSpeedupPercent}%",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    color = NeonEmerald
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = recommendation.reason,
                fontSize = 9.sp,
                color = TextSecondary,
                maxLines = 2,
                lineHeight = 12.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onInstall,
                colors = ButtonDefaults.buttonColors(containerColor = NeonIndigo, contentColor = Color.White),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(26.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("Synthesize & Install", fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun UpgradeModuleItemCard(
    module: UpgradeModuleEntity,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    val typeIcon = when (module.moduleType) {
        "MODEL_ADAPTER" -> Icons.Default.Psychology
        "KERNEL_EXTENSION" -> Icons.Default.Memory
        "WORKFLOW_AUTOMATION" -> Icons.Default.Speed
        else -> Icons.Default.Extension
    }

    val typeColor = when (module.moduleType) {
        "MODEL_ADAPTER" -> NeonIndigo
        "KERNEL_EXTENSION" -> NeonCyan
        "WORKFLOW_AUTOMATION" -> NeonEmerald
        else -> NeonPurple
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CyberSurface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (module.isEnabled) CyberCardBorder else Color(0xFF1E293B)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(typeColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = typeIcon,
                            contentDescription = null,
                            tint = typeColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = module.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (module.isEnabled) TextPrimary else TextMuted
                        )
                        Text(
                            text = "${module.version} • ${module.moduleType.replace("_", " ")}",
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            color = typeColor
                        )
                    }
                }

                Switch(
                    checked = module.isEnabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = NeonCyan,
                        checkedTrackColor = NeonCyan.copy(alpha = 0.4f),
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = CyberSurfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = module.description,
                fontSize = 11.sp,
                color = TextSecondary,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Footer metrics & delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(CyberSurfaceVariant)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "+${module.performanceGainPercent}% Speedup",
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = NeonEmerald
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(CyberSurfaceVariant)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (module.ramImpactMb <= 0) "${module.ramImpactMb} MB RAM" else "+${module.ramImpactMb} MB RAM",
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = if (module.ramImpactMb <= 0) NeonCyan else TextSecondary
                        )
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Uninstall Module",
                        tint = TextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
