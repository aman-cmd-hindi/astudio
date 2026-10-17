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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.R
import com.example.model.PermissionItem
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.viewmodel.FocusViewModel

@Composable
fun PermissionsScreen(
    viewModel: FocusViewModel,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
    onOpenPrivacyPolicy: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val permissions by viewModel.permissions.collectAsState()

    // Sync live permission statuses on resume
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.syncPermissionsWithSystem()
                viewModel.queryUsageStatsIfPermitted()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val grantedCount = permissions.count { it.granted }
    val totalCount = permissions.size
    val progressPercent = (grantedCount.toFloat() / totalCount.coerceAtLeast(1)).coerceIn(0f, 1f)

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Hero Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(SurfaceContainer)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Stars,
                        contentDescription = null,
                        tint = StarGoldPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = SurfaceContainerHigh.copy(alpha = 0.6f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Filled.VerifiedUser, contentDescription = null, tint = EmeraldTertiary, modifier = Modifier.size(14.dp))
                        Text("PRODUCTION SECURITY SHIELD", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = EmeraldTertiary)
                    }
                }

                Text(
                    text = "System Permissions",
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                    color = OnSurface
                )

                Text(
                    text = "To reliably enforce app blocking and analyze screen time, Aether requires the system authorizations below. No personal data is stored or transmitted.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Permissions Stack Cards
            permissions.forEach { perm ->
                GlassCard(
                    cornerRadius = 20.dp,
                    backgroundColor = if (perm.granted) SurfaceContainer.copy(alpha = 0.75f) else SurfaceContainer.copy(alpha = 0.95f),
                    borderColor = if (perm.granted) EmeraldTertiary.copy(alpha = 0.35f) else GlassBorderDefault
                ) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (perm.granted) EmeraldTertiaryContainer.copy(alpha = 0.25f)
                                        else IrisPurpleSecondaryContainer.copy(alpha = 0.35f)
                                    )
                            ) {
                                val vectorIcon = when (perm.key) {
                                    "admin" -> Icons.Filled.LockClock
                                    "accessibility" -> Icons.Filled.Shield
                                    "usage" -> Icons.Filled.QueryStats
                                    "overlay" -> Icons.Filled.Layers
                                    "battery" -> Icons.Filled.BatterySaver
                                    "notif" -> Icons.Filled.NotificationsActive
                                    else -> Icons.Filled.Security
                                }
                                Icon(
                                    imageVector = vectorIcon,
                                    contentDescription = perm.title,
                                    tint = if (perm.granted) EmeraldTertiary else StarGoldPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = perm.title,
                                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 17.sp),
                                    color = OnSurface
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = perm.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnSurfaceVariant
                                )
                            }
                        }

                        // Status Pill
                        Surface(
                            shape = CircleShape,
                            color = if (perm.granted) EmeraldTertiaryContainer.copy(alpha = 0.25f)
                            else StarGoldPrimaryContainer.copy(alpha = 0.2f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                if (perm.granted) {
                                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = EmeraldTertiary, modifier = Modifier.size(14.dp))
                                } else {
                                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(StarGoldPrimary))
                                }
                                Text(
                                    text = if (perm.granted) "Granted" else "Pending",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (perm.granted) EmeraldTertiary else StarGoldPrimary
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = perm.tagLine,
                            style = MaterialTheme.typography.labelSmall,
                            color = OnSurfaceVariant
                        )

                        if (!perm.granted) {
                            Button(
                                onClick = { viewModel.requestPermission(context, perm.key) },
                                colors = ButtonDefaults.buttonColors(containerColor = StarGoldPrimary),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text("Grant in Settings", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = OnPrimary)
                                Spacer(Modifier.width(4.dp))
                                Icon(Icons.Filled.OpenInNew, contentDescription = null, tint = OnPrimary, modifier = Modifier.size(14.dp))
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Filled.Verified, contentDescription = null, tint = EmeraldTertiary, modifier = Modifier.size(14.dp))
                                Text("Verified Active", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold), color = EmeraldTertiary)
                            }
                        }
                    }
                }
            }

            // Explicit Privacy Policy Card
            GlassCard(
                cornerRadius = 16.dp,
                borderColor = EmeraldTertiary.copy(alpha = 0.4f)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(32.dp).clip(CircleShape).background(EmeraldTertiaryContainer.copy(alpha = 0.25f))
                        ) {
                            Icon(Icons.Filled.PrivacyTip, contentDescription = null, tint = EmeraldTertiary, modifier = Modifier.size(18.dp))
                        }
                        Text(
                            text = "Privacy Commitment",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldTertiary
                        )
                    }

                    Text(
                        text = stringResource(id = R.string.privacy_statement),
                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                        color = Color.White
                    )

                    if (onOpenPrivacyPolicy != null) {
                        TextButton(
                            onClick = onOpenPrivacyPolicy,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Read Complete Privacy Policy & Asset Disclosures", style = MaterialTheme.typography.labelSmall, color = StarGoldPrimary)
                            Icon(Icons.Filled.ArrowForward, contentDescription = null, tint = StarGoldPrimary, modifier = Modifier.size(12.dp))
                        }
                    }
                }
            }

            Spacer(Modifier.height(110.dp))
        }

        // Sticky Bottom Bar
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = SurfaceContainerHigh.copy(alpha = 0.95f),
            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderDefault),
            shadowElevation = 24.dp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = StarGoldPrimary, modifier = Modifier.size(16.dp))
                        Text(
                            text = "Security Setup: $grantedCount of $totalCount Granted",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = OnSurface
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        repeat(totalCount) { idx ->
                            Box(
                                modifier = Modifier
                                    .size(if (idx < grantedCount) 8.dp else 6.dp)
                                    .clip(CircleShape)
                                    .background(if (idx < grantedCount) EmeraldTertiary else SurfaceContainerHighest)
                            )
                        }
                    }
                }

                LinearProgressIndicator(
                    progress = { progressPercent },
                    color = EmeraldTertiary,
                    trackColor = SurfaceContainerLowest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                )

                Button(
                    onClick = onContinue,
                    colors = ButtonDefaults.buttonColors(containerColor = StarGoldPrimary),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Filled.DoneAll, contentDescription = null, tint = OnPrimary, modifier = Modifier.size(18.dp))
                        Text(
                            text = if (grantedCount == totalCount) "All Authorized — Done" else "Continue to Focus ($grantedCount/$totalCount)",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = OnPrimary
                        )
                    }
                }
            }
        }
    }
}
