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
import androidx.compose.ui.window.Dialog
import com.example.model.ScreenTab
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.viewmodel.FocusViewModel

@Composable
fun SettingsScreen(
    viewModel: FocusViewModel,
    onOpenPermissions: () -> Unit,
    modifier: Modifier = Modifier
) {
    val wallet by viewModel.wallet.collectAsState()
    val schedule by viewModel.schedule.collectAsState()
    val permissions by viewModel.permissions.collectAsState()

    var showResetModal by remember { mutableStateOf(false) }

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
                Text(
                    text = "CONFIGURATION",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                    color = StarGoldPrimary
                )
                Text(
                    text = "Settings & Safeguards",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = OnSurface
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SurfaceContainerHigh)
            ) {
                Icon(Icons.Filled.Tune, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(20.dp))
            }
        }

        // Profile / Tier Snippet Card
        GlassCard(cornerRadius = 24.dp) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(SurfaceContainerHighest)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Profile",
                        tint = StarGoldPrimary,
                        modifier = Modifier
                            .size(36.dp)
                            .align(Alignment.Center)
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(StarGoldPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Shield,
                            contentDescription = null,
                            tint = SurfaceDark,
                            modifier = Modifier
                                .size(12.dp)
                                .align(Alignment.Center)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Alex Rivera",
                            style = MaterialTheme.typography.headlineSmall,
                            color = OnSurface
                        )
                        Surface(shape = CircleShape, color = StarGoldPrimary.copy(alpha = 0.2f)) {
                            Text(
                                text = "TIER IV",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = StarGoldPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "Focus Guardian Level 4",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = IrisPurpleSecondary
                    )
                    Spacer(Modifier.height(4.dp))
                    Surface(shape = CircleShape, color = SurfaceContainerLowest.copy(alpha = 0.8f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Icon(Icons.Filled.Stars, contentDescription = null, tint = StarGoldPrimary, modifier = Modifier.size(14.dp))
                            Text(
                                text = "${wallet.lifetimeStars} Lifetime Stars",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurface
                            )
                        }
                    }
                }
            }
        }

        // Section 1: System Permissions Summary
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
            ) {
                Text(
                    text = "SYSTEM PERMISSIONS",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.8.sp),
                    color = OnSurfaceVariant
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(EmeraldTertiary))
                    Text("Protected", style = MaterialTheme.typography.labelSmall, color = EmeraldTertiary)
                }
            }

            GlassCard(cornerRadius = 20.dp) {
                // Usage Access
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(SurfaceContainerHigh)) {
                            Icon(Icons.Filled.QueryStats, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(18.dp))
                        }
                        Column {
                            Text("Usage Access", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold), color = OnSurface)
                            Text("App tracking & session timers", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                        }
                    }
                    Surface(shape = CircleShape, color = EmeraldTertiaryContainer.copy(alpha = 0.3f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = EmeraldTertiary, modifier = Modifier.size(12.dp))
                            Text("Active", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = EmeraldTertiary)
                        }
                    }
                }

                HorizontalDivider(color = SurfaceContainerHighest.copy(alpha = 0.4f), modifier = Modifier.padding(vertical = 8.dp))

                // Overlay Shield
                val overlayGranted = permissions.firstOrNull { it.key == "overlay" }?.granted ?: false
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(SurfaceContainerHigh)) {
                            Icon(Icons.Filled.Layers, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(18.dp))
                        }
                        Column {
                            Text("Overlay Shield", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold), color = OnSurface)
                            Text("Modal block enforcement", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                        }
                    }
                    Surface(shape = CircleShape, color = if (overlayGranted) EmeraldTertiaryContainer.copy(alpha = 0.3f) else StarGoldPrimaryContainer.copy(alpha = 0.2f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)) {
                            Icon(if (overlayGranted) Icons.Filled.CheckCircle else Icons.Filled.HourglassTop, contentDescription = null, tint = if (overlayGranted) EmeraldTertiary else StarGoldPrimary, modifier = Modifier.size(12.dp))
                            Text(if (overlayGranted) "Active" else "Pending", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = if (overlayGranted) EmeraldTertiary else StarGoldPrimary)
                        }
                    }
                }

                HorizontalDivider(color = SurfaceContainerHighest.copy(alpha = 0.4f), modifier = Modifier.padding(vertical = 8.dp))

                // Device Admin
                val adminGranted = permissions.firstOrNull { it.key == "admin" }?.granted ?: false
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(SurfaceContainerHigh)) {
                            Icon(Icons.Filled.Security, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(18.dp))
                        }
                        Column {
                            Text("Device Admin", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold), color = OnSurface)
                            Text("Anti-uninstall persistence", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                        }
                    }
                    Surface(shape = CircleShape, color = if (adminGranted) StarGoldPrimaryContainer.copy(alpha = 0.25f) else SurfaceContainerHighest) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)) {
                            Icon(Icons.Filled.Lock, contentDescription = null, tint = if (adminGranted) StarGoldPrimary else OnSurfaceVariant, modifier = Modifier.size(12.dp))
                            Text(if (adminGranted) "Strict" else "Pending", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = if (adminGranted) StarGoldPrimary else OnSurfaceVariant)
                        }
                    }
                }

                Spacer(Modifier.height(6.dp))

                TextButton(
                    onClick = onOpenPermissions,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Review All Permissions", style = MaterialTheme.typography.labelMedium, color = IrisPurpleSecondary)
                    Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = IrisPurpleSecondary, modifier = Modifier.size(16.dp))
                }
            }
        }

        // Section 2: Focus Safeguards
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
            ) {
                Text("FOCUS SAFEGUARDS", style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.8.sp), color = OnSurfaceVariant)
                Text("Rule Engine", style = MaterialTheme.typography.labelSmall, color = StarGoldPrimary)
            }

            GlassCard(cornerRadius = 20.dp) {
                // Default Schedule row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.setTab(ScreenTab.SCHEDULE) }
                        .padding(vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(SurfaceContainerHigh)) {
                            Icon(Icons.Filled.Schedule, contentDescription = null, tint = IrisPurpleSecondary, modifier = Modifier.size(18.dp))
                        }
                        Column {
                            Text("Default Schedule", style = MaterialTheme.typography.labelLarge, color = OnSurface)
                            Text("${schedule.startHour}:00 ${schedule.startPeriod} – ${schedule.endHour}:00 ${schedule.endPeriod} (Weekdays)", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                        }
                    }
                    Icon(Icons.Rounded.ChevronRight, contentDescription = "Edit Schedule", tint = OnSurfaceVariant)
                }

                Spacer(Modifier.height(12.dp))

                // Early Unlock Fee Badge Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SurfaceContainerLowest.copy(alpha = 0.8f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("EARLY UNLOCK FEE", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = OnSurfaceVariant)
                            Surface(shape = CircleShape, color = SurfaceContainerHigh) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)) {
                                    Icon(Icons.Filled.Lock, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(10.dp))
                                    Text("Streak Locked", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = OnSurfaceVariant)
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Filled.Stars, contentDescription = null, tint = StarGoldPrimary, modifier = Modifier.size(18.dp))
                            Text("5 Stars per 15-minute pass", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = StarGoldPrimary)
                        }
                        Text("Cannot be modified without sacrificing current 14-day shield streak.", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant.copy(alpha = 0.8f))
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Emergency Unlock Pass
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(SurfaceContainerHigh)) {
                            Icon(Icons.Filled.Key, contentDescription = null, tint = EmeraldTertiary, modifier = Modifier.size(18.dp))
                        }
                        Column {
                            Text("Emergency Unlock Pass", style = MaterialTheme.typography.labelLarge, color = OnSurface)
                            Text("1 free pass replenished every 30 days", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Surface(shape = CircleShape, color = SurfaceContainerHigh) {
                            Text("0 Available", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = OnSurfaceVariant, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                        }
                        Text("Renews in 12d", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = OnSurfaceVariant)
                    }
                }
            }
        }

        // Section 3: Critical Actions
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
            ) {
                Text("CRITICAL ACTIONS", style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.8.sp), color = ErrorRed)
                Icon(Icons.Filled.Warning, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(14.dp))
            }

            GlassCard(cornerRadius = 20.dp) {
                // Reset Star Balance
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                ) {
                    Column {
                        Text("Reset Star Balance", style = MaterialTheme.typography.labelLarge, color = OnSurface)
                        Text("Surrenders ${wallet.balance} earned focus stars", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                    }
                    Button(
                        onClick = { showResetModal = true },
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorContainerRed.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("Reset", style = MaterialTheme.typography.labelMedium, color = ErrorRed)
                    }
                }

                HorizontalDivider(color = SurfaceContainerHighest.copy(alpha = 0.4f), modifier = Modifier.padding(vertical = 8.dp))

                // Clear Restriction Rules
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                ) {
                    Column {
                        Text("Clear Restriction Rules", style = MaterialTheme.typography.labelLarge, color = OnSurface)
                        Text("Unblocks all apps & active schedules", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                    }
                    Button(
                        onClick = { viewModel.clearRestrictionRules() },
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHigh),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("Clear", style = MaterialTheme.typography.labelMedium, color = OnSurface)
                    }
                }

                HorizontalDivider(color = SurfaceContainerHighest.copy(alpha = 0.4f), modifier = Modifier.padding(vertical = 8.dp))

                // Export Focus Data
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                ) {
                    Column {
                        Text("Export Focus Data", style = MaterialTheme.typography.labelLarge, color = OnSurface)
                        Text("Download screen telemetry as CSV", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                    }
                    Button(
                        onClick = { viewModel.showToast("Exporting focus telemetry (focus_data.csv)...") },
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHighest),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Filled.Download, contentDescription = null, tint = IrisPurpleSecondary, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("CSV", style = MaterialTheme.typography.labelMedium, color = IrisPurpleSecondary)
                    }
                }
            }
        }

        Spacer(Modifier.height(60.dp))
    }

    // Reset Confirmation Modal
    if (showResetModal) {
        Dialog(onDismissRequest = { showResetModal = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = SurfaceContainerHigh,
                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderDefault),
                shadowElevation = 24.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.padding(24.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(ErrorContainerRed.copy(alpha = 0.4f))
                    ) {
                        Icon(Icons.Filled.RestartAlt, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(30.dp))
                    }

                    Text("Reset Star Balance?", style = MaterialTheme.typography.headlineSmall, color = OnSurface)
                    Text(
                        text = "This will clear your ${wallet.balance} celestial stars and immediately revoke emergency lock bypasses. This cannot be undone.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { showResetModal = false },
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainer),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Keep Stars", color = OnSurface)
                        }

                        Button(
                            onClick = {
                                viewModel.resetStarBalance()
                                showResetModal = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ErrorContainerRed),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Yes, Reset", color = ErrorRed)
                        }
                    }
                }
            }
        }
    }
}
