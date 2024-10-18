package ru.rznnike.eyehealthmanager.app.model.test.acuity

object EmptyAcuitySymbol : IAcuitySymbol {
    override fun getDrawableRes() = 0

    override fun getId() = -10L
}