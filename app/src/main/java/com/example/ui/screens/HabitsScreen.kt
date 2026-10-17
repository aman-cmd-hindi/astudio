package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.ExternalTrackerType
import com.example.model.HabitItem
import com.example.model.TrackerLink
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.viewmodel.FocusViewModel

@Composable
fun HabitsScreen(
    viewModel: FocusViewModel,
    modifier: Modifier = Modifier
) {
    val habits by viewModel.habits.collectAsState()
    val streak by viewModel.streak.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val cloudSyncStatus by viewModel.cloudSyncStatus.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newCategory by remember { mutableStateOf("Mindfulness") }
    var newReward by remember { mutableIntStateOf(2) }

    var habitToLink by remember { mutableStateOf<HabitItem?>(null) }
    var selectedTrackerType by remember { mutableStateOf(ExternalTrackerType.GOOGLE_FITBIT) }
    var trackerGoalText by remember { mutableStateOf("10,000 steps") }

    var showImportDialog by remember { mutableStateOf(false) }
    var importFileText by remember { mutableStateOf("") }

    val completedCount = habits.count { it.isCompletedToday }
    val totalCount = habits.size
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Sub-header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(
                        imageVector = Icons.Filled.SelfImprovement,
                        contentDescription = null,
                        tint = StarGoldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "DISCIPLINE & RHYTHM",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                        color = StarGoldPrimary
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Habit Tracker",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = OnSurface
                    )
                    Surface(
                        shape = CircleShape,
                        color = SurfaceContainerHigh,
                        modifier = Modifier.clickable { viewModel.syncWithSupabase() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = if (currentUser != null) Icons.Filled.CloudDone else Icons.Filled.CloudQueue,
                                contentDescription = "Cloud Persistence",
                                tint = if (currentUser != null) EmeraldTertiary else OnSurfaceVariant,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = if (currentUser != null) "Supabase Sync" else "Local Only",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = if (currentUser != null) EmeraldTertiary else OnSurfaceVariant
                            )
                        }
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { showImportDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Filled.AttachFile, contentDescription = "Import Habit File", tint = StarGoldPrimary, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("File", style = MaterialTheme.typography.labelSmall, color = StarGoldPrimary)
                }

                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = StarGoldPrimary),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Habit", tint = OnPrimary, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("New", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = OnPrimary)
                }
            }
        }

        // Overview Summary Card
        GlassCard(cornerRadius = 24.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "TODAY'S COMPLETION",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = OnSurfaceVariant
                        )
                        Text(
                            text = "$completedCount of $totalCount Habits Done",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = OnSurface
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = if (completedCount == totalCount && totalCount > 0) EmeraldTertiaryContainer.copy(alpha = 0.3f) else SurfaceContainerHigh
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = if (completedCount == totalCount && totalCount > 0) Icons.Filled.CheckCircle else Icons.Filled.Schedule,
                                contentDescription = null,
                                tint = if (completedCount == totalCount && totalCount > 0) EmeraldTertiary else StarGoldPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (completedCount == totalCount && totalCount > 0) "All Complete!" else "${(progress * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (completedCount == totalCount && totalCount > 0) EmeraldTertiary else StarGoldPrimary
                            )
                        }
                    }
                }

                LinearProgressIndicator(
                    progress = { progress },
                    color = EmeraldTertiary,
                    trackColor = SurfaceContainerLowest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Active Streak: ${streak.currentStreakDays} days clean",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                    Text(
                        text = "+${habits.filter { it.isCompletedToday }.sumOf { it.starReward }} Stars earned today",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = StarGoldPrimary
                    )
                }
            }
        }

        // External Trackers Integration Hub Card
        GlassCard(
            cornerRadius = 20.dp,
            borderColor = IrisPurpleSecondary.copy(alpha = 0.4f)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(IrisPurpleSecondaryContainer.copy(alpha = 0.4f))
                        ) {
                            Icon(Icons.Filled.Sync, contentDescription = null, tint = IrisPurpleSecondary, modifier = Modifier.size(18.dp))
                        }
                        Column {
                            Text(
                                text = "External Trackers Integration",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = OnSurface
                            )
                            Text(
                                text = "Google Fitbit • Strava • Vivo • Phone Tracker",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.syncExternalTrackers() },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldTertiary),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Sync", tint = OnPrimary, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Sync All", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = OnPrimary)
                    }
                }

                // Tracker badges
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TrackerPill("Fitbit", Color(0xFF00B0B9), Icons.Filled.DirectionsWalk, Modifier.weight(1f))
                    TrackerPill("Strava", Color(0xFFFC4C02), Icons.Filled.DirectionsBike, Modifier.weight(1f))
                    TrackerPill("Vivo", Color(0xFF4169E1), Icons.Filled.Favorite, Modifier.weight(1f))
                    TrackerPill("Phone", Color(0xFF4CAF50), Icons.Filled.PhoneAndroid, Modifier.weight(1f))
                }
            }
        }

        // Habit Cards List
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "DAILY FOCUS DISCIPLINES",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.8.sp),
                color = OnSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            habits.forEach { habit ->
                HabitCardItem(
                    habit = habit,
                    onToggle = { viewModel.toggleHabit(habit.id) },
                    onDelete = { viewModel.deleteHabit(habit.id) },
                    onLinkTracker = {
                        habitToLink = habit
                        selectedTrackerType = habit.linkedTracker?.trackerType ?: ExternalTrackerType.GOOGLE_FITBIT
                        trackerGoalText = habit.linkedTracker?.metricGoal ?: "10,000 steps"
                    }
                )
            }
        }

        Spacer(Modifier.height(80.dp))
    }

    // Modal: Link External Tracker
    if (habitToLink != null) {
        val targetHabit = habitToLink!!
        Dialog(onDismissRequest = { habitToLink = null }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = SurfaceContainerHigh,
                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderDefault),
                shadowElevation = 24.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Connect External Tracker",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = OnSurface
                    )
                    Text(
                        text = "Link \"${targetHabit.title}\" to an activity tracker to auto-verify completions via device health telemetry.",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )

                    Text("Select Fitness Tracker", style = MaterialTheme.typography.labelMedium, color = OnSurfaceVariant)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        ExternalTrackerType.values().forEach { type ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (selectedTrackerType == type) SurfaceContainerHighest else SurfaceContainer,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (selectedTrackerType == type) Color(type.brandColorHex) else GlassBorderDefault
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedTrackerType = type
                                        if (trackerGoalText.isBlank()) {
                                            trackerGoalText = when (type) {
                                                ExternalTrackerType.GOOGLE_FITBIT -> "10,000 steps"
                                                ExternalTrackerType.STRAVA -> "30m activity"
                                                ExternalTrackerType.VIVO_TRACKER -> "2,500 mL & Vitals"
                                                ExternalTrackerType.PHONE_TRACKER -> "6,000 steps"
                                                else -> "Daily Goal"
                                            }
                                        }
                                    }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .clip(CircleShape)
                                                .background(Color(type.brandColorHex))
                                        )
                                        Text(
                                            text = type.displayName,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                            color = OnSurface
                                        )
                                    }
                                    if (selectedTrackerType == type) {
                                        Icon(Icons.Filled.Check, contentDescription = null, tint = EmeraldTertiary, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = trackerGoalText,
                        onValueChange = { trackerGoalText = it },
                        label = { Text("Metric Benchmark / Goal") },
                        placeholder = { Text("e.g. 10,000 steps or 30 mins") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = StarGoldPrimary,
                            unfocusedBorderColor = GlassBorderDefault,
                            focusedLabelColor = StarGoldPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (targetHabit.linkedTracker != null) {
                            TextButton(
                                onClick = {
                                    viewModel.unlinkHabitFromTracker(targetHabit.id)
                                    habitToLink = null
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Unlink", color = ErrorRed)
                            }
                        }
                        TextButton(
                            onClick = { habitToLink = null },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel", color = OnSurfaceVariant)
                        }
                        Button(
                            onClick = {
                                viewModel.linkHabitToTracker(targetHabit.id, selectedTrackerType, trackerGoalText.trim().ifEmpty { "Daily Goal" })
                                habitToLink = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StarGoldPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Save", color = OnPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Modal: Import Habit Tracker File / Template
    if (showImportDialog) {
        Dialog(onDismissRequest = { showImportDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = SurfaceContainerHigh,
                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderDefault),
                shadowElevation = 24.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Import Habit Tracker File",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = OnSurface
                    )
                    Text(
                        text = "Paste your habit tracker JSON schema or load pre-linked tracker routines (Fitbit, Strava, Vivo) to sync with Supabase.",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )

                    OutlinedTextField(
                        value = importFileText,
                        onValueChange = { importFileText = it },
                        label = { Text("Habit Tracker JSON Content") },
                        placeholder = { Text("[{\"title\": \"10,000 Steps\", \"category\": \"Fitness\"}]") },
                        maxLines = 6,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = StarGoldPrimary,
                            unfocusedBorderColor = GlassBorderDefault,
                            focusedLabelColor = StarGoldPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                    )

                    // Quick Load Template button
                    OutlinedButton(
                        onClick = {
                            importFileText = """[
  {"title": "Walk 10,000 Steps", "category": "Fitness", "starReward": 3, "tracker_type": "GOOGLE_FITBIT", "tracker_goal": "10,000 steps daily"},
  {"title": "30m Cardio / Ride", "category": "Active", "starReward": 4, "tracker_type": "STRAVA", "tracker_goal": "30 mins cycling"},
  {"title": "Hydration & Active Recovery", "category": "Health", "starReward": 2, "tracker_type": "VIVO_TRACKER", "tracker_goal": "2,500 mL water & vitals"},
  {"title": "Morning Digital Fast", "category": "Mindfulness", "starReward": 2},
  {"title": "90m Deep Work Sanctuary", "category": "Productivity", "starReward": 4}
]"""
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.AutoFixHigh, contentDescription = null, tint = StarGoldPrimary, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Load External Trackers Template", style = MaterialTheme.typography.labelSmall, color = StarGoldPrimary)
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(
                            onClick = { showImportDialog = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel", color = OnSurfaceVariant)
                        }
                        Button(
                            onClick = {
                                if (importFileText.isNotBlank()) {
                                    val success = viewModel.importHabitTrackerJson(importFileText)
                                    if (success) {
                                        showImportDialog = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StarGoldPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Import", color = OnPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Add Custom Habit Modal
    if (showAddDialog) {
        Dialog(onDismissRequest = { showAddDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = SurfaceContainerHigh,
                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderDefault),
                shadowElevation = 24.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "New Focus Habit",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = OnSurface
                    )

                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Habit Title") },
                        placeholder = { Text("e.g. Leave phone outside bedroom") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = StarGoldPrimary,
                            unfocusedBorderColor = GlassBorderDefault,
                            focusedLabelColor = StarGoldPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Category", style = MaterialTheme.typography.labelMedium, color = OnSurfaceVariant)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("Mindfulness", "Focus", "Health", "Sleep", "Fitness").forEach { cat ->
                            Surface(
                                shape = CircleShape,
                                color = if (newCategory == cat) StarGoldPrimary else SurfaceContainerHighest,
                                modifier = Modifier.clickable { newCategory = cat }
                            ) {
                                Text(
                                    text = cat,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = if (newCategory == cat) OnPrimary else OnSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Text("Reward: +$newReward Stars", style = MaterialTheme.typography.labelMedium, color = StarGoldPrimary)
                    Slider(
                        value = newReward.toFloat(),
                        onValueChange = { newReward = it.toInt() },
                        valueRange = 1f..6f,
                        steps = 4,
                        colors = SliderDefaults.colors(
                            thumbColor = StarGoldPrimary,
                            activeTrackColor = StarGoldPrimary,
                            inactiveTrackColor = SurfaceContainerLowest
                        )
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(
                            onClick = { showAddDialog = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel", color = OnSurfaceVariant)
                        }
                        Button(
                            onClick = {
                                if (newTitle.isNotBlank()) {
                                    viewModel.addCustomHabit(newTitle, newCategory, newReward)
                                    showAddDialog = false
                                    newTitle = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StarGoldPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Create", color = OnPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrackerPill(label: String, color: Color, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, color.copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp)
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(12.dp))
            Spacer(Modifier.width(3.dp))
            Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold), color = color)
        }
    }
}

@Composable
fun HabitCardItem(
    habit: HabitItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onLinkTracker: () -> Unit
) {
    GlassCard(
        cornerRadius = 18.dp,
        backgroundColor = if (habit.isCompletedToday) SurfaceContainer.copy(alpha = 0.9f) else SurfaceContainer.copy(alpha = 0.75f),
        borderColor = if (habit.isCompletedToday) EmeraldTertiary.copy(alpha = 0.35f) else GlassBorderDefault
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // Interactive Checkbox Circle
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (habit.isCompletedToday) EmeraldTertiary else SurfaceContainerHigh)
                            .clickable(onClick = onToggle)
                    ) {
                        if (habit.isCompletedToday) {
                            Icon(Icons.Filled.Check, contentDescription = "Completed", tint = SurfaceDark, modifier = Modifier.size(20.dp))
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(SurfaceContainerHighest)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = habit.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                textDecoration = if (habit.isCompletedToday) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                            ),
                            color = if (habit.isCompletedToday) OnSurfaceVariant else OnSurface
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(shape = CircleShape, color = IrisPurpleSecondaryContainer.copy(alpha = 0.3f)) {
                                Text(
                                    text = habit.category,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = IrisPurpleSecondary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                Icon(Icons.Filled.LocalFireDepartment, contentDescription = null, tint = StarGoldPrimary, modifier = Modifier.size(12.dp))
                                Text(
                                    text = "${habit.streakDays}d streak",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = OnSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Reward Badge & Delete Option
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        shape = CircleShape,
                        color = if (habit.isCompletedToday) EmeraldTertiaryContainer.copy(alpha = 0.25f) else StarGoldPrimaryContainer.copy(alpha = 0.25f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Filled.Stars, contentDescription = null, tint = if (habit.isCompletedToday) EmeraldTertiary else StarGoldPrimary, modifier = Modifier.size(12.dp))
                            Text(
                                text = "+${habit.starReward}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (habit.isCompletedToday) EmeraldTertiary else StarGoldPrimary
                            )
                        }
                    }

                    IconButton(onClick = onDelete, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "Delete", tint = OnSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Linked Tracker Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceContainerHigh.copy(alpha = 0.5f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                if (habit.linkedTracker != null) {
                    val tracker = habit.linkedTracker
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(tracker.trackerType.brandColorHex))
                        )
                        Text(
                            text = "${tracker.trackerType.displayName}: ${tracker.metricGoal}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 10.sp),
                            color = OnSurface
                        )
                        Text(
                            text = "(${tracker.lastSyncValue})",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = EmeraldTertiary
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Filled.LinkOff, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(12.dp))
                        Text(
                            text = "No external tracker linked",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = OnSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = SurfaceContainerHighest,
                    modifier = Modifier.clickable(onClick = onLinkTracker)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = if (habit.linkedTracker != null) Icons.Filled.Edit else Icons.Filled.AddLink,
                            contentDescription = "Link Tracker",
                            tint = StarGoldPrimary,
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = if (habit.linkedTracker != null) "Configure" else "Link Tracker",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                            color = StarGoldPrimary
                        )
                    }
                }
            }
        }
    }
}
