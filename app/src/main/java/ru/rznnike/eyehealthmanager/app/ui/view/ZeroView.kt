package ru.rznnike.eyehealthmanager.app.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.utils.extensions.setVisible
import ru.rznnike.eyehealthmanager.app.utils.extensions.toHtmlSpanned
import ru.rznnike.eyehealthmanager.databinding.ViewZeroBinding

class ZeroView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {
    private val binding = ViewZeroBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    var messageText: String = ""
        set(value) {
            field = value
            updateMessage()
        }
    var actionText: String = ""
        set(value) {
            field = value
            updateActionText()
        }
    @DrawableRes var iconRes: Int = 0
        set(value) {
            field = value
            updateIcon()
        }
    var actionCallback: (() -> Unit)? = null

    init {
        initAttributes(context, attrs)
    }

    private fun initAttributes(context: Context, attrs: AttributeSet?) {
        if (attrs == null) return

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.ZeroView)
        messageText = attributes.getString(R.styleable.ZeroView_zv_message_text) ?: ""
        actionText = attributes.getString(R.styleable.ZeroView_zv_action_text) ?: ""
        iconRes = attributes.getResourceId(R.styleable.ZeroView_zv_icon, 0)
        attributes.recycle()

        binding.buttonAction.setOnClickListener {
            actionCallback?.invoke()
        }
    }

    private fun updateMessage() = binding.textViewMessage.apply {
        text = messageText.toHtmlSpanned()
        setVisible(messageText.isNotBlank())
    }

    private fun updateActionText() = binding.buttonAction.apply {
        text = actionText
        setVisible(actionText.isNotBlank())
    }

    private fun updateIcon() = binding.imageViewIcon.apply {
        setImageResource(iconRes)
        setVisible(iconRes != 0)
    }
}
