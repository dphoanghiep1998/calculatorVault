package com.neko.hiepdph.calculatorvault.common.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.withStyledAttributes
import com.neko.hiepdph.calculatorvault.R

class RoundedImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    val path = Path()
    private var cornerRadiusTL: Float = 0f
    private var cornerRadiusTR: Float = 0f
    private var cornerRadiusBL: Float = 0f
    private var cornerRadiusBR: Float = 0f

    init {
        context.withStyledAttributes(attrs, R.styleable.RoundedImageView) {
            cornerRadiusTL = getDimension(R.styleable.RoundedImageView_cornerRadiusTopLeft, 0F)
            cornerRadiusBL = getDimension(R.styleable.RoundedImageView_cornerRadiusBotLeft, 0F)
            cornerRadiusTR = getDimension(R.styleable.RoundedImageView_cornerRadiusTopRight, 0F)
            cornerRadiusBR = getDimension(R.styleable.RoundedImageView_cornerRadiusBotRight, 0F)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        val corners = floatArrayOf(
            cornerRadiusTL, cornerRadiusTL, cornerRadiusTR, cornerRadiusTR, cornerRadiusBR, cornerRadiusBR, cornerRadiusBL, cornerRadiusBL
        )

        path.addRoundRect(
            0f, 0f, width.toFloat(), height.toFloat(), corners, Path.Direction.CW
        )

        canvas?.clipPath(path)
        super.onDraw(canvas)
    }
}