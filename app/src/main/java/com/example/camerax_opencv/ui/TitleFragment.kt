package com.example.camerax_opencv.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.camerax_opencv.R
import com.example.camerax_opencv.databinding.FragmentTitleBinding

class TitleFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentTitleBinding>(
            inflater,
            R.layout.fragment_title, container, false
        )
        binding.gaussianBlurButton.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_titleFragment_to_gaussianBlurFragment)
        }
        binding.thresholdButton.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_titleFragment_to_thresholdFragment)
        }
        return binding.root
    }
}