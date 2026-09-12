package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.AgentEntity
import com.example.engine.AgentExecutionStep
import com.example.engine.AgentStepType
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
import com.example.ui.theme.TerminalGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AgentsScreen(
    agents: List<AgentEntity>,
    activeSteps: List<AgentExecutionStep>,
    isAgentRunning: Boolean,
    onExecuteAgent: (agent: AgentEntity, goal: String) -> Unit,
    onCreateAgent: (name: String, role: String, desc: String, prompt: String, tools: List<String>) -> Unit,
    onDeleteAgent: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedAgentToRun by remember { mutableStateOf<AgentEntity?>(null) }
    var customGoalInput by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDarkBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Autonomous AI Agents",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Local Tool Loops • Zero External Plugins Needed",
                        fontSize = 11.sp,
                        color = NeonCyan
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E1B4B))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${agents.size} Active",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonIndigo
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Live Execution Pipeline Console (if steps are present)
            if (activeSteps.isNotEmpty() || isAgentRunning) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isAgentRunning) NeonCyan else NeonEmerald)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isAgentRunning) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = NeonCyan,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = NeonEmerald,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isAgentRunning) "Autonomous Execution in Progress..." else "Execution Completed",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isAgentRunning) NeonCyan else NeonEmerald
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Steps Log
                        LazyColumn(
                            modifier = Modifier.height(180.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(activeSteps) { step ->
                                AgentStepRow(step = step)
                            }
                        }
                    }
                }
            }

            // Agents Catalog
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(agents, key = { it.id }) { agent ->
                    AgentCard(
                        agent = agent,
                        isRunning = isAgentRunning,
                        onRun = {
                            selectedAgentToRun = agent
                            customGoalInput = when (agent.id.toInt()) {
                                1 -> "Audit Vivo Y31 Pro 8GB RAM headroom, calibrate thermal governor, and purge background cache."
                                2 -> "Perform autonomous research on latest on-device LLM INT4 quantization breakthroughs."
                                3 -> "Analyze all local documents and extract privacy & hardware performance guidelines."
                                4 -> "Check device battery drain rate, inspect installed apps, and open device settings."
                                else -> "Execute autonomous goal with on-device tools."
                            }
                        },
                        onDelete = { onDeleteAgent(agent.id) }
                    )
                }
            }
        }

        // FAB to build custom agent
        FloatingActionButton(
            onClick = { showCreateDialog = true },
            containerColor = NeonIndigo,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("create_agent_fab")
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Create Custom Agent")
        }

        // Execute Agent Modal
        if (selectedAgentToRun != null) {
            ExecuteAgentDialog(
                agent = selectedAgentToRun!!,
                initialGoal = customGoalInput,
                onDismiss = { selectedAgentToRun = null },
                onExecute = { goal ->
                    onExecuteAgent(selectedAgentToRun!!, goal)
                    selectedAgentToRun = null
                }
            )
        }

        // Create Custom Agent Modal
        if (showCreateDialog) {
            CreateAgentDialog(
                onDismiss = { showCreateDialog = false },
                onConfirm = { name, role, desc, prompt, tools ->
                    onCreateAgent(name, role, desc, prompt, tools)
                    showCreateDialog = false
                }
            )
        }
    }
}

@Composable
fun AgentStepRow(step: AgentExecutionStep) {
    val (typeColor, badgeText) = when (step.stepType) {
        AgentStepType.THOUGHT -> Pair(NeonPurple, "THOUGHT")
        AgentStepType.ACTION -> Pair(NeonCyan, "ACTION [${step.toolName ?: "KERNEL"}]")
        AgentStepType.OBSERVATION -> Pair(TerminalCyan, "OBSERVATION")
        AgentStepType.RESULT -> Pair(NeonEmerald, "RESULT")
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(CyberSurfaceVariant)
            .padding(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${step.stepNumber}. $badgeText",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = typeColor
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = step.message,
            fontSize = 11.sp,
            color = TextPrimary,
            lineHeight = 15.sp
        )
        if (step.toolOutput != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = step.toolOutput,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun AgentCard(
    agent: AgentEntity,
    isRunning: Boolean,
    onRun: () -> Unit,
    onDelete: () -> Unit
) {
    val icon = when (agent.iconName) {
        "battery" -> Icons.Default.BatteryChargingFull
        "travel_explore" -> Icons.Default.Language
        "description" -> Icons.Default.Description
        "terminal" -> Icons.Default.Terminal
        else -> Icons.Default.SmartToy
    }

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
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberSurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = agent.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = agent.role,
                            fontSize = 10.sp,
                            color = NeonIndigo
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = onRun,
                        enabled = !isRunning,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Run",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Execute", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    if (agent.id > 4) { // Custom agents can be deleted
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = agent.description,
                fontSize = 11.sp,
                color = TextSecondary,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Tools: ${agent.toolsJson}",
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    color = NeonPurple
                )
                Text(
                    text = "Runs: ${agent.runCount}",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
fun ExecuteAgentDialog(
    agent: AgentEntity,
    initialGoal: String,
    onDismiss: () -> Unit,
    onExecute: (String) -> Unit
) {
    var goalText by remember { mutableStateOf(initialGoal) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CyberSurface,
        title = {
            Column {
                Text("Launch ${agent.name}", color = NeonCyan, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("Autonomous Reasoning & Tool Execution Loop", fontSize = 11.sp, color = TextSecondary)
            }
        },
        text = {
            Column {
                Text("Specify execution objective or parameters:", fontSize = 12.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = goalText,
                    onValueChange = { goalText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = CyberCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onExecute(goalText) },
                enabled = goalText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
            ) {
                Text("Start Autonomous Loop")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
fun CreateAgentDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, role: String, desc: String, prompt: String, tools: List<String>) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var systemPrompt by remember { mutableStateOf("") }
    val selectedTools = remember { mutableStateOf(mutableSetOf("DEVICE_CONTROL", "LOCAL_RAG")) }

    val availableTools = listOf("DEVICE_CONTROL", "LOCAL_RAG", "WEB_SEARCH", "BATTERY_OPTIMIZER", "APP_LAUNCH")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CyberSurface,
        title = {
            Text("Build Custom On-Device AI Agent", color = NeonIndigo, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        },
        text = {
            LazyColumn(
                modifier = Modifier.height(300.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Agent Name", color = TextSecondary) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonIndigo,
                            unfocusedBorderColor = CyberCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }
                item {
                    OutlinedTextField(
                        value = role,
                        onValueChange = { role = it },
                        label = { Text("Role / Specialty", color = TextSecondary) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonIndigo,
                            unfocusedBorderColor = CyberCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }
                item {
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Description", color = TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonIndigo,
                            unfocusedBorderColor = CyberCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }
                item {
                    OutlinedTextField(
                        value = systemPrompt,
                        onValueChange = { systemPrompt = it },
                        label = { Text("System Directives / Prompt", color = TextSecondary) },
                        modifier = Modifier.height(90.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonIndigo,
                            unfocusedBorderColor = CyberCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }
                item {
                    Text("Select Allowed Tools:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                    availableTools.forEach { tool ->
                        val isChecked = selectedTools.value.contains(tool)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isChecked) Color(0xFF1E1B4B) else CyberSurfaceVariant)
                                .clickable {
                                    val current = selectedTools.value.toMutableSet()
                                    if (isChecked) current.remove(tool) else current.add(tool)
                                    selectedTools.value = current
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "• $tool",
                                fontSize = 11.sp,
                                color = if (isChecked) NeonCyan else TextSecondary,
                                fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(name, role, desc, systemPrompt, selectedTools.value.toList())
                },
                enabled = name.isNotBlank() && role.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = NeonIndigo, contentColor = Color.White)
            ) {
                Text("Assemble & Commit Agent")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
