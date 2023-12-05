package ru.rznnike.eyehealthmanager.app.dialog.alert

import android.text.InputType

enum class AlertDialogInputType(
    val value: Int
) {
    NONE(InputType.TYPE_NULL),
    INT(InputType.TYPE_CLASS_NUMBER),
    FLOAT(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL),
    STRING(InputType.TYPE_CLASS_TEXT),
    STRING_MULTILINE(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE)
}