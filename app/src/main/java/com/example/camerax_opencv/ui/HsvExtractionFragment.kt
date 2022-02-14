package com.example.camerax_opencv.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.camerax_opencv.R
import com.example.camerax_opencv.data.HsvextractionViewModel
import com.example.camerax_opencv.databinding.FragmentHsvextractionBinding
import com.example.camerax_opencv.util.CameraUtil
import com.example.camerax_opencv.util.ProcessImageAnalyzer

class HsvExtractionFragment : Fragment() {
    private val viewModel: HsvextractionViewModel by viewModels()

    companion object {

        init {
            System.loadLibrary("opencv_java4")
        }
    }

    private var _binding: FragmentHsvextractionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHsvextractionBinding.inflate(inflater, container, false)
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

        val sliderH = binding.sliderH
        sliderH.setValues(0.0F, 255.0F)
        sliderH.addOnChangeListener { _, _, _ ->
            val values = sliderH.values
            val upperH = values[1].toDouble()
            val lowerH = values[0].toDouble()
            viewModel.onUpperHChange(upperH)
            viewModel.onLowerHChange(lowerH)
            binding.UpperHText.text = getString(R.string.upper_h, upperH.toString())
            binding.LowerHText.text = getString(R.string.lower_h, lowerH.toString())
        }

        val sliderS = binding.sliderS
        sliderS.setValues(0.0F, 255.0F)
        sliderS.addOnChangeListener { _, _, _ ->
            val values = sliderS.values
            val upperS = values[1].toDouble()
            val lowerS = values[0].toDouble()
            viewModel.onUpperSChange(upperS)
            viewModel.onLowerSChange(lowerS)
            binding.UpperSText.text = getString(R.string.upper_s, upperS.toString())
            binding.LowerSText.text = getString(R.string.lower_s, lowerS.toString())
        }

        val sliderV = binding.sliderV
        sliderV.setValues(0.0F, 255.0F)
        sliderV.addOnChangeListener { _, _, _ ->
            val values = sliderV.values
            val upperV = values[1].toDouble()
            val lowerV = values[0].toDouble()
            viewModel.onUpperVChange(upperV)
            viewModel.onLowerVChange(lowerV)
            binding.UpperVText.text = getString(R.string.upper_v, upperV.toString())
            binding.LowerVText.text = getString(R.string.lower_v, lowerV.toString())
        }

        return binding.root
    }

    private fun Fragment?.runOnUiThread(action: () -> Unit) {
        this ?: return
        if (!isAdded) return
        activity?.runOnUiThread(action)
    }
}
