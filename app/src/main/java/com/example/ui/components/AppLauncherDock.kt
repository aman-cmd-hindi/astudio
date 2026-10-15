package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppItem
import com.example.ui.theme.*

@Composable
fun AppLauncherDock(
    apps: List<AppItem>,
    onLaunchApp: (AppItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = SurfaceContainerLowest.copy(alpha = 0.85f),
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderDefault),
        shadowElevation = 12.dp,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.Apps,
                        contentDescription = null,
                        tint = StarGoldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "App Simulator Dock",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = OnSurface
                    )
                }
                Text(
                    text = "Tap to test shield",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = OnSurfaceVariant
                )
            }

            Spacer(Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(apps) { app ->
                    DockAppIcon(app = app, onClick = { onLaunchApp(app) })
                }
            }
        }
    }
}

@Composable
fun DockAppIcon(
    app: AppItem,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(54.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (app.isSystemProtected) EmeraldTertiaryContainer.copy(alpha = 0.25f)
                    else if (app.blocked) SurfaceContainerHigh
                    else SurfaceContainerHighest
                )
                .border(
                    1.dp,
                    if (app.blocked) StarGoldPrimary.copy(alpha = 0.3f) else GlassBorderDefault,
                    RoundedCornerShape(12.dp)
                )
        ) {
            Icon(
                imageVector = getAppVectorIcon(app.iconKey),
                contentDescription = app.name,
                tint = if (app.isSystemProtected) EmeraldTertiary
                else if (app.blocked) StarGoldPrimary
                else IrisPurpleSecondary,
                modifier = Modifier.size(22.dp)
            )

            if (app.blocked) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(2.dp)
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(ErrorRed)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Blocked",
                        tint = SurfaceDark,
                        modifier = Modifier
                            .size(8.dp)
                            .align(Alignment.Center)
                    )
                }
            } else if (app.isSystemProtected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(2.dp)
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(EmeraldTertiary)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Safe",
                        tint = SurfaceDark,
                        modifier = Modifier
                            .size(8.dp)
                            .align(Alignment.Center)
                    )
                }
            }
        }
        Spacer(Modifier.height(3.dp))
        Text(
            text = app.name,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
            color = OnSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

fun getAppVectorIcon(key: String): ImageVector {
    return when (key.lowercase()) {
        "phone" -> Icons.Filled.Phone
        "contacts" -> Icons.Filled.ContactPhone
        "instagram" -> Icons.Filled.CameraAlt
        "tiktok" -> Icons.Filled.MusicNote
        "reddit" -> Icons.Filled.Forum
        "netflix" -> Icons.Filled.Movie
        "youtube" -> Icons.Filled.PlayArrow
        "twitter" -> Icons.Filled.Tag
        "slack" -> Icons.Filled.Chat
        "amazon" -> Icons.Filled.ShoppingBag
        else -> Icons.Filled.Apps
    }
}
