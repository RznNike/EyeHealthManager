package ru.rznnike.eyehealthmanager.domain.model

import androidx.annotation.DrawableRes

interface IAcuitySymbol {
    @DrawableRes fun getDrawableRes(): Int

    fun getId(): Long
}