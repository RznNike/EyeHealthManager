package ru.rznnike.eyehealthmanager.app.ui.item

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.global.BaseBindingItem
import ru.rznnike.eyehealthmanager.app.model.test.TestTypeVM
import ru.rznnike.eyehealthmanager.app.utils.extensions.setScaleOnTouch
import ru.rznnike.eyehealthmanager.databinding.ItemTestTypeBinding
import ru.rznnike.eyehealthmanager.domain.model.test.TestType

class TestTypeItem(
    val testType: TestType
) : BaseBindingItem<ItemTestTypeBinding>() {
    override var identifier = testType.id.toLong()

    override val type: Int = R.id.testTypeItem

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?) =
        ItemTestTypeBinding.inflate(inflater, parent, false)

    override fun ItemTestTypeBinding.bindView() {
        val testTypeVM = TestTypeVM[testType]
        textViewName.setText(testTypeVM.nameResId)
        imageViewIcon.setImageResource(testTypeVM.iconResId)

        root.setScaleOnTouch()
    }
}