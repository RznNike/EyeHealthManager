package ru.rznnike.eyehealthmanager.domain.model.test

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ru.rznnike.eyehealthmanager.domain.R
import ru.rznnike.eyehealthmanager.domain.model.test.acuity.AcuityTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.astigmatism.AstigmatismTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.colorperception.ColorPerceptionTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.contrast.ContrastTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.daltonism.DaltonismTestResult
import ru.rznnike.eyehealthmanager.domain.model.test.nearfar.NearFarTestResult

enum class TestType(
    val id: Int,
    @StringRes val nameResId: Int,
    @DrawableRes val iconResId: Int,
    val exportHeader: String
) {
    ACUITY(
        id = 1,
        nameResId = R.string.test_acuity,
        iconResId = R.drawable.ic_acuity,
        exportHeader = AcuityTestResult.EXPORT_HEADER
    ),
    ASTIGMATISM(
        id = 3,
        nameResId = R.string.test_astigmatism,
        iconResId = R.drawable.ic_astigmatism,
        exportHeader = AstigmatismTestResult.EXPORT_HEADER
    ),
    NEAR_FAR(
        id = 4,
        nameResId = R.string.test_nearsightedness_farsightedness,
        iconResId = R.drawable.ic_near_far,
        exportHeader = NearFarTestResult.EXPORT_HEADER
    ),
    COLOR_PERCEPTION(
        id = 5,
        nameResId = R.string.test_color_perception,
        iconResId = R.drawable.ic_color_perception,
        exportHeader = ColorPerceptionTestResult.EXPORT_HEADER
    ),
    DALTONISM(
        id = 6,
        nameResId = R.string.test_daltonism,
        iconResId = R.drawable.ic_daltonism,
        exportHeader = DaltonismTestResult.EXPORT_HEADER
    ),
    CONTRAST(
        id = 7,
        nameResId = R.string.test_contrast,
        iconResId = R.drawable.ic_contrast,
        exportHeader = ContrastTestResult.EXPORT_HEADER
    );

    companion object {
        operator fun get(id: Int?) = entries.find { it.id == id } ?: ACUITY
    }
}