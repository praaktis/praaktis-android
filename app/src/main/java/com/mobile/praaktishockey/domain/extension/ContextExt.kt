package com.mobile.praaktishockey.domain.extension

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.mobile.praaktishockey.R

fun AppCompatActivity.supportFragmentTransaction(init: FragmentTransaction.() -> Unit) {
    if (!isFinishing) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.init()
        transaction.commit()
    }
}

fun AppCompatActivity.supportStateLosFragmentTransaction(init: FragmentTransaction.() -> Unit) {
    if (!isFinishing) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.init()
        transaction.commitNowAllowingStateLoss()
    }
}

fun AppCompatActivity.showOrReplaceLast(tag: String, replaceFunc: FragmentTransaction.() -> Unit) {
    val manager = supportFragmentManager
    val fragment = manager.findFragmentByTag(tag)
    if (fragment?.isHidden == true) {
        manager.beginTransaction().show(fragment).commit()
    }
    if (fragment == null) {
        if (!isFinishing) {
            val transaction = manager.beginTransaction()
            transaction.replaceFunc()
            transaction.commit()
        }
    }
}

fun Context.makeToast(message: String) {
    if (this is Activity)
        runOnUiThread { Toast.makeText(this, message, Toast.LENGTH_SHORT).show() }
}

fun Context.makeToast(@StringRes messageId: Int) {
    if (this is Activity)
        runOnUiThread { Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show() }
}

fun Activity.alert(init: AlertDialog.Builder.() -> Unit): AlertDialog {
    val builder = androidx.appcompat.app.AlertDialog.Builder(this, R.style.AppAlertDialogTheme)
    builder.setCancelable(false)
    builder.init()
    return builder.create()
}

fun Activity.showAlert(title: Int = R.string.error, init: AlertDialog.Builder.() -> Unit) = alert {
    setTitle(title)
    init()
}.show()

fun Activity.showAlertMessage(title: String = "", init: AlertDialog.Builder.() -> Unit) = alert {
    setMessage(title)
    init()
}.show()

fun Fragment.alert(init: AlertDialog.Builder.() -> Unit) = activity?.alert(init)

fun Context.dpToPx(dp: Int) : Int {
    val displayMetrics = resources.displayMetrics
    return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
}