package ru.rznnike.eyehealthmanager.app.model.test.acuity

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.domain.model.test.acuity.AcuityTestSymbolsType

enum class AcuityTestSymbolsTypeVM(
    val data: AcuityTestSymbolsType,
    @DrawableRes val iconResId: Int,
    @StringRes val nameResId: Int
) {
    LETTERS_RU(
        data = AcuityTestSymbolsType.LETTERS_RU,
        iconResId = R.drawable.ic_letters_ru_sh,
        nameResId = R.string.symbols_letters_ru
    ),
    LETTERS_EN(
        data = AcuityTestSymbolsType.LETTERS_EN,
        iconResId = R.drawable.ic_letters_en_f,
        nameResId = R.string.symbols_letters_en
    ),
    SQUARE(
        data = AcuityTestSymbolsType.SQUARE,
        iconResId = R.drawable.ic_square_symbol_1,
        nameResId = R.string.symbols_square
    ),
    TRIANGLE(
        data = AcuityTestSymbolsType.TRIANGLE,
        iconResId = R.drawable.ic_triangle_symbol_1,
        nameResId = R.string.symbols_triangle
    );

    companion object {
        operator fun get(data: AcuityTestSymbolsType?) = entries.find { it.data == data } ?: LETTERS_RU
    }
}