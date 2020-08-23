package com.mobile.gympraaktis.domain.common

import android.view.View

class FadeTransformation : androidx.viewpager.widget.ViewPager.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.translationX = -position * page.width
        page.alpha = 1 - Math.abs(position)
    }
}