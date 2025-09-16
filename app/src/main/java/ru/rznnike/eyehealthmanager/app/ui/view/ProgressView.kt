package ru.rznnike.eyehealthmanager.app.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.utils.extensions.setVisible
import ru.rznnike.eyehealthmanager.databinding.ViewProgressBinding
import java.util.*
import kotlin.concurrent.schedule

private const val PROGRESS_DELAY_MS = 250L

class ProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {
    private val binding = ViewProgressBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    private var progressTask: TimerTask? = null
    private var goalProgressIsVisible = false

    init {
        attrs?.let {
            context.withStyledAttributes(attrs, R.styleable.ProgressView) {
                val text = getString(R.styleable.ProgressView_pv_text)
                val textColor = getColor(
                    R.styleable.ProgressView_pv_text_color,
                    ContextCompat.getColor(context, R.color.colorAccent)
                )
                val progressColor = getColor(
                    R.styleable.ProgressView_pv_progress_color,
                    ContextCompat.getColor(context, R.color.colorAccent)
                )
                val backgroundColor = getColor(R.styleable.ProgressView_pv_background, 0)

                setProgressText(text)
                setProgressTextColor(textColor)
                setProgressColor(progressColor)
                setProgressBackground(backgroundColor)
            }
        }

        binding.layoutContent.visibility = visibility
    }

    public override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        progressTask?.cancel()
    }

    fun setProgressText(text: CharSequence?) {
        binding.apply {
            textView.text = text
            textView.visibility = if (text.isNullOrBlank()) GONE else VISIBLE
        }
    }

    fun setProgressText(@StringRes textRes: Int) {
        setProgressText(context.getString(textRes))
    }

    fun setProgressTextColor(color: Int) {
        binding.textView.setTextColor(color)
    }

    fun setProgressColor(color: Int) {
        binding.progressBar.progressColor = color
    }

    fun setProgressBackground(backgroundColor: Int) {
        binding.layoutContent.setBackgroundColor(backgroundColor)
    }

    fun setProgress(show: Boolean) = setProgress(show = show, immediately = false)

    fun setProgress(show: Boolean, immediately: Boolean) {
        if ((goalProgressIsVisible == show) && (progressTask != null)) return

        goalProgressIsVisible = show
        progressTask?.cancel()
        if (immediately) {
            post {
                setVisible(show)
                binding.layoutContent.setVisible(show)
            }
        } else {
            if (show) setVisible()
            progressTask = Timer().schedule(PROGRESS_DELAY_MS) {
                post {
                    setVisible(show)
                    binding.layoutContent.setVisible(show)
                }
            }
        }
    }
}
