package ru.rznnike.eyehealthmanager.app.utils.extensions

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ru.rznnike.eyehealthmanager.R

fun View?.setVisible() {
    this?.visibility = View.VISIBLE
}

fun View?.setVisible(isVisible: Boolean) {
    this?.visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun View?.setInvisible() {
    this?.visibility = View.INVISIBLE
}

fun View?.setGone() {
    this?.visibility = View.GONE
}

fun View?.updateMargins(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) {
    this?.apply {
        if (layoutParams is ViewGroup.MarginLayoutParams) {
            val marginLayoutParams = layoutParams as ViewGroup.MarginLayoutParams
            marginLayoutParams.setMargins(left, top, right, bottom)
            requestLayout()
        }
    }
}

fun TextView.setDrawable(
    left: Drawable? = null,
    top: Drawable? = null,
    right: Drawable? = null,
    bottom: Drawable? = null
) = setCompoundDrawablesRelativeWithIntrinsicBounds(left, top, right, bottom)

fun TextView.setDrawableRes(
    @DrawableRes left: Int = 0,
    @DrawableRes top: Int = 0,
    @DrawableRes right: Int = 0,
    @DrawableRes bottom: Int = 0
) = setCompoundDrawablesRelativeWithIntrinsicBounds(left, top, right, bottom)

fun TextView.setDrawableTintRes(@ColorRes res: Int) {
    context?.let {
        TextViewCompat.setCompoundDrawableTintList(
            this,
            ContextCompat.getColorStateList(it, res)
        )
    }
}

fun View.setBackgroundTint(@ColorRes res: Int) {
    context?.let { backgroundTintList = ContextCompat.getColorStateList(it, res) }
}

fun View.setForegroundRes(@DrawableRes res: Int) {
    context?.let { foreground = ContextCompat.getDrawable(it, res) }
}

fun ImageView.setImageTint(@ColorRes res: Int) {
    context?.let { imageTintList = ContextCompat.getColorStateList(it, res) }
}

fun TextView.setTextColorRes(@ColorRes res: Int) {
    context?.let { setTextColor(ContextCompat.getColor(it, res)) }
}

fun SwipeRefreshLayout.setupDefaults() {
    setColorSchemeResources(R.color.colorAccent)
    setProgressBackgroundColorSchemeResource(R.color.colorBackgroundGray)
    setProgressViewOffset(false, progressViewStartOffset, 50)
}

@SuppressLint("ClickableViewAccessibility")
fun View.setScaleOnTouch() {
    val pressedScale = 0.96f
    val normalScale = 1f
    val durationMs = 200L
    setOnTouchListener { view, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                view.animate()
                    .scaleX(pressedScale)
                    .scaleY(pressedScale)
                    .setDuration(durationMs)
                    .setStartDelay(0)
                    .start()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                view.animate()
                    .scaleX(normalScale)
                    .scaleY(normalScale)
                    .setDuration(durationMs)
                    .setStartDelay(0)
                    .start()
            }
        }
        false
    }
}