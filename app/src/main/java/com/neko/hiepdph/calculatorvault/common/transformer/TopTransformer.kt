package com.neko.hiepdph.calculatorvault.common.transformer

import android.view.View
import androidx.viewpager.widget.ViewPager
import kotlin.math.abs

class TopTransformer : ViewPager.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.translationY = -page.height * position
        page.alpha = 1 - abs(position)
    }
}