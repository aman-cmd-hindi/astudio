package com.example.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.example.MainActivity
import com.example.data.FocusRepository
import java.util.Calendar

class FocusAccessibilityService : AccessibilityService() {

    private lateinit var repository: FocusRepository

    override fun onCreate() {
        super.onCreate()
        repository = FocusRepository(applicationContext)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            return
        }

        val targetPackage = event.packageName?.toString() ?: return
        if (targetPackage == packageName || targetPackage == "com.android.systemui") {
            return
        }

        val apps = repository.getInitialApps()
        val matchedApp = apps.firstOrNull { it.id == targetPackage || it.name.equals(targetPackage, ignoreCase = true) }

        if (matchedApp != null && matchedApp.blocked && !matchedApp.isSystemProtected) {
            val now = System.currentTimeMillis()
            val isUnlocked = matchedApp.unlockedUntilEpochMillis != null && now < matchedApp.unlockedUntilEpochMillis

            if (!isUnlocked && isCurrentlyRestricted()) {
                val intent = Intent(this, MainActivity::class.java).apply {
                    action = "com.example.ACTION_SHOW_SHIELD"
                    putExtra("EXTRA_APP_ID", matchedApp.id)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                }
                startActivity(intent)
            }
        }
    }

    private fun isCurrentlyRestricted(): Boolean {
        val config = repository.getSchedule()
        if (!config.enabled) return false

        val cal = Calendar.getInstance()
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

    override fun onInterrupt() {}
}
