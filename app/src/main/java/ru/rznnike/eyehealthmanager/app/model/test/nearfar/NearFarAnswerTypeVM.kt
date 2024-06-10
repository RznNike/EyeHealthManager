package ru.rznnike.eyehealthmanager.app.model.test.nearfar

import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.domain.model.test.nearfar.NearFarAnswerType

@Parcelize
enum class NearFarAnswerTypeVM(
    val data: NearFarAnswerType,
    @StringRes val nameResId: Int
) : Parcelable {
    EQUAL(
        data = NearFarAnswerType.EQUAL,
        nameResId = R.string.normal_condition
    ),
    RED_BETTER(
        data = NearFarAnswerType.RED_BETTER,
        nameResId = R.string.possible_myopia
    ),
    GREEN_BETTER(
        data = NearFarAnswerType.GREEN_BETTER,
        nameResId = R.string.possible_farsightedness
    );

    companion object {
        operator fun get(data: NearFarAnswerType?) = entries.find { it.data == data } ?: EQUAL
    }
}