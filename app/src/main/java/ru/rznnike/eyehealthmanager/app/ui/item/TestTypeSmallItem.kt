package ru.rznnike.eyehealthmanager.app.ui.item

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.global.BaseBindingItem
import ru.rznnike.eyehealthmanager.databinding.ItemTestTypeSmallBinding
import ru.rznnike.eyehealthmanager.domain.model.enums.TestType

class TestTypeSmallItem(
    val testType: TestType,
    val selection: Boolean
) : BaseBindingItem<ItemTestTypeSmallBinding>() {
    override var identifier = testType.id.toLong()

    override val type: Int = R.id.testTypeSmallItem

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?) =
        ItemTestTypeSmallBinding.inflate(inflater, parent, false)

    override fun ItemTestTypeSmallBinding.bindView() {
        imageViewIcon.setImageResource(testType.iconResId)
        imageViewIcon.setBackgroundResource(
            if (selection) R.drawable.bg_rounded_4_outline_accent else R.color.colorTransparent
        )
    }
}