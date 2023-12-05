package ru.rznnike.eyehealthmanager.app.dialog.bottom

import com.google.android.material.bottomsheet.BottomSheetDialog

data class BottomDialogAction(
    val text: String,
    val selected: Boolean = false,
    val callback: (dialog: BottomSheetDialog) -> Unit
)
