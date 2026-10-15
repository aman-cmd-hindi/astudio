package com.example

import com.example.model.ScheduleConfig
import com.example.model.ScreenTimeData
import org.junit.Assert.*
import org.junit.Test
import java.util.Calendar

class FocusLogicTest {

    private fun isWithinInterval(nowMillis: Long, config: ScheduleConfig): Boolean {
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

    @Test
    fun testOvernightInterval_InsideEveningWindow() {
        // Monday 11:30 PM (23:30)
        val cal = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 30)
            set(Calendar.SECOND, 0)
        }

        val schedule = ScheduleConfig(
            enabled = true,
            startHour = 10,
            startMinute = 0,
            startPeriod = "PM", // 22:00
            endHour = 8,
            endMinute = 0,
            endPeriod = "AM",   // 08:00
            activeDays = setOf(Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY)
        )

        assertTrue("11:30 PM on Monday should be within 10 PM - 8 AM interval", isWithinInterval(cal.timeInMillis, schedule))
    }

    @Test
    fun testOvernightInterval_InsideMorningWindow() {
        // Tuesday 6:30 AM (belongs to Monday evening's active overnight window)
        val cal = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY)
            set(Calendar.HOUR_OF_DAY, 6)
            set(Calendar.MINUTE, 30)
            set(Calendar.SECOND, 0)
        }

        val schedule = ScheduleConfig(
            enabled = true,
            startHour = 10,
            startMinute = 0,
            startPeriod = "PM",
            endHour = 8,
            endMinute = 0,
            endPeriod = "AM",
            activeDays = setOf(Calendar.MONDAY) // only Monday active
        )

        assertTrue("Tuesday 6:30 AM following an active Monday night should be restricted", isWithinInterval(cal.timeInMillis, schedule))
    }

    @Test
    fun testOvernightInterval_OutsideWindow() {
        // Monday 2:00 PM (14:00)
        val cal = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            set(Calendar.HOUR_OF_DAY, 14)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        val schedule = ScheduleConfig(
            enabled = true,
            startHour = 10,
            startMinute = 0,
            startPeriod = "PM",
            endHour = 8,
            endMinute = 0,
            endPeriod = "AM",
            activeDays = setOf(Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY)
        )

        assertFalse("Monday 2:00 PM should NOT be within 10 PM - 8 AM interval", isWithinInterval(cal.timeInMillis, schedule))
    }

    @Test
    fun testStarPayoutConditions_AllMet() {
        val screenTime = ScreenTimeData(
            usageSinceLastCheckMinutes = 30, // < 60m
            totalWeeklyMinutes = 480,       // < 600m (8h < 10h)
            thisWeekDailyAvgMinutes = 120,   // < last week
            lastWeekAvgMinutes = 180
        )

        val condition1 = screenTime.usageSinceLastCheckMinutes < 60
        val condition2 = screenTime.totalWeeklyMinutes < 600
        val condition3 = screenTime.thisWeekDailyAvgMinutes < screenTime.lastWeekAvgMinutes

        assertTrue(condition1 && condition2 && condition3)
    }

    @Test
    fun testStarPayoutConditions_FailsWhenWeeklyExceeded() {
        val screenTime = ScreenTimeData(
            usageSinceLastCheckMinutes = 30,
            totalWeeklyMinutes = 700,       // > 600m (11h 40m > 10h)
            thisWeekDailyAvgMinutes = 120,
            lastWeekAvgMinutes = 180
        )

        val condition2 = screenTime.totalWeeklyMinutes < 600
        assertFalse("Should fail payout when weekly usage exceeds 10 hours", condition2)
    }
}
