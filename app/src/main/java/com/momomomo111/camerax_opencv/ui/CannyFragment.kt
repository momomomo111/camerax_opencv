package com.momomomo111.camerax_opencv.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.momomomo111.camerax_opencv.R
import com.momomomo111.camerax_opencv.data.CannyViewModel
import com.momomomo111.camerax_opencv.databinding.FragmentCannyBinding
import com.momomomo111.camerax_opencv.util.CameraUtil
import com.momomomo111.camerax_opencv.util.ProcessImageAnalyzer
import com.momomomo111.camerax_opencv.util.VibrationUtil.effectSlider
import com.momomomo111.camerax_opencv.util.VibrationUtil.setVibrator

class CannyFragment : Fragment() {
    private val cannyViewModel: CannyViewModel by viewModels()
    private val args: CannyFragmentArgs by navArgs()

    private var _binding: FragmentCannyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCannyBinding.inflate(inflater, container, false)
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
                cannyViewModel.params
            ),
        )

        val vibrator = setVibrator(this.context)

        binding.sliderThreshold1.addOnChangeListener { _, value, _ ->
            val thresh = value.toDouble()
            cannyViewModel.onThreshold1Change(thresh)
            binding.threshold1.text = getString(R.string.threshold1, thresh.toString())
            if (enableVibration) {
                vibrator.effectSlider()
            }
        }
        binding.sliderThreshold2.addOnChangeListener { _, value, _ ->
            val maxVal = value.toDouble()
            cannyViewModel.onThreshold2Change(maxVal)
            binding.threshold2.text = getString(R.string.threshold2, maxVal.toString())
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
