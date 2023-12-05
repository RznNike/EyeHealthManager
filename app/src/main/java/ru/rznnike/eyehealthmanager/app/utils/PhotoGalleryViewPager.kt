package ru.rznnike.eyehealthmanager.app.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.alexvasilkov.gestures.views.GestureImageView
import kotlin.math.abs

class PhotoGalleryViewPager : ViewPager {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun canScroll(v: View, checkV: Boolean, dx: Int, x: Int, y: Int): Boolean {
        if (v is GestureImageView) {
            val minZoom = v.controller.stateController.getMinZoom(v.controller.state)
            val zoom = v.controller.state.zoom

            // if zoom factor == min zoom factor => just let the view pager handle the scroll
            if (abs(minZoom - zoom) < EPSILON)
                return false

            val effectiveMovementArea = RectF()
            v.controller.stateController.getMovementArea(v.controller.state, effectiveMovementArea)
            val stateX = v.controller.state.x
            val width = effectiveMovementArea.width()

            // if user reached left edge && is swiping left => just let the view pager handle the scroll
            if (abs(stateX) < EPSILON && dx > 0)
                return false

            // if user reached right edge && is swiping right => just let the view pager handle the scroll
            return !(abs(stateX + width) < EPSILON && dx < 0)

        } else {
            return super.canScroll(v, checkV, dx, x, y)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        // only handle one finger touches! otherwise, the user probably does want to scale/pan
        return if (event.pointerCount != 1) false else super.onTouchEvent(event)
    }

    companion object {
        private const val EPSILON = 0.001f
    }
}