package ru.rznnike.eyehealthmanager.app.dialog.alert

import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import ru.rznnike.eyehealthmanager.R

data class AlertDialogParameters(
    @LayoutRes val layoutResId: Int,
    @IdRes val headerViewId: Int? = null,
    @IdRes val messageViewId: Int? = null,
    val buttonViewIds: List<Int> = emptyList(),
    val inputType: AlertDialogInputType = AlertDialogInputType.NONE,
    @IdRes val inputViewId: Int? = null,
) {
    companion object {
        val HORIZONTAL_2_OPTIONS_LEFT_ACCENT = AlertDialogParameters(
            layoutResId = R.layout.dialog_horizontal_2_options_left_accent,
            headerViewId = R.id.textViewDialogHeader,
            messageViewId = R.id.textViewDialogMessage,
            buttonViewIds = listOf(
                R.id.buttonDialogFirstAction,
                R.id.buttonDialogSecondAction
            )
        )
        val HORIZONTAL_2_OPTIONS_RIGHT_ACCENT = AlertDialogParameters(
            layoutResId = R.layout.dialog_horizontal_2_options_right_accent,
            headerViewId = R.id.textViewDialogHeader,
            messageViewId = R.id.textViewDialogMessage,
            buttonViewIds = listOf(
                R.id.buttonDialogFirstAction,
                R.id.buttonDialogSecondAction
            )
        )
        val VERTICAL_3_OPTIONS_TOP_ACCENT = AlertDialogParameters(
            layoutResId = R.layout.dialog_vertical_3_options_top_accent,
            headerViewId = R.id.textViewDialogHeader,
            messageViewId = R.id.textViewDialogMessage,
            buttonViewIds = listOf(
                R.id.buttonDialogFirstAction,
                R.id.buttonDialogSecondAction,
                R.id.buttonDialogThirdAction
            )
        )
        val VERTICAL_2_OPTIONS_TOP_ACCENT = AlertDialogParameters(
            layoutResId = R.layout.dialog_vertical_2_options_top_accent,
            headerViewId = R.id.textViewDialogHeader,
            messageViewId = R.id.textViewDialogMessage,
            buttonViewIds = listOf(
                R.id.buttonDialogFirstAction,
                R.id.buttonDialogSecondAction
            )
        )
        val VERTICAL_2_OPTIONS = AlertDialogParameters(
            layoutResId = R.layout.dialog_vertical_2_options,
            headerViewId = R.id.textViewDialogHeader,
            messageViewId = R.id.textViewDialogMessage,
            buttonViewIds = listOf(
                R.id.buttonDialogFirstAction,
                R.id.buttonDialogSecondAction
            )
        )
        val VERTICAL_1_OPTION_ACCENT = AlertDialogParameters(
            layoutResId = R.layout.dialog_vertical_1_option_accent,
            headerViewId = R.id.textViewDialogHeader,
            messageViewId = R.id.textViewDialogMessage,
            buttonViewIds = listOf(
                R.id.buttonDialogFirstAction
            )
        )
        val VERTICAL_1_OPTION_NO_ACCENT = AlertDialogParameters(
            layoutResId = R.layout.dialog_vertical_1_option_no_accent,
            headerViewId = R.id.textViewDialogHeader,
            messageViewId = R.id.textViewDialogMessage,
            buttonViewIds = listOf(
                R.id.buttonDialogFirstAction
            )
        )

        private val INPUT_BASE = AlertDialogParameters(
            layoutResId = R.layout.dialog_input_data,
            headerViewId = R.id.textViewDialogHeader,
            messageViewId = R.id.textViewDialogMessage,
            buttonViewIds = listOf(
                R.id.buttonDialogFirstAction
            ),
            inputViewId = R.id.editTextDialogInput
        )
        val INPUT_INT = INPUT_BASE.copy(inputType = AlertDialogInputType.INT)
        val INPUT_FLOAT = INPUT_BASE.copy(inputType = AlertDialogInputType.FLOAT)
        val INPUT_STRING = INPUT_BASE.copy(inputType = AlertDialogInputType.STRING)
        val INPUT_STRING_MULTILINE = INPUT_BASE.copy(inputType = AlertDialogInputType.STRING_MULTILINE)
    }
}
