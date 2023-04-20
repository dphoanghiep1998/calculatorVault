package com.neko.hiepdph.calculatorvault.common.transformer

import android.view.View
import androidx.viewpager.widget.ViewPager
import kotlin.math.abs

class NoTransformer : ViewPager.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        if (position < 0) {
            page.scrollX = ((page.width) * position).toInt()
        } else if (position > 0) {
            page.scrollX = (-(page.width * -position)).toInt()
        } else {
            page.scrollX = 0;
        }
    }
}