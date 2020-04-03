package com.mobile.praaktishockey.domain.extension

import android.app.Activity
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi

fun Activity.transparentStatusAndNavigationBar() {
    var flags = window.decorView.systemUiVisibility
    flags = flags or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    window.decorView.systemUiVisibility = flags
}

@RequiresApi(Build.VERSION_CODES.M)
fun Activity.setLightStatusBar() {
    var flags: Int = window.decorView.systemUiVisibility
    flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    window.decorView.systemUiVisibility = flags
}

@RequiresApi(Build.VERSION_CODES.M)
fun Activity.clearLightStatusBar() {
    var flags: Int = window.decorView.systemUiVisibility
    flags = flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    window.decorView.systemUiVisibility = flags
}

@RequiresApi(Build.VERSION_CODES.O)
fun Activity.setLightNavigationBar() {
    var flags: Int = window.decorView.systemUiVisibility
    flags = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
    window.decorView.systemUiVisibility = flags
}

@RequiresApi(Build.VERSION_CODES.O)
fun Activity.clearLightNavigationBar() {
    var flags: Int = window.decorView.systemUiVisibility
    flags = flags and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
    window.decorView.systemUiVisibility = flags
}