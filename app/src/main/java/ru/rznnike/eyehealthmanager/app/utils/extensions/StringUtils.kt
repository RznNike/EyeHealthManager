package ru.rznnike.eyehealthmanager.app.utils.extensions

import androidx.core.text.HtmlCompat
import java.util.Locale

fun String.toHtmlSpanned() = HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY)

fun String.capitalize() = replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(
        Locale.getDefault()
    ) else it.toString()
}