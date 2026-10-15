package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import com.example.model.ScreenTab
import com.example.ui.components.AppLauncherDock
import com.example.ui.components.GlassCard
import com.example.ui.components.StarPillBadge
import com.example.ui.theme.*
import com.example.viewmodel.FocusViewModel

@Composable
fun HomeScreen(
    viewModel: FocusViewModel,
    modifier: Modifier = Modifier
) {
    val wallet by viewModel.wallet.collectAsState()
    val schedule by viewModel.schedule.collectAsState()
    val apps by viewModel.apps.collectAsState()
    val screenTimeData by viewModel.screenTimeData.collectAsState()
    val simulatedTimeMillis by viewModel.simulatedTimeMillis.collectAsState()

    val isRestricted = viewModel.isWithinInterval(simulatedTimeMillis)
    val remainingIntervalText = viewModel.getRemainingIntervalText(simulatedTimeMillis)
    val blockedAppsCount = apps.count { it.blocked }
    val payoutConditions = remember(screenTimeData) { viewModel.evaluatePayoutConditions() }

    var snoozeActive by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isRestricted) IrisPurpleSecondary else EmeraldTertiary)
                    )
                    Text(
                        text = if (isRestricted) "SANCTUARY ACTIVE" else "DOWNTIME INACTIVE",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                        color = OnSurfaceVariant
                    )
                }
                Text(
                    text = "Good evening, Alex",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = OnSurface
                )
            }

            StarPillBadge(
                stars = wallet.balance,
                onClick = { viewModel.setTab(ScreenTab.WALLET) }
            )
        }

        // Status Card: Interval Window & Active Shield State
        GlassCard(
            cornerRadius = 24.dp,
            backgroundColor = SurfaceContainer.copy(alpha = 0.75f),
            borderColor = if (isRestricted) IrisPurpleSecondary.copy(alpha = 0.35f) else GlassBorderDefault
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isRestricted) IrisPurpleSecondaryContainer.copy(alpha = 0.40f) else EmeraldTertiaryContainer.copy(alpha = 0.20f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (isRestricted) IrisPurpleSecondary else EmeraldTertiary)
                        )
                        Text(
                            text = if (isRestricted) "RESTRICTED (ACTIVE)" else "FREE WINDOW",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isRestricted) OnSecondaryContainer else EmeraldTertiary
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                        imageVector = Icons.Filled.Shield,
                        contentDescription = null,
                        tint = EmeraldTertiary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Shielding $blockedAppsCount apps",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "CURRENT INTERVAL",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = OnSurfaceVariant
                    )
                    Text(
                        text = "${schedule.startHour}:${String.format("%02d", schedule.startMinute)} ${schedule.startPeriod} – ${schedule.endHour}:${String.format("%02d", schedule.endMinute)} ${schedule.endPeriod}",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = OnSurface
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "REMAINING",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = StarGoldPrimary
                    )
                    Text(
                        text = remainingIntervalText,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = StarGoldPrimary
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // Distraction Barrier Pill
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SurfaceContainerLow.copy(alpha = 0.85f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(IrisPurpleSecondaryContainer.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = null,
                                tint = IrisPurpleSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Distraction Barrier Engaged",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = OnSurface
                            )
                            Text(
                                text = "Social feeds, short video & games muted",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Active",
                        tint = EmeraldTertiary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Emergency Snooze Action
            Button(
                onClick = {
                    if (wallet.balance >= 10) {
                        viewModel.confirmUnlockWithStars(
                            AppItem("all", "Emergency Snooze", "Override", "", 0, 0, blocked = false),
                            cost = 10
                        )
                        snoozeActive = true
                    } else {
                        viewModel.showToast("Need 10 stars for Emergency Snooze!")
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = SurfaceContainerHighest.copy(alpha = 0.8f)
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.HourglassTop,
                        contentDescription = null,
                        tint = StarGoldPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (snoozeActive) "Snooze Active (15m)" else "Emergency Snooze",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = OnSurface
                    )
                    Surface(
                        shape = CircleShape,
                        color = StarGoldPrimary.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "10 STARS",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = StarGoldPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Interactive App Launcher Dock (Simulator)
        AppLauncherDock(
            apps = apps,
            onLaunchApp = { app -> viewModel.launchApp(app) }
        )

        // Screen Rhythm Card: 3 labelled values and progress bars
        GlassCard(cornerRadius = 24.dp) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.Timelapse,
                        contentDescription = null,
                        tint = EmeraldTertiary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Screen Rhythm",
                        style = MaterialTheme.typography.headlineSmall,
                        color = OnSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldTertiaryContainer.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "Real-time sync",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = EmeraldTertiary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Bar 1: Usage since last check
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Usage since last check",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurface
                    )
                    Text(
                        text = "${screenTimeData.usageSinceLastCheckMinutes}m / ${screenTimeData.maxUsageSinceLastCheckLimitMinutes}m limit",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = EmeraldTertiary
                    )
                }
                LinearProgressIndicator(
                    progress = { (screenTimeData.usageSinceLastCheckMinutes.toFloat() / screenTimeData.maxUsageSinceLastCheckLimitMinutes).coerceIn(0f, 1f) },
                    color = EmeraldTertiary,
                    trackColor = SurfaceContainerHighest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Well balanced pace", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                    Text("40% of cycle", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                }
            }

            Spacer(Modifier.height(14.dp))

            // Bar 2: This week daily avg
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "This week daily avg",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurface
                    )
                    Text(
                        text = "${screenTimeData.thisWeekDailyAvgMinutes / 60}h ${screenTimeData.thisWeekDailyAvgMinutes % 60}m / day",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = StarGoldPrimary
                    )
                }
                LinearProgressIndicator(
                    progress = { (screenTimeData.thisWeekDailyAvgMinutes.toFloat() / screenTimeData.dailyThresholdMinutes).coerceIn(0f, 1f) },
                    color = StarGoldPrimary,
                    trackColor = SurfaceContainerHighest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Target: < 2h 30m", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                    Text("58% quota", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                }
            }

            Spacer(Modifier.height(14.dp))

            // Value 3: Last week comparison
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SurfaceContainerLow.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(EmeraldTertiary.copy(alpha = 0.15f))
                        ) {
                            Icon(
                                imageVector = Icons.Filled.TrendingDown,
                                contentDescription = null,
                                tint = EmeraldTertiary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Last week avg: ${screenTimeData.lastWeekAvgMinutes / 60}h ${screenTimeData.lastWeekAvgMinutes % 60}m",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = OnSurface
                            )
                            Text(
                                text = "Calmer evenings baseline",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EmeraldTertiaryContainer.copy(alpha = 0.3f)
                    ) {
                        Text(
                            text = "38% Better",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldTertiary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Weekly Objective: 5-Star Bounty Vault (Conditions Checklist)
        GlassCard(cornerRadius = 24.dp) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "WEEKLY OBJECTIVE",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                        color = StarGoldPrimary
                    )
                    Text(
                        text = "5-Star Bounty Vault",
                        style = MaterialTheme.typography.headlineSmall,
                        color = OnSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EmeraldTertiary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "ON TRACK",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = EmeraldTertiary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // Checklist rows
            payoutConditions.forEach { condition ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceContainerHigh.copy(alpha = 0.4f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (condition.isMet) EmeraldTertiary.copy(alpha = 0.2f)
                                        else ErrorRed.copy(alpha = 0.2f)
                                    )
                            ) {
                                Icon(
                                    imageVector = if (condition.isMet) Icons.Filled.Check else Icons.Filled.Close,
                                    contentDescription = null,
                                    tint = if (condition.isMet) EmeraldTertiary else ErrorRed,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = condition.title,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = OnSurface
                                )
                                Text(
                                    text = condition.subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (condition.isMet) EmeraldTertiary.copy(alpha = 0.15f) else ErrorRed.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = condition.statusText,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (condition.isMet) EmeraldTertiary else ErrorRed,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Milestone progress
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Bounty Milestone Progress", style = MaterialTheme.typography.bodySmall, color = OnSurface)
                Text("3 / 3 Conditions", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = StarGoldPrimary)
            }
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { 1f },
                color = StarGoldPrimary,
                trackColor = SurfaceContainerHighest,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            )

            Spacer(Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                        imageVector = Icons.Filled.CardGiftcard,
                        contentDescription = null,
                        tint = StarGoldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text("Awarded automatically every 7 days", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                }
                Text("+5 Stars", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = StarGoldPrimary)
            }
        }

        Spacer(Modifier.height(60.dp))
    }
}
