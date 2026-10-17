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
import com.example.model.AppItem
import com.example.ui.components.CustomGlassToggle
import com.example.ui.components.GlassCard
import com.example.ui.components.getAppVectorIcon
import com.example.ui.theme.*
import com.example.viewmodel.FocusViewModel

enum class AppFilter {
    ALL, BLOCKED, SYSTEM
}

@Composable
fun BlockedAppsScreen(
    viewModel: FocusViewModel,
    modifier: Modifier = Modifier
) {
    val apps by viewModel.apps.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var currentFilter by remember { mutableStateOf(AppFilter.ALL) }

    val filteredApps = apps.filter { app ->
        val matchesQuery = searchQuery.isEmpty() ||
                app.name.contains(searchQuery, ignoreCase = true) ||
                app.category.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (currentFilter) {
            AppFilter.ALL -> true
            AppFilter.BLOCKED -> app.blocked
            AppFilter.SYSTEM -> app.isSystemProtected
        }

        matchesQuery && matchesFilter
    }

    val systemApps = filteredApps.filter { it.isSystemProtected }
    val userApps = filteredApps.filter { !it.isSystemProtected }
    val blockedCount = apps.count { it.blocked }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Status Aura Banner
        GlassCard(cornerRadius = 20.dp) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(IrisPurpleSecondaryContainer.copy(alpha = 0.4f))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Shield,
                            contentDescription = null,
                            tint = IrisPurpleSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Sanctuary Guard",
                                style = MaterialTheme.typography.headlineSmall,
                                color = OnSurface
                            )
                            Surface(shape = CircleShape, color = EmeraldTertiaryContainer.copy(alpha = 0.3f)) {
                                Text(
                                    text = "ACTIVE",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                    color = EmeraldTertiary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "$blockedCount mindful barriers holding space",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariant
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "YIELD BONUS",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = StarGoldPrimary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Icon(Icons.Filled.Stars, contentDescription = null, tint = StarGoldPrimary, modifier = Modifier.size(14.dp))
                        Text("+2.4x/hr", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = StarGoldPrimary)
                    }
                }
            }
        }

        // Frosted Search Field
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SurfaceContainerLowest.copy(alpha = 0.75f),
            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderDefault),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search",
                    tint = OnSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(8.dp))
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            "Search 42 installed apps...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurfaceVariant.copy(alpha = 0.6f)
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.weight(1f)
                )
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Filled.Close, contentDescription = "Clear", tint = OnSurfaceVariant)
                    }
                }
            }
        }

        // Filter Chips Segment
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // All
            Surface(
                onClick = { currentFilter = AppFilter.ALL },
                shape = CircleShape,
                color = if (currentFilter == AppFilter.ALL) SurfaceContainerHighest else SurfaceContainerLow
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(StarGoldPrimary))
                    Text(
                        text = "All (${apps.size})",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = if (currentFilter == AppFilter.ALL) OnSurface else OnSurfaceVariant
                    )
                }
            }

            // Blocked
            Surface(
                onClick = { currentFilter = AppFilter.BLOCKED },
                shape = CircleShape,
                color = if (currentFilter == AppFilter.BLOCKED) SurfaceContainerHighest else SurfaceContainerLow
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Filled.Lock, contentDescription = null, tint = EmeraldTertiary, modifier = Modifier.size(14.dp))
                    Text(
                        text = "Blocked ($blockedCount)",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = if (currentFilter == AppFilter.BLOCKED) OnSurface else OnSurfaceVariant
                    )
                }
            }

            // System Safe
            Surface(
                onClick = { currentFilter = AppFilter.SYSTEM },
                shape = CircleShape,
                color = if (currentFilter == AppFilter.SYSTEM) SurfaceContainerHighest else SurfaceContainerLow
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Filled.VerifiedUser, contentDescription = null, tint = StarGoldPrimary, modifier = Modifier.size(14.dp))
                    Text(
                        text = "System Safe (2)",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = if (currentFilter == AppFilter.SYSTEM) OnSurface else OnSurfaceVariant
                    )
                }
            }
        }



        // Section 1: System Protected Core (Phone, Contacts)
        if (systemApps.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Filled.VerifiedUser, contentDescription = null, tint = EmeraldTertiary, modifier = Modifier.size(14.dp))
                        Text(
                            text = "SYSTEM PROTECTED CORE",
                            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.8.sp),
                            color = OnSurfaceVariant
                        )
                    }
                    Text(
                        text = "Immutable Safe",
                        style = MaterialTheme.typography.labelSmall,
                        color = EmeraldTertiary
                    )
                }

                systemApps.forEach { app ->
                    GlassCard(
                        cornerRadius = 18.dp,
                        onClick = { viewModel.launchApp(app) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(
                                            if (app.id == "phone") EmeraldTertiaryContainer.copy(alpha = 0.3f)
                                            else IrisPurpleSecondaryContainer.copy(alpha = 0.3f)
                                        )
                                ) {
                                    Icon(
                                        imageVector = getAppVectorIcon(app.iconKey),
                                        contentDescription = app.name,
                                        tint = if (app.id == "phone") EmeraldTertiary else IrisPurpleSecondary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = app.name,
                                        style = MaterialTheme.typography.headlineSmall.copy(fontSize = 16.sp),
                                        color = OnSurface
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(app.category, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                                        Text("•", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                                        Text(app.avgDailyUsage, style = MaterialTheme.typography.bodySmall, color = EmeraldTertiary)
                                    }
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "ALWAYS ALLOWED",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = OnSurfaceVariant
                                )
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(SurfaceContainerHighest)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Lock,
                                        contentDescription = "Always Allowed",
                                        tint = OnSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Configured Sanctuaries (User Blocked & Managed Apps)
        if (userApps.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Filled.FilterDrama, contentDescription = null, tint = StarGoldPrimary, modifier = Modifier.size(14.dp))
                        Text(
                            text = "CONFIGURED SANCTUARIES",
                            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.8.sp),
                            color = OnSurfaceVariant
                        )
                    }
                    Text(
                        text = "$blockedCount Blocked",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = StarGoldPrimary
                    )
                }

                userApps.forEach { app ->
                    GlassCard(
                        cornerRadius = 18.dp,
                        onClick = { viewModel.launchApp(app) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
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
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(SurfaceContainerHigh)
                                ) {
                                    Icon(
                                        imageVector = getAppVectorIcon(app.iconKey),
                                        contentDescription = app.name,
                                        tint = if (app.blocked) StarGoldPrimary else OnSurfaceVariant,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = app.name,
                                            style = MaterialTheme.typography.headlineSmall.copy(fontSize = 16.sp),
                                            color = OnSurface
                                        )
                                        if (app.blocked) {
                                            Icon(Icons.Filled.Lock, contentDescription = "Shield Engaged", tint = EmeraldTertiary, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(app.category, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                                        Text("•", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                                        Text(
                                            text = if (app.blocked) app.avgDailyUsage else "Allowed (Unprotected)",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (app.blocked) StarGoldPrimary else OnSurfaceVariant
                                        )
                                    }
                                }
                            }

                            CustomGlassToggle(
                                checked = app.blocked,
                                onCheckedChange = { viewModel.toggleAppBlocked(app.id) },
                                activeColor = EmeraldTertiary
                            )
                        }
                    }
                }
            }
        }

        // Empty Search State
        if (filteredApps.isEmpty()) {
            GlassCard(
                cornerRadius = 24.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainerHigh)
                    ) {
                        Icon(Icons.Filled.ManageSearch, contentDescription = null, tint = StarGoldPrimary, modifier = Modifier.size(30.dp))
                    }
                    Text("No apps match", style = MaterialTheme.typography.headlineSmall, color = OnSurface)
                    Text(
                        text = if (searchQuery.isNotEmpty()) "Nothing discovered for \"$searchQuery\"" else "No apps in this category.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant
                    )
                    Button(
                        onClick = {
                            searchQuery = ""
                            currentFilter = AppFilter.ALL
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = StarGoldPrimary),
                        shape = CircleShape
                    ) {
                        Icon(Icons.Filled.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp), tint = OnPrimary)
                        Spacer(Modifier.width(4.dp))
                        Text("Show All Apps", color = OnPrimary)
                    }
                }
            }
        }


        Spacer(Modifier.height(60.dp))
    }
}
