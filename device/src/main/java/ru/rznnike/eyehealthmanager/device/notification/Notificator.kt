package ru.rznnike.eyehealthmanager.device.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.rznnike.eyehealthmanager.data.preference.PreferencesWrapper
import ru.rznnike.eyehealthmanager.device.R
import ru.rznnike.eyehealthmanager.domain.model.notification.CancelNotification
import ru.rznnike.eyehealthmanager.domain.model.notification.Notification
import ru.rznnike.eyehealthmanager.domain.model.notification.NotificationChannelType

private const val NOTIFICATION_CHANNEL_ID_SOUNDLESS_SUFFIX = "_soundless"

class Notificator : KoinComponent {
    private val context: Context by inject()
    private val preferencesWrapper: PreferencesWrapper by inject()

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannelType.entries.forEach {
                createIfNotExistNotificationChannel(
                    channelId = getChannelId(
                        notificationChannelType = it,
                        soundEnabled = true
                    ),
                    channelName = context.getString(it.nameResId),
                    soundEnabled = true
                )
                createIfNotExistNotificationChannel(
                    channelId = getChannelId(
                        notificationChannelType = it,
                        soundEnabled = false
                    ),
                    channelName = "%s (%s)".format(
                        context.getString(it.nameResId),
                        context.getString(R.string.notification_channel_soundless_suffix)
                    ),
                    soundEnabled = false
                )
            }
        }
    }

    private fun getChannelId(notificationChannelType: NotificationChannelType, soundEnabled: Boolean) =
        if (soundEnabled) {
            notificationChannelType.channelId
        } else {
            notificationChannelType.channelId + NOTIFICATION_CHANNEL_ID_SOUNDLESS_SUFFIX
        }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createIfNotExistNotificationChannel(
        channelId: String,
        channelName: String,
        soundEnabled: Boolean
    ) = notificationManager.getNotificationChannel(channelId) ?: run {
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
            if (soundEnabled) {
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setLegacyStreamType(AudioManager.STREAM_NOTIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                        .build()
                )
            } else {
                setSound(null, null)
            }
            enableLights(true)
            enableVibration(true)
        }

        notificationManager.createNotificationChannel(channel)
    }

    fun show(notification: Notification) {
        val soundEnabled = preferencesWrapper.notificationsSoundEnabled.get()
        val appNotification: android.app.Notification = createNotification(
            channelId = getChannelId(
                notificationChannelType = notification.channel,
                soundEnabled = soundEnabled
            ),
            title = notification.title,
            body = notification.message,
            intent = getPendingIntent(notification),
            soundEnabled = soundEnabled
        )
        showNotification(notification.uuid, notification.uuid.hashCode(), appNotification)
    }

    private fun getIntent(notification: Notification) =
        Intent(ACTION_NOTIFICATION).apply {
            putExtra(PARAM_NOTIFICATION, notification)
        }

    private fun getPendingIntent(notification: Notification): PendingIntent {
        val intent = getIntent(
            notification = notification
        )
        return PendingIntent.getActivity(
            context,
            notification.uuid.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )
    }

    private fun createNotification(
        channelId: String,
        title: String?,
        body: String?,
        intent: PendingIntent,
        soundEnabled: Boolean
    ): android.app.Notification =
        NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(intent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentTitle(title)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setContentText(body)
            .setColor(ContextCompat.getColor(context, R.color.globalColorAccent))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .apply {
                if (soundEnabled) {
                    setSound(
                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                        AudioManager.STREAM_NOTIFICATION
                    )
                } else {
                    setSound(null)
                }
            }
            .build()

    private fun showNotification(
        tag: String,
        notificationId: Int,
        notification: android.app.Notification
    ) {
        @Suppress("DEPRECATION")
        notification.defaults = getNotificationFlags()
        if (notificationManager.areNotificationsEnabled()) {
            notificationManager.notify(tag, notificationId, notification)
        }
    }

    private fun getNotificationFlags() =
        (android.app.Notification.DEFAULT_LIGHTS
                or android.app.Notification.DEFAULT_VIBRATE)

    fun cancel(cancelNotification: CancelNotification) {
        if (cancelNotification.id == CancelNotification.CANCEL_ALL) {
            notificationManager.cancelAll()
        } else {
            notificationManager.cancel(cancelNotification.id.toString(), cancelNotification.id)
        }
    }

    companion object {
        const val ACTION_NOTIFICATION = "ru.rznnike.eyehealthmanager.action.NOTIFICATION"
        const val PARAM_NOTIFICATION = "PARAM_NOTIFICATION"
    }
}