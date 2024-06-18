package ru.rznnike.eyehealthmanager.app.model.test.acuity

import androidx.annotation.DrawableRes
import ru.rznnike.eyehealthmanager.R

enum class AcuitySymbolLetterRu(
    @DrawableRes val iconResId: Int
) : IAcuitySymbol {
    SH(R.drawable.ic_letters_ru_sh),
    B(R.drawable.ic_letters_ru_b),
    M(R.drawable.ic_letters_ru_m),
    N(R.drawable.ic_letters_ru_n),
    K(R.drawable.ic_letters_ru_k),
    Y(R.drawable.ic_letters_ru_y),
    I(R.drawable.ic_letters_ru_i);

    override fun getDrawableRes() = iconResId

    override fun getId() = hashCode().toLong()
}