package com.neko.hiepdph.calculatorvault.common.customview

import android.content.Context
import android.util.AttributeSet
import android.widget.ProgressBar
import com.neko.hiepdph.calculatorvault.R

class ChromeProgressBar @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ProgressBar(context, attrs, defStyleAttr) {
    private var listener: OnProgressChange? = null

    interface OnProgressChange {
        fun onChange(progress: Int)
    }

    init {

        val typedArray = context?.obtainStyledAttributes(attrs, R.styleable.ChromeProgressBar)

        typedArray?.recycle()
    }

    fun setOnProgressBarChange(listener: OnProgressChange) {
        this.listener = listener
    }

    override fun setProgress(progress: Int) {
        super.setProgress(progress)
        listener?.onChange(progress)
    }
}