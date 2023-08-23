package com.neko.hiepdph.calculatorvault.common.transformer

import android.view.View
import androidx.viewpager.widget.ViewPager
import kotlin.math.abs

class RotationTransformer : ViewPager.PageTransformer {
    companion object {
        private const val ROTATION_MAX = 60.0f
    }

    override fun transformPage(page: View, position: Float) {
        when {
            position < -1 -> { // [-Infinity,-1)
                page.rotation = -ROTATION_MAX
                page.alpha = 0.0f
            }
            position <= 1 -> { // [-1,1]
                val rotation = ROTATION_MAX * position
                page.rotation = rotation
                page.alpha = 1 - abs(position)
                page.pivotX = page.width * 0.5f
                page.pivotY = page.height.toFloat()
            }
            else -> { // (1,+Infinity]
                page.rotation = ROTATION_MAX
                page.alpha = 0.0f
            }
        }
    }
}