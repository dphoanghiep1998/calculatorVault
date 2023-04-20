package com.neko.hiepdph.calculatorvault.common.transformer

import android.view.View
import androidx.viewpager.widget.ViewPager
import kotlin.math.abs

class ZoomTransformer : ViewPager.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        val scaleFactor = 0.85f.coerceAtLeast(1 - abs(position))
        page.scaleX = scaleFactor
        page.scaleY = scaleFactor
        page.alpha = 1 - abs(position)
    }
}