package ru.rznnike.eyehealthmanager.app.ui.view

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import ru.rznnike.eyehealthmanager.databinding.ViewPercentProgressBinding

private const val PROGRESS_MULTIPLIER = 100 // more steps for smooth animation
private const val PROGRESS_ANIMATION_MS = 500L

class PercentProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {
    private val binding = ViewPercentProgressBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    var max: Int = 100
        set(value) {
            field = value
            updateMax()
        }
    var progress: Int = 0
        set(value) {
            field = value
            updateProgress()
        }

    init {
        updateMax()
        updateProgress()
    }

    private fun updateMax() = binding.apply {
        percentProgressBar.max = max * PROGRESS_MULTIPLIER
    }

    private fun updateProgress() = binding.apply {
        textViewProgress.text = "%d%%".format(progress)
        ObjectAnimator.ofInt(percentProgressBar, "progress", progress * PROGRESS_MULTIPLIER)
            .setDuration(PROGRESS_ANIMATION_MS)
            .start()
    }
}
