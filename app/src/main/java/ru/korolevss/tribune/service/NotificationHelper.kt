package ru.korolevss.tribune.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import ru.korolevss.tribune.R
import java.util.*

object NotificationHelper {
    private const val UPLOAD_CHANEL_ID = "upload_chanel_id"
    private var channelCreated = false
    private var lastNotificationId: Int? = null

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notify)
            val descriptionText = context.getString(R.string.description_text)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(UPLOAD_CHANEL_ID, name, importance)
                .apply {
                    description = descriptionText
                }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE)
                        as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    fun notifyFromFCM(context: Context, title: String) {
        createNotificationChannelIfNotCreated(context)
        val builder = NotificationCompat.Builder(context, UPLOAD_CHANEL_ID)
            .setSmallIcon(R.drawable.ic_public_black_24dp)
            .setContentTitle(title)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with (NotificationManagerCompat.from(context)) {
            val notificationId = Random().nextInt(100000)
            lastNotificationId = notificationId
            notify(notificationId, builder.build())
        }
    }

    private fun createNotificationChannelIfNotCreated(context: Context) {
        if (!channelCreated) {
            createNotificationChannel(context)
            channelCreated = true
        }
    }
}
