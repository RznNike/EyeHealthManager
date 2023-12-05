package ru.rznnike.eyehealthmanager.app.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.utils.extensions.setImageTint
import ru.rznnike.eyehealthmanager.app.utils.extensions.setTextColorRes
import ru.rznnike.eyehealthmanager.databinding.ViewNavbarItemBinding

class NavbarItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {
    private val binding = ViewNavbarItemBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    var title: String = ""
        set(value) {
            field = value
            updateText()
        }
    @DrawableRes var iconResId: Int = R.drawable.icon
        set(value) {
            field = value
            updateIcon()
        }
    var selection: Boolean = false
        set(value) {
            field = value
            updateSelection()
        }

    init {
        initAttributes(context, attrs)
    }

    private fun initAttributes(context: Context, attrs: AttributeSet?) {
        if (attrs == null) return

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.NavbarItemView)
        title = attributes.getString(R.styleable.NavbarItemView_niv_title) ?: ""
        iconResId = attributes.getResourceId(R.styleable.NavbarItemView_niv_icon, R.drawable.icon)
        selection = attributes.getBoolean(R.styleable.NavbarItemView_niv_selection, false)
        attributes.recycle()
    }

    private fun updateText() = binding.textViewTitle.apply {
        text = title
    }

    private fun updateIcon() = binding.imageViewIcon.apply {
        setImageResource(iconResId)
    }

    private fun updateSelection() = binding.apply {
        val colorResId = if (selection) R.color.colorAccent else R.color.colorTextDark
        imageViewIcon.setImageTint(colorResId)
        textViewTitle.setTextColorRes(colorResId)
    }
}
