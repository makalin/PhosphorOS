package com.phosphoros.launcher

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class PhosphorNotificationListener : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val n = sbn.notification ?: return
        val extras = n.extras
        val title = extras?.getCharSequence("android.title")?.toString()?.trim().orEmpty()
        val text = extras?.getCharSequence("android.text")?.toString()?.trim().orEmpty()
        val from = sbn.packageName.orEmpty()

        if (title.isBlank() && text.isBlank()) return

        NotificationsRepo.push(
            NotificationLine(
                title = title,
                text = text,
                from = from,
                atEpochMs = System.currentTimeMillis(),
            ),
        )
    }
}

