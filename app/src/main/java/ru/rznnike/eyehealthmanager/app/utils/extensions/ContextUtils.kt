package ru.rznnike.eyehealthmanager.app.utils.extensions

import android.app.Activity
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Rect
import android.net.Uri
import android.os.*
import android.util.DisplayMetrics
import android.util.TypedValue
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.window.layout.WindowMetricsCalculator
import ru.rznnike.eyehealthmanager.BuildConfig
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.dispatcher.notifier.Notifier
import ru.rznnike.eyehealthmanager.domain.model.RemoteFile
import java.io.File
import java.util.*

fun Context.convertDpToPx(value: Float) = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    value,
    resources.displayMetrics
)

fun Context.convertPxToDp(value: Float) =
    value * (resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)

fun Context.convertMmToPx(value: Float) = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_MM,
    value,
    resources.displayMetrics
)

fun Context.convertPxToMm(value: Float) =
    value / (resources.displayMetrics.xdpi * (1.0f / 25.4f))


fun Context.convertSpToPx(value: Float) = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_SP,
    value,
    resources.displayMetrics
)

val Fragment.deviceSize: Rect
    get() = requireActivity().deviceSize

val Activity.deviceSize: Rect
    get() = WindowMetricsCalculator.getOrCreate()
        .computeCurrentWindowMetrics(this)
        .bounds
