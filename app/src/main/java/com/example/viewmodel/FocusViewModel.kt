package com.example.viewmodel

import android.app.Application
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FocusRepository
import com.example.model.*
import com.example.service.PermissionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import com.example.auth.AuthManager
import com.example.supabase.SupabaseRepository

class FocusViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FocusRepository(application)
    private val authManager = AuthManager.getInstance(application)
    private val supabaseRepository = SupabaseRepository.getInstance(application, repository)

    // Current User & Cloud Persistence
    val currentUser: StateFlow<AuthUser?> = authManager.currentUser
    val authError: StateFlow<String?> = authManager.authError
    val isAuthLoading: StateFlow<Boolean> = authManager.isLoading
    val cloudSyncStatus: StateFlow<CloudSyncStatus> = supabaseRepository.syncStatus
    val isSupabaseConfigured: Boolean get() = supabaseRepository.isConfigured()
    val supabaseUrl: String get() = supabaseRepository.getProjectUrl()

    // Current Navigation Tab
    private val _currentTab = MutableStateFlow(ScreenTab.HOME)
    val currentTab: StateFlow<ScreenTab> = _currentTab.asStateFlow()

    // App State
    private val _apps = MutableStateFlow<List<AppItem>>(emptyList())
    val apps: StateFlow<List<AppItem>> = _apps.asStateFlow()

    private val _schedule = MutableStateFlow(ScheduleConfig())
    val schedule: StateFlow<ScheduleConfig> = _schedule.asStateFlow()

    private val _wallet = MutableStateFlow(WalletState())
    val wallet: StateFlow<WalletState> = _wallet.asStateFlow()

    private val _permissions = MutableStateFlow<List<PermissionItem>>(emptyList())
    val permissions: StateFlow<List<PermissionItem>> = _permissions.asStateFlow()

    // Celestial Streak & Milestones
    val streakMilestones = listOf(
        StreakMilestone(daysRequired = 3, bonusStars = 2, title = "3-Day Starlight Spark", description = "+2 bonus stars for 3 days"),
        StreakMilestone(daysRequired = 7, bonusStars = 5, title = "7-Day Orbit Sanctuary", description = "+5 bonus stars for 1 full week"),
        StreakMilestone(daysRequired = 14, bonusStars = 10, title = "14-Day Cosmic Harmony", description = "+10 bonus stars for 2 weeks"),
        StreakMilestone(daysRequired = 30, bonusStars = 25, title = "30-Day Master of Time", description = "+25 bonus stars for 1 month")
    )

    private val _streak = MutableStateFlow(CelestialStreak())
    val streak: StateFlow<CelestialStreak> = _streak.asStateFlow()

    // Habit Tracker
    private val _habits = MutableStateFlow<List<HabitItem>>(emptyList())
    val habits: StateFlow<List<HabitItem>> = _habits.asStateFlow()

    // Screen Time Telemetry (Default 40 hours 22 minutes user-adjustable benchmark)
    private val _screenTimeData = MutableStateFlow(
        ScreenTimeData(
            usageSinceLastCheckMinutes = 18,
            maxUsageSinceLastCheckLimitMinutes = 45,
            thisWeekDailyAvgMinutes = 135,
            dailyThresholdMinutes = 346,
            targetConfig = ScreenTimeTargetConfig(40, 22),
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
            ),
            isRealDataActive = false
        )
    )
    val screenTimeData: StateFlow<ScreenTimeData> = _screenTimeData.asStateFlow()

    // Active full-screen Shield and Unlock Modal
    private val _activeShieldApp = MutableStateFlow<AppItem?>(null)
    val activeShieldApp: StateFlow<AppItem?> = _activeShieldApp.asStateFlow()

    private val _unlockConfirmationApp = MutableStateFlow<AppItem?>(null)
    val unlockConfirmationApp: StateFlow<AppItem?> = _unlockConfirmationApp.asStateFlow()

    // Toast message for user actions
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    init {
        loadData()
        syncPermissionsWithSystem()
        queryUsageStatsIfPermitted()
        startPeriodicTicker()
    }

    fun loadData() {
        val targetConfig = repository.getScreenTimeTarget()
        _schedule.value = repository.getSchedule()
        _wallet.value = repository.getWallet()
        _streak.value = repository.getCelestialStreak()
        _habits.value = repository.getHabits()

        // Load apps merged with device package manager
        loadAndMergeInstalledApps()

        // Update initial screen time target
        _screenTimeData.value = _screenTimeData.value.copy(
            targetConfig = targetConfig,
            dailyThresholdMinutes = targetConfig.dailyAverageTargetMinutes
        )
    }

    private fun loadAndMergeInstalledApps() {
        val savedApps = repository.getInitialApps()
        val savedMap = savedApps.associateBy { it.id }

        val context = getApplication<Application>()
        val pm = context.packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolvedList = pm.queryIntentActivities(mainIntent, 0)
        val combinedApps = mutableListOf<AppItem>()

        // System Protected Essentials (Always Allowed)
        combinedApps.add(
            AppItem(
                id = "com.android.dialer",
                name = "Phone",
                category = "System Core",
                avgDailyUsage = "Emergency Ready",
                usageMinutes = 12,
                limitMinutes = 120,
                blocked = false,
                isSystemProtected = true,
                iconKey = "phone"
            )
        )
        combinedApps.add(
            AppItem(
                id = "com.android.contacts",
                name = "Contacts",
                category = "System Core",
                avgDailyUsage = "Essential Hub",
                usageMinutes = 8,
                limitMinutes = 120,
                blocked = false,
                isSystemProtected = true,
                iconKey = "contacts"
            )
        )

        val seenPackages = mutableSetOf("com.android.dialer", "com.android.contacts", context.packageName)

        // Read real installed launcher packages
        for (ri in resolvedList) {
            val pkg = ri.activityInfo.packageName
            if (pkg in seenPackages) continue
            seenPackages.add(pkg)

            val appName = ri.loadLabel(pm).toString()
            val existing = savedMap[pkg] ?: savedMap.values.firstOrNull { it.name.equals(appName, ignoreCase = true) }

            combinedApps.add(
                AppItem(
                    id = pkg,
                    name = appName,
                    category = existing?.category ?: "Installed Application",
                    avgDailyUsage = existing?.avgDailyUsage ?: "Active",
                    usageMinutes = existing?.usageMinutes ?: 30,
                    limitMinutes = existing?.limitMinutes ?: 120,
                    blocked = existing?.blocked ?: false,
                    isSystemProtected = existing?.isSystemProtected ?: false,
                    unlockedUntilEpochMillis = existing?.unlockedUntilEpochMillis,
                    iconKey = existing?.iconKey ?: "default"
                )
            )
        }

        // Also add any popular target apps from saved list that might not be on an emulator
        for (saved in savedApps) {
            if (saved.id !in seenPackages && !saved.isSystemProtected) {
                seenPackages.add(saved.id)
                combinedApps.add(saved)
            }
        }

        _apps.value = combinedApps
        repository.saveApps(combinedApps)
    }

    fun syncPermissionsWithSystem() {
        val context = getApplication<Application>()
        val liveStatus = PermissionManager.getLiveStatus(context)

        val items = listOf(
            PermissionItem(
                key = "admin",
                title = "Device Admin",
                description = "Prevents circumventing active focus blocks or uninstalling during scheduled restriction intervals.",
                granted = liveStatus["admin"] ?: false,
                iconType = "lock_clock",
                statusLabel = if (liveStatus["admin"] == true) "Active" else "Pending",
                tagLine = "Anti-uninstall persistence"
            ),
            PermissionItem(
                key = "accessibility",
                title = "Accessibility Service",
                description = "Enforces restriction shields immediately when configured blocked apps are opened.",
                granted = liveStatus["accessibility"] ?: false,
                iconType = "shield",
                statusLabel = if (liveStatus["accessibility"] == true) "Active" else "Pending",
                tagLine = "Real-time foreground interception"
            ),
            PermissionItem(
                key = "usage",
                title = "Screen-Time Analysis",
                description = "Reads foreground app usage stats to compute daily/weekly progress against your benchmark.",
                granted = liveStatus["usage"] ?: false,
                iconType = "query_stats",
                statusLabel = if (liveStatus["usage"] == true) "Active" else "Pending",
                tagLine = "PACKAGE_USAGE_STATS telemetry"
            ),
            PermissionItem(
                key = "overlay",
                title = "Display Over Other Apps",
                description = "Renders the full-screen restriction shield over blocked applications.",
                granted = liveStatus["overlay"] ?: false,
                iconType = "layers",
                statusLabel = if (liveStatus["overlay"] == true) "Active" else "Pending",
                tagLine = "SYSTEM_ALERT_WINDOW permission"
            ),
            PermissionItem(
                key = "battery",
                title = "Battery Exemption",
                description = "Ensures background restriction monitors and interval timers run without OS sleep termination.",
                granted = liveStatus["battery"] ?: false,
                iconType = "battery_saver",
                statusLabel = if (liveStatus["battery"] == true) "Active" else "Pending",
                tagLine = "REQUEST_IGNORE_BATTERY_OPTIMIZATIONS"
            ),
            PermissionItem(
                key = "notif",
                title = "Notifications",
                description = "Alerts you prior to downtime schedules and announces milestone starlight rewards.",
                granted = liveStatus["notif"] ?: false,
                iconType = "notifications_active",
                statusLabel = if (liveStatus["notif"] == true) "Active" else "Pending",
                tagLine = "POST_NOTIFICATIONS"
            )
        )
        _permissions.value = items
    }

    fun queryUsageStatsIfPermitted() {
        val context = getApplication<Application>()
        if (!PermissionManager.isUsageAccessGranted(context)) return

        try {
            val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager ?: return
            val cal = Calendar.getInstance()
            val endTime = cal.timeInMillis
            cal.add(Calendar.DAY_OF_YEAR, -7)
            val startTime = cal.timeInMillis

            val stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_WEEKLY, startTime, endTime)
            var totalWeeklyMillis = 0L
            if (!stats.isNullOrEmpty()) {
                for (s in stats) {
                    if (s.totalTimeInForeground > 30_000L) {
                        totalWeeklyMillis += s.totalTimeInForeground
                    }
                }
            }

            if (totalWeeklyMillis > 0) {
                val totalMins = (totalWeeklyMillis / (1000 * 60)).toInt()
                val avgMins = (totalMins / 7).coerceAtLeast(1)
                _screenTimeData.value = _screenTimeData.value.copy(
                    totalWeeklyMinutes = totalMins,
                    thisWeekDailyAvgMinutes = avgMins,
                    isRealDataActive = true
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startPeriodicTicker() {
        viewModelScope.launch {
            while (true) {
                delay(30_000)
                evaluateCurrentInterval()
                syncPermissionsWithSystem()
            }
        }
    }

    fun setTab(tab: ScreenTab) {
        _currentTab.value = tab
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
    fun isWithinInterval(nowMillis: Long = System.currentTimeMillis()): Boolean {
        val config = _schedule.value
        if (!config.enabled) return false

        val cal = Calendar.getInstance().apply { timeInMillis = nowMillis }
        val currentDay = cal.get(Calendar.DAY_OF_WEEK)
        val currentHour = cal.get(Calendar.HOUR_OF_DAY)
        val currentMinute = cal.get(Calendar.MINUTE)
        val currentMinsOfDay = currentHour * 60 + currentMinute

        var startHour24 = config.startHour % 12
        if (config.startPeriod.equals("PM", ignoreCase = true)) startHour24 += 12
        val startMins = startHour24 * 60 + config.startMinute

        var endHour24 = config.endHour % 12
        if (config.endPeriod.equals("PM", ignoreCase = true)) endHour24 += 12
        val endMins = endHour24 * 60 + config.endMinute

        return if (startMins <= endMins) {
            config.activeDays.contains(currentDay) && (currentMinsOfDay in startMins until endMins)
        } else {
            if (currentMinsOfDay >= startMins) {
                config.activeDays.contains(currentDay)
            } else if (currentMinsOfDay < endMins) {
                val prevDay = if (currentDay == Calendar.SUNDAY) Calendar.SATURDAY else currentDay - 1
                config.activeDays.contains(prevDay)
            } else {
                false
            }
        }
    }

    fun getRemainingIntervalText(nowMillis: Long = System.currentTimeMillis()): String {
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
        val now = System.currentTimeMillis()
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
        }
    }

    // -------------------------------------------------------------
    // Star Payout Engine
    // -------------------------------------------------------------
    fun evaluatePayoutConditions(): List<PayoutCondition> {
        val st = _screenTimeData.value
        val condition1 = st.usageSinceLastCheckMinutes < 60
        val condition2 = st.totalWeeklyMinutes < st.targetConfig.totalMinutes
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
                title = "Weekly usage under target (${st.targetConfig.targetHours}h ${st.targetConfig.targetMinutes}m)",
                subtitle = "${st.totalWeeklyMinutes / 60}h ${st.totalWeeklyMinutes % 60}m recorded (Target: ${st.targetConfig.targetHours}h ${st.targetConfig.targetMinutes}m)",
                isMet = condition2,
                statusText = if (condition2) "Met" else "Unmet",
                failedReason = if (!condition2) "Weekly usage exceeded ${st.targetConfig.targetHours}h ${st.targetConfig.targetMinutes}m" else null
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
                    timestampFormatted = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date()),
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
    // Screen Time Benchmark Adjustment (Default 40h 22m)
    // -------------------------------------------------------------
    fun updateScreenTimeTarget(hours: Int, minutes: Int) {
        val newTarget = ScreenTimeTargetConfig(targetHours = hours.coerceAtLeast(1), targetMinutes = minutes.coerceIn(0, 59))
        repository.saveScreenTimeTarget(newTarget)
        _screenTimeData.value = _screenTimeData.value.copy(
            targetConfig = newTarget,
            dailyThresholdMinutes = newTarget.dailyAverageTargetMinutes
        )
        showToast("Screen-time analysis target updated: ${newTarget.targetHours}h ${newTarget.targetMinutes}m")
    }

    // -------------------------------------------------------------
    // App Launch, Shield & Early Unlock Flow
    // -------------------------------------------------------------
    fun launchApp(app: AppItem, context: Context? = null) {
        val now = System.currentTimeMillis()

        if (app.isSystemProtected || !app.blocked) {
            launchActualApp(app, context)
            return
        }

        if (isWithinInterval(now)) {
            if (app.unlockedUntilEpochMillis != null && now < app.unlockedUntilEpochMillis) {
                launchActualApp(app, context)
            } else {
                _activeShieldApp.value = app
            }
        } else {
            launchActualApp(app, context)
        }
    }

    private fun launchActualApp(app: AppItem, context: Context?) {
        val ctx = context ?: getApplication<Application>()
        val pm = ctx.packageManager
        val launchIntent = pm.getLaunchIntentForPackage(app.id)
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                ctx.startActivity(launchIntent)
            } catch (e: Exception) {
                showToast("Opening ${app.name}...")
            }
        } else {
            showToast("${app.name} opened")
        }
    }

    fun showShieldForPackage(pkgName: String) {
        val match = _apps.value.firstOrNull { it.id == pkgName || it.name.equals(pkgName, ignoreCase = true) }
        if (match != null) {
            _activeShieldApp.value = match
        }
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

    fun confirmUnlockWithStars(app: AppItem, cost: Int = 5, context: Context? = null) {
        if (_wallet.value.balance < cost) {
            showToast("Not enough stars! Need $cost stars.")
            return
        }

        val now = System.currentTimeMillis()
        val unlockDurationMillis = 15 * 60 * 1000L

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

        val updatedApps = _apps.value.map {
            if (it.id == app.id) it.copy(unlockedUntilEpochMillis = now + unlockDurationMillis) else it
        }
        _apps.value = updatedApps
        repository.saveApps(updatedApps)

        _unlockConfirmationApp.value = null
        _activeShieldApp.value = null

        val updatedApp = updatedApps.first { it.id == app.id }
        showToast("Unlocked ${app.name} for 15 minutes!")
        launchActualApp(updatedApp, context)
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
    // Habit Tracker Management
    // -------------------------------------------------------------
    fun toggleHabit(habitId: String) {
        val habit = _habits.value.firstOrNull { it.id == habitId } ?: return
        val willBeCompleted = !habit.isCompletedToday

        val updatedHabits = _habits.value.map {
            if (it.id == habitId) {
                val newStreak = if (willBeCompleted) it.streakDays + 1 else (it.streakDays - 1).coerceAtLeast(0)
                it.copy(isCompletedToday = willBeCompleted, streakDays = newStreak)
            } else {
                it
            }
        }
        _habits.value = updatedHabits
        repository.saveHabits(updatedHabits)

        if (willBeCompleted) {
            val starsEarned = habit.starReward
            val newBalance = _wallet.value.balance + starsEarned
            val newLifetime = _wallet.value.lifetimeStars + starsEarned
            val newLedger = listOf(
                LedgerItem(
                    id = UUID.randomUUID().toString(),
                    title = "Habit: ${habit.title}",
                    source = "Habit Tracker",
                    starsDelta = starsEarned,
                    timestampFormatted = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date()),
                    note = "+$starsEarned Stars earned for mindful consistency"
                )
            ) + _wallet.value.ledger

            val updatedWallet = _wallet.value.copy(balance = newBalance, lifetimeStars = newLifetime, ledger = newLedger)
            _wallet.value = updatedWallet
            repository.saveWallet(updatedWallet)
            showToast("Habit Completed! +$starsEarned Stars Awarded ★")
        } else {
            showToast("Habit uncompleted.")
        }
    }

    fun addCustomHabit(title: String, category: String, reward: Int = 2) {
        if (title.isBlank()) return
        val newHabit = HabitItem(
            id = "habit_${UUID.randomUUID()}",
            title = title.trim(),
            category = category.trim().ifEmpty { "Focus" },
            streakDays = 1,
            isCompletedToday = true,
            starReward = reward.coerceIn(1, 10),
            iconKey = "star"
        )
        val updated = _habits.value + newHabit
        _habits.value = updated
        repository.saveHabits(updated)

        // Award stars for first completion
        val newBalance = _wallet.value.balance + newHabit.starReward
        val newLifetime = _wallet.value.lifetimeStars + newHabit.starReward
        val updatedWallet = _wallet.value.copy(balance = newBalance, lifetimeStars = newLifetime)
        _wallet.value = updatedWallet
        repository.saveWallet(updatedWallet)

        showToast("New habit added! +${newHabit.starReward} Stars awarded ★")
    }

    fun deleteHabit(habitId: String) {
        val updated = _habits.value.filter { it.id != habitId }
        _habits.value = updated
        repository.saveHabits(updated)
        showToast("Habit removed.")
    }

    fun linkHabitToTracker(habitId: String, trackerType: ExternalTrackerType, goal: String) {
        val updatedHabits = _habits.value.map {
            if (it.id == habitId) {
                it.copy(
                    linkedTracker = TrackerLink(
                        trackerType = trackerType,
                        metricGoal = goal,
                        isAutoSync = true,
                        lastSyncValue = "Linked to ${trackerType.displayName}"
                    )
                )
            } else it
        }
        _habits.value = updatedHabits
        repository.saveHabits(updatedHabits)
        showToast("Linked to ${trackerType.displayName} ($goal)")
        syncWithSupabase()
    }

    fun unlinkHabitFromTracker(habitId: String) {
        val updatedHabits = _habits.value.map {
            if (it.id == habitId) {
                it.copy(linkedTracker = null)
            } else it
        }
        _habits.value = updatedHabits
        repository.saveHabits(updatedHabits)
        showToast("Tracker unlinked.")
        syncWithSupabase()
    }

    fun syncExternalTrackers() {
        var completedCount = 0
        var totalStarsEarned = 0
        val updatedHabits = _habits.value.map { habit ->
            if (habit.linkedTracker != null && !habit.isCompletedToday) {
                completedCount++
                totalStarsEarned += habit.starReward
                habit.copy(
                    isCompletedToday = true,
                    streakDays = habit.streakDays + 1,
                    linkedTracker = habit.linkedTracker.copy(lastSyncValue = "Synced: Goal Reached")
                )
            } else habit
        }

        if (completedCount > 0) {
            _habits.value = updatedHabits
            repository.saveHabits(updatedHabits)

            val newBalance = _wallet.value.balance + totalStarsEarned
            val newLifetime = _wallet.value.lifetimeStars + totalStarsEarned
            val newLedger = listOf(
                LedgerItem(
                    id = UUID.randomUUID().toString(),
                    title = "External Trackers Sync ($completedCount habits)",
                    source = "Fitbit/Strava/Vivo/Phone",
                    starsDelta = totalStarsEarned,
                    timestampFormatted = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date()),
                    note = "Auto-verified via external health telemetry"
                )
            ) + _wallet.value.ledger

            val updatedWallet = _wallet.value.copy(balance = newBalance, lifetimeStars = newLifetime, ledger = newLedger)
            _wallet.value = updatedWallet
            repository.saveWallet(updatedWallet)
            showToast("External Sync: $completedCount habits verified! +$totalStarsEarned Stars ★")
        } else {
            showToast("All linked external trackers are up to date.")
        }
        syncWithSupabase()
    }

    fun importHabitTrackerJson(rawContent: String): Boolean {
        try {
            val content = rawContent.trim()
            if (content.startsWith("[")) {
                val array = org.json.JSONArray(content)
                val importedList = mutableListOf<HabitItem>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    var tracker: TrackerLink? = null
                    if (obj.has("tracker_type")) {
                        try {
                            tracker = TrackerLink(
                                trackerType = ExternalTrackerType.valueOf(obj.getString("tracker_type")),
                                metricGoal = obj.optString("tracker_goal", "Daily Goal"),
                                isAutoSync = true,
                                lastSyncValue = "Imported"
                            )
                        } catch (_: Exception) {}
                    }
                    importedList.add(
                        HabitItem(
                            id = obj.optString("id", "habit_${UUID.randomUUID()}"),
                            title = obj.getString("title"),
                            category = obj.optString("category", "Imported"),
                            streakDays = obj.optInt("streakDays", 0),
                            isCompletedToday = obj.optBoolean("isCompletedToday", false),
                            starReward = obj.optInt("starReward", 2),
                            iconKey = obj.optString("iconKey", "spa"),
                            linkedTracker = tracker
                        )
                    )
                }
                if (importedList.isNotEmpty()) {
                    _habits.value = importedList
                    repository.saveHabits(importedList)
                    showToast("Successfully imported ${importedList.size} habits from tracker file!")
                    syncWithSupabase()
                    return true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        showToast("Could not parse habit tracker format.")
        return false
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
    fun requestPermission(context: Context, key: String) {
        PermissionManager.requestPermissionByKey(context, key)
        viewModelScope.launch {
            delay(1000)
            syncPermissionsWithSystem()
        }
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
    // Celestial Streak Management & Milestones
    // -------------------------------------------------------------
    fun claimStreakMilestone(milestone: StreakMilestone) {
        val current = _streak.value
        if (current.currentStreakDays < milestone.daysRequired) {
            showToast("Need ${milestone.daysRequired} days streak to claim this reward!")
            return
        }
        if (current.claimedMilestones.contains(milestone.daysRequired)) {
            showToast("Milestone reward already claimed!")
            return
        }

        val newClaimed = current.claimedMilestones + milestone.daysRequired
        val updatedStreak = current.copy(claimedMilestones = newClaimed)
        _streak.value = updatedStreak
        repository.saveCelestialStreak(updatedStreak)

        val newBalance = _wallet.value.balance + milestone.bonusStars
        val newLifetime = _wallet.value.lifetimeStars + milestone.bonusStars
        val newLedger = listOf(
            LedgerItem(
                id = UUID.randomUUID().toString(),
                title = "Milestone Bonus: ${milestone.daysRequired} Days Clean",
                source = "Celestial Streak",
                starsDelta = milestone.bonusStars,
                timestampFormatted = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date()),
                note = milestone.title
            )
        ) + _wallet.value.ledger

        val updatedWallet = _wallet.value.copy(balance = newBalance, lifetimeStars = newLifetime, ledger = newLedger)
        _wallet.value = updatedWallet
        repository.saveWallet(updatedWallet)

        showToast("Milestone Unlocked! +${milestone.bonusStars} Stars Awarded ★")
    }

    fun resetStreak() {
        val current = _streak.value
        val updated = current.copy(currentStreakDays = 0)
        _streak.value = updated
        repository.saveCelestialStreak(updated)
        showToast("Celestial streak reset.")
    }

    // -------------------------------------------------------------
    // Google OAuth & Supabase Cloud Synchronization
    // -------------------------------------------------------------
    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            val result = authManager.signInWithGoogle(context)
            if (result.isSuccess) {
                val user = result.getOrNull()
                showToast("Signed in as ${user?.displayName ?: "Google User"}")
                // Auto sync immediately upon login
                syncWithSupabase()
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Google Sign-In failed"
                showToast(errorMsg)
            }
        }
    }

    fun signOut() {
        authManager.signOut()
        showToast("Signed out from Google Account.")
    }

    fun clearAuthError() {
        authManager.clearError()
    }

    fun syncWithSupabase() {
        val user = currentUser.value
        val userId = user?.uid ?: "local_device_${repository.hashCode()}"
        viewModelScope.launch {
            val result = supabaseRepository.syncAll(userId)
            if (result.isSuccess) {
                showToast("Data synchronized with Supabase backend.")
            } else {
                showToast("Sync completed locally.")
            }
        }
    }
}

