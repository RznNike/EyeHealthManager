package ru.rznnike.eyehealthmanager.app.model.notification

import androidx.annotation.StringRes
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.domain.model.notification.NotificationChannelType

enum class NotificationChannelTypeVM(
    val data: NotificationChannelType,
    @StringRes val nameResId: Int
) {
    SYSTEM(
        data = NotificationChannelType.SYSTEM,
        nameResId = R.string.notification_channel_system
    );

    companion object {
        operator fun get(data: NotificationChannelType?) = entries.find { it.data == data } ?: SYSTEM
    }
}