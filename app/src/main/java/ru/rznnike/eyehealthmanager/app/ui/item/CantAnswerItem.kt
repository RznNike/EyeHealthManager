package ru.rznnike.eyehealthmanager.app.ui.item

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.global.BaseBindingItem
import ru.rznnike.eyehealthmanager.databinding.ItemCantAnswerBinding

class CantAnswerItem : BaseBindingItem<ItemCantAnswerBinding>() {
    override var identifier = -10L

    override val type: Int = R.id.cantAnswerItem

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?) =
        ItemCantAnswerBinding.inflate(inflater, parent, false)

    override fun ItemCantAnswerBinding.bindView() {
        val background = if (isSelected) {
            R.drawable.bg_rounded_4_outline_accent
        } else {
            R.color.colorTransparent
        }
        textViewName.setBackgroundResource(background)
    }
}