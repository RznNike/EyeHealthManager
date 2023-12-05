package ru.rznnike.eyehealthmanager.domain.model.enums

import androidx.annotation.DrawableRes
import ru.rznnike.eyehealthmanager.domain.R
import ru.rznnike.eyehealthmanager.domain.model.IAcuitySymbol

enum class AcuitySymbolSquare(
    @DrawableRes val iconResId: Int
) : IAcuitySymbol {
    SYMBOL_1(R.drawable.ic_square_symbol_1),
    SYMBOL_2(R.drawable.ic_square_symbol_2),
    SYMBOL_3(R.drawable.ic_square_symbol_3),
    SYMBOL_4(R.drawable.ic_square_symbol_4),
    SYMBOL_5(R.drawable.ic_square_symbol_5),
    SYMBOL_6(R.drawable.ic_square_symbol_6),
    SYMBOL_7(R.drawable.ic_square_symbol_7),
    SYMBOL_8(R.drawable.ic_square_symbol_8),
    SYMBOL_9(R.drawable.ic_square_symbol_9),
    SYMBOL_10(R.drawable.ic_square_symbol_10);

    override fun getDrawableRes() = iconResId

    override fun getTag() = toString()
}