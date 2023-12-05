package ru.rznnike.eyehealthmanager.app.dialog.bottom

import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatTextView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.utils.extensions.setTextColorRes

class BottomDialogButtonItem(
    @LayoutRes buttonLayoutResId: Int,
    val bottomDialogAction: BottomDialogAction
) : AbstractItem<BottomDialogButtonItem.ViewHolder>() {
    override val type: Int = R.id.bottomDialogButtonItem

    override val layoutRes = buttonLayoutResId

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    class ViewHolder(view: View) : FastAdapter.ViewHolder<BottomDialogButtonItem>(view) {
        private val textViewName = view.findViewById<AppCompatTextView>(R.id.textViewContent)

        override fun bindView(item: BottomDialogButtonItem, payloads: List<Any>) {
            textViewName?.apply {
                text = item.bottomDialogAction.text
                setTextColorRes(
                    if (item.bottomDialogAction.selected) R.color.colorAccent else R.color.colorText
                )
            }
        }

        override fun unbindView(item: BottomDialogButtonItem) = Unit
    }
}