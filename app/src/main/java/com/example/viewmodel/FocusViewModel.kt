package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FocusRepository
import com.example.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class FocusViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FocusRepository(application)

    // Current Navigation Tab
    private val _currentTab = MutableStateFlow(ScreenTab.HOME)
    val currentTab: StateFlow<ScreenTab> = _currentTab.asStateFlow()

    // Simulated Clock / Time Machine
    private val _simulatedTimeMillis = MutableStateFlow(System.currentTimeMillis())
    val simulatedTimeMillis: StateFlow<Long> = _simulatedTimeMillis.asStateFlow()

    private val _isDemoControlsExpanded = MutableStateFlow(false)
    val isDemoControlsExpanded: StateFlow<Boolean> = _isDemoControlsExpanded.asStateFlow()

    // App State
    private val _apps = MutableStateFlow<List<AppItem>>(emptyList())
    val apps: StateFlow<List<AppItem>> = _apps.asStateFlow()

    private val _schedule = MutableStateFlow(ScheduleConfig())
    val schedule: StateFlow<ScheduleConfig> = _schedule.asStateFlow()

    private val _wallet = MutableStateFlow(WalletState())
    val wallet: StateFlow<WalletState> = _wallet.asStateFlow()

    private val _permissions = MutableStateFlow<List<PermissionItem>>(emptyList())
    val permissions: StateFlow<List<PermissionItem>> = _permissions.asStateFlow()

    // Screen Time Telemetry
    private val _screenTimeData = MutableStateFlow(
        ScreenTimeData(
            usageSinceLastCheckMinutes = 18,
            maxUsageSinceLastCheckLimitMinutes = 45,
            thisWeekDailyAvgMinutes = 135,
            dailyThresholdMinutes = 150,
            lastWeekAvgMinutes = 220,
            totalWeeklyMinutes = 862,
            dailyBars = listOf(
                DailyUsageBar("M", 100, "1h 40m"),
                DailyUsageBar("T", 130, "2h 10m"),
                DailyUsageBar("W", 75, "1h 15m"),
                DailyUsageBar("T", 58, "58m", isHighlighted = true),
                DailyUsageBar("F", 185, "3h 05m"),
                DailyUsageBar("S", 160, "2h 40m"),
                DailyUsageBar("S", 154, "2h 34m", isToday = true, isHighlighted = true)
            )
        )
    )
    val screenTimeData: StateFlow<ScreenTimeData> = _screenTimeData.asStateFlow()

    // Active full-screen Shield and Unlock Modal
    private val _activeShieldApp = MutableStateFlow<AppItem?>(null)
    val activeShieldApp: StateFlow<AppItem?> = _activeShieldApp.asStateFlow()

    private val _unlockConfirmationApp = MutableStateFlow<AppItem?>(null)
    val unlockConfirmationApp: StateFlow<AppItem?> = _unlockConfirmationApp.asStateFlow()

    // Simulated open app view (when unlocked or free)
    private val _currentlyOpenSimulatedApp = MutableStateFlow<AppItem?>(null)
    val currentlyOpenSimulatedApp: StateFlow<AppItem?> = _currentlyOpenSimulatedApp.asStateFlow()

    // Toast message for user actions
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // 30-second recurring tick for interval evaluation
    init {
        loadData()
        startPeriodicTicker()
    }

    private fun loadData() {
        _apps.value = repository.getInitialApps()
        _schedule.value = repository.getSchedule()
        _wallet.value = repository.getWallet()
        _permissions.value = repository.getPermissions()
    }

    private fun startPeriodicTicker() {
        viewModelScope.launch {
            while (true) {
                delay(30_000)
                // Trigger interval re-evaluation
                evaluateCurrentInterval()
            }
        }
    }

    fun setTab(tab: ScreenTab) {
        _currentTab.value = tab
    }

    fun toggleDemoControls() {
        _isDemoControlsExpanded.value = !_isDemoControlsExpanded.value
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
        viewModelScope.launch {
            delay(2800)
            if (_toastMessage.value == msg) {
                _toastMessage.value = null
            }
        }
    }

    // -------------------------------------------------------------
    // Interval Calculation Logic (Timezone-Aware & Overnight Window)
    // -------------------------------------------------------------
    fun isWithinInterval(nowMillis: Long = _simulatedTimeMillis.value): Boolean {
        val config = _schedule.value
        if (!config.enabled) return false

        val cal = Calendar.getInstance().apply { timeInMillis = nowMillis }
        val currentDay = cal.get(Calendar.DAY_OF_WEEK)
        val currentHour = cal.get(Calendar.HOUR_OF_DAY)
        val currentMinute = cal.get(Calendar.MINUTE)
        val currentMinsOfDay = currentHour * 60 + currentMinute

        // Convert Schedule Start to 24h minutes
        var startHour24 = config.startHour % 12
        if (config.startPeriod.equals("PM", ignoreCase = true)) startHour24 += 12
        val startMins = startHour24 * 60 + config.startMinute

        // Convert Schedule End to 24h minutes
        var endHour24 = config.endHour % 12
        if (config.endPeriod.equals("PM", ignoreCase = true)) endHour24 += 12
        val endMins = endHour24 * 60 + config.endMinute

        return if (startMins <= endMins) {
            // Same-day window (e.g. 9:00 AM to 5:00 PM)
            config.activeDays.contains(currentDay) && (currentMinsOfDay in startMins until endMins)
        } else {
            // Overnight window (e.g. 10:00 PM to 8:00 AM)
            if (currentMinsOfDay >= startMins) {
                // In the evening portion of the window
                config.activeDays.contains(currentDay)
            } else if (currentMinsOfDay < endMins) {
                // In the morning portion of the window, belongs to previous day's schedule
                val prevDay = if (currentDay == Calendar.SUNDAY) Calendar.SATURDAY else currentDay - 1
                config.activeDays.contains(prevDay)
            } else {
                false
            }
        }
    }

    fun getRemainingIntervalText(nowMillis: Long = _simulatedTimeMillis.value): String {
        val config = _schedule.value
        if (!config.enabled || !isWithinInterval(nowMillis)) return "Free Window"

        val cal = Calendar.getInstance().apply { timeInMillis = nowMillis }
        val currentHour = cal.get(Calendar.HOUR_OF_DAY)
        val currentMinute = cal.get(Calendar.MINUTE)
        val currentMins = currentHour * 60 + currentMinute

        var endHour24 = config.endHour % 12
        if (config.endPeriod.equals("PM", ignoreCase = true)) endHour24 += 12
        val endMins = endHour24 * 60 + config.endMinute

        var diff = endMins - currentMins
        if (diff <= 0) diff += 24 * 60

        val hours = diff / 60
        val mins = diff % 60
        return "${hours}h ${mins}m"
    }

    private fun evaluateCurrentInterval() {
        // Check if any temporarily unlocked app has expired
        val now = _simulatedTimeMillis.value
        var changed = false
        val updated = _apps.value.map { app ->
            if (app.unlockedUntilEpochMillis != null && now >= app.unlockedUntilEpochMillis) {
                changed = true
                app.copy(unlockedUntilEpochMillis = null)
            } else {
                app
            }
        }
        if (changed) {
            _apps.value = updated
            repository.saveApps(updated)
            // If the currently open app's unlock window ended and interval is active, trigger shield!
            val openApp = _currentlyOpenSimulatedApp.value
            if (openApp != null && openApp.blocked && isWithinInterval(now)) {
                _currentlyOpenSimulatedApp.value = null
                _activeShieldApp.value = openApp
            }
        }
    }

    // -------------------------------------------------------------
    // Star Payout Engine
    // -------------------------------------------------------------
    fun evaluatePayoutConditions(): List<PayoutCondition> {
        val st = _screenTimeData.value
        val condition1 = st.usageSinceLastCheckMinutes < 60
        val condition2 = st.totalWeeklyMinutes < 600
        val condition3 = st.thisWeekDailyAvgMinutes < st.lastWeekAvgMinutes

        return listOf(
            PayoutCondition(
                id = "since_check",
                title = "Usage since last check under 1 hour",
                subtitle = "${st.usageSinceLastCheckMinutes}m sustained (Limit: 60m)",
                isMet = condition1,
                statusText = if (condition1) "Met" else "Unmet",
                failedReason = if (!condition1) "Usage exceeded 1 hour" else null
            ),
            PayoutCondition(
                id = "weekly_total",
                title = "Weekly usage under 10 hours",
                subtitle = "${st.totalWeeklyMinutes / 60}h ${st.totalWeeklyMinutes % 60}m recorded (Target: < 10h)",
                isMet = condition2,
                statusText = if (condition2) "Met" else "Unmet",
                failedReason = if (!condition2) "Weekly usage exceeded 10h" else null
            ),
            PayoutCondition(
                id = "lower_than_prev",
                title = "Current week lower than previous week",
                subtitle = "Avg ${st.thisWeekDailyAvgMinutes / 60}h ${st.thisWeekDailyAvgMinutes % 60}m vs ${st.lastWeekAvgMinutes / 60}h ${st.lastWeekAvgMinutes % 60}m",
                isMet = condition3,
                statusText = if (condition3) "Met" else "Unmet",
                failedReason = if (!condition3) "Higher than previous week" else null
            )
        )
    }

    fun triggerWeeklyPayoutCheck() {
        val conditions = evaluatePayoutConditions()
        val allMet = conditions.all { it.isMet }

        if (allMet) {
            val starsEarned = 5
            val newBalance = _wallet.value.balance + starsEarned
            val newLifetime = _wallet.value.lifetimeStars + starsEarned
            val newLedger = listOf(
                LedgerItem(
                    id = UUID.randomUUID().toString(),
                    title = "Weekly Low Usage Bounty",
                    source = "Weekly Goal Met",
                    starsDelta = starsEarned,
                    timestampFormatted = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(_simulatedTimeMillis.value)),
                    note = "All 3 mindful conditions fulfilled"
                )
            ) + _wallet.value.ledger

            val updatedWallet = WalletState(balance = newBalance, lifetimeStars = newLifetime, ledger = newLedger)
            _wallet.value = updatedWallet
            repository.saveWallet(updatedWallet)
            showToast("Bounty Claimed! +5 Stars awarded")
        } else {
            val failed = conditions.firstOrNull { !it.isMet }
            showToast("Payout skipped: ${failed?.failedReason ?: "Conditions not met"}")
        }
    }

    // -------------------------------------------------------------
    // App Launch, Shield & Early Unlock Flow
    // -------------------------------------------------------------
    fun launchApp(app: AppItem) {
        val now = _simulatedTimeMillis.value

        if (app.isSystemProtected || !app.blocked) {
            // Free to open
            _activeShieldApp.value = null
            _currentlyOpenSimulatedApp.value = app
            return
        }

        // App is configured as blocked
        if (isWithinInterval(now)) {
            // Check if temporarily unlocked
            if (app.unlockedUntilEpochMillis != null && now < app.unlockedUntilEpochMillis) {
                _activeShieldApp.value = null
                _currentlyOpenSimulatedApp.value = app
            } else {
                // Strict restriction: Show full-screen Shield!
                _currentlyOpenSimulatedApp.value = null
                _activeShieldApp.value = app
            }
        } else {
            // Interval is currently inactive: Allowed freely
            _activeShieldApp.value = null
            _currentlyOpenSimulatedApp.value = app
        }
    }

    fun closeSimulatedApp() {
        _currentlyOpenSimulatedApp.value = null
    }

    fun dismissShield() {
        _activeShieldApp.value = null
        _unlockConfirmationApp.value = null
    }

    fun requestUnlockConfirmation(app: AppItem) {
        _unlockConfirmationApp.value = app
    }

    fun cancelUnlockConfirmation() {
        _unlockConfirmationApp.value = null
    }

    fun confirmUnlockWithStars(app: AppItem, cost: Int = 5) {
        if (_wallet.value.balance < cost) {
            showToast("Not enough stars! Need $cost stars.")
            return
        }

        val now = _simulatedTimeMillis.value
        val unlockDurationMillis = 15 * 60 * 1000L // 15-minute pass

        // Deduct stars
        val newBalance = _wallet.value.balance - cost
        val newLedger = listOf(
            LedgerItem(
                id = UUID.randomUUID().toString(),
                title = "Early Unlock: ${app.name} (15m)",
                source = "Shield Override",
                starsDelta = -cost,
                timestampFormatted = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(now)),
                note = "Manual override pass"
            )
        ) + _wallet.value.ledger

        val updatedWallet = _wallet.value.copy(balance = newBalance, ledger = newLedger)
        _wallet.value = updatedWallet
        repository.saveWallet(updatedWallet)

        // Mark app as unlocked until duration
        val updatedApps = _apps.value.map {
            if (it.id == app.id) it.copy(unlockedUntilEpochMillis = now + unlockDurationMillis) else it
        }
        _apps.value = updatedApps
        repository.saveApps(updatedApps)

        // Dismiss shield and launch app!
        _unlockConfirmationApp.value = null
        _activeShieldApp.value = null
        val updatedApp = updatedApps.first { it.id == app.id }
        _currentlyOpenSimulatedApp.value = updatedApp
        showToast("Unlocked ${app.name} for 15 minutes!")
    }

    // -------------------------------------------------------------
    // Blocked Apps Management
    // -------------------------------------------------------------
    fun toggleAppBlocked(appId: String) {
        val updated = _apps.value.map {
            if (it.id == appId && !it.isSystemProtected) {
                it.copy(blocked = !it.blocked)
            } else {
                it
            }
        }
        _apps.value = updated
        repository.saveApps(updated)
    }

    // -------------------------------------------------------------
    // Schedule Configuration
    // -------------------------------------------------------------
    fun updateSchedule(newConfig: ScheduleConfig) {
        _schedule.value = newConfig
        repository.saveSchedule(newConfig)
        showToast("Restriction Schedule saved!")
        evaluateCurrentInterval()
    }

    // -------------------------------------------------------------
    // Permissions Management
    // -------------------------------------------------------------
    fun grantPermission(key: String) {
        val updated = _permissions.value.map {
            if (it.key == key) it.copy(granted = true, statusLabel = "Active") else it
        }
        _permissions.value = updated
        repository.savePermission(key, true)
        showToast("Permission granted! +30 Starlight earned.")
    }

    // -------------------------------------------------------------
    // Settings & Critical Actions
    // -------------------------------------------------------------
    fun resetStarBalance() {
        val updatedWallet = _wallet.value.copy(balance = 0)
        _wallet.value = updatedWallet
        repository.saveWallet(updatedWallet)
        showToast("Star balance reset to 0.")
    }

    fun clearRestrictionRules() {
        val updatedApps = _apps.value.map {
            if (!it.isSystemProtected) it.copy(blocked = false) else it
        }
        _apps.value = updatedApps
        repository.saveApps(updatedApps)
        showToast("All apps unblocked and restrictions cleared.")
    }

    // -------------------------------------------------------------
    // Demo Time Travel Controls
    // -------------------------------------------------------------
    fun advanceSimulatedTime(minutes: Long) {
        _simulatedTimeMillis.value += minutes * 60 * 1000
        evaluateCurrentInterval()
        showToast("Fast forwarded $minutes minutes")
    }

    fun jumpToSpecificHour(hour24: Int, minute: Int = 0) {
        val cal = Calendar.getInstance().apply {
            timeInMillis = _simulatedTimeMillis.value
            set(Calendar.HOUR_OF_DAY, hour24)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }
        _simulatedTimeMillis.value = cal.timeInMillis
        evaluateCurrentInterval()
        showToast("Jumped to ${String.format("%02d:%02d", hour24, minute)}")
    }

    fun resetToRealTime() {
        _simulatedTimeMillis.value = System.currentTimeMillis()
        evaluateCurrentInterval()
        showToast("Time reset to real-world clock")
    }
}
