package ru.rznnike.eyehealthmanager.app.model.test.acuity

import androidx.annotation.DrawableRes

interface IAcuitySymbol {
    @DrawableRes fun getDrawableRes(): Int

    fun getId(): Long
}