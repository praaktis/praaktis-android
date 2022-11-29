package com.mobile.gympraaktis.domain.extension

import android.widget.ImageView
import com.mobile.gympraaktis.domain.common.GlideApp

fun ImageView.loadUrl(url: String?) {
    GlideApp.with(context)
        .load(url)
        .into(this)
}