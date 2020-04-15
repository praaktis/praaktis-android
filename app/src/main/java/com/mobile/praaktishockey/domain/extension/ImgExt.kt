package com.mobile.praaktishockey.domain.extension

import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.domain.common.GlideApp

fun ImageView.loadUrl(url: String?) {
    GlideApp.with(context)
        .load(url)
        .into(this)
}

fun ImageView.loadAvatar(url: String?, @DrawableRes placeholder: Int = R.drawable.ic_user_blue) {
    Glide.with(context)
        .load(url)
        .apply(
            RequestOptions()
                .placeholder(placeholder)
                .fitCenter()
        )
        .optionalCenterCrop()
        .into(this)
}