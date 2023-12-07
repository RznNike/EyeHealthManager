package ru.rznnike.eyehealthmanager.domain.model.enums

import androidx.annotation.DrawableRes
import ru.rznnike.eyehealthmanager.domain.R
import ru.rznnike.eyehealthmanager.domain.model.IAcuitySymbol

enum class AcuitySymbolLetterEn(
    @DrawableRes val iconResId: Int
) : IAcuitySymbol {
    E(R.drawable.ic_letters_en_e),
    F(R.drawable.ic_letters_en_f),
    P(R.drawable.ic_letters_en_p),
    T(R.drawable.ic_letters_en_t),
    O(R.drawable.ic_letters_en_o),
    Z(R.drawable.ic_letters_en_z),
    L(R.drawable.ic_letters_en_l),
    D(R.drawable.ic_letters_en_d),
    C(R.drawable.ic_letters_en_c);

    override fun getDrawableRes() = iconResId

    override fun getId() = hashCode().toLong()
}