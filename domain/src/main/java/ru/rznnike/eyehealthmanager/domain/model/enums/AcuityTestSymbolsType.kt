package ru.rznnike.eyehealthmanager.domain.model.enums

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ru.rznnike.eyehealthmanager.domain.R

enum class AcuityTestSymbolsType(
    val id: Int,
    @DrawableRes val iconResId: Int,
    @StringRes val nameResId: Int
) {
    LETTERS_RU(1, R.drawable.ic_letters_ru_sh, R.string.symbols_letters_ru),
    LETTERS_EN(2, R.drawable.ic_letters_en_f, R.string.symbols_letters_en),
    SQUARE(3, R.drawable.ic_square_symbol_1, R.string.symbols_square),
    TRIANGLE(4, R.drawable.ic_triangle_symbol_1, R.string.symbols_triangle);

    companion object {
        operator fun get(id: Int?) = values().find { it.id == id } ?: LETTERS_RU

        fun parseName(string: String?) = values().find { it.toString() == string }
    }
}