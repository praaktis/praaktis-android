package com.mobile.praaktishockey.domain.extension

import android.widget.ImageView
import com.mobile.praaktishockey.domain.common.GlideApp

fun ImageView.loadUrl(url: String?) {
    GlideApp.with(context)
        .load(url)
        .into(this)
}