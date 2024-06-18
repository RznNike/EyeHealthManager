package ru.rznnike.eyehealthmanager.app.model.test.daltonism

import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.domain.model.test.daltonism.DaltonismAnomalyType

@Parcelize
enum class DaltonismAnomalyTypeVM(
    val data: DaltonismAnomalyType,
    @StringRes val nameResId: Int
) : Parcelable {
    NONE(
        data = DaltonismAnomalyType.NONE,
        nameResId = R.string.rabkin_result_normal
    ),
    PROTANOPIA(
        data = DaltonismAnomalyType.PROTANOPIA,
        nameResId = R.string.rabkin_result_protanopia
    ),
    DEITERANOPIA(
        data = DaltonismAnomalyType.DEITERANOPIA,
        nameResId = R.string.rabkin_result_deiteranopia
    ),
    PROTANOMALY_A(
        data = DaltonismAnomalyType.PROTANOMALY_A,
        nameResId = R.string.rabkin_result_protanomaly_a
    ),
    PROTANOMALY_B(
        data = DaltonismAnomalyType.PROTANOMALY_B,
        nameResId = R.string.rabkin_result_protanomaly_b
    ),
    PROTANOMALY_C(
        data = DaltonismAnomalyType.PROTANOMALY_C,
        nameResId = R.string.rabkin_result_protanomaly_c
    ),
    DEITERANOMALY_A(
        data = DaltonismAnomalyType.DEITERANOMALY_A,
        nameResId = R.string.rabkin_result_deiteranomaly_a
    ),
    DEITERANOMALY_B(
        data = DaltonismAnomalyType.DEITERANOMALY_B,
        nameResId = R.string.rabkin_result_deiteranomaly_b
    ),
    DEITERANOMALY_C(
        data = DaltonismAnomalyType.DEITERANOMALY_C,
        nameResId = R.string.rabkin_result_deiteranomaly_c
    ),
    PATHOLOGY(
        data = DaltonismAnomalyType.PATHOLOGY,
        nameResId = R.string.rabkin_result_parhology
    );

    companion object {
        operator fun get(data: DaltonismAnomalyType?) = entries.find { it.data == data } ?: NONE
    }
}