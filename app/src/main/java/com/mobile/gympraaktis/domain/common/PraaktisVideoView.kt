package com.mobile.gympraaktis.domain.common

import android.content.Context
import android.util.AttributeSet
import android.widget.VideoView

class PraaktisVideoView (context: Context, attr: AttributeSet): VideoView(context, attr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, /*context.dpToPx(300)*/heightMeasureSpec)
    }

}