package com.example.ui.screens

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppItem
import com.example.ui.components.getAppVectorIcon
import com.example.ui.theme.*

@Composable
fun SimulatedAppView(
    app: AppItem,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceContainerLowest)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Simulated App Top Bar
            Surface(
                color = SurfaceContainerLow,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        IconButton(onClick = onClose) {
                            Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = "Return", tint = OnSurface)
                        }
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceContainerHigh)
                        ) {
                            Icon(
                                imageVector = getAppVectorIcon(app.iconKey),
                                contentDescription = app.name,
                                tint = StarGoldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = app.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = OnSurface
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = if (app.isSystemProtected) EmeraldTertiaryContainer.copy(alpha = 0.25f) else StarGoldPrimaryContainer.copy(alpha = 0.25f)
                    ) {
                        Text(
                            text = if (app.isSystemProtected) "SYSTEM SAFE" else "UNLOCKED (15m)",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                            color = if (app.isSystemProtected) EmeraldTertiary else StarGoldPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Simulated App Content Feed
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Return Banner
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SurfaceContainer.copy(alpha = 0.8f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderDefault),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Aether Focus Shield Paused",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = OnSurface
                            )
                            Text(
                                text = "You unlocked this app using stars. Use mindfully.",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnSurfaceVariant
                            )
                        }
                        Button(
                            onClick = onClose,
                            colors = ButtonDefaults.buttonColors(containerColor = StarGoldPrimaryContainer),
                            shape = CircleShape,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Lock App", style = MaterialTheme.typography.labelSmall, color = OnPrimary)
                        }
                    }
                }

                // Simulated Content Blocks
                repeat(4) { idx ->
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = SurfaceContainerLow,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(SurfaceContainerHighest)
                                )
                                Column {
                                    Box(
                                        modifier = Modifier
                                            .size(90.dp, 12.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(SurfaceContainerHighest)
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp, 8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(SurfaceContainerHighest.copy(alpha = 0.6f))
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceContainerHigh)
                            ) {
                                Icon(
                                    imageVector = getAppVectorIcon(app.iconKey),
                                    contentDescription = null,
                                    tint = OnSurfaceVariant.copy(alpha = 0.2f),
                                    modifier = Modifier
                                        .size(48.dp)
                                        .align(Alignment.Center)
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(SurfaceContainerHighest))
                                Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(SurfaceContainerHighest))
                                Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(SurfaceContainerHighest))
                            }
                        }
                    }
                }
            }
        }
    }
}
