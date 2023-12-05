package ru.rznnike.eyehealthmanager.app.utils.extensions

import android.os.Handler
import android.os.Looper
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

fun Fragment.withDelay(delay: Long, block: () -> Unit) {
    view?.postDelayed(block, delay) ?: Handler(Looper.getMainLooper()).postDelayed(block, delay)
}

fun Fragment.getColor(@ColorRes color: Int) = ContextCompat.getColor(requireContext(), color)
