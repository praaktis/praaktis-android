package com.mobile.praaktishockey.ui.friends.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.BaseActivity
import com.mobile.praaktishockey.ui.friends.vm.InviteFriendsActivityViewModel
import kotlinx.android.synthetic.main.activity_invite_friends.*
import com.mobile.praaktishockey.BuildConfig
import com.mobile.praaktishockey.domain.extension.*


class InviteFriendsActivity constructor(override val layoutId: Int = R.layout.activity_invite_friends) :
    BaseActivity() {

    companion object {
        @JvmField
        val INVITE_FRIEND_REQUEST_CODE = 21
        @JvmStatic
        fun start(activity: BaseActivity) {
            val intent = Intent(activity, InviteFriendsActivity::class.java)
            activity.startActivityForResult(intent, INVITE_FRIEND_REQUEST_CODE)
        }
    }

    override val mViewModel: InviteFriendsActivityViewModel?
        get() = getViewModel { InviteFriendsActivityViewModel(application) }

    override fun initUI(savedInstanceState: Bundle?) {
        initToolbar()
        cvInvite.onClick {
            if (etInviteEmail.isEmailValid())
                mViewModel?.inviteFriend(etInviteEmail.text.toString())
        }
        mViewModel?.inviteFriendEvent?.observe(this, Observer {
            makeToast(it)
            etInviteEmail.clearText()
            setResult(Activity.RESULT_OK)
        })
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_left_arrow)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(0, 0, 0, "Share")
            ?.setIcon(android.R.drawable.ic_menu_share)
            ?.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == 0) {
            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name")
                var shareMessage = "\nLet me recommend you this application\n\n"
                shareMessage =
                    shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(shareIntent, "choose one"))
            } catch (e: Exception) {
                //e.toString();
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}