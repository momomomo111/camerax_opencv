package com.momomomo111.camerax_opencv.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.momomomo111.camerax_opencv.R
import com.momomomo111.camerax_opencv.data.GaussianblurViewModel
import com.momomomo111.camerax_opencv.databinding.FragmentGaussianblurBinding
import com.momomomo111.camerax_opencv.util.CameraUtil
import com.momomomo111.camerax_opencv.util.ProcessImageAnalyzer
import com.momomomo111.camerax_opencv.util.VibrationUtil
import com.momomomo111.camerax_opencv.util.VibrationUtil.effectSlider

class GaussianBlurFragment : Fragment() {
    private val gaussianblurViewModel: GaussianblurViewModel by viewModels()
    private val args: GaussianBlurFragmentArgs by navArgs()

    private var _binding: FragmentGaussianblurBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGaussianblurBinding.inflate(inflater, container, false)
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
                gaussianblurViewModel.params
            )
        )

        val vibrator = VibrationUtil.setVibrator(this.context)

        binding.sliderKSize.addOnChangeListener { _, value, _ ->
            val kSize = value.toDouble()
            gaussianblurViewModel.onKSizeChange(kSize)
            binding.kSizeText.text = getString(R.string.k_size, kSize.toString())
            if (enableVibration) {
                vibrator.effectSlider()
            }
        }
        binding.sliderSigmaX.addOnChangeListener { _, value, _ ->
            val sigmaX = value.toDouble()
            gaussianblurViewModel.onSigmaXChange(sigmaX)
            binding.sigmaXText.text = getString(R.string.sigma_x, sigmaX.toString())
            if (enableVibration) {
                vibrator.effectSlider()
            }
        }
        binding.sliderSigmaY.addOnChangeListener { _, value, _ ->
            val sigmaY = value.toDouble()
            gaussianblurViewModel.onSigmaYChange(sigmaY)
            binding.sigmaYText.text = getString(R.string.sigma_y, sigmaY.toString())
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
