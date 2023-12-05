package ru.rznnike.eyehealthmanager.domain.model

object EmptyAcuitySymbol : IAcuitySymbol {
    override fun getDrawableRes() = 0

    override fun getTag() = ""
}