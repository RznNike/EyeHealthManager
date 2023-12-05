package ru.rznnike.eyehealthmanager.app.dialog.bottom

import androidx.annotation.DimenRes
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import ru.rznnike.eyehealthmanager.R

data class BottomDialogParameters(
    @LayoutRes val layoutResId: Int = R.layout.bottom_dialog,
    @LayoutRes val buttonLayoutResId: Int = R.layout.item_bottom_dialog_button,
    @IdRes val contentViewId: Int = R.id.layoutDialogContent,
    @IdRes val recyclerViewId: Int = R.id.recyclerViewDialog,
    @IdRes val headerViewId: Int? = R.id.textViewDialogHeader,
    @DimenRes val paddingBetweenItemsResId: Int = 0
)
