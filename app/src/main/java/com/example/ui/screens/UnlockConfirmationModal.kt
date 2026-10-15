package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.AppItem
import com.example.ui.theme.*

@Composable
fun UnlockConfirmationModal(
    app: AppItem,
    currentBalance: Int,
    cost: Int = 5,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    val hasEnoughStars = currentBalance >= cost
    val remainingAfterUnlock = (currentBalance - cost).coerceAtLeast(0)

    Dialog(onDismissRequest = onCancel) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = SurfaceContainerHigh.copy(alpha = 0.96f),
            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderDefault),
            shadowElevation = 32.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Star Cost Emblem
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(SurfaceContainerLowest)
                        .border(1.5.dp, StarGoldPrimary.copy(alpha = 0.5f), CircleShape)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(StarGoldPrimaryContainer, StarGoldPrimary)
                                )
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = SurfaceDark,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Minus pill
                    Surface(
                        shape = CircleShape,
                        color = ErrorContainerRed,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 2.dp, y = 2.dp)
                    ) {
                        Text(
                            text = "-$cost",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                            color = ErrorRed,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Modal Headings
                Text(
                    text = "Unlock ${app.name} early?",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = OnSurface,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "This will lift restrictions for 15 minutes. Unlocking uses hard-earned stars and may affect your weekly 5-star bounty.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                // Cost Breakdown Inner Glass Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SurfaceContainerLowest.copy(alpha = 0.8f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Row 1: Unlock Cost
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Filled.Toll, contentDescription = null, tint = StarGoldPrimaryContainer, modifier = Modifier.size(16.dp))
                                Text("Unlock Cost", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                            }
                            Surface(shape = CircleShape, color = StarGoldPrimaryContainer.copy(alpha = 0.2f)) {
                                Text(
                                    text = "-$cost Stars",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = StarGoldPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        HorizontalDivider(color = SurfaceContainerHighest.copy(alpha = 0.4f))

                        // Row 2: Current Balance
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Filled.AccountBalanceWallet, contentDescription = null, tint = IrisPurpleSecondary, modifier = Modifier.size(16.dp))
                                Text("Current Balance", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                            }
                            Text(
                                text = "$currentBalance Stars",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = OnSurface
                            )
                        }

                        // Row 3: Remaining Balance
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Filled.LockReset, contentDescription = null, tint = EmeraldTertiary, modifier = Modifier.size(16.dp))
                                Text("Remaining After Unlock", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariant)
                            }
                            Text(
                                text = "$remainingAfterUnlock Stars",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = EmeraldTertiary
                            )
                        }
                    }
                }

                // If not enough stars, show alert banner
                if (!hasEnoughStars) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = ErrorContainerRed.copy(alpha = 0.35f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Icon(Icons.Filled.LockClock, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(20.dp))
                            Column {
                                Text("Not enough stars", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = ErrorRed)
                                Text("Need $cost stars, but vault holds $currentBalance stars.", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                            }
                        }
                    }
                }

                // Actions
                Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = onConfirm,
                        enabled = hasEnoughStars,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StarGoldPrimaryContainer,
                            disabledContainerColor = SurfaceContainerHighest.copy(alpha = 0.5f),
                            disabledContentColor = OnSurfaceVariant.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Filled.LockOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                            Text(
                                text = if (hasEnoughStars) "Confirm Unlock (Spend $cost Stars)" else "Not enough stars (Need $cost, have $currentBalance)",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    Button(
                        onClick = onCancel,
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerLowest.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Filled.Close, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(16.dp))
                            Text("Keep Restricted (Cancel)", style = MaterialTheme.typography.labelMedium, color = OnSurface)
                        }
                    }
                }

                // Motivational note
                Text(
                    text = "Staying locked for 45 more mins earns +2 bonus stars",
                    style = MaterialTheme.typography.bodySmall,
                    color = EmeraldTertiary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
