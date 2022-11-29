package com.mobile.gympraaktis.domain.extension

import android.app.Activity
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity

fun Activity.hideSystemUI() {
    // Set the IMMERSIVE flag.
    // Set the content to appear under the system bars so that the content
    // doesn't resize when the system bars hide and show.
    var flags = window.decorView.systemUiVisibility
    flags = flags or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    window.decorView.systemUiVisibility = flags
}

fun Activity.showSystemUI() {
    var flags = window.decorView.systemUiVisibility
    flags =
        flags and View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN.inv() and
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY.inv() and
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION.inv() and
                View.SYSTEM_UI_FLAG_FULLSCREEN.inv() and
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY.inv()
    window.decorView.systemUiVisibility = flags
}

fun Activity.transparentStatusAndNavigationBar() {
    var flags = window.decorView.systemUiVisibility
    flags = flags or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    window.decorView.systemUiVisibility = flags
}

fun FragmentActivity.setLightStatusBar() {
    var flags: Int = window.decorView.systemUiVisibility
    flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    window.decorView.systemUiVisibility = flags
}

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