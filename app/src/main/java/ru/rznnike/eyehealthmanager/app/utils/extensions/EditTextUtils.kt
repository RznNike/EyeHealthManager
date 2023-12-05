package ru.rznnike.eyehealthmanager.app.utils.extensions

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.style.UnderlineSpan
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import java.lang.reflect.Field

fun EditText.syncWithValue(value: String?) {
    val notNullValue = value.orEmpty()
    if (notNullValue != text.toString()) {
        setText(notNullValue)
    }
}

fun EditText.syncWithValue(value: Double?) = value?.let {
    val editTextValue = text.toString().toDoubleOrNull() ?: 0.0
    if (editTextValue != it) {
        setText(value.toString())
    }
} ?: run {
    if (!text.isNullOrBlank()) {
        text = null
    }
}

fun EditText.syncWithValue(value: Int?) = value?.let {
    val editTextValue = text.toString().toIntOrNull() ?: 0
    if (editTextValue != it) {
        setText(value.toString())
    }
} ?: run {
    if (!text.isNullOrBlank()) {
        text = null
    }
}

fun EditText?.removeFocusAndSpan() {
    this?.apply {
        clearFocus()
        postDelayed(
            {
                for (span in text.getSpans(0, text.length, UnderlineSpan::class.java)) {
                    text.removeSpan(span)
                }
            },
            100
        )
    }
}

@SuppressLint("PrivateApi", "DiscouragedPrivateApi")
fun EditText?.setAccentColor(accentColor: Int, selectionColor: Int? = null) {
    this?.apply {
        highlightColor = selectionColor ?: accentColor

        fun Drawable.copy() = constantState?.newDrawable()?.mutate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            textSelectHandle?.copy()?.let {
                it.setTint(accentColor)
                setTextSelectHandle(it)
            }
            textSelectHandleLeft?.copy()?.let {
                it.setTint(accentColor)
                setTextSelectHandleLeft(it)
            }
            textSelectHandleRight?.copy()?.let {
                it.setTint(accentColor)
                setTextSelectHandleRight(it)
            }
            textCursorDrawable?.copy()?.let {
                it.setTint(accentColor)
                textCursorDrawable = it
            }

        } else try {
            val fEditor: Field = TextView::class.java.getDeclaredField("mEditor")
            fEditor.isAccessible = true
            val editor = fEditor.get(this)
            val fSelectHandleLeft: Field =
                editor.javaClass.getDeclaredField("mSelectHandleLeft")
            val fSelectHandleLeftRes: Field =
                TextView::class.java.getDeclaredField("mTextSelectHandleLeftRes")
            val fSelectHandleRight: Field =
                editor.javaClass.getDeclaredField("mSelectHandleRight")
            val fSelectHandleRightRes: Field =
                TextView::class.java.getDeclaredField("mTextSelectHandleRightRes")
            val fSelectHandleCenter: Field =
                editor.javaClass.getDeclaredField("mSelectHandleCenter")
            val fSelectHandleCenterRes: Field =
                TextView::class.java.getDeclaredField("mTextSelectHandleRes")
            val fCursorDrawableRes: Field =
                TextView::class.java.getDeclaredField("mCursorDrawableRes")

            fun tintFieldDrawableRes(
                fieldFrom: Field,
                objectFrom: Any?,
                fieldTo: Field,
                objectTo: Any?,
                color: Int
            ) {
                fieldFrom.isAccessible = true
                fieldTo.isAccessible = true
                (fieldFrom.get(objectFrom) as? Int?)?.let {
                    val drawable = ContextCompat.getDrawable(context, it)?.copy()
                    drawable?.setTint(color)
                    fieldTo.set(objectTo, drawable)
                }
            }

            tintFieldDrawableRes(fSelectHandleCenterRes, this, fSelectHandleCenter, editor, accentColor)
            tintFieldDrawableRes(fSelectHandleLeftRes, this, fSelectHandleLeft, editor, accentColor)
            tintFieldDrawableRes(fSelectHandleRightRes, this, fSelectHandleRight, editor, accentColor)
            tintFieldDrawableRes(fCursorDrawableRes, this, fCursorDrawableRes, this, accentColor)
        } catch (ignored: Exception) { }
    }
}

fun EditText.setOnKeyboardActionListener(keyId: Int, action: () -> Unit) {
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == keyId) {
            action.invoke()
            true
        } else {
            false
        }
    }
}
