package ru.rznnike.eyehealthmanager.app.ui.item

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.global.BaseBindingItem
import ru.rznnike.eyehealthmanager.databinding.ItemSymbolBinding
import ru.rznnike.eyehealthmanager.app.model.test.acuity.IAcuitySymbol

class SymbolItem(
    val symbol: IAcuitySymbol
) : BaseBindingItem<ItemSymbolBinding>() {
    override var identifier = symbol.getId()

    override val type: Int = R.id.symbolItem

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?) =
        ItemSymbolBinding.inflate(inflater, parent, false)

    override fun ItemSymbolBinding.bindView() {
        imageViewIcon.setImageResource(symbol.getDrawableRes())
        imageViewIcon.setBackgroundResource(
            if (isSelected) R.drawable.bg_rounded_8_outline_accent else R.color.colorTransparent
        )
    }
}