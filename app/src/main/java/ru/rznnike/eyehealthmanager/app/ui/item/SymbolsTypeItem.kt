package ru.rznnike.eyehealthmanager.app.ui.item

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.global.BaseBindingItem
import ru.rznnike.eyehealthmanager.app.model.test.acuity.AcuityTestSymbolsTypeVM
import ru.rznnike.eyehealthmanager.databinding.ItemSymbolsTypeBinding
import ru.rznnike.eyehealthmanager.domain.model.test.acuity.AcuityTestSymbolsType

class SymbolsTypeItem(
    val symbolsType: AcuityTestSymbolsType
) : BaseBindingItem<ItemSymbolsTypeBinding>() {
    override var identifier = symbolsType.id.toLong()

    override val type: Int = R.id.symbolsTypeItem

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?) =
        ItemSymbolsTypeBinding.inflate(inflater, parent, false)

    override fun ItemSymbolsTypeBinding.bindView() {
        imageViewIcon.setImageResource(AcuityTestSymbolsTypeVM[symbolsType].iconResId)
        imageViewIcon.setBackgroundResource(
            if (isSelected) R.drawable.bg_rounded_8_outline_accent else R.color.colorTransparent
        )
    }
}