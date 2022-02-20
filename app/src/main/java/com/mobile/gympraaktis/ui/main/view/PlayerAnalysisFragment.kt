package com.mobile.gympraaktis.ui.main.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mobile.gympraaktis.R

class PlayerAnalysisFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_player_analysis, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            PlayerAnalysisFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}