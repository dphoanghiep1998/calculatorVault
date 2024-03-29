package com.neko.hiepdph.calculatorvault.common.customview

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import com.github.chrisbanes.photoview.PhotoView
import com.neko.hiepdph.calculatorvault.R

interface RotateInterface {
    fun onRoteClick()
}

class CustomPhotoView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : PhotoView(context, attrs, defStyleAttr) {
    private var angle = 0
    private var mListener: RotateInterface? = null

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomPhotoView)
        typedArray.recycle()
    }

    fun setupRotate(listener: RotateInterface) {
        mListener = listener
    }


     fun toggleRotate() {
        angle++
        Log.d("TAG", "toggleRotate: " + angle)
        if (angle > 3) {
            angle = 0
        }
        setRotationTo(90f * angle)
        invalidate()
    }
}