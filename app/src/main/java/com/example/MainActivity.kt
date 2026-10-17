package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScreenTab
import com.example.ui.components.AmbientGlassBackdrop
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.FocusViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: FocusViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleShieldIntent(intent)
        setContent {
            AetherFocusTheme {
                AetherFocusApp(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleShieldIntent(intent)
    }

    private fun handleShieldIntent(intent: Intent?) {
        if (intent?.action == "com.example.ACTION_SHOW_SHIELD") {
            val appId = intent.getStringExtra("EXTRA_APP_ID")
            if (!appId.isNullOrEmpty()) {
                viewModel.showShieldForPackage(appId)
            }
        }
    }
}

@Composable
fun AetherFocusApp(viewModel: FocusViewModel) {
    val context = LocalContext.current
    val currentTab by viewModel.currentTab.collectAsState()
    val wallet by viewModel.wallet.collectAsState()

    val activeShieldApp by viewModel.activeShieldApp.collectAsState()
    val unlockConfirmationApp by viewModel.unlockConfirmationApp.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    var showPermissionsScreen by remember { mutableStateOf(false) }

    // System Back Handler
    BackHandler(
        enabled = activeShieldApp != null ||
                unlockConfirmationApp != null ||
                showPermissionsScreen ||
                currentTab != ScreenTab.HOME
    ) {
        when {
            unlockConfirmationApp != null -> viewModel.cancelUnlockConfirmation()
            activeShieldApp != null -> viewModel.dismissShield()
            showPermissionsScreen -> showPermissionsScreen = false
            currentTab == ScreenTab.PRIVACY_POLICY -> viewModel.setTab(ScreenTab.SETTINGS)
            currentTab != ScreenTab.HOME -> viewModel.setTab(ScreenTab.HOME)
        }
    }

    AmbientGlassBackdrop {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                ) {
                    // Top App Header
                    Surface(
                        color = SurfaceContainerLowest.copy(alpha = 0.75f),
                        shadowElevation = 4.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            // Logo + Title
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(IrisPurpleSecondaryContainer)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = "Logo",
                                        tint = StarGoldPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "AETHER FOCUS",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            letterSpacing = 1.2.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        ),
                                        color = StarGoldPrimary
                                    )
                                    Text(
                                        text = when (currentTab) {
                                            ScreenTab.HOME -> "Home Sanctuary"
                                            ScreenTab.SCREEN_TIME -> "Screen Time Analysis"
                                            ScreenTab.BLOCKED_APPS -> "App Blocker"
                                            ScreenTab.HABITS -> "Habit Tracker"
                                            ScreenTab.WALLET -> "Star Wallet"
                                            ScreenTab.SETTINGS -> "Settings & Privacy"
                                            ScreenTab.SCHEDULE -> "Focus Schedule"
                                            ScreenTab.PRIVACY_POLICY -> "Privacy Policy"
                                        },
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = OnSurface
                                    )
                                }
                            }

                            // Star count badge & profile button
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    onClick = { viewModel.setTab(ScreenTab.WALLET) },
                                    shape = CircleShape,
                                    color = SurfaceContainerHigh.copy(alpha = 0.7f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, StarGoldPrimary.copy(alpha = 0.3f))
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.HotelClass,
                                            contentDescription = null,
                                            tint = StarGoldPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = "${wallet.balance}",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = StarGoldPrimary
                                        )
                                    }
                                }

                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(StarGoldPrimary)
                                        .clickable { viewModel.setTab(ScreenTab.SETTINGS) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Shield,
                                        contentDescription = "Settings",
                                        tint = OnPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            bottomBar = {
                // Elevated Frosted Glass Dock
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = SurfaceContainer.copy(alpha = 0.90f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderDefault),
                        shadowElevation = 16.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            NavBarItem(
                                label = "Home",
                                icon = Icons.Filled.Spa,
                                isSelected = currentTab == ScreenTab.HOME,
                                onClick = { viewModel.setTab(ScreenTab.HOME) }
                            )

                            NavBarItem(
                                label = "Screen Time",
                                icon = Icons.Filled.Timelapse,
                                isSelected = currentTab == ScreenTab.SCREEN_TIME,
                                onClick = { viewModel.setTab(ScreenTab.SCREEN_TIME) }
                            )

                            NavBarItem(
                                label = "Blocker",
                                icon = Icons.Filled.Shield,
                                isSelected = currentTab == ScreenTab.BLOCKED_APPS,
                                onClick = { viewModel.setTab(ScreenTab.BLOCKED_APPS) }
                            )

                            NavBarItem(
                                label = "Habits",
                                icon = Icons.Filled.SelfImprovement,
                                isSelected = currentTab == ScreenTab.HABITS,
                                onClick = { viewModel.setTab(ScreenTab.HABITS) }
                            )

                            NavBarItem(
                                label = "Wallet",
                                icon = Icons.Filled.Stars,
                                isSelected = currentTab == ScreenTab.WALLET,
                                onClick = { viewModel.setTab(ScreenTab.WALLET) }
                            )

                            NavBarItem(
                                label = "Settings",
                                icon = Icons.Filled.Tune,
                                isSelected = currentTab == ScreenTab.SETTINGS || currentTab == ScreenTab.PRIVACY_POLICY,
                                onClick = { viewModel.setTab(ScreenTab.SETTINGS) }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Active Screen Switcher
                AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = "tab_transition"
                ) { targetTab ->
                    when (targetTab) {
                        ScreenTab.HOME -> HomeScreen(viewModel = viewModel)
                        ScreenTab.SCREEN_TIME -> ScreenTimeScreen(viewModel = viewModel)
                        ScreenTab.BLOCKED_APPS -> BlockedAppsScreen(viewModel = viewModel)
                        ScreenTab.HABITS -> HabitsScreen(viewModel = viewModel)
                        ScreenTab.WALLET -> WalletScreen(viewModel = viewModel)
                        ScreenTab.SETTINGS -> SettingsScreen(
                            viewModel = viewModel,
                            onOpenPermissions = { showPermissionsScreen = true },
                            onOpenPrivacyPolicy = { viewModel.setTab(ScreenTab.PRIVACY_POLICY) }
                        )
                        ScreenTab.SCHEDULE -> ScheduleScreen(viewModel = viewModel)
                        ScreenTab.PRIVACY_POLICY -> PrivacyPolicyScreen(
                            onBack = { viewModel.setTab(ScreenTab.SETTINGS) }
                        )
                    }
                }

                // Toast Notification Banner (Floating pill)
                if (toastMessage != null) {
                    Surface(
                        shape = CircleShape,
                        color = SurfaceContainerHighest.copy(alpha = 0.95f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StarGoldPrimary.copy(alpha = 0.4f)),
                        shadowElevation = 16.dp,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Filled.Info, contentDescription = null, tint = StarGoldPrimary, modifier = Modifier.size(18.dp))
                            Text(
                                text = toastMessage ?: "",
                                style = MaterialTheme.typography.labelMedium,
                                color = OnSurface
                            )
                        }
                    }
                }
            }
        }

        // Onboarding / Permissions Full Screen Modal
        if (showPermissionsScreen) {
            PermissionsScreen(
                viewModel = viewModel,
                onContinue = { showPermissionsScreen = false },
                onOpenPrivacyPolicy = {
                    showPermissionsScreen = false
                    viewModel.setTab(ScreenTab.PRIVACY_POLICY)
                }
            )
        }

        // Full Screen Blocked-App Shield Overlay
        if (activeShieldApp != null) {
            ShieldOverlayScreen(
                app = activeShieldApp!!,
                viewModel = viewModel,
                onDismiss = { viewModel.dismissShield() },
                onRequestUnlock = { app -> viewModel.requestUnlockConfirmation(app) }
            )
        }

        // Unlock Confirmation Modal
        if (unlockConfirmationApp != null) {
            UnlockConfirmationModal(
                app = unlockConfirmationApp!!,
                currentBalance = wallet.balance,
                cost = 5,
                onConfirm = { viewModel.confirmUnlockWithStars(unlockConfirmationApp!!, cost = 5, context = context) },
                onCancel = { viewModel.cancelUnlockConfirmation() }
            )
        }
    }
}

@Composable
fun NavBarItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    hasBadge: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.scale(if (isSelected) 1.05f else 1.0f)
        ) {
            Box {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (isSelected) StarGoldPrimary else OnSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                if (hasBadge) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 3.dp, y = (-2).dp)
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(StarGoldPrimary)
                    )
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (isSelected) StarGoldPrimary else OnSurfaceVariant
            )
        }
    }
}
