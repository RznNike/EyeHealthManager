package ru.rznnike.eyehealthmanager.app.presentation.acuity.test

import androidx.annotation.DrawableRes
import moxy.viewstate.strategy.AddToEndSingleTagStrategy
import moxy.viewstate.strategy.StateStrategyType
import moxy.viewstate.strategy.alias.AddToEndSingle
import ru.rznnike.eyehealthmanager.app.global.presentation.NavigationMvpView
import ru.rznnike.eyehealthmanager.domain.model.enums.AcuityTestSymbolsType
import ru.rznnike.eyehealthmanager.domain.model.enums.TestEyesType

private const val DATA_TAG = "DATA_TAG"

interface AcuityTestView : NavigationMvpView {
    @AddToEndSingle
    fun setProgress(show: Boolean, immediately: Boolean = true)

    @StateStrategyType(value = AddToEndSingleTagStrategy::class, tag = DATA_TAG)
    fun showInfo(eyesType: TestEyesType, progress: Int)

    @StateStrategyType(value = AddToEndSingleTagStrategy::class, tag = DATA_TAG)
    fun showTestStep(
        @DrawableRes imageResId: Int,
        vision: Int,
        dpmm: Float,
        distance: Int,
        progress: Int
    )

    @StateStrategyType(value = AddToEndSingleTagStrategy::class, tag = DATA_TAG)
    fun showAnswerVariants(symbolsType: AcuityTestSymbolsType, progress: Int)
}
