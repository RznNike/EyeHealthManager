package ru.rznnike.eyehealthmanager.domain.model.enums

import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize
import ru.rznnike.eyehealthmanager.domain.R

@Parcelize
enum class NearFarAnswerType(
    val id: Int,
    @StringRes val nameResId: Int
) : Parcelable {
    RED_BETTER(
        id = 1,
        nameResId = R.string.possible_myopia
    ),
    GREEN_BETTER(
        id = 2,
        nameResId = R.string.possible_farsightedness
    ),
    EQUAL(
        id = 0,
        nameResId = R.string.normal_condition
    );

    companion object {
        operator fun get(id: Int?) = entries.find { it.id == id } ?: EQUAL

        operator fun get(name: String?) = entries.find { it.toString() == name }
    }
}