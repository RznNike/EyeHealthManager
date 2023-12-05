package ru.rznnike.eyehealthmanager.domain.model.enums

import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize
import ru.rznnike.eyehealthmanager.domain.R

@Parcelize
enum class NotificationChannelType(
    val id: Int,
    val channelId: String,
    @StringRes val nameResId: Int
) : Parcelable {
    SYSTEM(0, "system_channel", R.string.notification_channel_system);

    companion object {
        operator fun get(id: Int?) = values().find { it.id == id } ?: SYSTEM
    }
}