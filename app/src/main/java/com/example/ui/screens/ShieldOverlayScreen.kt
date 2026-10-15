package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppItem
import com.example.model.ScreenTab
import com.example.ui.components.getAppVectorIcon
import com.example.ui.theme.*
import com.example.viewmodel.FocusViewModel

@Composable
fun ShieldOverlayScreen(
    app: AppItem,
    viewModel: FocusViewModel,
    onDismiss: () -> Unit,
    onRequestUnlock: (AppItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val wallet by viewModel.wallet.collectAsState()
    val schedule by viewModel.schedule.collectAsState()
    val simulatedTimeMillis by viewModel.simulatedTimeMillis.collectAsState()
    val remainingIntervalText = viewModel.getRemainingIntervalText(simulatedTimeMillis)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceDark.copy(alpha = 0.95f))
    ) {
        // Blurred background simulation cards (representing the blocked app)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .alpha(0.25f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SurfaceContainerHigh,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {}
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SurfaceContainerHighest,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {}
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SurfaceContainerHigh,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {}
        }

        // Deep Glass Tint Scrim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SurfaceContainerLowest.copy(alpha = 0.88f))
        )

        // Main Monolith Shield Container
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = SurfaceContainer.copy(alpha = 0.92f),
                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderDefault),
                shadowElevation = 24.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Cosmic Shield Emblem
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(StarGoldPrimaryContainer.copy(alpha = 0.35f), SurfaceContainerHigh)
                                )
                            )
                            .border(2.dp, StarGoldPrimary.copy(alpha = 0.6f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Shield,
                            contentDescription = null,
                            tint = StarGoldPrimary,
                            modifier = Modifier.size(48.dp)
                        )
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = null,
                            tint = SurfaceDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // App Identity Badge Pill
                    Surface(
                        shape = CircleShape,
                        color = SurfaceContainerHighest
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = getAppVectorIcon(app.iconKey),
                                contentDescription = null,
                                tint = StarGoldPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${app.name} is Shielded",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = OnSurface
                            )
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(EmeraldTertiary))
                        }
                    }

                    // Primary Messaging
                    Text(
                        text = "This app is restricted until ${schedule.endHour}:00 ${schedule.endPeriod}",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = OnSurface,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Take a deep breath. Your evening focus window has $remainingIntervalText remaining.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    // Focus Bounty Incentive Card
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SurfaceContainerHigh.copy(alpha = 0.8f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(StarGoldPrimary.copy(alpha = 0.2f))
                            ) {
                                Icon(Icons.Filled.HotelClass, contentDescription = null, tint = StarGoldPrimary, modifier = Modifier.size(20.dp))
                            }
                            Column {
                                Text(
                                    text = "FOCUS BOUNTY",
                                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                                    color = StarGoldPrimary
                                )
                                Text(
                                    text = "Staying focused tonight will earn +2 Stars towards your weekly sanctuary bounty.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    // Primary Button: Unlock with 5 Stars
                    Button(
                        onClick = { onRequestUnlock(app) },
                        colors = ButtonDefaults.buttonColors(containerColor = StarGoldPrimaryContainer),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Filled.Stars, contentDescription = null, tint = OnPrimary, modifier = Modifier.size(22.dp))
                            Text(
                                text = "Unlock with 5 Stars",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = OnPrimary
                            )
                            Icon(Icons.Filled.ArrowForward, contentDescription = null, tint = OnPrimary, modifier = Modifier.size(18.dp))
                        }
                    }

                    // Balance & Wallet Link
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Filled.Star, contentDescription = null, tint = StarGoldPrimary, modifier = Modifier.size(16.dp))
                            Text("Current Balance: ${wallet.balance} Stars", style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
                        }

                        Text(
                            text = "View Star Wallet",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = IrisPurpleSecondary,
                            modifier = Modifier.clickable {
                                onDismiss()
                                viewModel.setTab(ScreenTab.WALLET)
                            }
                        )
                    }

                    HorizontalDivider(color = SurfaceContainerHighest.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 4.dp))

                    // Close and stay focused
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHighest.copy(alpha = 0.7f)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Filled.NaturePeople, contentDescription = null, tint = OnSurface, modifier = Modifier.size(18.dp))
                            Text(
                                text = "Close and stay focused",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = OnSurface
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Bottom Reassurance Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Filled.VerifiedUser, contentDescription = null, tint = EmeraldTertiary, modifier = Modifier.size(16.dp))
                Text(
                    text = "Aether Screen-Time Shield is actively guarding your intention.",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
            }
        }
    }
}


