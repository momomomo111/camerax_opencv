package com.momomomo111.camerax_opencv.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.momomomo111.camerax_opencv.R
import com.momomomo111.camerax_opencv.data.RgbextractionViewModel
import com.momomomo111.camerax_opencv.databinding.FragmentRgbextractionBinding
import com.momomomo111.camerax_opencv.util.CameraUtil
import com.momomomo111.camerax_opencv.util.ProcessImageAnalyzer
import com.momomomo111.camerax_opencv.util.VibrationUtil
import com.momomomo111.camerax_opencv.util.VibrationUtil.effectSlider

class RgbExtractionFragment : Fragment() {
    private val rgbextractionViewModel: RgbextractionViewModel by viewModels()
    private val args: GaussianBlurFragmentArgs by navArgs()

    private var _binding: FragmentRgbextractionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRgbextractionBinding.inflate(inflater, container, false)
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
                        _binding?.imageView?.setImageBitmap(
                            it
                        )
                    }
                },
                rgbextractionViewModel.params
            )
        )

        val vibrator = VibrationUtil.setVibrator(this.context)

        val sliderR = binding.sliderR
        sliderR.setValues(0.0F, 255.0F)
        sliderR.addOnChangeListener { _, _, _ ->
            val values = sliderR.values
            val upperR = values[1].toDouble()
            val lowerR = values[0].toDouble()
            rgbextractionViewModel.onUpperRChange(upperR)
            rgbextractionViewModel.onLowerRChange(lowerR)
            binding.UpperRText.text = getString(R.string.upper_r, upperR.toString())
            binding.LowerRText.text = getString(R.string.lower_r, lowerR.toString())
            if (enableVibration) {
                vibrator.effectSlider()
            }
        }

        val sliderG = binding.sliderG
        sliderG.setValues(0.0F, 255.0F)
        sliderG.addOnChangeListener { _, _, _ ->
            val values = sliderG.values
            val upperG = values[1].toDouble()
            val lowerG = values[0].toDouble()
            rgbextractionViewModel.onUpperGChange(upperG)
            rgbextractionViewModel.onLowerGChange(lowerG)
            binding.UpperGText.text = getString(R.string.upper_g, upperG.toString())
            binding.LowerGText.text = getString(R.string.lower_g, lowerG.toString())
            if (enableVibration) {
                vibrator.effectSlider()
            }
        }

        val sliderB = binding.sliderB
        sliderB.setValues(0.0F, 255.0F)
        sliderB.addOnChangeListener { _, _, _ ->
            val values = sliderB.values
            val upperB = values[1].toDouble()
            val lowerB = values[0].toDouble()
            rgbextractionViewModel.onUpperBChange(upperB)
            rgbextractionViewModel.onLowerBChange(lowerB)
            binding.UpperBText.text = getString(R.string.upper_b, upperB.toString())
            binding.LowerBText.text = getString(R.string.lower_b, lowerB.toString())
            if (enableVibration) {
                vibrator.effectSlider()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun Fragment.runOnUiThread(action: () -> Unit) {
        if (!isAdded) return
        activity?.runOnUiThread(action)
    }
}
