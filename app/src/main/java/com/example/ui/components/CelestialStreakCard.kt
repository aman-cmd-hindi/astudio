package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.*
import com.example.model.CelestialStreak
import com.example.model.StreakMilestone
import com.example.ui.theme.*

@Composable
fun CelestialStreakCard(
    streak: CelestialStreak,
    milestones: List<StreakMilestone>,
    isLimitMaintained: Boolean = true,
    onClaimMilestone: (StreakMilestone) -> Unit,
    onAdvanceStreak: () -> Unit,
    onResetStreak: () -> Unit,
    modifier: Modifier = Modifier
) {
    var burstTriggerKey by remember { mutableStateOf(0) }

    GlassCard(
        cornerRadius = 24.dp,
        borderColor = StarGoldPrimary.copy(alpha = 0.35f),
        backgroundColor = SurfaceContainer.copy(alpha = 0.80f),
        modifier = modifier.fillMaxWidth()
    ) {
        // Header
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
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(StarGoldPrimaryContainer.copy(alpha = 0.45f), SurfaceContainerHigh)
                            )
                        )
                        .border(1.dp, StarGoldPrimary.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocalFireDepartment,
                        contentDescription = "Streak Flame",
                        tint = StarGoldPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column {
                    Text(
                        text = "CELESTIAL STREAK",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp, fontWeight = FontWeight.Bold),
                        color = StarGoldPrimary
                    )
                    Text(
                        text = "Mindful Habit Continuity",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                }
            }

            Surface(
                shape = CircleShape,
                color = StarGoldPrimaryContainer.copy(alpha = 0.20f),
                border = androidx.compose.foundation.BorderStroke(1.dp, StarGoldPrimary.copy(alpha = 0.4f))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        tint = StarGoldPrimary,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "+2.5x Yield Active",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = StarGoldPrimary
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // Large Counter & Subtitle with Celestial Particle Aura
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Box(
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (isLimitMaintained) {
                        CelestialParticleEffect(
                            isActive = isLimitMaintained,
                            burstTriggerKey = burstTriggerKey,
                            modifier = Modifier
                                .size(170.dp, 70.dp)
                                .offset(x = (-10).dp)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "${streak.currentStreakDays}",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 48.sp,
                                lineHeight = 52.sp
                            ),
                            color = StarGoldPrimary
                        )
                        Text(
                            text = if (streak.currentStreakDays == 1) "Day" else "Days",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = OnSurface,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { burstTriggerKey++ }
                        .padding(vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        tint = if (isLimitMaintained) EmeraldTertiary else OnSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = if (isLimitMaintained) "Daily Limit Maintained (< 2h 30m)" else "Daily Limit Exceeded",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = if (isLimitMaintained) EmeraldTertiary else ErrorRed
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SurfaceContainerHigh.copy(alpha = 0.8f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.MilitaryTech,
                        contentDescription = null,
                        tint = IrisPurpleSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Best: ${streak.bestStreakDays}d",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = OnSurface
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // Visual 7-day cyclical trail
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = SurfaceContainerLow.copy(alpha = 0.65f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "7-Day Orbit Cycle",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant
                    )
                    Text(
                        text = "Day ${((streak.currentStreakDays - 1) % 7) + 1} of 7",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = StarGoldPrimary
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val currentCycleDay = ((streak.currentStreakDays - 1) % 7) + 1
                    for (dayNum in 1..7) {
                        val isCompleted = streak.currentStreakDays > 0 && dayNum <= currentCycleDay
                        val isCurrent = streak.currentStreakDays > 0 && dayNum == currentCycleDay

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isCurrent -> StarGoldPrimaryContainer
                                            isCompleted -> StarGoldPrimary.copy(alpha = 0.25f)
                                            else -> SurfaceContainerHighest
                                        }
                                    )
                                    .border(
                                        width = if (isCurrent) 1.5.dp else 1.dp,
                                        color = if (isCompleted) StarGoldPrimary else GlassBorderDefault,
                                        shape = CircleShape
                                    )
                            ) {
                                if (isCompleted) {
                                    Icon(
                                        imageVector = if (isCurrent) Icons.Filled.LocalFireDepartment else Icons.Filled.Check,
                                        contentDescription = null,
                                        tint = if (isCurrent) OnPrimary else StarGoldPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                } else {
                                    Text(
                                        text = "$dayNum",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                        color = OnSurfaceVariant
                                    )
                                }
                            }
                            Text(
                                text = "D$dayNum",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = if (isCompleted) StarGoldPrimary else OnSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // Milestone Bonus Rewards
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "STREAK MILESTONES",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.8.sp),
                    color = OnSurfaceVariant
                )
                Text(
                    text = "Unlock bonus stars",
                    style = MaterialTheme.typography.labelSmall,
                    color = StarGoldPrimary
                )
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(milestones) { milestone ->
                    val isReached = streak.currentStreakDays >= milestone.daysRequired
                    val isClaimed = streak.claimedMilestones.contains(milestone.daysRequired)
                    val daysRemaining = (milestone.daysRequired - streak.currentStreakDays).coerceAtLeast(0)

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = when {
                            isClaimed -> SurfaceContainerLow.copy(alpha = 0.7f)
                            isReached -> StarGoldPrimaryContainer.copy(alpha = 0.18f)
                            else -> SurfaceContainerHigh.copy(alpha = 0.5f)
                        },
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            when {
                                isClaimed -> EmeraldTertiary.copy(alpha = 0.4f)
                                isReached -> StarGoldPrimary.copy(alpha = 0.6f)
                                else -> GlassBorderDefault
                            }
                        ),
                        modifier = Modifier.width(170.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(
                                        imageVector = Icons.Filled.Stars,
                                        contentDescription = null,
                                        tint = if (isReached) StarGoldPrimary else OnSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "${milestone.daysRequired} Days",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = OnSurface
                                    )
                                }

                                Surface(
                                    shape = CircleShape,
                                    color = if (isReached) StarGoldPrimaryContainer else SurfaceContainerHighest
                                ) {
                                    Text(
                                        text = "+${milestone.bonusStars} ★",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (isReached) OnPrimary else OnSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Text(
                                text = milestone.title,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = OnSurfaceVariant,
                                maxLines = 1
                            )

                            // Claim / Status Button
                            when {
                                isClaimed -> {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = EmeraldTertiaryContainer.copy(alpha = 0.25f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(vertical = 5.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.CheckCircle,
                                                contentDescription = null,
                                                tint = EmeraldTertiary,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(Modifier.width(4.dp))
                                            Text(
                                                text = "Claimed ✓",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = EmeraldTertiary
                                            )
                                        }
                                    }
                                }
                                isReached -> {
                                    Button(
                                        onClick = {
                                            burstTriggerKey++
                                            onClaimMilestone(milestone)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = StarGoldPrimaryContainer),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Celebration,
                                            contentDescription = null,
                                            tint = OnPrimary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            text = "Claim +${milestone.bonusStars}★",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = OnPrimary
                                        )
                                    }
                                }
                                else -> {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = SurfaceContainerHighest,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "$daysRemaining days left",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                            color = OnSurfaceVariant,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                            modifier = Modifier.padding(vertical = 5.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Demo Actions (Interactive Simulation)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    burstTriggerKey++
                    onAdvanceStreak()
                },
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHigh),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.AddCircle,
                    contentDescription = null,
                    tint = StarGoldPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Advance +1 Clean Day",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = OnSurface
                )
            }

            Text(
                text = "Reset",
                style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariant),
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onResetStreak() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
