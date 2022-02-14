package com.example.camerax_opencv.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.camerax_opencv.data.GaussianblurViewModel
import com.example.camerax_opencv.databinding.FragmentGaussianblurBinding
import com.example.camerax_opencv.util.CameraUtil
import com.example.camerax_opencv.util.ProcessImageAnalyzer

class GaussianBlurFragment : Fragment() {
    private val viewModel: GaussianblurViewModel by viewModels()

    companion object {

        init {
            System.loadLibrary("opencv_java4")
        }
    }

    private var _binding: FragmentGaussianblurBinding? = null
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGaussianblurBinding.inflate(inflater, container, false)
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

        binding.sliderKSize.addOnChangeListener { _, value, _ ->
            // Responds to when slider's value is changed
            val kSize = value.toDouble()
            viewModel.onKSizeChange(kSize)
        }

        binding.sliderSigmaX.addOnChangeListener { _, value, _ ->
            // Responds to when slider's value is changed
            val sigmaX = value.toDouble()
            viewModel.onSigmaXChange(sigmaX)
        }

        binding.sliderSigmaY.addOnChangeListener { _, value, _ ->
            // Responds to when slider's value is changed
            val sigmaY = value.toDouble()
            viewModel.onSigmaYChange(sigmaY)
        }

        return binding.root
    }

    private fun Fragment?.runOnUiThread(action: () -> Unit) {
        this ?: return
        if (!isAdded) return
        activity?.runOnUiThread(action)
    }
}
