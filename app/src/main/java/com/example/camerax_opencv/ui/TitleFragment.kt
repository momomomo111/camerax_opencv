package com.example.camerax_opencv.ui

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.camerax_opencv.R
import com.example.camerax_opencv.databinding.FragmentTitleBinding
import com.example.camerax_opencv.util.CameraUtil

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
        binding.cannyButton.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_titleFragment_to_cannyFragment)
        }
        binding.grayScaleButton.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_titleFragment_to_grayScaleFragment)
        }
        binding.colorExtractionButton.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_titleFragment_to_colorExtractionFragment)
        }
        if (!CameraUtil.checkPermissions(requireContext())) {
            CameraUtil.userRequestPermissions(requireActivity())
        }
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.options_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }
}