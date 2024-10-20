package ru.rznnike.eyehealthmanager.app.ui.item

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.global.BaseBindingItem
import ru.rznnike.eyehealthmanager.app.model.test.TestEyesTypeVM
import ru.rznnike.eyehealthmanager.databinding.ItemEyesTypeBinding
import ru.rznnike.eyehealthmanager.domain.model.test.TestEyesType

class EyesTypeItem(
    val eyesType: TestEyesType
) : BaseBindingItem<ItemEyesTypeBinding>() {
    override var identifier = eyesType.id.toLong()

    override val type: Int = R.id.eyesTypeItem

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?) =
        ItemEyesTypeBinding.inflate(inflater, parent, false)

    override fun ItemEyesTypeBinding.bindView() {
        textViewName.setText(TestEyesTypeVM[eyesType].nameResId)
        textViewName.setBackgroundResource(
            if (isSelected) R.drawable.bg_rounded_8_outline_accent else R.color.colorTransparent
        )
    }
}