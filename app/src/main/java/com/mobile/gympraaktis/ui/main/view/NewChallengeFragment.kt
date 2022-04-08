package com.mobile.gympraaktis.ui.main.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.base.BaseFragment
import com.mobile.gympraaktis.data.entities.PlayerEntity
import com.mobile.gympraaktis.databinding.FragmentNewChallengeBinding
import com.mobile.gympraaktis.databinding.LayoutTargetChallengesBinding
import com.mobile.gympraaktis.domain.common.AppGuide
import com.mobile.gympraaktis.domain.common.pref.SettingsStorage
import com.mobile.gympraaktis.domain.common.resettableLazy
import com.mobile.gympraaktis.domain.entities.ChallengeDTO
import com.mobile.gympraaktis.domain.extension.*
import com.mobile.gympraaktis.ui.challenge.ChallengeVideoActivity
import com.mobile.gympraaktis.ui.main.adapter.ChallengesAdapter
import com.mobile.gympraaktis.ui.main.vm.MainViewModel
import com.mobile.gympraaktis.ui.main.vm.NewChallengeViewModel
import com.takusemba.spotlight.OnSpotlightListener
import com.takusemba.spotlight.Spotlight
import com.takusemba.spotlight.Target
import kotlinx.android.synthetic.main.fragment_new_challenge.*


class NewChallengeFragment constructor(override val layoutId: Int = R.layout.fragment_new_challenge) :
    BaseFragment<FragmentNewChallengeBinding>() {

    companion object {
        const val TAG: String = "NewChallengeFragment"

        @JvmStatic
        fun getInstance(): Fragment = NewChallengeFragment()

        const val PRAAKTIS_SDK_PERMISSIONS = 111
    }

    override val mViewModel: NewChallengeViewModel by viewModels()

    private lateinit var mainViewModel: MainViewModel

    private var challenge: ChallengeDTO? = null

    override fun initUI(savedInstanceState: Bundle?) {
        mainViewModel = ViewModelProvider(activity)[MainViewModel::class.java]

        val adapter = ChallengesAdapter {
            closeSpotlight()

            this.challenge = it
            handleChallengeClick()
        }
        rv_challenges.adapter = adapter

        mainViewModel.challengesEvent.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it) {
                startGuideIfNecessary()
            }
        })

        setupSelectPlayerField()

    }

    private fun setupSelectPlayerField() {
        mViewModel.observePlayers().observe(viewLifecycleOwner) { players ->
            val defaultPlayerId = SettingsStorage.instance.getSelectedPlayerId()
            if (defaultPlayerId != -1L) {
                players.find {
                    it.id == defaultPlayerId
                }?.let {
                    binding.dropdownSelectPlayer.setText(it.name)
                    binding.dropdownSelectPlayer.tag = it
                }
            }
            binding.dropdownSelectPlayer.setDropdownValues(players.map { it.name }.toTypedArray())
            binding.dropdownSelectPlayer.setOnItemClickListener { parent, view, position, id ->
                players.getOrNull(position)?.let {
                    binding.dropdownSelectPlayer.tag = it
                    SettingsStorage.instance.setSelectedPlayerId(it.id)
                }
            }
        }
    }

    private fun handleChallengeClick() {
        if (binding.dropdownSelectPlayer.tag != null) {
            if (ContextCompat.checkSelfPermission(
                    activity, Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    activity, Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO
                    ), PRAAKTIS_SDK_PERMISSIONS
                )
            } else {
                openChallengeVideo()
            }
        } else {
            context?.makeToast(R.string.select_player)
        }
    }

    private fun openChallengeVideo() {
        challenge?.let {
            it.videoUrl?.let { it1 -> (activity as MainActivity).schedulePreloadWork(it1) }

            ChallengeVideoActivity.start(
                activity,
                it,
                binding.dropdownSelectPlayer.tag as PlayerEntity
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PRAAKTIS_SDK_PERMISSIONS -> {
                val permissionsGrantedList: MutableList<Boolean> = mutableListOf()
                grantResults.forEach {
                    permissionsGrantedList.add(it == PackageManager.PERMISSION_GRANTED)
                }

                openChallengeVideo()
                return
                if (!permissionsGrantedList.contains(false)) {
                    openChallengeVideo()
                } else {
                    var showRationale = false
                    kotlin.run loop@{
                        permissions.forEach {
                            if (shouldShowRequestPermissionRationale(it)) {
                                showRationale = true
                                return@loop
                            }
                        }
                    }
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

    private val spotlightDelegate = resettableLazy { initGuide() }
    private val spotlight by spotlightDelegate
    private var isGuideStarted = false

    private fun startGuideIfNecessary() {
        if (!AppGuide.isGuideDone(TAG)) {
            AppGuide.setGuideDone(TAG)
            binding.rvChallenges.doOnPreDraw {
                spotlight.start()
            }
        }
        binding.ivInfo.setOnClickListener {
            binding.nestedScroll.fullScroll(View.FOCUS_UP)
            binding.nestedScroll.smoothScrollTo(0, 0)
            restartSpotlight()
        }
    }

    private fun restartSpotlight() {
        if (spotlightDelegate.isInitialized())
            spotlightDelegate.reset()
        spotlight.start()
    }

    private fun closeSpotlight() {
        if (isGuideStarted)
            spotlight.finish()
    }

    private fun initGuide(): Spotlight {
        return Spotlight.Builder(activity)
            .setTargets(challengeTarget())
            .setBackgroundColor(R.color.primaryColor_alpha_90)
            .setOnSpotlightListener(object : OnSpotlightListener {
                override fun onStarted() {
                    isGuideStarted = true
                    binding.ivInfo.hideAnimWithScale()
                }

                override fun onEnded() {
                    isGuideStarted = false
                    binding.ivInfo.showAnimWithScale()
                }
            })
            .build()
    }

    private fun challengeTarget(): Target {
        val target = LayoutTargetChallengesBinding.inflate(layoutInflater)
        target.closeSpotlight.setOnClickListener { closeSpotlight() }

        target.customText.text =
            "Discover all the Challenges and Select the one you want to try"

        val rvLocation = IntArray(2)
        binding.rvChallenges.getLocationOnScreen(rvLocation)

        val rvVisibleRect = Rect()
        binding.rvChallenges.getLocalVisibleRect(rvVisibleRect)

        return Target.Builder()
            /*.setAnchor(
                rvLocation[0] + binding.rvChallenges.width / 2f,
                rvLocation[1] + rvVisibleRect.height() / 2f
            )*/
            .setOverlay(target.root)
            /*.setShape(
                RoundedRectangle(
                    rvVisibleRect.height().toFloat() + 20.dp,
                    binding.rvChallenges.width.toFloat() + 20.dp,
                    4.dp.toFloat()
                )
            )*/
            .build()
    }

}
