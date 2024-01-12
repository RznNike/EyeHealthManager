package ru.rznnike.eyehealthmanager.app.dialog

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import ru.rznnike.eyehealthmanager.R
import ru.rznnike.eyehealthmanager.app.dialog.alert.AlertDialogAction
import ru.rznnike.eyehealthmanager.app.dialog.alert.AlertDialogInputType
import ru.rznnike.eyehealthmanager.app.dialog.alert.AlertDialogParameters
import ru.rznnike.eyehealthmanager.app.dialog.bottom.BottomDialogAction
import ru.rznnike.eyehealthmanager.app.dialog.bottom.BottomDialogButtonItem
import ru.rznnike.eyehealthmanager.app.dialog.bottom.BottomDialogParameters
import ru.rznnike.eyehealthmanager.app.ui.view.EmptyDividerDecoration
import ru.rznnike.eyehealthmanager.app.utils.extensions.addSystemWindowInsetToPadding
import ru.rznnike.eyehealthmanager.app.utils.extensions.deviceSize
import ru.rznnike.eyehealthmanager.app.utils.extensions.toHtmlSpanned
import ru.rznnike.eyehealthmanager.domain.utils.millis
import ru.rznnike.eyehealthmanager.domain.utils.toDateTime
import ru.rznnike.eyehealthmanager.domain.utils.toLocalDate
import ru.rznnike.eyehealthmanager.domain.utils.toLocalDateTime
import java.time.Clock

fun Context.showAlertDialog(
    parameters: AlertDialogParameters,
    header: String,
    message: String? = null,
    cancellable: Boolean = true,
    onCancelListener: (() -> Unit)? = null,
    onInputListener: ((String) -> Unit)? = null,
    actions: List<AlertDialogAction>
) : AlertDialog {
    val dialogView = View.inflate(this, parameters.layoutResId, null)

    val dialog = AlertDialog.Builder(this, R.style.AppTheme_Dialog_Alert)
        .setView(dialogView)
        .setCancelable(cancellable)
        .setOnCancelListener {
            onCancelListener?.invoke()
        }
        .create()

    parameters.headerViewId?.let {
        val textViewDialogHeader = dialogView.findViewById<TextView>(it)
        textViewDialogHeader?.text = header
    }
    parameters.messageViewId?.let {
        val textViewDialogMessage = dialogView.findViewById<TextView>(it)
        textViewDialogMessage?.apply {
            if (message.isNullOrBlank()) {
                visibility = View.GONE
            } else {
                visibility = View.VISIBLE
                text = message.toHtmlSpanned()
            }
        }
    }
    for (i in (parameters.buttonViewIds.indices)) {
        actions.getOrNull(i)?.let { action ->
            val button = dialogView.findViewById<TextView>(parameters.buttonViewIds[i])
            button.text = action.text
            button.setOnClickListener {
                action.callback(dialog)
            }
        }
    }
    var editTextDialogInput: EditText? = null
    parameters.inputViewId?.let {
        editTextDialogInput = dialogView.findViewById<EditText>(it)?.apply {
            inputType = parameters.inputType.value
            gravity = when (parameters.inputType) {
                AlertDialogInputType.STRING_MULTILINE -> Gravity.START
                else -> Gravity.CENTER
            }
            addTextChangedListener { text ->
                onInputListener?.invoke(text.toString())
            }
        }
    }

    when (parameters.inputType) {
        AlertDialogInputType.NONE -> dialog.show()
        else -> {
            dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            dialog.show()
            editTextDialogInput?.requestFocus()
        }
    }
    return dialog
}

fun Fragment.showAlertDialog(
    parameters: AlertDialogParameters,
    header: String,
    message: String? = null,
    cancellable: Boolean = true,
    onCancelListener: (() -> Unit)? = null,
    onInputListener: ((String) -> Unit)? = null,
    actions: List<AlertDialogAction>
) = requireContext().showAlertDialog(
    parameters = parameters,
    header = header,
    message = message,
    cancellable = cancellable,
    onCancelListener = onCancelListener,
    onInputListener = onInputListener,
    actions = actions
)

fun Fragment.showBottomDialog(
    parameters: BottomDialogParameters = BottomDialogParameters(),
    header: String? = null,
    cancellable: Boolean = true,
    onCancelListener: (() -> Unit)? = null,
    actions: List<BottomDialogAction>
) : BottomSheetDialog {
    val dialog = BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialog)
    val dialogView = View.inflate(requireContext(), parameters.layoutResId, null)
    dialog.setContentView(dialogView)
    dialogView.setOnClickListener { dialog.cancel() }
    dialog.setCancelable(cancellable)
    dialog.setOnCancelListener {
        onCancelListener?.invoke()
    }

    val recyclerView: RecyclerView = dialogView.findViewById(parameters.recyclerViewId)
    val itemAdapter: ItemAdapter<IItem<*>> = ItemAdapter()
    val adapterActions: FastAdapter<IItem<*>> = FastAdapter.with(itemAdapter)
    adapterActions.setHasStableIds(true)
    adapterActions.onClickListener = { _, _, item, _ ->
        when (item) {
            is BottomDialogButtonItem -> {
                item.bottomDialogAction.callback.invoke(dialog)
                true
            }
            else -> false
        }
    }
    recyclerView.apply {
        layoutManager = LinearLayoutManager(context)
        adapter = adapterActions
        itemAnimator = null
        if (parameters.paddingBetweenItemsResId > 0) {
            addItemDecoration(
                EmptyDividerDecoration(
                    requireContext(),
                    parameters.paddingBetweenItemsResId,
                    false
                )
            )
        }
    }
    itemAdapter.setNewList(
        actions.map {
            BottomDialogButtonItem(
                buttonLayoutResId = parameters.buttonLayoutResId,
                bottomDialogAction = it
            )
        }
    )

    parameters.headerViewId?.let {
        val textViewDialogHeader = dialogView.findViewById<TextView>(it)
        textViewDialogHeader?.apply {
            if (header.isNullOrBlank()) {
                visibility = View.GONE
            } else {
                visibility = View.VISIBLE
                text = header
            }
        }
    }

    BottomSheetBehavior.from(dialogView.parent as View).apply {
        state = BottomSheetBehavior.STATE_EXPANDED
        skipCollapsed = true
        addBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(view: View, state: Int) {
                    if (state == BottomSheetBehavior.STATE_HIDDEN) {
                        dialog.cancel()
                    }
                }

                override fun onSlide(view: View, v: Float) = Unit
            }
        )
    }
    dialog.window?.setLightNavigationBar()
    dialogView.findViewById<View>(parameters.recyclerViewId)
        ?.addSystemWindowInsetToPadding(bottom = true)

    (dialogView as? ConstraintLayout)?.let { rootLayout ->
        ConstraintSet().apply {
            clone(rootLayout)
            val maxHeight = deviceSize.height() / 2
            constrainMaxHeight(parameters.contentViewId, maxHeight)
            applyTo(rootLayout)
        }
    }

    dialog.show()
    return dialog
}

fun Context.showDatePicker(
    preselectedDate: Long? = null,
    maxDate: Long? = null,
    minDate: Long? = null,
    enableTimePicker: Boolean = false,
    onCancel: (() -> Unit)? = null,
    onSuccess: (date: Long) -> Unit
) {
    val currentDate = (preselectedDate ?: Clock.systemUTC().millis()).toLocalDate()
    DatePickerDialog(
        this,
        android.R.style.ThemeOverlay_Material_Dialog,
        { _, year, month, dayOfMonth ->
            if (enableTimePicker) {
                showTimePicker(
                    preselectedTime = preselectedDate,
                    onCancel = onCancel,
                    onSuccess = { timestamp ->
                        val selectedTime = timestamp.toDateTime()
                            .withYear(year)
                            .withMonth(month + 1)
                            .withDayOfMonth(dayOfMonth)
                            .millis()
                        onSuccess(selectedTime)
                    }
                )
            } else {
                val selectedTime = Clock.systemUTC().millis().toDateTime().toLocalDate()
                    .withYear(year)
                    .withMonth(month + 1)
                    .withDayOfMonth(dayOfMonth)
                    .atStartOfDay()
                    .millis()
                onSuccess(selectedTime)
            }
        },
        currentDate.year,
        currentDate.monthValue - 1,
        currentDate.dayOfMonth
    ).apply {
        maxDate?.let { datePicker.maxDate = it }
        minDate?.let { datePicker.minDate = it }
        setOnCancelListener { onCancel?.invoke() }
        show()
    }
}

fun Context.showTimePicker(
    preselectedTime: Long? = null,
    onCancel: (() -> Unit)? = null,
    onSuccess: (timestamp: Long) -> Unit
) {
    val currentDate = (preselectedTime ?: Clock.systemUTC().millis()).toLocalDateTime()
    TimePickerDialog(
        this,
        android.R.style.ThemeOverlay_Material_Dialog,
        { _, hourOfDay, minute ->
            val selectedTime =  Clock.systemUTC().millis().toDateTime().toLocalDateTime()
                .withHour(hourOfDay)
                .withMinute(minute)
                .withSecond(0)
                .withNano(0)
                .millis()
            onSuccess(selectedTime)
        },
        currentDate.hour,
        currentDate.minute,
        true
    ).apply {
        setOnCancelListener { onCancel?.invoke() }
        show()
    }
}

fun Fragment.showDatePicker(
    preselectedDate: Long? = null,
    maxDate: Long? = null,
    minDate: Long? = null,
    enableTimePicker: Boolean = false,
    onCancel: (() -> Unit)? = null,
    onSuccess: (date: Long) -> Unit
) = requireContext().showDatePicker(
    preselectedDate = preselectedDate,
    maxDate = maxDate,
    minDate = minDate,
    enableTimePicker = enableTimePicker,
    onCancel = onCancel,
    onSuccess = onSuccess
)

fun Fragment.showTimePicker(
    preselectedTime: Long? = null,
    onCancel: (() -> Unit)? = null,
    onSuccess: (date: Long) -> Unit
) = requireContext().showTimePicker(
    preselectedTime = preselectedTime,
    onCancel = onCancel,
    onSuccess = onSuccess
)

fun Fragment.showCustomBottomDialog(
    rootView: View,
    constraintView: ConstraintLayout,
    constraintChildView: View,
    heightConstraint: Double = 1.0,
    allowDrag: Boolean = true,
    setupCallback: (dialog: BottomSheetDialog) -> Unit
) {
    val dialog = BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialog)
    dialog.setContentView(rootView)
    rootView.setOnClickListener { dialog.cancel() }
    dialog.setCancelable(true)

    setupCallback(dialog)

    BottomSheetBehavior.from(rootView.parent as View).apply {
        state = BottomSheetBehavior.STATE_EXPANDED
        skipCollapsed = true
        isDraggable = allowDrag
        addBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(view: View, state: Int) {
                    if (state == BottomSheetBehavior.STATE_HIDDEN) {
                        dialog.cancel()
                    }
                }

                override fun onSlide(view: View, v: Float) = Unit
            }
        )
    }
    dialog.window?.setLightNavigationBar()
    ConstraintSet().apply {
        clone(constraintView)
        val maxHeight = (deviceSize.height() * heightConstraint).toInt()
        constrainMaxHeight(constraintChildView.id, maxHeight)
        applyTo(constraintView)
    }

    dialog.show()
}

private fun Window.setLightNavigationBar() = when {
    Build.VERSION.SDK_INT >= 30 -> {
        insetsController?.setSystemBarsAppearance(
            WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
            WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
        )
    }
    Build.VERSION.SDK_INT >= 26 -> {
        @Suppress("DEPRECATION")
        decorView.systemUiVisibility = decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
    }
    else -> Unit
}