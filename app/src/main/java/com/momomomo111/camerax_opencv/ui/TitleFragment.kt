package com.momomomo111.camerax_opencv.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
    private val settingsViewModel: SettingsViewModel by activityViewModels()

    private var _binding: FragmentTitleBinding? = null
    private val binding get() = _binding!!
    private var isChecked = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTitleBinding.inflate(inflater, container, false)
        binding.gaussianBlurButton.setOnClickListener { view: View ->
            view.findNavController().navigate(
                TitleFragmentDirections.actionTitleFragmentToGaussianBlurFragment(isChecked)
            )
        }
        binding.thresholdButton.setOnClickListener { view: View ->
            view.findNavController()
                .navigate(TitleFragmentDirections.actionTitleFragmentToThresholdFragment(isChecked))
        }
        binding.cannyButton.setOnClickListener { view: View ->
            view.findNavController()
                .navigate(TitleFragmentDirections.actionTitleFragmentToCannyFragment(isChecked))
        }
        binding.grayScaleButton.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_titleFragment_to_grayScaleFragment)
        }
        binding.rgbExtractionButton.setOnClickListener { view: View ->
            view.findNavController().navigate(
                TitleFragmentDirections.actionTitleFragmentToRgbExtractionFragment(isChecked)
            )
        }
        binding.hsvExtractionButton.setOnClickListener { view: View ->
            view.findNavController()
                .navigate(
                    TitleFragmentDirections.actionTitleFragmentToHsvExtractionFragment(isChecked)
                )
        }
        binding.erodeButton.setOnClickListener { view: View ->
            view.findNavController()
                .navigate(TitleFragmentDirections.actionTitleFragmentToErodeFragment(isChecked))
        }
        binding.dilateButton.setOnClickListener { view: View ->
            view.findNavController()
                .navigate(TitleFragmentDirections.actionTitleFragmentToDilateFragment(isChecked))
        }
        if (!CameraUtil.checkPermissions(requireContext())) {
            CameraUtil.userRequestPermissions(requireActivity())
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.options_menu, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return when (menuItem.itemId) {
                        R.id.licenceFragment -> {
                            NavigationUI.onNavDestinationSelected(
                                menuItem,
                                requireView().findNavController()
                            )
                        }
                        else -> false
                    }
                }
            },
            viewLifecycleOwner, Lifecycle.State.RESUMED
        )

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                settingsViewModel.settingsUiState.collect {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
