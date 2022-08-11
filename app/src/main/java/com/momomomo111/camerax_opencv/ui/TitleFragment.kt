package com.momomomo111.camerax_opencv.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.momomomo111.camerax_opencv.R
import com.momomomo111.camerax_opencv.data.SettingsUiState
import com.momomomo111.camerax_opencv.data.SettingsViewModel
import com.momomomo111.camerax_opencv.databinding.FragmentTitleBinding
import com.momomomo111.camerax_opencv.util.CameraUtil
import kotlinx.coroutines.launch

class TitleFragment : Fragment() {
    private val settingsViewModel: SettingsViewModel by viewModels()

    private var _binding: FragmentTitleBinding? = null
    private val binding get() = _binding!!
    private var isChecked = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                settingsViewModel.settingsUiState.collect {
                    Log.d("hoge", it.toString())
                    isChecked = when (it) {
                        is SettingsUiState.Success -> {
                            it.vibration
                        }
                        is SettingsUiState.None -> {
                            false
                        }
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTitleBinding.inflate(inflater, container, false)
        binding.gaussianBlurButton.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_titleFragment_to_gaussianBlurFragment)
        }
        binding.thresholdButton.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_titleFragment_to_thresholdFragment)
        }
        binding.cannyButton.setOnClickListener { view: View ->
            Log.d("TitleFraghoge", isChecked.toString())
            view.findNavController()
                .navigate(TitleFragmentDirections.actionTitleFragmentToCannyFragment(isChecked))
        }
        binding.grayScaleButton.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_titleFragment_to_grayScaleFragment)
        }
        binding.rgbExtractionButton.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_titleFragment_to_rgbExtractionFragment)
        }
        binding.hsvExtractionButton.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_titleFragment_to_hsvExtractionFragment)
        }
        binding.erodeButton.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_titleFragment_to_erodeFragment)
        }
        binding.dilateButton.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_titleFragment_to_dilateFragment)
        }
        if (!CameraUtil.checkPermissions(requireContext())) {
            CameraUtil.userRequestPermissions(requireActivity())
        }
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.options_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController()) ||
            super.onOptionsItemSelected(item)
    }
}
