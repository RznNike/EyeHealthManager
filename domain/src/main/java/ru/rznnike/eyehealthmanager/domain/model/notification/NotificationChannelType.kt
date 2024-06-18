package ru.rznnike.eyehealthmanager.domain.model.notification

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class NotificationChannelType(
    val id: Int,
    val channelId: String
) : Parcelable {
    SYSTEM(
        id = 0,
        channelId = "system_channel"
    );

    companion object {
        operator fun get(id: Int?) = entries.find { it.id == id } ?: SYSTEM
    }
}