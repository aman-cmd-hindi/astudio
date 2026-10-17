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
