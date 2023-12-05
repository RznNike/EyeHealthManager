package ru.rznnike.eyehealthmanager.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ru.rznnike.eyehealthmanager.domain.model.enums.DaltonismAnomalyType

data class DaltonismTestQuestion(
    @DrawableRes val testImageResId: Int,
    @StringRes val answerResIds: List<Int>,
    val answerVariantsMap: Map<DaltonismAnomalyType, List<Int>>,
    val answerBooleanMap: Map<DaltonismAnomalyType, Boolean>
)