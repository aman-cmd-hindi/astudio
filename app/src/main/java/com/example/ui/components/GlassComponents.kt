package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AmbientGlassBackdrop(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .drawBehind {
                // Top-right Iris Purple glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(IrisPurpleSecondaryContainer.copy(alpha = 0.28f), Color.Transparent),
                        center = Offset(size.width * 0.9f, size.height * 0.1f),
                        radius = size.width * 0.65f
                    )
                )
                // Mid-left Amber Warmth glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(StarGoldPrimaryContainer.copy(alpha = 0.18f), Color.Transparent),
                        center = Offset(size.width * 0.1f, size.height * 0.45f),
                        radius = size.width * 0.55f
                    )
                )
                // Bottom-right Emerald Celestial glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(EmeraldTertiaryContainer.copy(alpha = 0.15f), Color.Transparent),
                        center = Offset(size.width * 0.8f, size.height * 0.85f),
                        radius = size.width * 0.6f
                    )
                )
            }
    ) {
        content()
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    backgroundColor: Color = SurfaceContainer.copy(alpha = 0.70f),
    borderColor: Color = GlassBorderDefault,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val cardModifier = if (onClick != null) {
        modifier
            .clip(shape)
            .background(backgroundColor)
            .border(1.dp, borderColor, shape)
            .clickable(onClick = onClick)
    } else {
        modifier
            .clip(shape)
            .background(backgroundColor)
            .border(1.dp, borderColor, shape)
    }

    Column(
        modifier = cardModifier.padding(16.dp),
        content = content
    )
}

@Composable
fun StarPillBadge(
    stars: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = StarGoldPrimaryContainer.copy(alpha = 0.20f),
        border = androidx.compose.foundation.BorderStroke(1.dp, StarGoldPrimary.copy(alpha = 0.45f)),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Stars",
                tint = StarGoldPrimary,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "$stars Stars",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = StarGoldPrimary
            )
        }
    }
}

@Composable
fun CustomGlassToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color = EmeraldTertiary
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = SurfaceDark,
            checkedTrackColor = activeColor,
            uncheckedThumbColor = OnSurfaceVariant,
            uncheckedTrackColor = SurfaceContainerHighest
        ),
        modifier = modifier
    )
}

@Composable
fun FloatingDemoTimeController(
    simulatedTimeMillis: Long,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onAdvanceTime: (Long) -> Unit,
    onJump11PM: () -> Unit,
    onJump11AM: () -> Unit,
    onResetRealTime: () -> Unit,
    onTriggerPayout: () -> Unit,
    isRestricted: Boolean,
    modifier: Modifier = Modifier
) {
    val timeFormat = SimpleDateFormat("h:mm a, EEE", Locale.getDefault())
    val formattedTime = timeFormat.format(Date(simulatedTimeMillis))

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SurfaceContainerLowest.copy(alpha = 0.92f),
        border = androidx.compose.foundation.BorderStroke(1.dp, IrisPurpleSecondary.copy(alpha = 0.4f)),
        shadowElevation = 8.dp,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isRestricted) IrisPurpleSecondaryContainer else EmeraldTertiary)
                    )
                    Text(
                        text = "Demo Clock: $formattedTime",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = OnSurface
                    )
                    Surface(
                        shape = CircleShape,
                        color = if (isRestricted) IrisPurpleSecondaryContainer.copy(alpha = 0.4f) else EmeraldTertiary.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = if (isRestricted) "RESTRICTED" else "FREE",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = if (isRestricted) IrisPurpleSecondary else EmeraldTertiary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                    contentDescription = "Expand Clock",
                    tint = OnSurfaceVariant
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.padding(top = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Fast Forward / Time Shift to test intervals & payouts:",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { onAdvanceTime(15) },
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHigh),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+15m", style = MaterialTheme.typography.labelSmall, color = OnSurface)
                        }
                        Button(
                            onClick = { onAdvanceTime(60) },
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHigh),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+1h", style = MaterialTheme.typography.labelSmall, color = OnSurface)
                        }
                        Button(
                            onClick = { onAdvanceTime(480) },
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHigh),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+8h", style = MaterialTheme.typography.labelSmall, color = OnSurface)
                        }
                        Button(
                            onClick = { onAdvanceTime(1440) },
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHigh),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+1d", style = MaterialTheme.typography.labelSmall, color = OnSurface)
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = onJump11PM,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = IrisPurpleSecondary),
                            border = androidx.compose.foundation.BorderStroke(1.dp, IrisPurpleSecondary.copy(alpha = 0.5f)),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("11:00 PM (Restricted)", style = MaterialTheme.typography.labelSmall)
                        }
                        OutlinedButton(
                            onClick = onJump11AM,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = EmeraldTertiary),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldTertiary.copy(alpha = 0.5f)),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("11:00 AM (Free)", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = onTriggerPayout,
                            colors = ButtonDefaults.buttonColors(containerColor = StarGoldPrimaryContainer),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.weight(1.3f)
                        ) {
                            Icon(Icons.Filled.Stars, contentDescription = null, modifier = Modifier.size(14.dp), tint = OnPrimary)
                            Spacer(Modifier.width(4.dp))
                            Text("Claim 5★ Payout", style = MaterialTheme.typography.labelSmall, color = OnPrimary)
                        }
                        Button(
                            onClick = onResetRealTime,
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHighest),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Real Time", style = MaterialTheme.typography.labelSmall, color = OnSurface)
                        }
                    }
                }
            }
        }
    }
}
