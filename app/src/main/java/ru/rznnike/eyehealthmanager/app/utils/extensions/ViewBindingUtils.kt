package ru.rznnike.eyehealthmanager.app.utils.extensions

import android.content.Context
import android.content.res.Resources
import android.os.Handler
import android.os.Looper
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding

val ViewBinding.context: Context
    get() = root.context

val ViewBinding.resources: Resources
    get() = root.resources

fun ViewBinding.getColor(@ColorRes color: Int) = ContextCompat.getColor(context, color)

fun ViewBinding.getDrawable(@DrawableRes drawable: Int) =
    ContextCompat.getDrawable(context, drawable)

fun ViewBinding.getString(@StringRes stringRes: Int): String = context.getString(stringRes)

fun ViewBinding.getString(@StringRes stringRes: Int, vararg formatArgs: Any): String =
    context.getString(stringRes, *formatArgs)

fun ViewBinding.withDelay(delay: Long, block: () -> Unit) {
    root.postDelayed(block, delay)
}
