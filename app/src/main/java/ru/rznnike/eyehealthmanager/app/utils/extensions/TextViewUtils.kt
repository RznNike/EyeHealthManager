package ru.rznnike.eyehealthmanager.app.utils.extensions

import android.text.SpannableString
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.View
import android.widget.TextView

fun TextView.removeLinkUnderlines(clickCallback: ((String) -> Unit)? = null) {
    val spannable = SpannableString(text)
    val spans = spannable.getSpans(0, spannable.length, URLSpan::class.java)
    for (span in spans) {
        val start = spannable.getSpanStart(span)
        val end = spannable.getSpanEnd(span)
        val newSpan = clickCallback?.let {
            ClickableSpanNoUnderline(span.url, clickCallback)
        } ?: run {
            URLSpanNoUnderline(span.url)
        }
        spannable.removeSpan(span)
        spannable.setSpan(newSpan, start, end, 0)
    }
    text = spannable
}

private class URLSpanNoUnderline(
    url: String
) : URLSpan(url) {
    override fun updateDrawState(textPaint: TextPaint) {
        super.updateDrawState(textPaint)
        textPaint.isUnderlineText = false
    }
}

private class ClickableSpanNoUnderline(
    val url: String,
    val clickCallback: ((String) -> Unit)
) : ClickableSpan() {
    override fun updateDrawState(textPaint: TextPaint) {
        super.updateDrawState(textPaint)
        textPaint.isUnderlineText = false
    }

    override fun onClick(widget: View) {
        clickCallback.invoke(url)
    }
}