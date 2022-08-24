package com.momomomo111.camerax_opencv.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.momomomo111.camerax_opencv.R
import com.momomomo111.camerax_opencv.data.ThresholdViewModel
import com.momomomo111.camerax_opencv.databinding.FragmentThresholdBinding
import com.momomomo111.camerax_opencv.util.CameraUtil
import com.momomomo111.camerax_opencv.util.ProcessImageAnalyzer
import com.momomomo111.camerax_opencv.util.VibrationUtil
import com.momomomo111.camerax_opencv.util.VibrationUtil.effectSlider

class ThresholdFragment : Fragment() {
    private val thresholdViewModel: ThresholdViewModel by viewModels()
    private val args: ThresholdFragmentArgs by navArgs()

    private var _binding: FragmentThresholdBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentThresholdBinding.inflate(inflater, container, false)
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
                thresholdViewModel.params
            )
        )

        val vibrator = VibrationUtil.setVibrator(this.context)

        _binding?.sliderThresh?.addOnChangeListener { _, value, _ ->
            val thresh = value.toDouble()
            thresholdViewModel.onThreshChange(thresh)
            binding.thresh.text = getString(R.string.thresh, thresh.toString())
            if (enableVibration) {
                vibrator.effectSlider()
            }
        }
        _binding?.sliderMaxVal?.addOnChangeListener { _, value, _ ->
            val maxVal = value.toDouble()
            thresholdViewModel.onMaxValChange(maxVal)
            binding.MaxVal.text = getString(R.string.max_val, maxVal.toString())
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
