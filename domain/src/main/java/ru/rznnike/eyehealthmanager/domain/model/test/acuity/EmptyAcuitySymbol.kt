package ru.rznnike.eyehealthmanager.domain.model.test.acuity

import ru.rznnike.eyehealthmanager.domain.model.test.acuity.IAcuitySymbol

object EmptyAcuitySymbol : IAcuitySymbol {
    override fun getDrawableRes() = 0

    override fun getId() = -10L
}