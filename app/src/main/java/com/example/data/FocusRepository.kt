package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar

class FocusRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("aether_focus_prefs", Context.MODE_PRIVATE)

    fun getInitialApps(): List<AppItem> {
        val storedJson = prefs.getString("apps_json", null)
        if (!storedJson.isNullOrEmpty()) {
            try {
                val array = JSONArray(storedJson)
                val list = mutableListOf<AppItem>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        AppItem(
                            id = obj.getString("id"),
                            name = obj.getString("name"),
                            category = obj.getString("category"),
                            avgDailyUsage = obj.getString("avgDailyUsage"),
                            usageMinutes = obj.optInt("usageMinutes", 60),
                            limitMinutes = obj.optInt("limitMinutes", 120),
                            blocked = obj.getBoolean("blocked"),
                            isSystemProtected = obj.optBoolean("isSystemProtected", false),
                            unlockedUntilEpochMillis = if (obj.has("unlockedUntilEpochMillis") && !obj.isNull("unlockedUntilEpochMillis")) obj.getLong("unlockedUntilEpochMillis") else null,
                            iconKey = obj.optString("iconKey", "default")
                        )
                    )
                }
                if (list.isNotEmpty()) return list
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Default initial apps
        return listOf(
            AppItem("phone", "Phone", "System Core", "Emergency Ready", 12, 120, blocked = false, isSystemProtected = true, iconKey = "phone"),
            AppItem("contacts", "Contacts", "System Core", "Essential Hub", 8, 120, blocked = false, isSystemProtected = true, iconKey = "contacts"),
            AppItem("instagram", "Instagram", "Social", "Avg 2h 15m/d", 192, 225, blocked = true, iconKey = "instagram"),
            AppItem("tiktok", "TikTok", "Entertainment", "Avg 1h 40m/d", 165, 240, blocked = true, iconKey = "tiktok"),
            AppItem("reddit", "Reddit", "Social Media", "Avg 58m/d", 58, 90, blocked = true, iconKey = "reddit"),
            AppItem("netflix", "Netflix", "Entertainment", "Avg 1h 10m/d", 70, 120, blocked = true, iconKey = "netflix"),
            AppItem("youtube", "YouTube", "Entertainment", "Avg 1h 50m/d", 110, 200, blocked = false, iconKey = "youtube"),
            AppItem("twitter", "Twitter / X", "Social Media", "Avg 1h 15m/d", 75, 180, blocked = true, iconKey = "twitter"),
            AppItem("slack", "Slack", "Productive", "Workplace Chat", 55, 300, blocked = false, iconKey = "slack"),
            AppItem("amazon", "Amazon Shopping", "Shopping", "Allowed (Unprotected)", 25, 90, blocked = false, iconKey = "amazon")
        )
    }

    fun saveApps(apps: List<AppItem>) {
        val array = JSONArray()
        for (app in apps) {
            val obj = JSONObject().apply {
                put("id", app.id)
                put("name", app.name)
                put("category", app.category)
                put("avgDailyUsage", app.avgDailyUsage)
                put("usageMinutes", app.usageMinutes)
                put("limitMinutes", app.limitMinutes)
                put("blocked", app.blocked)
                put("isSystemProtected", app.isSystemProtected)
                put("iconKey", app.iconKey)
                if (app.unlockedUntilEpochMillis != null) {
                    put("unlockedUntilEpochMillis", app.unlockedUntilEpochMillis)
                }
            }
            array.put(obj)
        }
        prefs.edit().putString("apps_json", array.toString()).apply()
    }

    fun getSchedule(): ScheduleConfig {
        val enabled = prefs.getBoolean("sched_enabled", true)
        val sH = prefs.getInt("sched_sH", 10)
        val sM = prefs.getInt("sched_sM", 0)
        val sP = prefs.getString("sched_sP", "PM") ?: "PM"
        val eH = prefs.getInt("sched_eH", 8)
        val eM = prefs.getInt("sched_eM", 0)
        val eP = prefs.getString("sched_eP", "AM") ?: "AM"
        val repeatWeekly = prefs.getBoolean("sched_repeat", true)
        val strictLock = prefs.getBoolean("sched_strict", true)
        val earlyPenalty = prefs.getBoolean("sched_penalty", true)
        val daysString = prefs.getString("sched_days", "2,3,4,5,6") ?: "2,3,4,5,6"
        val days = daysString.split(",").mapNotNull { it.trim().toIntOrNull() }.toSet()

        return ScheduleConfig(
            enabled = enabled,
            startHour = sH,
            startMinute = sM,
            startPeriod = sP,
            endHour = eH,
            endMinute = eM,
            endPeriod = eP,
            activeDays = if (days.isNotEmpty()) days else setOf(Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY),
            repeatWeekly = repeatWeekly,
            strictLock = strictLock,
            earlyUnlockPenalty = earlyPenalty
        )
    }

    fun saveSchedule(config: ScheduleConfig) {
        prefs.edit()
            .putBoolean("sched_enabled", config.enabled)
            .putInt("sched_sH", config.startHour)
            .putInt("sched_sM", config.startMinute)
            .putString("sched_sP", config.startPeriod)
            .putInt("sched_eH", config.endHour)
            .putInt("sched_eM", config.endMinute)
            .putString("sched_eP", config.endPeriod)
            .putBoolean("sched_repeat", config.repeatWeekly)
            .putBoolean("sched_strict", config.strictLock)
            .putBoolean("sched_penalty", config.earlyUnlockPenalty)
            .putString("sched_days", config.activeDays.joinToString(","))
            .apply()
    }

    fun getWallet(): WalletState {
        val balance = prefs.getInt("wallet_balance", 24)
        val lifetime = prefs.getInt("wallet_lifetime", 142)
        val storedJson = prefs.getString("wallet_ledger_json", null)
        val ledger = mutableListOf<LedgerItem>()

        if (!storedJson.isNullOrEmpty()) {
            try {
                val array = JSONArray(storedJson)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    ledger.add(
                        LedgerItem(
                            id = obj.getString("id"),
                            title = obj.getString("title"),
                            source = obj.getString("source"),
                            starsDelta = obj.getInt("starsDelta"),
                            timestampFormatted = obj.getString("timestampFormatted"),
                            note = obj.optString("note", "")
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (ledger.isEmpty()) {
            ledger.addAll(
                listOf(
                    LedgerItem("1", "Completed Evening Downtime", "Evening Sanctuary", 3, "Yesterday, 8:00 AM", "No blocked apps opened"),
                    LedgerItem("2", "Early Unlock: Instagram (15m)", "Shield Override", -5, "Oct 24, 11:15 PM", "Manual override"),
                    LedgerItem("3", "Weekly Low Usage Bounty", "Weekly Goal", 5, "Oct 22, 9:00 AM", "Stayed under 2h 30m daily goal"),
                    LedgerItem("4", "Streak Bonus: 3 Days Clean", "Milestone", 2, "Oct 20, 8:00 AM", "Consistency milestone"),
                    LedgerItem("5", "Early Unlock: YouTube (15m)", "Shield Override", -5, "Oct 18, 10:45 PM", "Manual override")
                )
            )
        }

        return WalletState(balance = balance, lifetimeStars = lifetime, ledger = ledger)
    }

    fun saveWallet(wallet: WalletState) {
        val array = JSONArray()
        for (item in wallet.ledger) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("title", item.title)
                put("source", item.source)
                put("starsDelta", item.starsDelta)
                put("timestampFormatted", item.timestampFormatted)
                put("note", item.note)
            }
            array.put(obj)
        }

        prefs.edit()
            .putInt("wallet_balance", wallet.balance)
            .putInt("wallet_lifetime", wallet.lifetimeStars)
            .putString("wallet_ledger_json", array.toString())
            .apply()
    }

    fun getPermissions(): List<PermissionItem> {
        val p1 = prefs.getBoolean("perm_usage", true)
        val p2 = prefs.getBoolean("perm_notif", true)
        val p3 = prefs.getBoolean("perm_overlay", false)
        val p4 = prefs.getBoolean("perm_admin", false)
        val p5 = prefs.getBoolean("perm_battery", false)

        return listOf(
            PermissionItem(
                key = "usage",
                title = "Usage Access",
                description = "Track screen time accurately to award stars fairly & detect habit loops.",
                granted = p1,
                iconType = "query_stats",
                statusLabel = if (p1) "Active" else "Pending",
                tagLine = "App tracking & session timers"
            ),
            PermissionItem(
                key = "notif",
                title = "Notifications",
                description = "Alert you before strict restriction windows begin with gentle chimes.",
                granted = p2,
                iconType = "notifications_active",
                statusLabel = if (p2) "Active" else "Pending",
                tagLine = "Bypass DND Enabled"
            ),
            PermissionItem(
                key = "overlay",
                title = "Display Over Apps",
                description = "Show luminous focus shield overlay whenever restricted apps are tapped.",
                granted = p3,
                iconType = "shield",
                statusLabel = if (p3) "Active" else "Pending",
                tagLine = "Modal block enforcement"
            ),
            PermissionItem(
                key = "admin",
                title = "Device Admin",
                description = "Prevent circumventing blocks or hasty uninstalls during active focus deep-work.",
                granted = p4,
                iconType = "lock_clock",
                statusLabel = if (p4) "Strict" else "Pending",
                tagLine = "Anti-uninstall persistence"
            ),
            PermissionItem(
                key = "battery",
                title = "Battery Exemption",
                description = "Ensure celestial background timers and starlight counters stay uninterrupted.",
                granted = p5,
                iconType = "battery_saver",
                statusLabel = if (p5) "Active" else "Pending",
                tagLine = "Zero sleep termination"
            )
        )
    }

    fun savePermission(key: String, granted: Boolean) {
        prefs.edit().putBoolean("perm_$key", granted).apply()
    }

    fun getCelestialStreak(): CelestialStreak {
        val current = prefs.getInt("streak_current", 5)
        val best = prefs.getInt("streak_best", 14)
        val claimedStr = prefs.getString("streak_claimed", "3") ?: "3"
        val claimed = claimedStr.split(",").mapNotNull { it.trim().toIntOrNull() }.toSet()
        val lastDate = prefs.getString("streak_last_date", "Today") ?: "Today"

        return CelestialStreak(
            currentStreakDays = current,
            bestStreakDays = best,
            claimedMilestones = claimed,
            lastEvaluatedDateFormatted = lastDate
        )
    }

    fun saveCelestialStreak(streak: CelestialStreak) {
        prefs.edit()
            .putInt("streak_current", streak.currentStreakDays)
            .putInt("streak_best", streak.bestStreakDays)
            .putString("streak_claimed", streak.claimedMilestones.joinToString(","))
            .putString("streak_last_date", streak.lastEvaluatedDateFormatted)
            .apply()
    }

    fun getScreenTimeTarget(): ScreenTimeTargetConfig {
        val h = prefs.getInt("target_hours", 40)
        val m = prefs.getInt("target_minutes", 22)
        return ScreenTimeTargetConfig(targetHours = h, targetMinutes = m)
    }

    fun saveScreenTimeTarget(config: ScreenTimeTargetConfig) {
        prefs.edit()
            .putInt("target_hours", config.targetHours)
            .putInt("target_minutes", config.targetMinutes)
            .apply()
    }

    fun getHabits(): List<HabitItem> {
        val json = prefs.getString("habits_json", null)
        if (!json.isNullOrEmpty()) {
            try {
                val array = JSONArray(json)
                val list = mutableListOf<HabitItem>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    var tracker: TrackerLink? = null
                    if (obj.has("tracker_type")) {
                        try {
                            val typeName = obj.getString("tracker_type")
                            val type = ExternalTrackerType.valueOf(typeName)
                            val goal = obj.optString("tracker_goal", "Daily Goal")
                            val autoSync = obj.optBoolean("tracker_auto_sync", true)
                            val lastVal = obj.optString("tracker_last_val", "Active")
                            tracker = TrackerLink(type, goal, autoSync, lastVal)
                        } catch (e: Exception) {
                            // Ignored if unknown tracker
                        }
                    }

                    list.add(
                        HabitItem(
                            id = obj.getString("id"),
                            title = obj.getString("title"),
                            category = obj.getString("category"),
                            streakDays = obj.optInt("streakDays", 0),
                            isCompletedToday = obj.optBoolean("isCompletedToday", false),
                            starReward = obj.optInt("starReward", 2),
                            iconKey = obj.optString("iconKey", "spa"),
                            linkedTracker = tracker
                        )
                    )
                }
                if (list.isNotEmpty()) return list
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return listOf(
            HabitItem(
                id = "habit_fitbit_1",
                title = "Walk 10,000 Steps",
                category = "Fitness",
                streakDays = 8,
                isCompletedToday = true,
                starReward = 3,
                iconKey = "directions_walk",
                linkedTracker = TrackerLink(
                    trackerType = ExternalTrackerType.GOOGLE_FITBIT,
                    metricGoal = "10,000 steps daily",
                    isAutoSync = true,
                    lastSyncValue = "10,420 steps logged"
                )
            ),
            HabitItem(
                id = "habit_strava_1",
                title = "30m Cardio / Cycling Ride",
                category = "Active",
                streakDays = 4,
                isCompletedToday = false,
                starReward = 4,
                iconKey = "fitness_center",
                linkedTracker = TrackerLink(
                    trackerType = ExternalTrackerType.STRAVA,
                    metricGoal = "30 mins activity",
                    isAutoSync = true,
                    lastSyncValue = "Synced via Strava"
                )
            ),
            HabitItem(
                id = "habit_vivo_1",
                title = "Hydration & Active Recovery",
                category = "Health",
                streakDays = 6,
                isCompletedToday = false,
                starReward = 2,
                iconKey = "water_drop",
                linkedTracker = TrackerLink(
                    trackerType = ExternalTrackerType.VIVO_TRACKER,
                    metricGoal = "2,500 mL & Active Vitals",
                    isAutoSync = true,
                    lastSyncValue = "Vivo Health Monitor"
                )
            ),
            HabitItem("habit_1", "Morning Digital Fast", "Mindfulness", 5, isCompletedToday = true, starReward = 2, iconKey = "wb_sunny"),
            HabitItem("habit_2", "No Phone During Meals", "Health", 7, isCompletedToday = false, starReward = 2, iconKey = "restaurant"),
            HabitItem("habit_3", "30m Offline Reading", "Focus", 3, isCompletedToday = false, starReward = 3, iconKey = "menu_book"),
            HabitItem("habit_4", "Phone Docked by 10 PM", "Sleep", 12, isCompletedToday = false, starReward = 3, iconKey = "bedtime"),
            HabitItem("habit_5", "90m Deep Work Sanctuary", "Productivity", 4, isCompletedToday = true, starReward = 4, iconKey = "self_improvement")
        )
    }

    fun saveHabits(habits: List<HabitItem>) {
        val array = JSONArray()
        for (h in habits) {
            val obj = JSONObject().apply {
                put("id", h.id)
                put("title", h.title)
                put("category", h.category)
                put("streakDays", h.streakDays)
                put("isCompletedToday", h.isCompletedToday)
                put("starReward", h.starReward)
                put("iconKey", h.iconKey)
                h.linkedTracker?.let { t ->
                    put("tracker_type", t.trackerType.name)
                    put("tracker_goal", t.metricGoal)
                    put("tracker_auto_sync", t.isAutoSync)
                    put("tracker_last_val", t.lastSyncValue)
                }
            }
            array.put(obj)
        }
        prefs.edit().putString("habits_json", array.toString()).apply()
    }

    fun resetAllData() {
        prefs.edit().clear().apply()
    }
}
