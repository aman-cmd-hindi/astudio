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
    val starsDelta: Int, // e.g. +3, -5, +5, +2
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

data class ScreenTimeData(
    val usageSinceLastCheckMinutes: Int = 18,
    val maxUsageSinceLastCheckLimitMinutes: Int = 45,
    val thisWeekDailyAvgMinutes: Int = 135, // 2h 15m
    val dailyThresholdMinutes: Int = 150, // 2h 30m
    val lastWeekAvgMinutes: Int = 220, // 3h 40m
    val totalWeeklyMinutes: Int = 862, // 14h 22m
    val dailyBars: List<DailyUsageBar> = emptyList()
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
    WALLET,
    SETTINGS,
    SCHEDULE
}
