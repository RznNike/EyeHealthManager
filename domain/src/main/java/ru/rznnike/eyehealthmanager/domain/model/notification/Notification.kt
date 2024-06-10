package ru.rznnike.eyehealthmanager.domain.model.notification

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import ru.rznnike.eyehealthmanager.domain.model.common.ExternalIntentData
import ru.rznnike.eyehealthmanager.domain.model.common.toExternalIntentData
import ru.rznnike.eyehealthmanager.domain.utils.currentTimeMillis
import java.util.UUID

@Parcelize
data class Notification(
    var uuid: String,
    var channel: NotificationChannelType = NotificationChannelType.SYSTEM,
    var title: String,
    var message: String,
    var externalIntentData: ExternalIntentData,
    val createdDate: Long = currentTimeMillis()
) : Parcelable {
    @IgnoredOnParcel
    var showed: Boolean = false

    companion object {
        const val TITLE = "title"
        const val MESSAGE = "body"
    }
}

fun Map<String, String>.toNotification(
    title: String? = null,
    message: String? = null
): Notification {
    val uuid = UUID.randomUUID().toString()
    val finalTitle = title ?: this[Notification.TITLE]
    val finalMessage = message ?: this[Notification.MESSAGE]

    return Notification(
        uuid = uuid,
        title = finalTitle ?: "",
        message = finalMessage ?: "",
        externalIntentData = this.toExternalIntentData()
    )
}
