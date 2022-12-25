package com.mobile.gympraaktis.ui.main.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mobile.gympraaktis.PraaktisApp
import com.mobile.gympraaktis.databinding.FragmentRateRoutineDialogBinding
import com.mobile.gympraaktis.domain.Constants
import com.mobile.gympraaktis.domain.extension.hide
import com.mobile.gympraaktis.domain.extension.invisible
import com.mobile.gympraaktis.domain.extension.makeToast
import com.mobile.gympraaktis.domain.extension.show
import com.mobile.gympraaktis.domain.model.FeedbackModel
import com.mobile.gympraaktis.domain.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    RateRoutineDialogFragment.newInstance().show(supportFragmentManager, "dialog")
 * </pre>
 */
class RateRoutineDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentRateRoutineDialogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRateRoutineDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.btnSubmit.setOnClickListener {
            if(binding.etFeedback.text.isNullOrBlank() && binding.ratingbar.rating < 1) {
                context?.makeToast("Please rate your experience")
                return@setOnClickListener
            }

            isCancelable = false
            lifecycleScope.launch(Dispatchers.IO) {

                withContext(Dispatchers.Main) {
                    binding.btnSubmit.invisible()
                    binding.progressCircular.show()
                }

                val result = ApiClient.service.storeFeedback(
                    FeedbackModel(
                        Constants.ROUTINE_ID,
                        PraaktisApp.routine?.name.orEmpty(),
                        binding.etFeedback.text.toString(),
                        binding.ratingbar.rating.toInt()
                    )
                )
                withContext(Dispatchers.Main) {
                    isCancelable = true
                    if (result.isSuccessful) {
                        val message =
                            (result.body() as Map<String, Any>)["message"].toString()
                        context?.makeToast(message)
                        dismiss()
                    } else {
                        context?.makeToast("Error")
                        binding.progressCircular.hide()
                        binding.btnSubmit.show()
                    }
                }
            }
        }
    }

    companion object {
        fun newInstance(): RateRoutineDialogFragment =
            RateRoutineDialogFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}