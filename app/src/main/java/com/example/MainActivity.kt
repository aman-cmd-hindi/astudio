package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScreenTab
import com.example.ui.components.AmbientGlassBackdrop
import com.example.ui.components.FloatingDemoTimeController
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.FocusViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: FocusViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AetherFocusTheme {
                AetherFocusApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun AetherFocusApp(viewModel: FocusViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val simulatedTimeMillis by viewModel.simulatedTimeMillis.collectAsState()
    val isDemoExpanded by viewModel.isDemoControlsExpanded.collectAsState()
    val isRestricted = viewModel.isWithinInterval(simulatedTimeMillis)
    val wallet by viewModel.wallet.collectAsState()

    val activeShieldApp by viewModel.activeShieldApp.collectAsState()
    val unlockConfirmationApp by viewModel.unlockConfirmationApp.collectAsState()
    val currentlyOpenApp by viewModel.currentlyOpenSimulatedApp.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    var showPermissionsScreen by remember { mutableStateOf(false) }

    // System Back Handler
    BackHandler(
        enabled = activeShieldApp != null ||
                unlockConfirmationApp != null ||
                currentlyOpenApp != null ||
                showPermissionsScreen ||
                currentTab != ScreenTab.HOME
    ) {
        when {
            unlockConfirmationApp != null -> viewModel.cancelUnlockConfirmation()
            activeShieldApp != null -> viewModel.dismissShield()
            currentlyOpenApp != null -> viewModel.closeSimulatedApp()
            showPermissionsScreen -> showPermissionsScreen = false
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
                        color = SurfaceContainerLowest.copy(alpha = 0.70f),
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
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(IrisPurpleSecondaryContainer)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = "Logo",
                                        tint = StarGoldPrimary,
                                        modifier = Modifier.size(18.dp)
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
                                            ScreenTab.HOME -> "Home"
                                            ScreenTab.SCREEN_TIME -> "Screen Time"
                                            ScreenTab.BLOCKED_APPS -> "Blocked Apps"
                                            ScreenTab.WALLET -> "Wallet"
                                            ScreenTab.SETTINGS -> "Settings"
                                            ScreenTab.SCHEDULE -> "Schedule"
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
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(StarGoldPrimary)
                                        .clickable { viewModel.setTab(ScreenTab.SETTINGS) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Person,
                                        contentDescription = "Profile",
                                        tint = OnPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(6.dp))

                    // Floating Demo Time Machine HUD Pill
                    FloatingDemoTimeController(
                        simulatedTimeMillis = simulatedTimeMillis,
                        isExpanded = isDemoExpanded,
                        onToggleExpand = { viewModel.toggleDemoControls() },
                        onAdvanceTime = { mins -> viewModel.advanceSimulatedTime(mins) },
                        onJump11PM = { viewModel.jumpToSpecificHour(23, 0) },
                        onJump11AM = { viewModel.jumpToSpecificHour(11, 0) },
                        onResetRealTime = { viewModel.resetToRealTime() },
                        onTriggerPayout = { viewModel.triggerWeeklyPayoutCheck() },
                        isRestricted = isRestricted
                    )
                }
            },
            bottomBar = {
                // Elevated Frosted Pill Dock
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = SurfaceContainer.copy(alpha = 0.85f),
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
                                label = "Shield",
                                icon = Icons.Filled.Shield,
                                isSelected = currentTab == ScreenTab.BLOCKED_APPS,
                                onClick = { viewModel.setTab(ScreenTab.BLOCKED_APPS) }
                            )

                            NavBarItem(
                                label = "Wallet",
                                icon = Icons.Filled.Stars,
                                isSelected = currentTab == ScreenTab.WALLET,
                                hasBadge = true,
                                onClick = { viewModel.setTab(ScreenTab.WALLET) }
                            )

                            NavBarItem(
                                label = "Settings",
                                icon = Icons.Filled.Tune,
                                isSelected = currentTab == ScreenTab.SETTINGS,
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
                        ScreenTab.WALLET -> WalletScreen(viewModel = viewModel)
                        ScreenTab.SETTINGS -> SettingsScreen(
                            viewModel = viewModel,
                            onOpenPermissions = { showPermissionsScreen = true }
                        )
                        ScreenTab.SCHEDULE -> ScheduleScreen(viewModel = viewModel)
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
                onContinue = { showPermissionsScreen = false }
            )
        }

        // Simulated Unlocked App View
        if (currentlyOpenApp != null) {
            SimulatedAppView(
                app = currentlyOpenApp!!,
                onClose = { viewModel.closeSimulatedApp() }
            )
        }

        // Full Screen Blocked-App Shield Overlay
        if (activeShieldApp != null && currentlyOpenApp == null) {
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
                onConfirm = { viewModel.confirmUnlockWithStars(unlockConfirmationApp!!, cost = 5) },
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
            .padding(horizontal = 10.dp, vertical = 6.dp)
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
                    modifier = Modifier.size(22.dp)
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
                    fontSize = 10.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (isSelected) StarGoldPrimary else OnSurfaceVariant
            )
        }
    }
}
