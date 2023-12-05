package ru.rznnike.eyehealthmanager.app.ui.view

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.annotation.Keep
import androidx.core.animation.doOnCancel
import androidx.core.animation.doOnEnd
import ru.rznnike.eyehealthmanager.R
import kotlin.math.atan2
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

class CircleProgressBar : View {
    var maxProgress = 100f
    var isClockWise: Boolean = false
        set(value) {
            field = value
            if (value) {
                if (sweepAngle > 0) {
                    sweepAngle = -sweepAngle
                }
            }
            invalidate()
        }
    var isTouchEnabled = false
        set(value) {
            field = value
            invalidate()
        }
    var isRoundedCorner: Boolean = false
        set(value) {
            field = value
            if (value) {
                innerCircle.strokeCap = Paint.Cap.ROUND
                outerCircle.strokeCap = Paint.Cap.ROUND
            } else {
                innerCircle.strokeCap = Paint.Cap.BUTT
                outerCircle.strokeCap = Paint.Cap.BUTT
            }
            invalidate()
        }
    var backgroundProgressWidth: Int = 0
        set(value) {
            field = value
            outerCircle.strokeWidth = backgroundProgressWidth.toFloat()
            requestLayout()
            invalidate()
        }
    var foregroundProgressWidth: Int = 0
        set(value) {
            field = value
            innerCircle.strokeWidth = foregroundProgressWidth.toFloat()
            requestLayout()
            invalidate()
        }
    var backgroundProgressColor: Int = 0
        set(value) {
            field = value
            outerCircle.color = value
            requestLayout()
            invalidate()
        }
    var foregroundProgressColor: Int = 0
        set(value) {
            field = value
            innerCircle.color = value
            requestLayout()
            invalidate()
        }

    var progress: Float = 0f
        @Keep
        set(value) {
            field = if (value <= maxProgress) value else maxProgress
            updateProgress(false)
        }
    var onProgressbarChangeListener: OnProgressbarChangeListener? = null

    private val innerCircle = Paint()
    private val outerCircle = Paint()
    private val rectF = RectF()
    private var moveCorrect: Boolean = false
    private val startAngle = -90
    private var sweepAngle = 0f
    private var centerPoint: Int = 0
    private var subtractingValue: Int = 0
    private var drawRadius: Int = 0
    private var drawOuterRadius: Int = 0
    private var objectAnimator: ObjectAnimator? = null
    private var viewWidth: Int = 0
    private var viewHeight: Int = 0

    constructor(context: Context) : super(context) {
        init()
    }

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet, i: Int = 0) : super(context, attrs, i) {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar, 0, 0)
        backgroundProgressWidth =
            attributes.getInteger(
                R.styleable.CircleProgressBar_cpb_backgroundProgressWidth,
                DEFAULT_BACKGROUND_CIRCLE_WIDTH
            )
        foregroundProgressWidth =
            attributes.getInteger(
                R.styleable.CircleProgressBar_cpb_foregroundProgressWidth,
                DEFAULT_FOREGROUND_PROGRESS_WIDTH
            )
        backgroundProgressColor =
            attributes.getColor(
                R.styleable.CircleProgressBar_cpb_backgroundProgressColor,
                DEFAULT_BACKGROUND_PROGRESS_COLOR
            )
        foregroundProgressColor =
            attributes.getColor(
                R.styleable.CircleProgressBar_cpb_foregroundProgressColor,
                DEFAULT_FOREGROUND_PROGRESS_COLOR
            )
        this.progress = attributes.getFloat(R.styleable.CircleProgressBar_cpb_progress, progress)
        this.isRoundedCorner =
            attributes.getBoolean(R.styleable.CircleProgressBar_cpb_roundedCorner, false)
        this.isClockWise = attributes.getBoolean(R.styleable.CircleProgressBar_cpb_clockwise, false)
        this.isTouchEnabled =
            attributes.getBoolean(R.styleable.CircleProgressBar_cpb_touchEnabled, false)

        attributes.recycle()
        init()
    }

    private fun init() {
        innerCircle.strokeWidth = foregroundProgressWidth.toFloat()
        innerCircle.isAntiAlias = true
        innerCircle.style = Paint.Style.STROKE
        innerCircle.color = foregroundProgressColor

        outerCircle.strokeWidth = backgroundProgressWidth.toFloat()
        outerCircle.isAntiAlias = true
        outerCircle.color = backgroundProgressColor
        outerCircle.style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(
            centerPoint.toFloat(),
            centerPoint.toFloat(),
            drawRadius.toFloat(),
            outerCircle
        )
        canvas.drawArc(rectF, startAngle.toFloat(), sweepAngle, false, innerCircle)
        super.onDraw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        viewWidth = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        viewHeight = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        centerPoint = min(viewWidth, viewHeight)
        val min = min(viewWidth, viewHeight)
        setMeasuredDimension(min, min)
        setRadiusRect()
    }

    private fun setRadiusRect() {
        centerPoint = min(viewWidth, viewHeight) / 2
        subtractingValue =
            if (backgroundProgressWidth > foregroundProgressWidth) backgroundProgressWidth else foregroundProgressWidth
        val newSeekWidth = subtractingValue / 2
        drawRadius = min((viewWidth - subtractingValue) / 2, (viewHeight - subtractingValue) / 2)
        drawOuterRadius = min(viewWidth - newSeekWidth, viewHeight - newSeekWidth)
        rectF.set(
            (subtractingValue / 2).toFloat(),
            (subtractingValue / 2).toFloat(),
            drawOuterRadius.toFloat(),
            drawOuterRadius.toFloat()
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isTouchEnabled) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    onProgressbarChangeListener?.onStartTracking(this)
                    checkForCorrect(event.x, event.y)
                }
                MotionEvent.ACTION_MOVE -> {
                    if (moveCorrect)
                        justMove(event.x, event.y)
                    updateProgress(true)
                }
                MotionEvent.ACTION_UP -> {
                    onProgressbarChangeListener?.onStopTracking(this)
                    moveCorrect = false
                }
            }
            return true
        }
        return false
    }

    private fun updateProgress(fromUser: Boolean) {
        sweepAngle = 360 * progress / maxProgress

        if (this.isClockWise) {
            if (sweepAngle > 0) {
                sweepAngle = -sweepAngle
            }
        }
        onProgressbarChangeListener?.onProgressChanged(this, progress, fromUser)
        invalidate()
    }

    private fun justMove(x: Float, y: Float) {
        if (isClockWise) {
            var degree =
                Math.toDegrees(atan2((x - centerPoint).toDouble(), (centerPoint - y).toDouble()))
                    .toFloat()
            if (degree > 0) {
                degree -= 360f
            }
            sweepAngle = degree
        } else {
            var degree =
                Math.toDegrees(atan2((x - centerPoint).toDouble(), (centerPoint - y).toDouble()))
                    .toFloat()
            if (degree < 0) {
                degree += 360f
            }

            sweepAngle = degree
        }
        progress = sweepAngle * maxProgress / 360

        invalidate()
    }

    private fun checkForCorrect(x: Float, y: Float) {
        val distance = sqrt(
            (x - centerPoint).toDouble().pow(2.0) + (y - centerPoint).toDouble().pow(2.0)
        ).toFloat()
        if (distance < drawOuterRadius / 2 + subtractingValue && distance > drawOuterRadius / 2 - subtractingValue * 2) {
            moveCorrect = true
            if (isClockWise) {
                var degree = Math.toDegrees(
                    atan2(
                        (x - centerPoint).toDouble(),
                        (centerPoint - y).toDouble()
                    )
                ).toFloat()
                if (degree > 0) {
                    degree -= 360f
                }
                sweepAngle = degree
            } else {
                var degree = Math.toDegrees(
                    atan2(
                        (x - centerPoint).toDouble(),
                        (centerPoint - y).toDouble()
                    )
                ).toFloat()
                if (degree < 0) {
                    degree += 360f
                }
                sweepAngle = degree
            }
            progress = sweepAngle * maxProgress / 360

            invalidate()
        }
    }

    fun setRepeatingProgressWithAnimation(
        progress: Float,
        duration: Int = 0
    ) {
        objectAnimator?.cancel()
        objectAnimator = ObjectAnimator.ofFloat(this, "progress", progress).also { animator ->
            animator.duration = duration.toLong()
            animator.interpolator = DecelerateInterpolator()
            animator.start()
            animator.doOnCancel { animator.removeAllListeners() }
            animator.doOnEnd {
                this.progress = 0f
                setRepeatingProgressWithAnimation(progress, duration)
            }
        }
    }

    fun setProgressWithAnimation(
        progress: Float,
        duration: Int = 0,
        onAnimationEndListener: (() -> Unit)? = null
    ) {
        objectAnimator?.cancel()
        objectAnimator = ObjectAnimator.ofFloat(this, "progress", progress).also { animator ->
            animator.duration = duration.toLong()
            animator.interpolator = DecelerateInterpolator()
            animator.start()
            animator.doOnCancel { animator.removeAllListeners() }
            animator.doOnEnd {
                onAnimationEndListener?.invoke()
            }
        }
    }

    fun reset() {
        progress = 0f
        objectAnimator?.cancel()
    }

    interface OnProgressbarChangeListener {
        fun onProgressChanged(
            circleProgressBar: CircleProgressBar,
            progress: Float,
            fromUser: Boolean
        )

        fun onStartTracking(circleProgressBar: CircleProgressBar)
        fun onStopTracking(circleProgressBar: CircleProgressBar)
    }

    companion object {
        private const val DEFAULT_FOREGROUND_PROGRESS_WIDTH = 10
        private const val DEFAULT_BACKGROUND_CIRCLE_WIDTH = 10
        private const val DEFAULT_BACKGROUND_PROGRESS_COLOR = Color.GRAY
        private const val DEFAULT_FOREGROUND_PROGRESS_COLOR = Color.BLACK
    }
}

