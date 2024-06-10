package ru.rznnike.eyehealthmanager.domain.model.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class ExternalIntentData(
    open var processed: Boolean = false
) : Parcelable {
    abstract fun copy(): ExternalIntentData

    data class App(
        override var processed: Boolean = false
    ) : ExternalIntentData() {
        override fun copy() = copy(processed = processed)
    }
}

fun Map<String, String>.toExternalIntentData(): ExternalIntentData {
    return ExternalIntentData.App()
}
