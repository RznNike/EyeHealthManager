package ru.rznnike.eyehealthmanager.domain.model.enums

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ru.rznnike.eyehealthmanager.domain.R

enum class TestType(
    val id: Int,
    @StringRes val nameResId: Int,
    @DrawableRes val iconResId: Int
) {
    ACUITY(1, R.string.test_acuity, R.drawable.ic_acuity),
    ASTIGMATISM(3, R.string.test_astigmatism, R.drawable.ic_astigmatism),
    NEAR_FAR(4, R.string.test_nearsightedness_farsightedness, R.drawable.ic_near_far),
    COLOR_PERCEPTION(5, R.string.test_color_perception, R.drawable.ic_color_perception),
    DALTONISM(6, R.string.test_daltonism, R.drawable.ic_daltonism),
    CONTRAST(7, R.string.test_contrast, R.drawable.ic_contrast);

    companion object {
        operator fun get(id: Int?) = values().find { it.id == id } ?: ACUITY
    }
}