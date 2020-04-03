package com.mobile.praaktishockey.ui.login.view

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.base.temp.BaseFragment
import com.mobile.praaktishockey.databinding.FragmentConfirmLoginBinding
import com.mobile.praaktishockey.domain.common.pref.SettingsStorage
import com.mobile.praaktishockey.domain.common.shape.CurvedEdgeTreatment
import com.mobile.praaktishockey.domain.entities.LanguageItem
import com.mobile.praaktishockey.domain.entities.UserDTO
import com.mobile.praaktishockey.domain.extension.dp
import com.mobile.praaktishockey.domain.extension.getViewModel
import com.mobile.praaktishockey.domain.extension.makeToast
import com.mobile.praaktishockey.domain.extension.onClick
import com.mobile.praaktishockey.ui.login.vm.ConfirmLoginFragmentViewModel
import com.mobile.praaktishockey.ui.main.view.MainActivity
import kotlinx.android.synthetic.main.fragment_confirm_login.*

class ConfirmLoginFragment constructor(override val layoutId: Int = R.layout.fragment_confirm_login) :
    BaseFragment<FragmentConfirmLoginBinding>() {

    companion object {
        val TAG = ConfirmLoginFragment::class.java.simpleName

        fun getInstance() = ConfirmLoginFragment().apply {
            arguments = Bundle().apply {

            }
        }
    }

    override val mViewModel: ConfirmLoginFragmentViewModel
        get() = getViewModel { ConfirmLoginFragmentViewModel(activity.application!!) }

    private var user: UserDTO? = null

    override fun initUI(savedInstanceState: Bundle?) {
        setupCurvedLayout()

        mViewModel.loadProfile()

        mViewModel.profileInfoEvent.observe(this, Observer {
            user = it
            setInfo(user!!)
        })

        tvStart.onClick {
            mViewModel.loadProfile()
        }

        mViewModel.resendActivationEvent.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, it.toString())
            activity.makeToast("Activation message sent")
        })

        binding.btnResend.setOnClickListener {
            mViewModel.resendActivation()
        }
    }

    private fun setupCurvedLayout() {
        binding.root.post {
            binding.cvCurvedLayout.apply {
                val curveSize = binding.root.width * 0.20f
                clipToOutline = false
                setContentPadding(0, curveSize.toInt(), 0, 0)

                val shapeAppearanceModel = ShapeAppearanceModel.Builder()
                    .setTopEdge(CurvedEdgeTreatment(curveSize))
                    .build()

                val shapeDrawable = MaterialShapeDrawable(shapeAppearanceModel).apply {
                    initializeElevationOverlay(context)
                    setTint(Color.WHITE)
                    setUseTintColorForShadow(false)
                    paintStyle = Paint.Style.FILL
                    shadowCompatibilityMode = MaterialShapeDrawable.SHADOW_COMPAT_MODE_ALWAYS
                    elevation = 8.dp.toFloat()
                }
                background = shapeDrawable
            }
        }
    }

    private fun setInfo(user: UserDTO) {
        tvThankYou.text = String.format(getString(R.string.thank_you_s), user.firstName)
        tvActivationSummary.text =
            String.format(getString(R.string.account_activation_summary), user.email)
        if (user.praaktisRegistered!!) {
            if (user.language != null)
                setLanguageAccordingly(mViewModel.getLanguageObject()!!)
            (activity as LoginActivity).isLoginProcessFinishSuccess = true
            activity.finish()
            MainActivity.start(activity)
        } else {
            activity.makeToast(getString(R.string.please_activate_link))
        }
    }

    private fun setLanguageAccordingly(language: LanguageItem) {
        val localeKey = when (language.key) {
            1 -> "en"
            2 -> "fr"
            else -> "en"
        }
        SettingsStorage.instance.lang = localeKey
    }
}
