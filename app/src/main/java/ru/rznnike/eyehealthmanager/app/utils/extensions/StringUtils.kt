package ru.rznnike.eyehealthmanager.app.utils.extensions

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.text.HtmlCompat
import androidx.core.text.inSpans
import java.util.*

fun String.toHtmlSpanned(): Spanned {
    return HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY)
}

fun getHexFromColor(color: Int) =
    "#%06X".format(0xFFFFFF and color)

fun String.capitalize() = replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(
        Locale.getDefault()
    ) else it.toString()
}

fun createStringWithClickableSpans(
    template: String,
    clickableTexts: List<Pair<String, () -> Unit>>
): Spanned {
    fun getClickableSpan(onClickListener: () -> Unit) = object : ClickableSpan() {
        override fun onClick(textView: View) = onClickListener()

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false
        }
    }

    val stringBuilder = SpannableStringBuilder()
    template.split("%s").forEachIndexed { index, part ->
        if (index > 0) {
            val spanIndex = index - 1
            clickableTexts.getOrNull(spanIndex)?.let { spanData ->
                stringBuilder.inSpans(getClickableSpan(spanData.second)) {
                    append(spanData.first.toHtmlSpanned())
                }
            }
        }
        "^ +".toRegex().find(part)?.let { match ->
            stringBuilder.append(match.value)
        }
        stringBuilder.append(
            part.replace("\n".toRegex(), "<br>")
                .toHtmlSpanned()
        )
    }

    return stringBuilder
}

fun createStringWithHighlights(
    text: String,
    partsToHighlight: List<String>,
    highlightColor: Int
): Spanned {
    val highlightColorHex = getHexFromColor(highlightColor)

    var resultText = text
    partsToHighlight.forEach { part ->
        resultText = resultText.replaceFirst(
            part,
            "<font color=\"%s\"><b>%s</b></font>".format(
                highlightColorHex,
                part
            )
        )
    }

    return resultText.replace("\n".toRegex(), "<br>").toHtmlSpanned()
}