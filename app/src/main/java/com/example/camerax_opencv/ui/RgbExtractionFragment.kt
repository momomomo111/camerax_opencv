package com.example.camerax_opencv.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.camerax_opencv.data.RgbextractionViewModel
import com.example.camerax_opencv.databinding.FragmentRgbextractionBinding
import com.example.camerax_opencv.util.CameraUtil
import com.example.camerax_opencv.util.ProcessImageAnalyzer

class RgbExtractionFragment : Fragment() {
    private val viewModel by lazy { ViewModelProvider(this).get(RgbextractionViewModel::class.java) }

    companion object {

        init {
            System.loadLibrary("opencv_java4")
        }
    }

    private var _binding: FragmentRgbextractionBinding? = null
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRgbextractionBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
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

        val sliderR = binding.sliderR
        sliderR.setValues(0.0F, 255.0F)
        sliderR.addOnChangeListener { _, _, _ ->
            val values = sliderR.values
            val upperR = values[1].toDouble()
            val lowerR = values[0].toDouble()
            viewModel.onUpperRChange(upperR)
            viewModel.onLowerRChange(lowerR)
        }

        val sliderG = binding.sliderG
        sliderG.setValues(0.0F, 255.0F)
        sliderG.addOnChangeListener { _, _, _ ->
            val values = sliderG.values
            val upperG = values[1].toDouble()
            val lowerG = values[0].toDouble()
            viewModel.onUpperGChange(upperG)
            viewModel.onLowerGChange(lowerG)
        }

        val sliderB = binding.sliderB
        sliderB.setValues(0.0F, 255.0F)
        sliderB.addOnChangeListener { _, _, _ ->
            val values = sliderB.values
            val upperB = values[1].toDouble()
            val lowerB = values[0].toDouble()
            viewModel.onUpperBChange(upperB)
            viewModel.onLowerBChange(lowerB)
        }

        return binding.root
    }

    private fun Fragment?.runOnUiThread(action: () -> Unit) {
        this ?: return
        if (!isAdded) return
        activity?.runOnUiThread(action)
    }
}
