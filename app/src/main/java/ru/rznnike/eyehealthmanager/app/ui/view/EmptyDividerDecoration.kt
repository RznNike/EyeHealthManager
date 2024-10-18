package ru.rznnike.eyehealthmanager.app.ui.view

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.flexbox.FlexboxLayoutManager

private const val HORIZONTAL = 0
private const val VERTICAL = 1
private const val GRID = 2

class EmptyDividerDecoration(
    context: Context,
    @DimenRes cardInsets: Int,
    private val applyOutsideDecoration: Boolean = true
) : RecyclerView.ItemDecoration() {
    private val spacing: Int = context.resources.getDimensionPixelOffset(cardInsets)
    private var displayMode: Int = -1

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildViewHolder(view).absoluteAdapterPosition
        val itemCount = state.itemCount
        val layoutManager = parent.layoutManager
        setSpacingForDirection(outRect, layoutManager, position, itemCount)
    }

    private fun setSpacingForDirection(
        outRect: Rect,
        layoutManager: RecyclerView.LayoutManager?,
        position: Int,
        itemCount: Int
    ) {
        if (displayMode == -1) {
            displayMode = resolveDisplayMode(layoutManager)
        }
        val reverseLayout = (layoutManager as? LinearLayoutManager)?.reverseLayout == true

        when (displayMode) {
            HORIZONTAL -> {
                val left = if (applyOutsideDecoration || (position > 0)) spacing else 0
                val right = if (applyOutsideDecoration && (position == itemCount - 1)) spacing else 0

                outRect.left = if (reverseLayout) right else left
                outRect.right = if (reverseLayout) left else right
                outRect.top = 0
                outRect.bottom = 0
            }
            VERTICAL -> {
                val top = if (applyOutsideDecoration || (position > 0)) spacing else 0
                val bottom = if (applyOutsideDecoration && (position == itemCount - 1)) spacing else 0

                outRect.left = 0
                outRect.right = 0
                outRect.top = if (reverseLayout) bottom else top
                outRect.bottom = if (reverseLayout) top else bottom
            }
            GRID -> {
                outRect.left = spacing
                outRect.right = spacing
                outRect.top = spacing
                outRect.bottom = spacing
            }
        }
    }

    private fun resolveDisplayMode(layoutManager: RecyclerView.LayoutManager?): Int {
        return when (layoutManager) {
            is StaggeredGridLayoutManager -> GRID
            is GridLayoutManager -> GRID
            is FlexboxLayoutManager -> GRID
            else -> if (layoutManager!!.canScrollHorizontally()) HORIZONTAL else VERTICAL
        }
    }
}