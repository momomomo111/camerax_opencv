package com.example.camerax_opencv.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.camerax_opencv.data.DilateViewModel
import com.example.camerax_opencv.databinding.FragmentDilateBinding
import com.example.camerax_opencv.util.CameraUtil
import com.example.camerax_opencv.util.ProcessImageAnalyzer

class DilateFragment : Fragment() {
    private val viewModel by lazy { ViewModelProvider(this).get(DilateViewModel::class.java) }

    companion object {

        init {
            System.loadLibrary("opencv_java4")
        }
    }

    private var _binding: FragmentDilateBinding? = null
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDilateBinding.inflate(inflater, container, false)
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
            val kSize = value.toInt()
            viewModel.onKSizeChange(kSize)
        }

        binding.sliderIterations.addOnChangeListener { _, value, _ ->
            // Responds to when slider's value is changed
            val iterations = value.toInt()
            viewModel.onIterationsChange(iterations)
        }

        return binding.root
    }

    private fun Fragment?.runOnUiThread(action: () -> Unit) {
        this ?: return
        if (!isAdded) return
        activity?.runOnUiThread(action)
    }
}
