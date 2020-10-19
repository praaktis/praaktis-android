package com.mobile.gympraaktis.base

import android.os.Bundle
import android.transition.Fade
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.mobile.gympraaktis.domain.common.ProgressLoadingDialog
import com.mobile.gympraaktis.domain.extension.makeToast

abstract class BaseFragment<B : ViewDataBinding> : Fragment() {

    @get:LayoutRes
    protected abstract val layoutId: Int

    protected lateinit var binding: B

    abstract val mViewModel: BaseViewModel

    val progressLoadingDialog by lazy { ProgressLoadingDialog(context!!) }

    val activity by lazy { getActivity() as BaseActivity<*> }

    override fun onCreate(savedInstanceState: Bundle?) {
        val fade = Fade()
        fade.duration = 150
        enterTransition = fade
        exitTransition = fade
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        initUI(savedInstanceState)

        mViewModel.errorMessage.observe(this, Observer {
            if (it is String) activity.makeToast(it)
            else if (it is Int) {
                activity.makeToast(it)
            }
        })
        mViewModel.showHideEvent.observe(this, Observer {
            if (it) progressLoadingDialog.show()
            else progressLoadingDialog.dismiss()
        })
    }

    protected abstract fun initUI(savedInstanceState: Bundle?)

    override fun onDestroy() {
        super.onDestroy()
        mViewModel.onDestroy()
    }
}