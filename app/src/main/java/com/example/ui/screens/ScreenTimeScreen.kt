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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.DailyUsageBar
import com.example.model.ScreenTimeTargetConfig
import com.example.service.PermissionManager
import com.example.ui.components.DockAppIcon
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.viewmodel.FocusViewModel

enum class ScreenTimePeriod {
    TODAY, THIS_WEEK, LAST_WEEK
}

@Composable
fun ScreenTimeScreen(
    viewModel: FocusViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val screenTimeData by viewModel.screenTimeData.collectAsState()
    val apps by viewModel.apps.collectAsState()
    val permissions by viewModel.permissions.collectAsState()

    val isUsageGranted = permissions.firstOrNull { it.key == "usage" }?.granted ?: false

    var selectedPeriod by remember { mutableStateOf(ScreenTimePeriod.THIS_WEEK) }
    var showAdjustTargetDialog by remember { mutableStateOf(false) }

    // Target variables
    val targetConfig = screenTimeData.targetConfig
    val targetTotalMins = targetConfig.totalMinutes
    val currentWeeklyMins = screenTimeData.totalWeeklyMinutes
    val percentOfTarget = (currentWeeklyMins.toFloat() / targetTotalMins.coerceAtLeast(1)).coerceIn(0f, 2f)

    val remainingMinutes = (targetTotalMins - currentWeeklyMins).coerceAtLeast(0)
    val remainingHoursStr = "${remainingMinutes / 60}h ${remainingMinutes % 60}m"

    val dailyAverageTargetMins = targetConfig.dailyAverageTargetMinutes
    val dailyAvgRecordedMins = screenTimeData.thisWeekDailyAvgMinutes

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
                        imageVector = Icons.Filled.Insights,
                        contentDescription = null,
                        tint = StarGoldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "TELEMETRY & ANALYSIS",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                        color = StarGoldPrimary
                    )
                }
                Text(
                    text = "Screen Time Analysis",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = OnSurface
                )
            }

            // Adjust Target Button
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SurfaceContainerHigh,
                modifier = Modifier.clickable { showAdjustTargetDialog = true }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Tune,
                        contentDescription = "Adjust Target",
                        tint = StarGoldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Edit Goal",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = StarGoldPrimary
                    )
                }
            }
        }

        // Usage Access Permission Banner (If not yet active)
        if (!isUsageGranted) {
            GlassCard(
                cornerRadius = 18.dp,
                backgroundColor = SurfaceContainerHighest.copy(alpha = 0.85f),
                borderColor = StarGoldPrimary.copy(alpha = 0.4f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.QueryStats, contentDescription = null, tint = StarGoldPrimary, modifier = Modifier.size(24.dp))
                        Column {
                            Text("Real Screen-Time Access", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = OnSurface)
                            Text("Grant PACKAGE_USAGE_STATS to read live foreground app usage directly.", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                        }
                    }
                    Button(
                        onClick = { viewModel.requestPermission(context, "usage") },
                        colors = ButtonDefaults.buttonColors(containerColor = StarGoldPrimary),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Grant", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = OnPrimary)
                    }
                }
            }
        }

        // PRIMARY ANALYSIS CARD: Comparing Against User-Adjustable Target (Default 40h 22m)
        GlassCard(cornerRadius = 24.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "WEEKLY CEILING BENCHMARK",
                                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.8.sp),
                                color = OnSurfaceVariant
                            )
                            Surface(shape = CircleShape, color = StarGoldPrimaryContainer.copy(alpha = 0.3f)) {
                                Text(
                                    text = "Adjustable",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                    color = StarGoldPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = "${targetConfig.targetHours}h ${targetConfig.targetMinutes}m",
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                            color = StarGoldPrimary
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("Recorded This Week", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                        Text(
                            text = "${currentWeeklyMins / 60}h ${currentWeeklyMins % 60}m",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (percentOfTarget < 1.0f) EmeraldTertiary else ErrorRed
                        )
                        Text(
                            text = if (percentOfTarget < 1.0f) "$remainingHoursStr remaining" else "Exceeded ceiling",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = if (percentOfTarget < 1.0f) EmeraldTertiary else ErrorRed
                        )
                    }
                }

                // Progress Bar vs Ceiling
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    LinearProgressIndicator(
                        progress = { percentOfTarget.coerceIn(0f, 1f) },
                        color = if (percentOfTarget < 0.8f) EmeraldTertiary else if (percentOfTarget < 1.0f) StarGoldPrimary else ErrorRed,
                        trackColor = SurfaceContainerLowest,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "${(percentOfTarget * 100).toInt()}% of weekly target consumed",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant
                        )
                        Text(
                            text = "Daily Allowance: ${dailyAverageTargetMins / 60}h ${dailyAverageTargetMins % 60}m/d",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = IrisPurpleSecondary
                        )
                    }
                }

                // Three Key Metric Glass Badges
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MetricMiniCard(
                        title = "Daily Average",
                        value = "${dailyAvgRecordedMins / 60}h ${dailyAvgRecordedMins % 60}m",
                        sub = if (dailyAvgRecordedMins <= dailyAverageTargetMins) "Under daily cap" else "Over daily cap",
                        isGood = dailyAvgRecordedMins <= dailyAverageTargetMins,
                        modifier = Modifier.weight(1f)
                    )

                    MetricMiniCard(
                        title = "vs Prev Week",
                        value = "${screenTimeData.lastWeekAvgMinutes / 60}h ${screenTimeData.lastWeekAvgMinutes % 60}m",
                        sub = if (dailyAvgRecordedMins < screenTimeData.lastWeekAvgMinutes) "Improving 18%" else "+5% trend",
                        isGood = dailyAvgRecordedMins < screenTimeData.lastWeekAvgMinutes,
                        modifier = Modifier.weight(1f)
                    )

                    MetricMiniCard(
                        title = "Status",
                        value = if (percentOfTarget < 1f) "On Track" else "Alert",
                        sub = "Starlight eligible",
                        isGood = percentOfTarget < 1f,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Period Tab Selector (Today / This Week / Last Week)
        Surface(
            shape = CircleShape,
            color = SurfaceContainer.copy(alpha = 0.8f),
            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderDefault),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(4.dp)) {
                listOf(
                    ScreenTimePeriod.TODAY to "Today",
                    ScreenTimePeriod.THIS_WEEK to "This Week",
                    ScreenTimePeriod.LAST_WEEK to "Last Week"
                ).forEach { (period, title) ->
                    val isSelected = selectedPeriod == period
                    Surface(
                        shape = CircleShape,
                        color = if (isSelected) StarGoldPrimary else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedPeriod = period }
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isSelected) OnPrimary else OnSurfaceVariant,
                            modifier = Modifier.padding(vertical = 8.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }

        // 7-Day Usage Bar Chart
        GlassCard(cornerRadius = 24.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Daily Screen Rhythm",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = OnSurface
                    )
                    Text(
                        text = "Ceiling line: ${dailyAverageTargetMins / 60}h ${dailyAverageTargetMins % 60}m",
                        style = MaterialTheme.typography.labelSmall,
                        color = StarGoldPrimary
                    )
                }

                // Bar Visualization
                val maxMinutesInChart = 240
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .padding(horizontal = 8.dp)
                ) {
                    screenTimeData.dailyBars.forEach { bar ->
                        val barFraction = (bar.minutes.toFloat() / maxMinutesInChart).coerceIn(0.12f, 1f)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = bar.formattedDuration,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = if (bar.isToday) StarGoldPrimary else OnSurfaceVariant
                            )

                            Spacer(Modifier.height(4.dp))

                            Box(
                                modifier = Modifier
                                    .width(18.dp)
                                    .fillMaxHeight(barFraction)
                                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                    .background(
                                        if (bar.isToday) StarGoldPrimary
                                        else if (bar.minutes > dailyAverageTargetMins) ErrorRed.copy(alpha = 0.8f)
                                        else IrisPurpleSecondary.copy(alpha = 0.6f)
                                    )
                            )

                            Spacer(Modifier.height(6.dp))

                            Text(
                                text = bar.dayLabel,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (bar.isToday) StarGoldPrimary else OnSurface
                            )
                        }
                    }
                }
            }
        }

        // Per-App Usage Breakdown
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "APPLICATION CONSUMPTION BREAKDOWN",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.8.sp),
                color = OnSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            apps.take(8).forEach { app ->
                GlassCard(cornerRadius = 16.dp) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            DockAppIcon(app = app, onClick = { viewModel.launchApp(app, context) })
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = app.name,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = OnSurface
                                    )
                                    if (app.blocked) {
                                        Surface(shape = CircleShape, color = IrisPurpleSecondaryContainer.copy(alpha = 0.4f)) {
                                            Text(
                                                text = "Restricted",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                color = IrisPurpleSecondary,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = app.category,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnSurfaceVariant
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${app.usageMinutes}m",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (app.usageMinutes > 60) StarGoldPrimary else OnSurface
                            )
                            Text(
                                text = "Avg ${app.avgDailyUsage}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = OnSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(80.dp))
    }

    // Modal: Adjust Screen Time Target Benchmark (Default 40h 22m)
    if (showAdjustTargetDialog) {
        var hoursInput by remember { mutableIntStateOf(targetConfig.targetHours) }
        var minsInput by remember { mutableIntStateOf(targetConfig.targetMinutes) }

        Dialog(onDismissRequest = { showAdjustTargetDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = SurfaceContainerHigh,
                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderDefault),
                shadowElevation = 24.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Customize Analysis Target",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = OnSurface
                    )

                    Text(
                        text = "Set your weekly screen time ceiling. By default, this is tuned to 40 hours 22 minutes.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant
                    )

                    // Hours Stepper
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Weekly Hours:", style = MaterialTheme.typography.bodyLarge, color = OnSurface)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(onClick = { if (hoursInput > 5) hoursInput -= 1 }) {
                                Icon(Icons.Filled.RemoveCircleOutline, contentDescription = "Decrease", tint = StarGoldPrimary)
                            }
                            Text(
                                text = "${hoursInput}h",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = StarGoldPrimary
                            )
                            IconButton(onClick = { if (hoursInput < 100) hoursInput += 1 }) {
                                Icon(Icons.Filled.AddCircleOutline, contentDescription = "Increase", tint = StarGoldPrimary)
                            }
                        }
                    }

                    // Minutes Stepper
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Weekly Minutes:", style = MaterialTheme.typography.bodyLarge, color = OnSurface)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(onClick = { if (minsInput > 0) minsInput -= 1 }) {
                                Icon(Icons.Filled.RemoveCircleOutline, contentDescription = "Decrease", tint = StarGoldPrimary)
                            }
                            Text(
                                text = "${minsInput}m",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = StarGoldPrimary
                            )
                            IconButton(onClick = { if (minsInput < 59) minsInput += 1 }) {
                                Icon(Icons.Filled.AddCircleOutline, contentDescription = "Increase", tint = StarGoldPrimary)
                            }
                        }
                    }

                    // Quick presets
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                        listOf(
                            (40 to 22) to "Default (40h 22m)",
                            (30 to 0) to "30h 0m",
                            (20 to 0) to "20h 0m"
                        ).forEach { (pair, label) ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (hoursInput == pair.first && minsInput == pair.second) StarGoldPrimary else SurfaceContainer,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        hoursInput = pair.first
                                        minsInput = pair.second
                                    }
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (hoursInput == pair.first && minsInput == pair.second) OnPrimary else OnSurface,
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { showAdjustTargetDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainer),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel", color = OnSurface)
                        }

                        Button(
                            onClick = {
                                viewModel.updateScreenTimeTarget(hoursInput, minsInput)
                                showAdjustTargetDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StarGoldPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Apply Benchmark", color = OnPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricMiniCard(
    title: String,
    value: String,
    sub: String,
    isGood: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = SurfaceContainerLowest.copy(alpha = 0.75f),
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderDefault),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(title, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = OnSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = OnSurface)
            Text(sub, style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = if (isGood) EmeraldTertiary else ErrorRed)
        }
    }
}
