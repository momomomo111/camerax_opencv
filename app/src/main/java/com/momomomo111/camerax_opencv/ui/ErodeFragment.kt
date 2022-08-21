package com.momomomo111.camerax_opencv.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.momomomo111.camerax_opencv.R
import com.momomomo111.camerax_opencv.data.ErodeViewModel
import com.momomomo111.camerax_opencv.databinding.FragmentErodeBinding
import com.momomomo111.camerax_opencv.util.CameraUtil
import com.momomomo111.camerax_opencv.util.ProcessImageAnalyzer
import com.momomomo111.camerax_opencv.util.VibrationUtil
import com.momomomo111.camerax_opencv.util.VibrationUtil.effectSlider

class ErodeFragment : Fragment() {
    private val erodeViewModel: ErodeViewModel by viewModels()
    private val args: ErodeFragmentArgs by navArgs()

    private var _binding: FragmentErodeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentErodeBinding.inflate(inflater, container, false)
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
                erodeViewModel.params
            )
        )

        val vibrator = VibrationUtil.setVibrator(this.context)

        binding.sliderKSize.addOnChangeListener { _, value, _ ->
            val kSize = value.toInt()
            erodeViewModel.onKSizeChange(kSize)
            binding.kSizeText.text = getString(R.string.k_size, kSize.toString())
            if (enableVibration) {
                vibrator.effectSlider()
            }
        }
        binding.sliderIterations.addOnChangeListener { _, value, _ ->
            val iterations = value.toInt()
            erodeViewModel.onIterationsChange(iterations)
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

    private fun Fragment.runOnUiThread(action: () -> Unit) {
        if (!isAdded) return
        activity?.runOnUiThread(action)
    }
}
