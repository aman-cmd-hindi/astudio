package com.example.ui.screens

import androidx.compose.animation.*
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
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.viewmodel.FocusViewModel

enum class LedgerFilter {
    ALL, EARNED, SPENT
}

@Composable
fun WalletScreen(
    viewModel: FocusViewModel,
    modifier: Modifier = Modifier
) {
    val wallet by viewModel.wallet.collectAsState()

    var activeFilter by remember { mutableStateOf(LedgerFilter.ALL) }

    val filteredLedger = remember(wallet.ledger, activeFilter) {
        when (activeFilter) {
            LedgerFilter.ALL -> wallet.ledger
            LedgerFilter.EARNED -> wallet.ledger.filter { it.starsDelta > 0 }
            LedgerFilter.SPENT -> wallet.ledger.filter { it.starsDelta < 0 }
        }
    }

    val equivalentPasses = wallet.balance / 5

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
                        imageVector = Icons.Filled.Stars,
                        contentDescription = null,
                        tint = StarGoldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "TREASURY BALANCE",
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                        color = StarGoldPrimary
                    )
                }
                Text(
                    text = "Star Wallet & Ledger",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = OnSurface
                )
            }

            Surface(
                shape = CircleShape,
                color = SurfaceContainerHigh
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(EmeraldTertiary))
                    Text(
                        text = "BOUNTY SYNC",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = EmeraldTertiary
                    )
                }
            }
        }

        // Hero Star Balance Card (Frosted Luminary)
        GlassCard(cornerRadius = 24.dp) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(StarGoldPrimaryContainer.copy(alpha = 0.25f))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = StarGoldPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "AVAILABLE RESERVE",
                            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                            color = OnSurfaceVariant
                        )
                        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "${wallet.balance}",
                                style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold),
                                color = StarGoldPrimary
                            )
                            Text(
                                text = "Stars",
                                style = MaterialTheme.typography.headlineSmall,
                                color = OnSurface
                            )
                        }
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = SurfaceContainerHighest
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = StarGoldPrimary, modifier = Modifier.size(14.dp))
                        Text("Luminous", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = StarGoldPrimary)
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Equivalence Statement
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SurfaceContainer.copy(alpha = 0.7f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Icon(Icons.Filled.Key, contentDescription = null, tint = IrisPurpleSecondary, modifier = Modifier.size(18.dp))
                    Text(
                        text = "Equivalent to $equivalentPasses early unlock passes",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = OnSurface
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // Quick Action Chips
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { viewModel.triggerWeeklyPayoutCheck() },
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHigh),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.Bolt, contentDescription = null, tint = StarGoldPrimary, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Earn More", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold), color = StarGoldPrimary)
                }

                Button(
                    onClick = { viewModel.showToast("Bounty is actively tracking your screen time") },
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHigh),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.Verified, contentDescription = null, tint = EmeraldTertiary, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Bounty: Active", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold), color = OnSurface)
                }
            }
        }

        // 7-Day Star Harvest Visualizer Card
        GlassCard(cornerRadius = 20.dp) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("7-DAY STAR HARVEST", style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.8.sp), color = OnSurfaceVariant)
                Text("+10 Net Gain", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = EmeraldTertiary)
            }

            Spacer(Modifier.height(12.dp))

            // Mini bar track
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                val harvestBars = listOf(
                    "M" to 0.4f,
                    "T" to 0.85f,
                    "W" to 0.3f,
                    "T" to 1.0f,
                    "F" to 0.5f,
                    "S" to 0.25f,
                    "S" to 0.7f
                )
                harvestBars.forEachIndexed { idx, (day, frac) ->
                    val isPeak = idx == 1 || idx == 3
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(frac)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(if (isPeak) StarGoldPrimary else StarGoldPrimary.copy(alpha = 0.35f))
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = day,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isPeak) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isPeak) StarGoldPrimary else OnSurfaceVariant
                        )
                    }
                }
            }
        }

        // Ledger Filters & Toggle Empty Demo
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    onClick = { activeFilter = LedgerFilter.ALL },
                    shape = CircleShape,
                    color = if (activeFilter == LedgerFilter.ALL) StarGoldPrimary else SurfaceContainerHigh
                ) {
                    Text(
                        text = "All Activity",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (activeFilter == LedgerFilter.ALL) OnPrimary else OnSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }

                Surface(
                    onClick = { activeFilter = LedgerFilter.EARNED },
                    shape = CircleShape,
                    color = if (activeFilter == LedgerFilter.EARNED) StarGoldPrimary else SurfaceContainerHigh
                ) {
                    Text(
                        text = "Earned (+)",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (activeFilter == LedgerFilter.EARNED) OnPrimary else OnSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }

                Surface(
                    onClick = { activeFilter = LedgerFilter.SPENT },
                    shape = CircleShape,
                    color = if (activeFilter == LedgerFilter.SPENT) StarGoldPrimary else SurfaceContainerHigh
                ) {
                    Text(
                        text = "Spent (-)",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (activeFilter == LedgerFilter.SPENT) OnPrimary else OnSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Ledger List Items
        if (filteredLedger.isEmpty()) {
            GlassCard(
                cornerRadius = 20.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainerHighest)
                    ) {
                        Icon(Icons.Filled.AutoStories, contentDescription = null, tint = StarGoldPrimary, modifier = Modifier.size(28.dp))
                    }
                    Text("No transactions yet", style = MaterialTheme.typography.headlineSmall, color = OnSurface)
                    Text(
                        text = "Complete your daily habits or stay within focus intervals to earn stars.",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                filteredLedger.forEach { item ->
                    val isEarned = item.starsDelta > 0
                    GlassCard(cornerRadius = 18.dp) {
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
                                        .clip(CircleShape)
                                        .background(
                                            if (isEarned) StarGoldPrimary.copy(alpha = 0.15f)
                                            else ErrorContainerRed.copy(alpha = 0.3f)
                                        )
                                ) {
                                    Icon(
                                        imageVector = if (isEarned) Icons.Filled.MilitaryTech else Icons.Filled.LockOpen,
                                        contentDescription = null,
                                        tint = if (isEarned) StarGoldPrimary else ErrorRed,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                                        color = OnSurface
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(item.timestampFormatted, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                                        Text("•", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                                        Text(
                                            text = item.note,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (isEarned) OnSurfaceVariant else ErrorRed
                                        )
                                    }
                                }
                            }

                            Surface(
                                shape = CircleShape,
                                color = if (isEarned) StarGoldPrimaryContainer else ErrorContainerRed
                            ) {
                                Text(
                                    text = if (isEarned) "+${item.starsDelta} ★" else "${item.starsDelta} ★",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (isEarned) OnPrimary else ErrorRed,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Ambient Reward Policy Banner
        GlassCard(cornerRadius = 18.dp) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.Spa,
                    contentDescription = null,
                    tint = IrisPurpleSecondary,
                    modifier = Modifier.size(22.dp)
                )
                Column {
                    Text(
                        text = "Mindful Friction Mechanic",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = OnSurface
                    )
                    Text(
                        text = "Stars represent reclamation tokens, not currency. Spending them to unlock early is an intentional pause to protect your sacred hours.",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                }
            }
        }

        Spacer(Modifier.height(60.dp))
    }
}
