package com.mobile.gympraaktis.ui.main.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mobile.gympraaktis.databinding.FragmentDescriptionBinding

class DescriptionFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() =
            DescriptionFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    private lateinit var binding: FragmentDescriptionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDescriptionBinding.inflate(inflater, container, false)
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNext.setOnClickListener {
            (parentFragment as StartupFragment).nextPage()
//            RateRoutineDialogFragment.newInstance()
//                .show(requireActivity().supportFragmentManager, "dialog")
        }
    }

}