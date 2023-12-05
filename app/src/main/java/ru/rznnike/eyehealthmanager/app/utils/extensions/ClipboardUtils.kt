package ru.rznnike.eyehealthmanager.app.utils.extensions

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.fragment.app.Fragment

fun Activity.copyToClipboard(text: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    val clip = ClipData.newPlainText("", text)
    clipboard?.setPrimaryClip(clip)
}

fun Fragment.copyToClipboard(text: String) = activity?.copyToClipboard(text)