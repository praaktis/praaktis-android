package com.mobile.praaktishockey.domain.extension

import android.app.Activity
import android.view.View

fun Activity.transparentStatusAndNavigationBar() {
    var flags = window.decorView.systemUiVisibility
    flags = flags or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    window.decorView.systemUiVisibility = flags
}
