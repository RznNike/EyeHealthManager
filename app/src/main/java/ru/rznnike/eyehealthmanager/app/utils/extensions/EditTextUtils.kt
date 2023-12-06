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
