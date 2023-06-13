package com.neko.hiepdph.calculatorvault.common.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.neko.hiepdph.calculatorvault.R
import kotlin.math.roundToInt


class CircleProgressBar @JvmOverloads constructor(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {
    /**
     * ProgressBar's line thickness
     */
    private var strokeWidth = 4f
    private var progress = 0f
    private var min = 0
    private var max = 100

    /**
     * Start the progress at 12 o'clock
     */
    private val startAngle = -90
    private var color = Color.DKGRAY
    private var rectF: RectF? = null
    private var backgroundPaint: Paint? = null
    private var foregroundPaint: Paint? = null

    init {
        val typedArray = context?.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar)
        init(context!!, attrs!!)
        typedArray?.recycle()
    }

    private fun init(context: Context, attrs: AttributeSet) {
        rectF = RectF()
        val typedArray = context.theme.obtainStyledAttributes(
            attrs, R.styleable.CircleProgressBar, 0, 0
        )
        //Reading values from the XML layout
        try {
            strokeWidth = typedArray.getDimension(
                R.styleable.CircleProgressBar_progressBarThickness, strokeWidth
            )
            progress = typedArray.getFloat(R.styleable.CircleProgressBar_progress, progress)
            color = typedArray.getInt(R.styleable.CircleProgressBar_progressbarColor, color)
            min = typedArray.getInt(R.styleable.CircleProgressBar_min, min)
            max = typedArray.getInt(R.styleable.CircleProgressBar_max, max)
        } finally {
            typedArray.recycle()
        }
        backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        backgroundPaint!!.color = adjustAlpha(color, 0.3f)
        backgroundPaint!!.style = Paint.Style.STROKE
        backgroundPaint!!.strokeWidth = strokeWidth
        foregroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        foregroundPaint!!.color = color
        foregroundPaint!!.style = Paint.Style.STROKE
        foregroundPaint!!.strokeWidth = strokeWidth
    }

    private fun adjustAlpha(color: Int, factor: Float): Int {
        val alpha = (Color.alpha(color) * factor).roundToInt()
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Color.argb(alpha, red, green, blue)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        val width = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        val min = width.coerceAtMost(height)
        setMeasuredDimension(min, min)
        rectF!![0 + strokeWidth / 2, 0 + strokeWidth / 2, min - strokeWidth / 2] =
            min - strokeWidth / 2
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawOval(rectF!!, backgroundPaint!!)
        val angle = 360 * progress / max
        canvas.drawArc(rectF!!, startAngle.toFloat(), angle, false, foregroundPaint!!)
    }

    fun setProgress(progress: Float) {
        this.progress = progress
        invalidate() // Notify the view to redraw it self (the onDraw method is called)
    }

}