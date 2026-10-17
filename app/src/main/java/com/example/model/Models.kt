package com.example.model

import java.util.Calendar

data class AppItem(
    val id: String,
    val name: String,
    val category: String,
    val avgDailyUsage: String,
    val usageMinutes: Int,
    val limitMinutes: Int,
    val blocked: Boolean,
    val isSystemProtected: Boolean = false,
    val unlockedUntilEpochMillis: Long? = null,
    val iconKey: String = "default"
)

data class ScheduleConfig(
    val enabled: Boolean = true,
    val startHour: Int = 10,
    val startMinute: Int = 0,
    val startPeriod: String = "PM",
    val endHour: Int = 8,
    val endMinute: Int = 0,
    val endPeriod: String = "AM",
    val activeDays: Set<Int> = setOf(
        Calendar.MONDAY,
        Calendar.TUESDAY,
        Calendar.WEDNESDAY,
        Calendar.THURSDAY,
        Calendar.FRIDAY
    ),
    val repeatWeekly: Boolean = true,
    val strictLock: Boolean = true,
    val earlyUnlockPenalty: Boolean = true
)

data class LedgerItem(
    val id: String,
    val title: String,
    val source: String,
    val starsDelta: Int,
    val timestampFormatted: String,
    val note: String
)

data class WalletState(
    val balance: Int = 24,
    val lifetimeStars: Int = 142,
    val ledger: List<LedgerItem> = emptyList()
)

data class DailyUsageBar(
    val dayLabel: String,
    val minutes: Int,
    val formattedDuration: String,
    val isToday: Boolean = false,
    val isHighlighted: Boolean = false
)

data class ScreenTimeTargetConfig(
    val targetHours: Int = 40,
    val targetMinutes: Int = 22
) {
    val totalMinutes: Int
        get() = targetHours * 60 + targetMinutes

    val dailyAverageTargetMinutes: Int
        get() = totalMinutes / 7
}

data class ScreenTimeData(
    val usageSinceLastCheckMinutes: Int = 18,
    val maxUsageSinceLastCheckLimitMinutes: Int = 45,
    val thisWeekDailyAvgMinutes: Int = 135,
    val dailyThresholdMinutes: Int = 346, // 40h22m / 7 ≈ 346 minutes (5h 46m)
    val targetConfig: ScreenTimeTargetConfig = ScreenTimeTargetConfig(40, 22),
    val lastWeekAvgMinutes: Int = 220,
    val totalWeeklyMinutes: Int = 862,
    val dailyBars: List<DailyUsageBar> = emptyList(),
    val isRealDataActive: Boolean = false
)

enum class ExternalTrackerType(val displayName: String, val brandColorHex: Long) {
    GOOGLE_FITBIT("Google Fitbit", 0xFF00B0B9),
    STRAVA("Strava", 0xFFFC4C02),
    VIVO_TRACKER("Vivo Health", 0xFF4169E1),
    PHONE_TRACKER("Phone Step Tracker", 0xFF4CAF50),
    HEALTH_CONNECT("Health Connect", 0xFF4285F4),
    OTHER("External Fitness App", 0xFF9C27B0)
}

data class TrackerLink(
    val trackerType: ExternalTrackerType,
    val metricGoal: String,
    val isAutoSync: Boolean = true,
    val lastSyncValue: String = "Active"
)

data class HabitItem(
    val id: String,
    val title: String,
    val category: String,
    val streakDays: Int = 0,
    val isCompletedToday: Boolean = false,
    val starReward: Int = 2,
    val iconKey: String = "spa",
    val linkedTracker: TrackerLink? = null
)

data class PayoutCondition(
    val id: String,
    val title: String,
    val subtitle: String,
    val isMet: Boolean,
    val statusText: String,
    val failedReason: String? = null
)

data class PermissionItem(
    val key: String,
    val title: String,
    val description: String,
    val granted: Boolean,
    val iconType: String,
    val statusLabel: String,
    val tagLine: String
)

enum class ScreenTab {
    HOME,
    SCREEN_TIME,
    BLOCKED_APPS,
    HABITS,
    WALLET,
    SETTINGS,
    SCHEDULE,
    PRIVACY_POLICY
}

data class StreakMilestone(
    val daysRequired: Int,
    val bonusStars: Int,
    val title: String,
    val description: String = ""
)

data class CelestialStreak(
    val currentStreakDays: Int = 5,
    val bestStreakDays: Int = 14,
    val claimedMilestones: Set<Int> = setOf(3),
    val lastEvaluatedDateFormatted: String = "Today"
)

data class AuthUser(
    val uid: String,
    val email: String? = null,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val isAnonymous: Boolean = false
)

enum class SyncState {
    IDLE,
    SYNCING,
    SYNCED,
    OFFLINE_LOCAL,
    ERROR
}

data class CloudSyncStatus(
    val state: SyncState = SyncState.OFFLINE_LOCAL,
    val lastSyncedTimeFormatted: String = "Not synced yet",
    val itemsSyncedCount: Int = 0,
    val message: String = "Ready for cross-device synchronization"
)

