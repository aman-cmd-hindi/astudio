package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.GlassCard
import com.example.ui.theme.*

@Composable
fun PrivacyPolicyScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)
    val exactStatement = stringResource(id = R.string.privacy_statement)

    Column(
        modifier = modifier
            .testTag("privacy_policy_screen")
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Navigation Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .testTag("privacy_back_button")
                    .clip(CircleShape)
                    .background(SurfaceContainerHigh)
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = OnSurface
                )
            }

            Surface(
                shape = CircleShape,
                color = EmeraldTertiaryContainer.copy(alpha = 0.25f),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldTertiary.copy(alpha = 0.4f))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Shield,
                        contentDescription = null,
                        tint = EmeraldTertiary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Zero Data Collection",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = EmeraldTertiary
                    )
                }
            }
        }

        // Title Header
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "DATA & PRIVACY",
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp),
                color = StarGoldPrimary
            )
            Text(
                text = "Privacy Policy",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = OnSurface
            )
            Text(
                text = "Transparent on-device security guarantees for Aether Focus.",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant
            )
        }

        // PRIMARY HIGHLIGHT BOX - EXACT REQUIRED STATEMENT
        GlassCard(
            modifier = Modifier.testTag("privacy_statement_card"),
            cornerRadius = 20.dp,
            backgroundColor = SurfaceContainerHighest.copy(alpha = 0.85f),
            borderColor = EmeraldTertiary.copy(alpha = 0.5f)
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(EmeraldTertiaryContainer.copy(alpha = 0.35f))
                ) {
                    Icon(
                        imageVector = Icons.Filled.VerifiedUser,
                        contentDescription = null,
                        tint = EmeraldTertiary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "CORE PRIVACY GUARANTEE",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp),
                        color = EmeraldTertiary
                    )

                    Text(
                        text = exactStatement,
                        modifier = Modifier.testTag("privacy_statement_text"),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 22.sp
                        ),
                        color = Color.White
                    )
                }
            }
        }

        // Section: On-Device Processing
        GlassCard(cornerRadius = 20.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Filled.Lock, contentDescription = null, tint = StarGoldPrimary, modifier = Modifier.size(20.dp))
                    Text(
                        text = "100% On-Device Operation",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = OnSurface
                    )
                }

                Text(
                    text = "Aether Focus does not run remote tracking servers, third-party analytics trackers, or telemetry SDKs. Your habits, star balances, app restrictions, and screen time numbers never leave your phone.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant
                )
            }
        }

        // Section: Required Permissions Breakdown
        GlassCard(cornerRadius = 20.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Permission Disclosures",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = OnSurface
                )

                PermissionItemRow(
                    title = "Device Administrator",
                    usage = "Used exclusively to prevent circumventing active schedules or hastily uninstalling during restriction windows."
                )

                HorizontalDivider(color = SurfaceContainerHighest.copy(alpha = 0.3f))

                PermissionItemRow(
                    title = "Accessibility Service",
                    usage = "Detects when blocked apps are opened on device to immediately show the focus shield."
                )

                HorizontalDivider(color = SurfaceContainerHighest.copy(alpha = 0.3f))

                PermissionItemRow(
                    title = "Screen-Time Analysis (PACKAGE_USAGE_STATS)",
                    usage = "Reads local foreground usage durations solely to compute progress against your user-adjustable 40h 22m benchmark."
                )

                HorizontalDivider(color = SurfaceContainerHighest.copy(alpha = 0.3f))

                PermissionItemRow(
                    title = "Display Over Other Apps (SYSTEM_ALERT_WINDOW)",
                    usage = "Draws the luminous restriction shield overlay above restricted applications."
                )

                HorizontalDivider(color = SurfaceContainerHighest.copy(alpha = 0.3f))

                PermissionItemRow(
                    title = "Battery Optimization Exemption",
                    usage = "Prevents Android OS from killing downtime monitors during active schedules."
                )
            }
        }

        // HTML asset reference note
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SurfaceContainerLowest.copy(alpha = 0.7f),
            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderDefault)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(14.dp)
            ) {
                Icon(Icons.Filled.Description, contentDescription = null, tint = StarGoldPrimary, modifier = Modifier.size(20.dp))
                Column {
                    Text(
                        text = "Bundled HTML Policy Asset",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = OnSurface
                    )
                    Text(
                        text = "Packaged at file:///android_asset/privacy_policy.html for offline legal compliance.",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                }
            }
        }

        Spacer(Modifier.height(80.dp))
    }
}

@Composable
private fun PermissionItemRow(title: String, usage: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = IrisPurpleSecondary
        )
        Text(
            text = usage,
            style = MaterialTheme.typography.bodySmall,
            color = OnSurfaceVariant
        )
    }
}
