package com.momomomo111.camerax_opencv.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.momomomo111.camerax_opencv.R
import com.momomomo111.camerax_opencv.data.DilateViewModel
import com.momomomo111.camerax_opencv.databinding.FragmentDilateBinding
import com.momomomo111.camerax_opencv.util.CameraUtil
import com.momomomo111.camerax_opencv.util.ProcessImageAnalyzer
import com.momomomo111.camerax_opencv.util.VibrationUtil
import com.momomomo111.camerax_opencv.util.VibrationUtil.effectSlider

class DilateFragment : Fragment() {
    private val dilateViewModel: DilateViewModel by viewModels()
    private val args: DilateFragmentArgs by navArgs()

    private var _binding: FragmentDilateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDilateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val enableVibration = args.vibrationEnable

        CameraUtil.startCamera(
            requireContext(),
            ProcessImageAnalyzer(
                {
                    runOnUiThread {
                        binding.imageView.setImageBitmap(
                            it
                        )
                    }
                },
                binding.previewView,
                dilateViewModel.params
            ),
            binding.previewView.surfaceProvider
        )

        val vibrator = VibrationUtil.setVibrator(this.context)

        binding.sliderKSize.addOnChangeListener { _, value, _ ->
            val kSize = value.toInt()
            dilateViewModel.onKSizeChange(kSize)
            binding.kSizeText.text = getString(R.string.k_size, kSize.toString())
            if (enableVibration) {
                vibrator.effectSlider()
            }
        }
        binding.sliderIterations.addOnChangeListener { _, value, _ ->
            val iterations = value.toInt()
            dilateViewModel.onIterationsChange(iterations)
            binding.iterationsText.text = getString(R.string.iterations, iterations.toString())
            if (enableVibration) {
                vibrator.effectSlider()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun Fragment?.runOnUiThread(action: () -> Unit) {
        this ?: return
        if (!isAdded) return
        activity?.runOnUiThread(action)
    }
}
