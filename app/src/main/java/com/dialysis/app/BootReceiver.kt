package com.dialysis.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dialysis.app.data.AppPreferences
import com.dialysis.app.data.ReminderManager

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefs = AppPreferences(context)
            if (prefs.isReminderEnabled()) {
                val reminderManager = ReminderManager(context)
                reminderManager.rescheduleAllReminders(prefs)
            }
        }
    }
}
