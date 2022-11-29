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
import com.mobile.gympraaktis.domain.extension.replaceFragment
import com.praaktis.exerciseengine.Engine.ExerciseEngineActivity
import timber.log.Timber

class InstructionFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() =
            InstructionFragment().apply {
                arguments = Bundle().apply {
                }
            }

        const val SDK_REQUEST_CODE = 333
        const val CHALLENGE_ITEM = "ANALYSIS_ITEM"
        const val CHALLENGE_RESULT = "CHALLENGE_RESULT"
        const val RAW_VIDEO_PATH = "RAW_VIDEO_PATH"
        const val VIDEO_PATH = "VIDEO_PATH"
        const val SINGLE_USER_MODE = "SINGLE_USER_MODE"
        const val SERVER_NAME = "SERVER_NAME"
        const val VIDEO_ID = "VIDEO_ID"
    }

    private lateinit var binding: FragmentInstructionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
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
        intent.putExtra("EXERCISE", 103)
        intent.putExtra("PLAYER", 8)
        intent.putExtra("SINGLE_USER_MODE", false)
        startActivityForResult(intent, SDK_REQUEST_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SDK_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    @Suppress("UNCHECKED_CAST")
                    val result =
                        data?.getSerializableExtra("result") as HashMap<String, Any>? ?: hashMapOf()
                    Timber.d("Result: $result")
                    val rawVideo = data?.getStringExtra(RAW_VIDEO_PATH)
                    val video = data?.getStringExtra(VIDEO_PATH)
                    val videoId = data?.getStringExtra(VIDEO_ID)

                    Timber.d("rawVideo: $rawVideo")
                    Timber.d("video: $video")
                    Timber.d("videoId: $videoId")

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
                    Timber.d("LOGOUT EVENT : AUTHENTICATION_FAILED")
                }
                ExerciseEngineActivity.CALIBRATION_FAILED -> {
                    Timber.d("Calibration failed, please try again!")
                    Timber.d("ERROR EVENT : $resultCode")
                    Timber.d("Result NOT OK " + data?.getSerializableExtra("result"))
                }
                ExerciseEngineActivity.POOR_CONNECTION -> {
                    Timber.d("Poor connection, please try again!")
                    Timber.d("ERROR EVENT : $resultCode")
                    Timber.d("Result NOT OK " + data?.getSerializableExtra("result"))
                }
                ExerciseEngineActivity.CANNOT_REACH_SERVER -> {
                    Timber.d("Cannot reach server, please try again!")
                    Timber.d("ERROR EVENT : $resultCode")
                    Timber.d("Result NOT OK " + data?.getSerializableExtra("result"))
                }
                ExerciseEngineActivity.SMTH_WENT_WRONG -> {
                    Timber.d("Something went wrong, please try again!")
                }
                Activity.RESULT_CANCELED -> {

                }
                else -> {
                    Timber.d("Something went wrong, please try again!")
                }
            }
        }
    }


}