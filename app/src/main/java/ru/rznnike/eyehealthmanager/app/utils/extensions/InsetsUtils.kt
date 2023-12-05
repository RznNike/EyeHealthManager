package ru.rznnike.eyehealthmanager.app.utils.extensions

import android.view.View
import android.view.ViewGroup
import androidx.core.view.*
import androidx.core.view.WindowInsetsCompat.Type.ime
import androidx.core.view.WindowInsetsCompat.Type.systemBars

fun View.addSystemWindowInsetToPadding(
    left: Boolean = false,
    top: Boolean = false,
    right: Boolean = false,
    bottom: Boolean = false,
    leftOffset: Int = 0,
    topOffset: Int = 0,
    rightOffset: Int = 0,
    bottomOffset: Int = 0,
    includeIme: Boolean = true
) {
    val (initialLeft, initialTop, initialRight, initialBottom) = listOf(
        paddingLeft + leftOffset,
        paddingTop + topOffset,
        paddingRight + rightOffset,
        paddingBottom + bottomOffset
    )

    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val mask = if (includeIme) (systemBars() or ime()) else systemBars()
        val insetsCompat = insets.getInsets(mask)
        view.updatePadding(
            left = initialLeft + (if (left) insetsCompat.left.coerceAtLeast(0) else 0),
            top = initialTop + (if (top) insetsCompat.top.coerceAtLeast(0) else 0),
            right = initialRight + (if (right) insetsCompat.right.coerceAtLeast(0) else 0),
            bottom = initialBottom + (if (bottom) insetsCompat.bottom.coerceAtLeast(0) else 0)
        )

        insets
    }
}

fun View.addSystemWindowInsetToMargin(
    left: Boolean = false,
    top: Boolean = false,
    right: Boolean = false,
    bottom: Boolean = false,
    leftOffset: Int = 0,
    topOffset: Int = 0,
    rightOffset: Int = 0,
    bottomOffset: Int = 0,
    includeIme: Boolean = true
) {
    val (initialLeft, initialTop, initialRight, initialBottom) = listOf(
        marginLeft + leftOffset,
        marginTop + topOffset,
        marginRight + rightOffset,
        marginBottom + bottomOffset
    )

    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        view.updateLayoutParams {
            (this as? ViewGroup.MarginLayoutParams)?.let {
                val mask = if (includeIme) (systemBars() or ime()) else systemBars()
                val insetsCompat = insets.getInsets(mask)
                updateMargins(
                    left = initialLeft + (if (left) insetsCompat.left.coerceAtLeast(0) else 0),
                    top = initialTop + (if (top) insetsCompat.top.coerceAtLeast(0) else 0),
                    right = initialRight + (if (right) insetsCompat.right.coerceAtLeast(0) else 0),
                    bottom = initialBottom + (if (bottom) insetsCompat.bottom.coerceAtLeast(0) else 0)
                )
            }
        }

        insets
    }
}