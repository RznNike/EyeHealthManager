package ru.rznnike.eyehealthmanager.app.model.test.daltonism

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ru.rznnike.eyehealthmanager.domain.model.test.daltonism.DaltonismAnomalyType

data class DaltonismTestQuestion(
    @DrawableRes val testImageResId: Int,
    @StringRes val answerResIds: List<Int>,
    val answerVariantsMap: Map<DaltonismAnomalyType, List<Int>>,
    val answerBooleanMap: Map<DaltonismAnomalyType, Boolean>
)