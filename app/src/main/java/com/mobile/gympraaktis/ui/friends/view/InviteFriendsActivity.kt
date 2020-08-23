package com.mobile.gympraaktis.ui.friends.view

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.afollestad.vvalidator.form
import com.mobile.gympraaktis.BuildConfig
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.base.temp.BaseActivity
import com.mobile.gympraaktis.databinding.ActivityInviteFriendsBinding
import com.mobile.gympraaktis.domain.extension.*
import com.mobile.gympraaktis.ui.friends.vm.InviteFriendsActivityViewModel
import kotlinx.android.synthetic.main.activity_invite_friends.*

class InviteFriendsActivity constructor(override val layoutId: Int = R.layout.activity_invite_friends) :
    BaseActivity<ActivityInviteFriendsBinding>() {

    companion object {
        @JvmField
        val INVITE_FRIEND_REQUEST_CODE = 21

        @JvmStatic
        fun start(activity: AppCompatActivity) {
            val intent = Intent(activity, InviteFriendsActivity::class.java)
            activity.startActivityForResult(intent, INVITE_FRIEND_REQUEST_CODE)
        }
    }

    override val mViewModel: InviteFriendsActivityViewModel?
        get() = getViewModel { InviteFriendsActivityViewModel(application) }

    override fun initUI(savedInstanceState: Bundle?) {
        transparentStatusAndNavigationBar()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setLightNavigationBar()
        }

        initToolbar()

        form {
            inputLayout(binding.tilEmail) {
                isNotEmpty()
                isEmail()
            }
            submitWith(binding.btnInvite) {
                mViewModel?.inviteFriend(binding.etEmail.text.toString())
            }
        }

        mViewModel?.inviteFriendEvent?.observe(this, Observer {
            makeToast(it)
            binding.etEmail.clearText()
            setResult(Activity.RESULT_OK)
            showResultDialog(it)
        })
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_back)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(0, 0, 0, "Share")
            ?.setIcon(R.drawable.ic_share)
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

    private fun showResultDialog(message: String) {
        materialAlert {
            setMessage(message)
            setPositiveButton("OK") { _, _ -> }
        }.show()
    }

}