package com.mobile.gympraaktis.ui.main.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.databinding.FragmentInstructionBinding
import com.mobile.gympraaktis.domain.Constants
import com.mobile.gympraaktis.domain.extension.materialAlert
import com.mobile.gympraaktis.domain.extension.replaceFragment
import com.praaktis.exerciseengine.Engine.ExerciseEngineActivity
import timber.log.Timber

class InstructionFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = InstructionFragment().apply {
            arguments = Bundle().apply {}
        }
    }

    private lateinit var binding: FragmentInstructionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentInstructionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNext.setOnClickListener {
            startExercise()
        }

        binding.btnBack.setOnClickListener {
            (parentFragment as StartupFragment).previousPage()
        }
    }

    private fun startExercise() {
        val intent = Intent(context, ExerciseEngineActivity::class.java)
        intent.putExtra("EXERCISE", Constants.ROUTINE_ID)
        intent.putExtra("PLAYER", 1)
        intent.putExtra("SINGLE_USER_MODE", Constants.SINGLE_USER_MODE)
        startActivityForResult(intent, Constants.SDK_REQUEST_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.SDK_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    @Suppress("UNCHECKED_CAST") val result =
                        data?.getSerializableExtra("result") as HashMap<String, Any>? ?: hashMapOf()
                    Timber.d("Result: $result")

                    activity?.replaceFragment(ExerciseResultFragment.TAG) {
                        replace(
                            R.id.container,
                            ExerciseResultFragment.newInstance(result),
                            ExerciseResultFragment.TAG
                        )
                        addToBackStack(ExerciseResultFragment.TAG)
                    }
                }
                ExerciseEngineActivity.AUTHENTICATION_FAILED -> {
                    showErrorDialog("LOGOUT EVENT : AUTHENTICATION_FAILED")
                }
                ExerciseEngineActivity.CALIBRATION_FAILED -> {
                    showErrorDialog("Calibration failed, please try again!")
                    Timber.d("ERROR EVENT : $resultCode")
                    Timber.d("Result NOT OK " + data?.getSerializableExtra("result"))
                }
                ExerciseEngineActivity.POOR_CONNECTION -> {
                    showErrorDialog("Poor connection, please try again!")
                    Timber.d("ERROR EVENT : $resultCode")
                    Timber.d("Result NOT OK " + data?.getSerializableExtra("result"))
                }
                ExerciseEngineActivity.CANNOT_REACH_SERVER -> {
                    showErrorDialog("Cannot reach server, please try again!")
                    Timber.d("ERROR EVENT : $resultCode")
                    Timber.d("Result NOT OK " + data?.getSerializableExtra("result"))
                }
                ExerciseEngineActivity.SMTH_WENT_WRONG -> {
                    showErrorDialog("Something went wrong, please try again!")
                }
                Activity.RESULT_CANCELED -> {

                }
                else -> {
                    showErrorDialog("Something went wrong, please try again!")
                }
            }
        }
    }

    private fun showErrorDialog(message: String) {
        materialAlert {
            setCancelable(true)
            setMessage(message)
            setPositiveButton(R.string.try_again) { dialog, which ->
                startExercise()
            }
            setNegativeButton(R.string.cancel) { dialog, which ->
                (parentFragment as StartupFragment).previousPage()
            }
        }?.show()
    }
}