package com.mobile.praaktishockey.ui.main.view

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.BaseFragment
import com.mobile.praaktishockey.domain.common.InfoDialog
import com.mobile.praaktishockey.domain.extension.addFragment
import com.mobile.praaktishockey.domain.extension.getViewModel
import com.mobile.praaktishockey.domain.extension.onClick
import com.mobile.praaktishockey.ui.friends.view.FriendsPagerFragment
import com.mobile.praaktishockey.ui.login.view.LoginActivity
import com.mobile.praaktishockey.ui.main.vm.MenuViewModel
import com.mobile.praaktishockey.ui.settings.view.SettingsFragment
import kotlinx.android.synthetic.main.fragment_menu.*


class MenuFragment constructor(override val layoutId: Int = R.layout.fragment_menu) : BaseFragment() {

    companion object {
        val TAG = MenuFragment::class.java.simpleName
    }

    override val mViewModel: MenuViewModel
        get() = getViewModel { MenuViewModel(Application()) }

    override fun initUI(savedInstanceState: Bundle?) {
        menu_settings.onClick {
            activity.addFragment {
                add(
                    R.id.menu_container,
                    SettingsFragment.getInstance(),
                    SettingsFragment.TAG
                )
                addToBackStack(SettingsFragment.TAG)
            }
        }

        menu_friends.onClick {
            activity.addFragment {
                add(
                    R.id.menu_container,
                    FriendsPagerFragment.getInstance(),
                    FriendsPagerFragment.TAG
                )
                addToBackStack(FriendsPagerFragment.TAG)
            }
        }

        menu_my_profile.onClick {
            activity.addFragment {
                add(
                    R.id.menu_container,
                    ProfileFragment(),
                    ProfileFragment.TAG
                )
                addToBackStack(TAG)
            }
        }
        menu_logout.onClick {
            val dialog =
                InfoDialog(context!!, getString(R.string.are_you_sure_logout), object : InfoDialog.InfoDialogListener {
                    override fun onOkClicked() {
                        mViewModel.logout()
                    }
                })
            dialog.show()
        }

        mViewModel.logoutEvent.observe(viewLifecycleOwner, Observer {
            if (it) {
                Log.d("HERELOGOUT", "LOGOUT")
                mViewModel.onLogoutSuccess()
                activity.finish()
                LoginActivity.start(context!!)
            }
        })
    }
}