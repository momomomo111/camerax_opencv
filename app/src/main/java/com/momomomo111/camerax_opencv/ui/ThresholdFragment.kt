package com.momomomo111.camerax_opencv.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.momomomo111.camerax_opencv.R
import com.momomomo111.camerax_opencv.data.ThresholdViewModel
import com.momomomo111.camerax_opencv.databinding.FragmentThresholdBinding
import com.momomomo111.camerax_opencv.util.CameraUtil
import com.momomomo111.camerax_opencv.util.ProcessImageAnalyzer

class ThresholdFragment : Fragment() {
    private val viewModel: ThresholdViewModel by viewModels()

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
                viewModel.params
            ),
            binding.previewView.surfaceProvider
        )

        binding.sliderThresh.addOnChangeListener { _, value, _ ->
            val thresh = value.toDouble()
            viewModel.onThreshChange(thresh)
            binding.thresh.text = getString(R.string.thresh, thresh.toString())
        }
        binding.sliderMaxVal.addOnChangeListener { _, value, _ ->
            val maxVal = value.toDouble()
            viewModel.onMaxValChange(maxVal)
            binding.MaxVal.text = getString(R.string.max_val, maxVal.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun Fragment?.runOnUiThread(action: () -> Unit) {
        this ?: return
        if (!isAdded || !isResumed) return
        activity?.runOnUiThread(action)
    }
}
