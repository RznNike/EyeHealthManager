package ru.rznnike.eyehealthmanager.app.model.test.astigmatism

import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.domain.model.test.astigmatism.AstigmatismAnswerType

@Parcelize
enum class AstigmatismAnswerTypeVM(
    val data: AstigmatismAnswerType,
    @StringRes val nameResId: Int
) : Parcelable {
    OK(
        data = AstigmatismAnswerType.OK,
        nameResId = R.string.normal_condition
    ),
    ANOMALY(
        data = AstigmatismAnswerType.ANOMALY,
        nameResId = R.string.possible_astigmatism
    );

    companion object {
        operator fun get(data: AstigmatismAnswerType?) = entries.find { it.data == data } ?: OK
    }
}