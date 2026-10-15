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
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppItem
import com.example.model.DailyUsageBar
import com.example.ui.components.DockAppIcon
import com.example.ui.components.GlassCard
import com.example.ui.components.getAppVectorIcon
import com.example.ui.theme.*
import com.example.viewmodel.FocusViewModel

enum class ScreenTimePeriod {
    TODAY, THIS_WEEK, LAST_WEEK
}

enum class ScreenTimeStateView {
    LIVE, EMPTY, LOADING
}

@Composable
fun ScreenTimeScreen(
    viewModel: FocusViewModel,
    modifier: Modifier = Modifier
) {
    val screenTimeData by viewModel.screenTimeData.collectAsState()
    val apps by viewModel.apps.collectAsState()

    var selectedPeriod by remember { mutableStateOf(ScreenTimePeriod.THIS_WEEK) }
    var selectedStateView by remember { mutableStateOf(ScreenTimeStateView.LIVE) }
    var appFilterBlockedOnly by remember { mutableStateOf(false) }

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
                        text = "MINDFUL PRESENCE",
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

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SurfaceContainerHigh
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Verified,
                        contentDescription = null,
                        tint = EmeraldTertiary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "SHIELD SYNCED",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = EmeraldTertiary
                    )
                }
            }
        }

        // Period Segmented Control Pill
        Surface(
            shape = RoundedCornerShape(30.dp),
            color = SurfaceContainerLow,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(4.dp)) {
                ScreenTimePeriod.values().forEach { period ->
                    val isSelected = selectedPeriod == period
                    val label = when (period) {
                        ScreenTimePeriod.TODAY -> "Today"
                        ScreenTimePeriod.THIS_WEEK -> "This week"
                        ScreenTimePeriod.LAST_WEEK -> "Last week"
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(26.dp))
                            .background(if (isSelected) SurfaceContainerHighest else Color.Transparent)
                            .clickable { selectedPeriod = period }
                            .padding(vertical = 10.dp)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal),
                            color = if (isSelected) StarGoldPrimary else OnSurfaceVariant
                        )
                    }
                }
            }
        }

        // Total Screen Time & 7-Day Bar Chart
        GlassCard(cornerRadius = 24.dp) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "TOTAL SCREEN TIME",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = OnSurfaceVariant
                    )
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        val displayHours = when (selectedPeriod) {
                            ScreenTimePeriod.TODAY -> "2h 34m"
                            ScreenTimePeriod.THIS_WEEK -> "14h 22m"
                            ScreenTimePeriod.LAST_WEEK -> "22h 10m"
                        }
                        val displayPeriodTag = when (selectedPeriod) {
                            ScreenTimePeriod.TODAY -> "/ today"
                            ScreenTimePeriod.THIS_WEEK -> "/ 7 days"
                            ScreenTimePeriod.LAST_WEEK -> "/ 7 days"
                        }
                        Text(
                            text = displayHours,
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                            color = OnSurface
                        )
                        Text(
                            text = displayPeriodTag,
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    Text(
                        text = "Daily Average: 2h 03m",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EmeraldTertiaryContainer.copy(alpha = 0.2f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.TrendingDown,
                            contentDescription = null,
                            tint = EmeraldTertiary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "-28% vs last wk",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldTertiary
                        )
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            // 7-day Bar Chart
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(horizontal = 4.dp)
            ) {
                val maxMinutes = 200f
                screenTimeData.dailyBars.forEach { bar ->
                    val barHeightFraction = (bar.minutes / maxMinutes).coerceIn(0.15f, 1f)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        Text(
                            text = bar.formattedDuration,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = if (bar.isHighlighted) StarGoldPrimary else OnSurfaceVariant
                        )
                        Spacer(Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(barHeightFraction)
                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                .background(
                                    if (bar.isToday) StarGoldPrimary
                                    else if (bar.isHighlighted) StarGoldPrimaryContainer
                                    else SurfaceContainerHighest
                                )
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = bar.dayLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (bar.isHighlighted) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (bar.isHighlighted) StarGoldPrimary else OnSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Goal benchmark
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .size(8.dp, 3.dp)
                            .clip(CircleShape)
                            .background(StarGoldPrimary)
                    )
                    Text("Daily Threshold: 2h 30m", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                }
                Text("5 of 7 Days Under Goal", style = MaterialTheme.typography.labelSmall, color = EmeraldTertiary)
            }
        }

        // State Variation Sandbox Toolbar
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = SurfaceContainerLowest.copy(alpha = 0.8f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                        imageVector = Icons.Filled.Science,
                        contentDescription = null,
                        tint = StarGoldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text("VIEW STATE:", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ScreenTimeStateView.values().forEach { stateView ->
                        val isSelected = selectedStateView == stateView
                        Surface(
                            onClick = { selectedStateView = stateView },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) SurfaceContainerHighest else Color.Transparent
                        ) {
                            Text(
                                text = stateView.name.lowercase().capitalize(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) StarGoldPrimary else OnSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Per-App Usage Section
        when (selectedStateView) {
            ScreenTimeStateView.LIVE -> {
                // Category Filter Tabs
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            onClick = { appFilterBlockedOnly = false },
                            shape = CircleShape,
                            color = if (!appFilterBlockedOnly) IrisPurpleSecondaryContainer.copy(alpha = 0.4f) else SurfaceContainerLow
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "All Apps",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (!appFilterBlockedOnly) IrisPurpleSecondary else OnSurfaceVariant
                                )
                                Surface(shape = CircleShape, color = SurfaceContainerHighest) {
                                    Text(
                                        text = "${apps.size}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = OnSurface,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }

                        Surface(
                            onClick = { appFilterBlockedOnly = true },
                            shape = CircleShape,
                            color = if (appFilterBlockedOnly) IrisPurpleSecondaryContainer.copy(alpha = 0.4f) else SurfaceContainerLow
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Filled.Lock, contentDescription = null, modifier = Modifier.size(12.dp), tint = OnSurfaceVariant)
                                Text(
                                    text = "Blocked",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (appFilterBlockedOnly) IrisPurpleSecondary else OnSurfaceVariant
                                )
                                Surface(shape = CircleShape, color = SurfaceContainerHighest) {
                                    Text(
                                        text = "${apps.count { it.blocked }}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = OnSurface,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // App Usage Rows
                val displayedApps = if (appFilterBlockedOnly) apps.filter { it.blocked } else apps
                displayedApps.forEach { app ->
                    val percentage = (app.usageMinutes.toFloat() / app.limitMinutes).coerceIn(0f, 1f)
                    GlassCard(
                        cornerRadius = 18.dp,
                        onClick = { viewModel.launchApp(app) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
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
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(SurfaceContainerHighest)
                                ) {
                                    Icon(
                                        imageVector = getAppVectorIcon(app.iconKey),
                                        contentDescription = app.name,
                                        tint = if (app.blocked) StarGoldPrimary else IrisPurpleSecondary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = app.name,
                                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                                            color = OnSurface
                                        )
                                        if (app.blocked) {
                                            Icon(
                                                imageVector = Icons.Filled.Lock,
                                                contentDescription = null,
                                                tint = ErrorRed,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${app.usageMinutes / 60}h ${app.usageMinutes % 60}m in foreground",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = OnSurfaceVariant
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (app.blocked) ErrorContainerRed.copy(alpha = 0.3f)
                                    else if (app.isSystemProtected) EmeraldTertiaryContainer.copy(alpha = 0.2f)
                                    else SurfaceContainerHighest
                                ) {
                                    Text(
                                        text = if (app.blocked) "Restricted" else if (app.isSystemProtected) "Essential" else "Monitored",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (app.blocked) ErrorRed else if (app.isSystemProtected) EmeraldTertiary else OnSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = "${(percentage * 100).toInt()}% of limit",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = OnSurfaceVariant
                                )
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        // App limit progress bar
                        LinearProgressIndicator(
                            progress = { percentage },
                            color = if (percentage > 0.8f) ErrorRed else if (app.blocked) StarGoldPrimary else IrisPurpleSecondary,
                            trackColor = SurfaceContainerHighest,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                    }
                }
            }

            ScreenTimeStateView.EMPTY -> {
                GlassCard(
                    cornerRadius = 24.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(SurfaceContainerHigh)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Nightlight,
                                contentDescription = null,
                                tint = StarGoldPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Text(
                            text = "Cosmic Tranquility",
                            style = MaterialTheme.typography.headlineSmall,
                            color = OnSurface
                        )
                        Text(
                            text = "No app usage logged for this selected period. Your attention remains pure and unscattered.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Button(
                            onClick = { selectedStateView = ScreenTimeStateView.LIVE },
                            colors = ButtonDefaults.buttonColors(containerColor = StarGoldPrimary),
                            shape = CircleShape
                        ) {
                            Text("Reset to Live View", color = OnPrimary)
                        }
                    }
                }
            }

            ScreenTimeStateView.LOADING -> {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    repeat(4) {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = SurfaceContainerHigh.copy(alpha = 0.5f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(SurfaceContainerHighest)
                                )
                                Spacer(Modifier.width(12.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Box(modifier = Modifier.size(120.dp, 14.dp).clip(RoundedCornerShape(4.dp)).background(SurfaceContainerHighest))
                                    Box(modifier = Modifier.size(80.dp, 10.dp).clip(RoundedCornerShape(4.dp)).background(SurfaceContainerHighest))
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(60.dp))
    }
}
