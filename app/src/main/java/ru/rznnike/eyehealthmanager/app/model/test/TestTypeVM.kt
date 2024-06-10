package ru.rznnike.eyehealthmanager.app.model.test

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.domain.model.test.TestType

enum class TestTypeVM(
    val data: TestType,
    @StringRes val nameResId: Int,
    @DrawableRes val iconResId: Int
) {
    ACUITY(
        data = TestType.ACUITY,
        nameResId = R.string.test_acuity,
        iconResId = R.drawable.ic_acuity
    ),
    ASTIGMATISM(
        data = TestType.ASTIGMATISM,
        nameResId = R.string.test_astigmatism,
        iconResId = R.drawable.ic_astigmatism
    ),
    NEAR_FAR(
        data = TestType.NEAR_FAR,
        nameResId = R.string.test_nearsightedness_farsightedness,
        iconResId = R.drawable.ic_near_far
    ),
    COLOR_PERCEPTION(
        data = TestType.COLOR_PERCEPTION,
        nameResId = R.string.test_color_perception,
        iconResId = R.drawable.ic_color_perception
    ),
    DALTONISM(
        data = TestType.DALTONISM,
        nameResId = R.string.test_daltonism,
        iconResId = R.drawable.ic_daltonism
    ),
    CONTRAST(
        data = TestType.CONTRAST,
        nameResId = R.string.test_contrast,
        iconResId = R.drawable.ic_contrast
    );

    companion object {
        operator fun get(data: TestType?) = entries.find { it.data == data } ?: ACUITY
    }
}