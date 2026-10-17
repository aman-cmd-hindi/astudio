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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog
import com.example.model.ScreenTab
import com.example.model.SyncState
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.viewmodel.FocusViewModel

@Composable
fun SettingsScreen(
    viewModel: FocusViewModel,
    onOpenPermissions: () -> Unit,
    modifier: Modifier = Modifier,
    onOpenPrivacyPolicy: () -> Unit = { viewModel.setTab(ScreenTab.PRIVACY_POLICY) }
) {
    val context = LocalContext.current
    val wallet by viewModel.wallet.collectAsState()
    val schedule by viewModel.schedule.collectAsState()
    val permissions by viewModel.permissions.collectAsState()
    val screenTimeData by viewModel.screenTimeData.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val cloudSyncStatus by viewModel.cloudSyncStatus.collectAsState()
    val isAuthLoading by viewModel.isAuthLoading.collectAsState()
    val target = screenTimeData.targetConfig

    var showResetModal by remember { mutableStateOf(false) }
    var showBenchmarkModal by remember { mutableStateOf(false) }

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
                            text = "Focus Companion",
                            style = MaterialTheme.typography.headlineSmall,
                            color = OnSurface
                        )
                        Surface(shape = CircleShape, color = StarGoldPrimary.copy(alpha = 0.2f)) {
                            Text(
                                text = "PRODUCTION",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = StarGoldPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "100% On-Device • Private & Offline",
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
                                text = "${wallet.balance} Available (${wallet.lifetimeStars} Lifetime Stars)",
                                style = MaterialTheme.typography.labelSmall,
                                color = OnSurface
                            )
                        }
                    }
                }
            }
        }

        // Section: Google OAuth & Supabase Backend Sync
        GlassCard(
            cornerRadius = 20.dp,
            borderColor = IrisPurpleSecondary.copy(alpha = 0.5f)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(IrisPurpleSecondaryContainer.copy(alpha = 0.35f))
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CloudSync,
                                contentDescription = null,
                                tint = IrisPurpleSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Account & Cloud Sync",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = OnSurface
                            )
                            Text(
                                text = "Cross-device persistence via Google OAuth & Supabase",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant
                            )
                        }
                    }

                    // Status Pill
                    Surface(
                        shape = CircleShape,
                        color = when (cloudSyncStatus.state) {
                            SyncState.SYNCED -> EmeraldTertiaryContainer.copy(alpha = 0.3f)
                            SyncState.SYNCING -> StarGoldPrimaryContainer.copy(alpha = 0.3f)
                            SyncState.ERROR -> ErrorContainerRed.copy(alpha = 0.3f)
                            else -> SurfaceContainerHigh
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (cloudSyncStatus.state) {
                                            SyncState.SYNCED -> EmeraldTertiary
                                            SyncState.SYNCING -> StarGoldPrimary
                                            SyncState.ERROR -> ErrorRed
                                            else -> OnSurfaceVariant
                                        }
                                    )
                            )
                            Text(
                                text = when (cloudSyncStatus.state) {
                                    SyncState.SYNCED -> "SYNCED"
                                    SyncState.SYNCING -> "SYNCING"
                                    SyncState.ERROR -> "ATTENTION"
                                    else -> "LOCAL"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                color = when (cloudSyncStatus.state) {
                                    SyncState.SYNCED -> EmeraldTertiary
                                    SyncState.SYNCING -> StarGoldPrimary
                                    SyncState.ERROR -> ErrorRed
                                    else -> OnSurfaceVariant
                                }
                            )
                        }
                    }
                }

                HorizontalDivider(color = GlassBorderDefault)

                // Account state
                if (currentUser != null) {
                    val user = currentUser!!
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(StarGoldPrimaryContainer)
                            ) {
                                Text(
                                    text = (user.displayName ?: user.email ?: "G").take(1).uppercase(),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = OnPrimary
                                )
                            }
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = user.displayName ?: "Google User",
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                        color = OnSurface
                                    )
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = "Verified",
                                        tint = EmeraldTertiary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Text(
                                    text = user.email ?: "Signed in via Google",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = { viewModel.signOut() },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed.copy(alpha = 0.5f)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Sign Out", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Connect your Google Account to persist habits, streaks, and focus metrics safely to your Supabase backend.",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                        Button(
                            onClick = { viewModel.signInWithGoogle(context) },
                            enabled = !isAuthLoading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SurfaceContainerHigh,
                                contentColor = OnSurface
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, StarGoldPrimary.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isAuthLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = StarGoldPrimary
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("Signing in...")
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.AccountCircle,
                                    contentDescription = null,
                                    tint = StarGoldPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "Sign in with Google (Firebase Auth)",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }

                // Supabase Sync Detail Box
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SurfaceContainerLowest.copy(alpha = 0.7f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderDefault)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Storage,
                                    contentDescription = null,
                                    tint = StarGoldPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Supabase Backend",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = OnSurface
                                )
                            }

                            Button(
                                onClick = { viewModel.syncWithSupabase() },
                                colors = ButtonDefaults.buttonColors(containerColor = StarGoldPrimary),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                shape = CircleShape
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Sync,
                                    contentDescription = null,
                                    tint = OnPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "Sync Now",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = OnPrimary
                                )
                            }
                        }

                        Text(
                            text = "URL: ${viewModel.supabaseUrl}",
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 11.sp),
                            color = OnSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = cloudSyncStatus.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (cloudSyncStatus.state == SyncState.ERROR) ErrorRed else EmeraldTertiary
                        )

                        Text(
                            text = "Last Synced: ${cloudSyncStatus.lastSyncedTimeFormatted}",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant
                        )
                    }
                }
            }
        }

        // Section: Privacy Policy & Legal Disclosures
        GlassCard(
            modifier = Modifier.testTag("privacy_policy_setting_card"),
            cornerRadius = 20.dp,
            borderColor = EmeraldTertiary.copy(alpha = 0.4f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenPrivacyPolicy)
                    .testTag("privacy_policy_setting_entry")
                    .padding(vertical = 4.dp)
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
                            .background(EmeraldTertiaryContainer.copy(alpha = 0.25f))
                    ) {
                        Icon(Icons.Filled.Policy, contentDescription = null, tint = EmeraldTertiary, modifier = Modifier.size(22.dp))
                    }
                    Column {
                        Text(
                            text = "Privacy Policy",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = OnSurface
                        )
                        Text(
                            text = "Zero personal data collection statement & disclosures",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                    }
                }
                Icon(Icons.Rounded.ChevronRight, contentDescription = "Open Privacy Policy", tint = EmeraldTertiary)
            }
        }

        // Section: Screen Time Analysis Target (Default 40h 22m)
        val target = screenTimeData.targetConfig
        GlassCard(cornerRadius = 20.dp) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showBenchmarkModal = true }
                    .padding(vertical = 4.dp)
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
                            .background(StarGoldPrimaryContainer.copy(alpha = 0.25f))
                    ) {
                        Icon(Icons.Filled.Timelapse, contentDescription = null, tint = StarGoldPrimary, modifier = Modifier.size(22.dp))
                    }
                    Column {
                        Text(
                            text = "Screen-Time Target Variable",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = OnSurface
                        )
                        Text(
                            text = "Target: ${target.targetHours}h ${target.targetMinutes}m weekly (${target.dailyAverageTargetMinutes / 60}h ${target.dailyAverageTargetMinutes % 60}m/day)",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                    }
                }
                Surface(shape = CircleShape, color = StarGoldPrimaryContainer.copy(alpha = 0.3f)) {
                    Text(
                        text = "Modify",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = StarGoldPrimary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
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
                Text(
                    text = "${permissions.count { it.granted }} of ${permissions.size} Granted",
                    style = MaterialTheme.typography.labelSmall,
                    color = EmeraldTertiary
                )
            }

            GlassCard(cornerRadius = 20.dp) {
                permissions.forEachIndexed { idx, perm ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(SurfaceContainerHigh)) {
                                Icon(
                                    imageVector = if (perm.granted) Icons.Filled.CheckCircle else Icons.Filled.Pending,
                                    contentDescription = null,
                                    tint = if (perm.granted) EmeraldTertiary else StarGoldPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(perm.title, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold), color = OnSurface)
                                Text(perm.tagLine, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                            }
                        }
                        Surface(shape = CircleShape, color = if (perm.granted) EmeraldTertiaryContainer.copy(alpha = 0.3f) else StarGoldPrimaryContainer.copy(alpha = 0.2f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)) {
                                Text(
                                    text = if (perm.granted) "Active" else "Pending",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (perm.granted) EmeraldTertiary else StarGoldPrimary
                                )
                            }
                        }
                    }

                    if (idx < permissions.size - 1) {
                        HorizontalDivider(color = SurfaceContainerHighest.copy(alpha = 0.4f), modifier = Modifier.padding(vertical = 6.dp))
                    }
                }

                Spacer(Modifier.height(8.dp))

                TextButton(
                    onClick = onOpenPermissions,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Manage System Permissions", style = MaterialTheme.typography.labelMedium, color = StarGoldPrimary)
                    Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = StarGoldPrimary, modifier = Modifier.size(16.dp))
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
                            Text("Restriction Schedule", style = MaterialTheme.typography.labelLarge, color = OnSurface)
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
                                    Text("Enforced", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = OnSurfaceVariant)
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Filled.Stars, contentDescription = null, tint = StarGoldPrimary, modifier = Modifier.size(18.dp))
                            Text("5 Stars per 15-minute pass", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = StarGoldPrimary)
                        }
                        Text("Earned through consistent mindful phone usage and habit completion.", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant.copy(alpha = 0.8f))
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
                Text("MAINTENANCE ACTIONS", style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.8.sp), color = ErrorRed)
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
                        Text("Resets current ${wallet.balance} stars", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
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
            }
        }

        Spacer(Modifier.height(60.dp))
    }

    // Modal: Benchmark Adjustment
    if (showBenchmarkModal) {
        var h by remember { mutableIntStateOf(target.targetHours) }
        var m by remember { mutableIntStateOf(target.targetMinutes) }

        Dialog(onDismissRequest = { showBenchmarkModal = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = SurfaceContainerHigh,
                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderDefault),
                shadowElevation = 24.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Modify Screen-Time Target", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = OnSurface)
                    Text("Default: 40 hours 22 minutes weekly.", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Hours:", style = MaterialTheme.typography.bodyLarge, color = OnSurface)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(onClick = { if (h > 5) h -= 1 }) { Icon(Icons.Filled.RemoveCircleOutline, contentDescription = null, tint = StarGoldPrimary) }
                            Text("${h}h", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = StarGoldPrimary)
                            IconButton(onClick = { if (h < 100) h += 1 }) { Icon(Icons.Filled.AddCircleOutline, contentDescription = null, tint = StarGoldPrimary) }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Minutes:", style = MaterialTheme.typography.bodyLarge, color = OnSurface)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(onClick = { if (m > 0) m -= 1 }) { Icon(Icons.Filled.RemoveCircleOutline, contentDescription = null, tint = StarGoldPrimary) }
                            Text("${m}m", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = StarGoldPrimary)
                            IconButton(onClick = { if (m < 59) m += 1 }) { Icon(Icons.Filled.AddCircleOutline, contentDescription = null, tint = StarGoldPrimary) }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { showBenchmarkModal = false },
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainer),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel", color = OnSurface)
                        }

                        Button(
                            onClick = {
                                viewModel.updateScreenTimeTarget(h, m)
                                showBenchmarkModal = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StarGoldPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Save Target", color = OnPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
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
                        text = "This will clear your ${wallet.balance} celestial stars. This cannot be undone.",
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
