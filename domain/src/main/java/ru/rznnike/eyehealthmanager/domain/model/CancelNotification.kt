package ru.rznnike.eyehealthmanager.domain.model

data class CancelNotification(val id: Int = CANCEL_ALL) {
    companion object {
        const val CANCEL_ALL: Int = Int.MIN_VALUE
    }
}