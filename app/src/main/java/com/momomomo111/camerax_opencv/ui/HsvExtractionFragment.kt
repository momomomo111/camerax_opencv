package com.momomomo111.camerax_opencv.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.momomomo111.camerax_opencv.R
import com.momomomo111.camerax_opencv.data.HsvextractionViewModel
import com.momomomo111.camerax_opencv.databinding.FragmentHsvextractionBinding
import com.momomomo111.camerax_opencv.util.CameraUtil
import com.momomomo111.camerax_opencv.util.ProcessImageAnalyzer
import com.momomomo111.camerax_opencv.util.VibrationUtil
import com.momomomo111.camerax_opencv.util.VibrationUtil.effectSlider

class HsvExtractionFragment : Fragment() {
    private val hsvextractionViewModel: HsvextractionViewModel by viewModels()
    private val args: GaussianBlurFragmentArgs by navArgs()

    private var _binding: FragmentHsvextractionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHsvextractionBinding.inflate(inflater, container, false)
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
                hsvextractionViewModel.params
            ),
            binding.previewView.surfaceProvider
        )

        val vibrator = VibrationUtil.setVibrator(this.context)

        val sliderH = binding.sliderH
        sliderH.setValues(0.0F, 255.0F)
        sliderH.addOnChangeListener { _, _, _ ->
            val values = sliderH.values
            val upperH = values[1].toDouble()
            val lowerH = values[0].toDouble()
            hsvextractionViewModel.onUpperHChange(upperH)
            hsvextractionViewModel.onLowerHChange(lowerH)
            binding.UpperHText.text = getString(R.string.upper_h, upperH.toString())
            binding.LowerHText.text = getString(R.string.lower_h, lowerH.toString())
            if (enableVibration) {
                vibrator.effectSlider()
            }
        }

        val sliderS = binding.sliderS
        sliderS.setValues(0.0F, 255.0F)
        sliderS.addOnChangeListener { _, _, _ ->
            val values = sliderS.values
            val upperS = values[1].toDouble()
            val lowerS = values[0].toDouble()
            hsvextractionViewModel.onUpperSChange(upperS)
            hsvextractionViewModel.onLowerSChange(lowerS)
            binding.UpperSText.text = getString(R.string.upper_s, upperS.toString())
            binding.LowerSText.text = getString(R.string.lower_s, lowerS.toString())
            if (enableVibration) {
                vibrator.effectSlider()
            }
        }

        val sliderV = binding.sliderV
        sliderV.setValues(0.0F, 255.0F)
        sliderV.addOnChangeListener { _, _, _ ->
            val values = sliderV.values
            val upperV = values[1].toDouble()
            val lowerV = values[0].toDouble()
            hsvextractionViewModel.onUpperVChange(upperV)
            hsvextractionViewModel.onLowerVChange(lowerV)
            binding.UpperVText.text = getString(R.string.upper_v, upperV.toString())
            binding.LowerVText.text = getString(R.string.lower_v, lowerV.toString())
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
