package com.mobile.gympraaktis.ui.main.view

import android.media.MediaPlayer
import android.media.PlaybackParams
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.databinding.FragmentFirstBinding
import com.mobile.gympraaktis.domain.extension.hide
import com.mobile.gympraaktis.domain.extension.show


class StartupFragment : Fragment() {

    companion object {
        const val TAG = "StartupFragment"
        fun newInstance() = StartupFragment()
    }

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    private fun initPlayer() {
        val path =
            "android.resource://" + requireActivity().packageName + "/" + R.raw.two_legged_squat_front

        binding.videoView.setOnPreparedListener {
            val playbackParams = PlaybackParams()
            it.playbackParams = playbackParams
            it.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT)
            binding.videoView.seekTo(1)
        }
        binding.ivPlayReply.show()
        binding.ivPlayReply.setOnClickListener {
            if(binding.videoView.isPlaying) {
                binding.videoView.pause()
                binding.ivPlayReply.setImageResource(R.drawable.ic_play)
            } else {
                binding.videoView.start()
                it.hide()
            }
        }
        binding.videoView.setOnCompletionListener {
            binding.ivPlayReply.show()
            binding.ivPlayReply.setImageResource(R.drawable.ic_play)
        }

        binding.videoView.setVideoURI(Uri.parse(path))

        binding.flVideoParent.setOnClickListener {
            if (binding.videoView.isPlaying) {
                if (binding.ivPlayReply.isVisible)
                    binding.ivPlayReply.hide()
                else
                    binding.ivPlayReply.show()

                binding.ivPlayReply.setImageResource(R.drawable.ic_pause)
            } else {
                binding.ivPlayReply.setImageResource(R.drawable.ic_play)
            }
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initPlayer()

        binding.vp.adapter = ScreenSlidePagerAdapter(childFragmentManager, lifecycle)
    }

    fun nextPage() {
        binding.vp.currentItem = binding.vp.currentItem + 1
    }

    fun previousPage() {
        binding.vp.currentItem = binding.vp.currentItem - 1
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private inner class ScreenSlidePagerAdapter(
        fragmentManager: FragmentManager,
        lifecycle: Lifecycle
    ) : FragmentStateAdapter(fragmentManager, lifecycle) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment = when (position) {
            0 -> DescriptionFragment.newInstance()
            else -> InstructionFragment.newInstance()
        }
    }
}