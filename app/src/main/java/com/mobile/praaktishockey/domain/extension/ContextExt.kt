package com.mobile.praaktishockey.domain.extension

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Dimension
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ButtonBarLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mobile.praaktishockey.BuildConfig
import com.mobile.praaktishockey.R
import com.nguyenhoanglam.imagepicker.model.Config
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker

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
    val builder = AlertDialog.Builder(this)
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

fun Activity.materialAlert(init: MaterialAlertDialogBuilder.() -> Unit): AlertDialog {
    val builder = MaterialAlertDialogBuilder(this)
    builder.init()
    val dialog = builder.create()

    dialog.setOnShowListener {
        val positiveBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        dialog.findViewById<TextView>(android.R.id.message)?.apply {
            gravity = Gravity.CENTER
            setTextSize(Dimension.SP, 18f)
            setTextColor(ContextCompat.getColor(context, R.color.purple_900_1))
            setTypeface(ResourcesCompat.getFont(context, R.font.lato))
            updatePadding(bottom = 16.dp)
        }
        (dialog.getButton(AlertDialog.BUTTON_POSITIVE).parent as ButtonBarLayout).updatePadding(
            bottom = 10.dp
        )
        positiveBtn.layoutParams = (positiveBtn.layoutParams as LinearLayout.LayoutParams).apply {
            weight = 10f
            gravity = Gravity.CENTER
            marginEnd = 6.dp
        }

        val negativeBtn = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        negativeBtn.layoutParams = (negativeBtn.layoutParams as LinearLayout.LayoutParams).apply {
            weight = 10f
            marginEnd = 10.dp
            gravity = Gravity.CENTER
        }
    }
    return dialog
}

fun Fragment.materialAlert(init: MaterialAlertDialogBuilder.() -> Unit) =
    activity?.materialAlert(init)


fun Context.dpToPx(dp: Int): Int {
    val displayMetrics = resources.displayMetrics
    return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
}

fun FragmentManager.switch(containerId: Int, newFrag: Fragment, tag: String) {
    var current = findFragmentByTag(tag)
    beginTransaction()
        .apply {

            //Hide the current fragment
            primaryNavigationFragment?.let { hide(it) }

            //Check if current fragment exists in fragmentManager
            if (current == null) {
                current = newFrag
                add(containerId, current!!, tag)
            } else {
                show(current!!)
            }
        }
//        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        .setPrimaryNavigationFragment(current)
        .setReorderingAllowed(true)
        .commitNowAllowingStateLoss()
}

fun Fragment.openImagePicker() {
    ImagePicker.with(this)
        .setToolbarColor("#000000")
        .setStatusBarColor("#000000")
        .setToolbarTextColor("#FFFFFF")
        .setToolbarIconColor("#FFFFFF")
        .setProgressBarColor("#CE0106")
        .setBackgroundColor("#66000000")
        .setShowCamera(true)
        .setMultipleMode(false)
        .setFolderMode(true)
        .setDoneTitle("Done")
        .setRootDirectoryName(Config.ROOT_DIR_DCIM)
        .setDirectoryName(getString(R.string.app_name))
        .start()
}