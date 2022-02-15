package com.momomomo111.camerax_opencv.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.momomomo111.camerax_opencv.R
import com.momomomo111.camerax_opencv.data.CannyViewModel
import com.momomomo111.camerax_opencv.databinding.FragmentCannyBinding
import com.momomomo111.camerax_opencv.util.CameraUtil
import com.momomomo111.camerax_opencv.util.ProcessImageAnalyzer

class CannyFragment : Fragment() {
    private val viewModel: CannyViewModel by viewModels()

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

        binding.sliderThreshold1.addOnChangeListener { _, value, _ ->
            val thresh = value.toDouble()
            viewModel.onThreshold1Change(thresh)
            binding.threshold1.text = getString(R.string.threshold1, thresh.toString())
        }
        binding.sliderThreshold2.addOnChangeListener { _, value, _ ->
            val maxVal = value.toDouble()
            viewModel.onThreshold2Change(maxVal)
            binding.threshold2.text = getString(R.string.threshold2, maxVal.toString())
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
