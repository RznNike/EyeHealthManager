package ru.rznnike.eyehealthmanager.domain.model.enums

import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize
import ru.rznnike.eyehealthmanager.domain.R

@Parcelize
enum class AstigmatismAnswerType(
    val id: Int,
    @StringRes val nameResId: Int
) : Parcelable {
    OK(
        id = 1,
        nameResId = R.string.normal_condition
    ),
    ANOMALY(
        id = 2,
        nameResId = R.string.possible_astigmatism
    );

    companion object {
        operator fun get(id: Int?) = entries.find { it.id == id } ?: OK

        operator fun get(name: String?) = entries.find { it.toString() == name }
    }
}