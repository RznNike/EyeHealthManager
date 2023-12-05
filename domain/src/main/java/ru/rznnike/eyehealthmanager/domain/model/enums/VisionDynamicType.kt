package ru.rznnike.eyehealthmanager.domain.model.enums

import androidx.annotation.StringRes
import ru.rznnike.eyehealthmanager.domain.R

enum class VisionDynamicType(
    @StringRes val nameResId: Int
) {
    SAME(R.string.error),
    INCREASE(R.string.error),
    DECREASE(R.string.error)
}