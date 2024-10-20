package ru.rznnike.eyehealthmanager.app.ui.item

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.global.BaseBindingItem
import ru.rznnike.eyehealthmanager.app.model.test.TestTypeVM
import ru.rznnike.eyehealthmanager.app.utils.extensions.setImageTint
import ru.rznnike.eyehealthmanager.databinding.ItemTestTypeIndicatorBinding
import ru.rznnike.eyehealthmanager.domain.model.test.TestType

class TestTypeIndicatorItem(
    private val testType: TestType,
    private val available: Boolean = false
) : BaseBindingItem<ItemTestTypeIndicatorBinding>() {
    override var identifier = testType.id.toLong()

    override val type: Int = R.id.testTypeIndicatorItem

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?) =
        ItemTestTypeIndicatorBinding.inflate(inflater, parent, false)

    override fun ItemTestTypeIndicatorBinding.bindView() {
        val testTypeVM = TestTypeVM[testType]
        textViewName.setText(testTypeVM.nameResId)
        imageViewIcon.setImageResource(testTypeVM.iconResId)
        if (available) {
            imageViewIndicator.setImageResource(R.drawable.ic_check)
            imageViewIndicator.setImageTint(R.color.colorAccent)
        } else {
            imageViewIndicator.setImageResource(R.drawable.ic_close)
            imageViewIndicator.setImageTint(R.color.colorRed)
        }
    }
}