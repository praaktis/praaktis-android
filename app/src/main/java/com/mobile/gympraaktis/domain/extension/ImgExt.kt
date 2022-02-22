package com.mobile.gympraaktis.domain.extension

import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.domain.common.GlideApp
import com.nguyenhoanglam.imagepicker.model.GridCount
import com.nguyenhoanglam.imagepicker.model.ImagePickerConfig
import com.nguyenhoanglam.imagepicker.model.RootDirectory

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

val IMAGE_PICKER_CONFIG = ImagePickerConfig(
    statusBarColor = "#000000",
    toolbarColor = "#000000",
    toolbarIconColor = "#FFFFFF",
    toolbarTextColor = "#FFFFFF",
    isLightStatusBar = false,
    isFolderMode = true,
    isMultipleMode = false,
    rootDirectory = RootDirectory.DCIM,
    subDirectory = "Photos",
    folderGridCount = GridCount(2, 4),
    imageGridCount = GridCount(3, 5),
    // See more at configuration attributes table below
)