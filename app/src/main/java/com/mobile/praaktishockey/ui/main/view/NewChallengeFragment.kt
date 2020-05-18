package com.mobile.praaktishockey.ui.main.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.temp.BaseFragment
import com.mobile.praaktishockey.databinding.FragmentNewChallengeBinding
import com.mobile.praaktishockey.domain.entities.ChallengeDTO
import com.mobile.praaktishockey.domain.extension.getViewModel
import com.mobile.praaktishockey.domain.extension.materialAlert
import com.mobile.praaktishockey.ui.challenge.ChallengeVideoActivity
import com.mobile.praaktishockey.ui.main.adapter.ChallengesAdapter
import com.mobile.praaktishockey.ui.main.vm.MainViewModel
import com.mobile.praaktishockey.ui.main.vm.NewChallengeViewModel
import kotlinx.android.synthetic.main.fragment_new_challenge.*


class NewChallengeFragment constructor(override val layoutId: Int = R.layout.fragment_new_challenge) :
    BaseFragment<FragmentNewChallengeBinding>() {

    companion object {
        @JvmField
        val TAG: String = NewChallengeFragment::class.java.simpleName

        @JvmStatic
        fun getInstance(): Fragment = NewChallengeFragment()

        const val CAMERA_AND_EXTERNAL_STORAGE_PERMISSIONS = 111
    }

    override val mViewModel: NewChallengeViewModel
        get() = getViewModel { NewChallengeViewModel(activity.application) }

    private lateinit var mainViewModel: MainViewModel

    private var challenge: ChallengeDTO? = null

    override fun initUI(savedInstanceState: Bundle?) {
        mainViewModel = ViewModelProvider(activity).get(MainViewModel::class.java)

        val adapter = ChallengesAdapter {
            this.challenge = it
            handleChallengeClick()
        }
        rv_challenges.adapter = adapter

        mainViewModel.challengesEvent.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

    }

    private fun handleChallengeClick() {
        if (ContextCompat.checkSelfPermission(
                activity, Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), CAMERA_AND_EXTERNAL_STORAGE_PERMISSIONS
            )
        } else {
            openChallengeVideo()
        }
    }

    private fun openChallengeVideo() {
        challenge?.let { ChallengeVideoActivity.start(activity, it) }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CAMERA_AND_EXTERNAL_STORAGE_PERMISSIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    openChallengeVideo()
                } else {
                    val showRationale =
                        shouldShowRequestPermissionRationale(permissions[0]) || shouldShowRequestPermissionRationale(
                            permissions[1]
                        )
                    materialAlert {
                        setMessage("Sorry!!!, you can't use challenges without granting permissions")
                        setPositiveButton(
                            R.string.ok
                        ) { dialog, which ->
                            if (!showRationale) {
                                openAppSettingsPage()
                            } else {
                                handleChallengeClick()
                            }
                        }
                        setNegativeButton(
                            R.string.cancel
                        ) { dialog, which -> }
                    }?.show()
                }
            }
        }
    }

    private fun openAppSettingsPage() {
        startActivity(Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", activity.packageName, null)
        })
    }

}
