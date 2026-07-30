package com.dialysis.app.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.dialysis.app.MedicationAlarmReceiver
import java.time.LocalTime
import java.time.LocalDateTime

data class DialysisCenter(
    val id: String,
    val name: String,
    val address: String,
    val phone: String? = null,
    val distance: String? = null,
    val level: String? = null,
    val lat: Double = 0.0,
    val lng: Double = 0.0
)

class ReminderManager(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleMedicationReminder(medId: String, medName: String, timeStr: String, medBadge: String? = null) {
        // Try parse time like "08:00", "8:00", "20:30"
        val time = parseTime(timeStr) ?: return

        val now = LocalDateTime.now()
        var reminderTime = LocalDateTime.now()
            .withHour(time.hour)
            .withMinute(time.minute)
            .withSecond(0)
            .withNano(0)

        // If time already passed today, schedule for tomorrow
        if (reminderTime.isBefore(now)) {
            reminderTime = reminderTime.plusDays(1)
        }

        val intent = Intent(context, MedicationAlarmReceiver::class.java).apply {
            putExtra(MedicationAlarmReceiver.EXTRA_MED_NAME, medName)
            putExtra(MedicationAlarmReceiver.EXTRA_MED_TIME, buildTimeDesc(timeStr, medBadge))
            putExtra(MedicationAlarmReceiver.EXTRA_NOTIFICATION_ID, medId.hashCode())
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            medId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerAtMillis = reminderTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent
                    )
                } else {
                    // Fallback to inexact alarm
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            // Permission not granted
            e.printStackTrace()
        }
    }

    fun cancelMedicationReminder(medId: String) {
        val intent = Intent(context, MedicationAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            medId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    fun rescheduleAllReminders(prefs: AppPreferences) {
        // Cancel all first
        val meds = prefs.getMedications()
        meds.forEach { med ->
            cancelMedicationReminder(med.id)
        }
        // Reschedule
        meds.forEach { med ->
            scheduleMedicationReminder(med.id, med.name, med.time, med.badge)
        }
    }

    private fun parseTime(timeStr: String): LocalTime? {
        // Try HH:mm format first
        val timePattern = Regex("""(\d{1,2}):(\d{2})""")
        val match = timePattern.find(timeStr)
        if (match != null) {
            val hour = match.groupValues[1].toIntOrNull() ?: return null
            val minute = match.groupValues[2].toIntOrNull() ?: return null
            if (hour in 0..23 && minute in 0..59) {
                return LocalTime.of(hour, minute)
            }
        }

        // Common Chinese time keywords
        return when {
            timeStr.contains("早上") || timeStr.contains("晨起") || timeStr.contains("早餐前") || timeStr.contains("早餐后") -> LocalTime.of(8, 0)
            timeStr.contains("中午") || timeStr.contains("午餐前") || timeStr.contains("午餐后") -> LocalTime.of(12, 0)
            timeStr.contains("晚上") || timeStr.contains("晚餐前") || timeStr.contains("晚餐后") -> LocalTime.of(18, 0)
            timeStr.contains("睡前") -> LocalTime.of(21, 0)
            timeStr.contains("餐前") -> LocalTime.of(11, 30)
            timeStr.contains("餐后") -> LocalTime.of(12, 30)
            else -> null
        }
    }

    private fun buildTimeDesc(timeStr: String, badge: String?): String {
        val badgeStr = if (!badge.isNullOrEmpty()) "（$badge）" else ""
        return "$timeStr$badgeStr"
    }

    companion object {
        // Pre-populated dialysis centers (Chongqing area coordinates)
        fun getDefaultDialysisCenters(): List<DialysisCenter> = listOf(
            DialysisCenter("c1", "大坪医院血液净化中心", "重庆市渝中区大坪长江支路10号", "023-68757123", "1.2km", "三甲", 29.5432, 106.5198),
            DialysisCenter("c2", "重庆医科大学附属第一医院血透中心", "重庆市渝中区友谊路1号", "023-89012345", "2.8km", "三甲", 29.5521, 106.5123),
            DialysisCenter("c3", "新桥医院肾内科血透室", "重庆市沙坪坝区新桥正街83号", "023-68774000", "5.1km", "三甲", 29.5389, 106.4367),
            DialysisCenter("c4", "西南医院血液净化中心", "重庆市沙坪坝区高滩岩正街30号", "023-65318301", "6.3km", "三甲", 29.5456, 106.4234),
            DialysisCenter("c5", "重庆市人民医院血透中心", "重庆市渝中区枇杷山正街104号", "023-63513351", "3.5km", "三甲", 29.5578, 106.5456),
            DialysisCenter("c6", "重医附二院血液透析中心", "重庆市渝中区临江路76号", "023-63693000", "2.1km", "三甲", 29.5612, 106.5678),
            DialysisCenter("c7", "九龙坡区人民医院血透室", "重庆市九龙坡区杨家坪前进路23号", null, "4.2km", "二甲", 29.5123, 106.5012),
            DialysisCenter("c8", "江北区第一人民医院肾内科", "重庆市江北区嘉陵一村1号", null, "7.8km", "二甲", 29.5789, 106.5345)
        )
    }
}
