package com.mobile.gympraaktis.domain.extension

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

fun AppCompatActivity.hideKeyboard(view: View) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun AppCompatActivity.hideKeyboard(activity: Activity) {
    val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view = activity.currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(activity)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun AppCompatActivity.toggleKeyboard(view: View) {
    val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager;
    inputManager.toggleSoftInputFromWindow(view.applicationWindowToken, InputMethodManager.SHOW_FORCED, 0);
}

fun Activity.loadAsync(@LayoutRes res: Int, target: ViewGroup? = null, action: View.() -> Unit) =
    AsyncLayoutInflater(this).inflate(res, target)
    { view, resid, parent ->
        with(parent) {
            this!!.addView(view)
            action(view)
        }
    }

fun Fragment.childFragmentTransaction(init: androidx.fragment.app.FragmentTransaction.() -> Unit) {
    if (!activity?.isFinishing!!) {
        val transaction = childFragmentManager.beginTransaction()
        transaction.init()
        transaction.commitAllowingStateLoss()
    }
}

fun AppCompatActivity.addFragment(init: androidx.fragment.app.FragmentTransaction.() -> Unit) {
    if (!isFinishing) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.init()
        transaction.commitAllowingStateLoss()
    }
}

fun AppCompatActivity.showOrReplace(
    tag: String,
    currentFragment: Fragment? = null,
    replaceFunc: FragmentTransaction.() -> Unit
) {
    val manager = supportFragmentManager
    val fragment = manager.findFragmentByTag(tag)

//    if (fragment != null) {
//        manager.beginTransaction().show(fragment).commitAllowingStateLoss()
//        fragment.userVisibleHint = true
//    }
//
//    if (currentFragment != null) {
//        manager.beginTransaction().hide(currentFragment).commitAllowingStateLoss()
//        currentFragment.userVisibleHint = false
//    }

//    if (fragment == null) {
        if (!isFinishing) {
            val transaction = manager.beginTransaction()
            transaction.replaceFunc()
            transaction.commitAllowingStateLoss()
//        }
    }
}

fun AppCompatActivity.replaceFragment(tag: String, replaceFunc: FragmentTransaction.() -> Unit) {
    val manager = supportFragmentManager
    manager.popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    if (!isFinishing) {
        val transaction = manager.beginTransaction()
        transaction.replaceFunc()
        transaction.commitAllowingStateLoss()
    }
}

fun AppCompatActivity.getVisibleFragment(): Fragment? {
    val fragments = supportFragmentManager.fragments
    if (!fragments.isEmpty()) {
        for (fragment in fragments) {
            if (fragment != null && fragment.isVisible) {
                return fragment
            }
        }
    }
    return null
}