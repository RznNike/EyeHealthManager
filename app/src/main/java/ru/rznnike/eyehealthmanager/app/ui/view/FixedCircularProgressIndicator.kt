package ru.rznnike.eyehealthmanager.app.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.databinding.ViewFixedCircularProgressIndicatorBinding
import kotlin.math.min
import androidx.core.content.withStyledAttributes

class FixedCircularProgressIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val binding = ViewFixedCircularProgressIndicatorBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    @ColorInt var progressColor: Int = ContextCompat.getColor(context, R.color.globalColorAccent)
        set(value) {
            field = value
            updateProgressColor()
        }

    init {
        initAttributes(context, attrs)
    }

    private fun initAttributes(context: Context, attrs: AttributeSet?) {
        if (attrs == null) return

        context.withStyledAttributes(attrs, R.styleable.FixedCircularProgressIndicator) {
            progressColor = getColor(
                R.styleable.FixedCircularProgressIndicator_fcpi_progress_color,
                ContextCompat.getColor(context, R.color.colorAccent)
            )
        }

        binding.apply {
            circularProgressIndicator.viewTreeObserver.addOnGlobalLayoutListener {
                if ((circularProgressIndicator.width != layoutContainer.width)
                    || (circularProgressIndicator.height != layoutContainer.height)
                ) {
                    circularProgressIndicator.updateLayoutParams {
                        width = layoutContainer.width
                        height = layoutContainer.height
                    }
                }
                circularProgressIndicator.indicatorSize = min(root.width, root.height)
            }
        }
    }

    private fun updateProgressColor() = binding.circularProgressIndicator.apply {
        setIndicatorColor(progressColor)
    }
}
