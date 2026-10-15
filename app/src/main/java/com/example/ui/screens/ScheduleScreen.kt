package com.example.ui.screens

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
import com.example.model.ScheduleConfig
import com.example.ui.components.CustomGlassToggle
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.viewmodel.FocusViewModel
import java.util.Calendar

@Composable
fun ScheduleScreen(
    viewModel: FocusViewModel,
    modifier: Modifier = Modifier
) {
    val schedule by viewModel.schedule.collectAsState()

    var enabled by remember(schedule) { mutableStateOf(schedule.enabled) }
    var startHour by remember(schedule) { mutableStateOf(schedule.startHour) }
    var startMinute by remember(schedule) { mutableStateOf(schedule.startMinute) }
    var startPeriod by remember(schedule) { mutableStateOf(schedule.startPeriod) }

    var endHour by remember(schedule) { mutableStateOf(schedule.endHour) }
    var endMinute by remember(schedule) { mutableStateOf(schedule.endMinute) }
    var endPeriod by remember(schedule) { mutableStateOf(schedule.endPeriod) }

    var activeDays by remember(schedule) { mutableStateOf(schedule.activeDays) }
    var repeatWeekly by remember(schedule) { mutableStateOf(schedule.repeatWeekly) }
    var strictLock by remember(schedule) { mutableStateOf(schedule.strictLock) }
    var earlyPenalty by remember(schedule) { mutableStateOf(schedule.earlyUnlockPenalty) }

    // Compute duration
    var startH24 = startHour % 12
    if (startPeriod == "PM") startH24 += 12
    val startTotalMins = startH24 * 60 + startMinute

    var endH24 = endHour % 12
    if (endPeriod == "PM") endH24 += 12
    val endTotalMins = endH24 * 60 + endMinute

    var diffMins = endTotalMins - startTotalMins
    if (diffMins <= 0) diffMins += 24 * 60
    val durationHours = diffMins / 60
    val durationRemainingMins = diffMins % 60
    val durationBadgeText = if (durationRemainingMins == 0) "$durationHours hours of shielded focus" else "${durationHours}h ${durationRemainingMins}m of shielded focus"

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
                        imageVector = Icons.Filled.AutoMode,
                        contentDescription = null,
                        tint = StarGoldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "TEMPORAL SHIELD",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                        color = StarGoldPrimary
                    )
                }
                Text(
                    text = "Restriction Schedule",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = OnSurface
                )
                Text(
                    text = "Define automatic downtime hours when chosen apps are blocked.",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
            }
        }

        // Master Schedule Active Card
        GlassCard(cornerRadius = 24.dp) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(EmeraldTertiaryContainer.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ShieldMoon,
                            contentDescription = null,
                            tint = EmeraldTertiary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Column {
                        Text(
                            text = if (enabled) "Schedule Active" else "Schedule Paused",
                            style = MaterialTheme.typography.headlineSmall,
                            color = OnSurface
                        )
                        Text(
                            text = if (enabled) "Shield activates tonight at ${startHour}:${String.format("%02d", startMinute)} $startPeriod" else "Restrictions paused",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                    }
                }

                CustomGlassToggle(
                    checked = enabled,
                    onCheckedChange = { enabled = it },
                    activeColor = EmeraldTertiary
                )
            }
        }

        // Interval Window Section
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
            ) {
                Text(
                    text = "INTERVAL WINDOW",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.8.sp),
                    color = OnSurfaceVariant
                )
                Surface(
                    shape = CircleShape,
                    color = StarGoldPrimary.copy(alpha = 0.15f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Filled.Timelapse, contentDescription = null, tint = StarGoldPrimary, modifier = Modifier.size(14.dp))
                        Text(durationBadgeText, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold), color = StarGoldPrimary)
                    }
                }
            }

            // Downtime Starts Picker Card
            GlassCard(cornerRadius = 20.dp) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Filled.Bedtime, contentDescription = null, tint = StarGoldPrimary, modifier = Modifier.size(18.dp))
                        Text("Downtime Starts", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold), color = OnSurfaceVariant)
                    }

                    // AM/PM Toggle
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SurfaceContainerHighest
                    ) {
                        Row(modifier = Modifier.padding(2.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (startPeriod == "AM") StarGoldPrimary else Color.Transparent)
                                    .clickable { startPeriod = "AM" }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("AM", style = MaterialTheme.typography.labelSmall, color = if (startPeriod == "AM") OnPrimary else OnSurfaceVariant)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (startPeriod == "PM") StarGoldPrimary else Color.Transparent)
                                    .clickable { startPeriod = "PM" }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("PM", style = MaterialTheme.typography.labelSmall, color = if (startPeriod == "PM") OnPrimary else OnSurfaceVariant)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${String.format("%02d", startHour)}:${String.format("%02d", startMinute)}",
                        style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold),
                        color = OnSurface
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = {
                                var total = startHour * 60 + startMinute - 15
                                if (total < 60) total += 12 * 60
                                startHour = total / 60
                                if (startHour == 0) startHour = 12
                                startMinute = total % 60
                            },
                            colors = IconButtonDefaults.iconButtonColors(containerColor = SurfaceContainerHigh)
                        ) {
                            Icon(Icons.Filled.Remove, contentDescription = "Subtract 15 mins", tint = OnSurface)
                        }

                        IconButton(
                            onClick = {
                                var total = startHour * 60 + startMinute + 15
                                if (total > 12 * 60 + 45) total -= 12 * 60
                                startHour = total / 60
                                if (startHour == 0) startHour = 12
                                startMinute = total % 60
                            },
                            colors = IconButtonDefaults.iconButtonColors(containerColor = SurfaceContainerHigh)
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "Add 15 mins", tint = OnSurface)
                        }
                    }
                }
            }

            // Downtime Ends Picker Card
            GlassCard(cornerRadius = 20.dp) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Filled.WbSunny, contentDescription = null, tint = IrisPurpleSecondary, modifier = Modifier.size(18.dp))
                        Text("Downtime Ends", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold), color = OnSurfaceVariant)
                    }

                    // AM/PM Toggle
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SurfaceContainerHighest
                    ) {
                        Row(modifier = Modifier.padding(2.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (endPeriod == "AM") IrisPurpleSecondaryContainer else Color.Transparent)
                                    .clickable { endPeriod = "AM" }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("AM", style = MaterialTheme.typography.labelSmall, color = if (endPeriod == "AM") OnSecondaryContainer else OnSurfaceVariant)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (endPeriod == "PM") IrisPurpleSecondaryContainer else Color.Transparent)
                                    .clickable { endPeriod = "PM" }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("PM", style = MaterialTheme.typography.labelSmall, color = if (endPeriod == "PM") OnSecondaryContainer else OnSurfaceVariant)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${String.format("%02d", endHour)}:${String.format("%02d", endMinute)}",
                        style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold),
                        color = OnSurface
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = {
                                var total = endHour * 60 + endMinute - 15
                                if (total < 60) total += 12 * 60
                                endHour = total / 60
                                if (endHour == 0) endHour = 12
                                endMinute = total % 60
                            },
                            colors = IconButtonDefaults.iconButtonColors(containerColor = SurfaceContainerHigh)
                        ) {
                            Icon(Icons.Filled.Remove, contentDescription = "Subtract 15 mins", tint = OnSurface)
                        }

                        IconButton(
                            onClick = {
                                var total = endHour * 60 + endMinute + 15
                                if (total > 12 * 60 + 45) total -= 12 * 60
                                endHour = total / 60
                                if (endHour == 0) endHour = 12
                                endMinute = total % 60
                            },
                            colors = IconButtonDefaults.iconButtonColors(containerColor = SurfaceContainerHigh)
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "Add 15 mins", tint = OnSurface)
                        }
                    }
                }
            }
        }

        // Active Days Section
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
            ) {
                Text(
                    text = "ACTIVE DAYS",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.8.sp),
                    color = OnSurfaceVariant
                )
                Text(
                    text = "${activeDays.size} days active",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = StarGoldPrimary
                )
            }

            GlassCard(cornerRadius = 20.dp) {
                val dayList = listOf(
                    Calendar.MONDAY to "M",
                    Calendar.TUESDAY to "T",
                    Calendar.WEDNESDAY to "W",
                    Calendar.THURSDAY to "T",
                    Calendar.FRIDAY to "F",
                    Calendar.SATURDAY to "S",
                    Calendar.SUNDAY to "S"
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    dayList.forEach { (calDay, label) ->
                        val isDaySelected = activeDays.contains(calDay)
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isDaySelected) StarGoldPrimaryContainer
                                    else SurfaceContainerHigh
                                )
                                .clickable {
                                    activeDays = if (isDaySelected) {
                                        activeDays - calDay
                                    } else {
                                        activeDays + calDay
                                    }
                                }
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isDaySelected) OnPrimary else OnSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Presets
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        onClick = {
                            activeDays = setOf(Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY)
                        },
                        shape = CircleShape,
                        color = IrisPurpleSecondaryContainer.copy(alpha = 0.35f)
                    ) {
                        Text(
                            text = "Weekdays",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    Surface(
                        onClick = {
                            activeDays = setOf(Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY)
                        },
                        shape = CircleShape,
                        color = SurfaceContainerHigh
                    ) {
                        Text(
                            text = "Every day",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    Surface(
                        onClick = {
                            activeDays = setOf(Calendar.SATURDAY, Calendar.SUNDAY)
                        },
                        shape = CircleShape,
                        color = SurfaceContainerHigh
                    ) {
                        Text(
                            text = "Weekends",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Guardrails & Strictness Section
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "GUARDRAILS & STRICTNESS",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.8.sp),
                color = OnSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            // Repeat Weekly
            GlassCard(cornerRadius = 18.dp) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(SurfaceContainerHighest)
                        ) {
                            Icon(Icons.Filled.Repeat, contentDescription = null, tint = StarGoldPrimary, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text("Repeat Weekly", style = MaterialTheme.typography.labelLarge, color = OnSurface)
                            Text("Keeps schedule running endlessly", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                        }
                    }
                    CustomGlassToggle(checked = repeatWeekly, onCheckedChange = { repeatWeekly = it })
                }
            }

            // Strict Lock (Admin)
            GlassCard(cornerRadius = 18.dp) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(SurfaceContainerHighest)
                        ) {
                            Icon(Icons.Filled.Lock, contentDescription = null, tint = IrisPurpleSecondary, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Strict Lock", style = MaterialTheme.typography.labelLarge, color = OnSurface)
                                Surface(shape = RoundedCornerShape(4.dp), color = ErrorContainerRed.copy(alpha = 0.3f)) {
                                    Text("ADMIN", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = ErrorRed, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                }
                            }
                            Text("Prevents deletion or override during interval", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                        }
                    }
                    CustomGlassToggle(checked = strictLock, onCheckedChange = { strictLock = it }, activeColor = StarGoldPrimaryContainer)
                }
            }

            // Early Unlock Penalty
            GlassCard(cornerRadius = 18.dp) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(SurfaceContainerHighest)
                        ) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = null, tint = StarGoldPrimary, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text("Early Unlock Penalty", style = MaterialTheme.typography.labelLarge, color = OnSurface)
                            Text("Deducts 5 stars if unlocked early", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                        }
                    }
                    CustomGlassToggle(checked = earlyPenalty, onCheckedChange = { earlyPenalty = it })
                }
            }
        }

        // Save Schedule Action Button
        Button(
            onClick = {
                viewModel.updateSchedule(
                    ScheduleConfig(
                        enabled = enabled,
                        startHour = startHour,
                        startMinute = startMinute,
                        startPeriod = startPeriod,
                        endHour = endHour,
                        endMinute = endMinute,
                        endPeriod = endPeriod,
                        activeDays = activeDays,
                        repeatWeekly = repeatWeekly,
                        strictLock = strictLock,
                        earlyUnlockPenalty = earlyPenalty
                    )
                )
            },
            colors = ButtonDefaults.buttonColors(containerColor = StarGoldPrimaryContainer),
            shape = CircleShape,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.Verified, contentDescription = null, tint = OnPrimary)
                Text(
                    text = "Save Schedule",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = OnPrimary
                )
            }
        }

        Spacer(Modifier.height(60.dp))
    }
}
