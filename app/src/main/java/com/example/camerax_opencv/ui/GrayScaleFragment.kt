package com.example.camerax_opencv.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.camerax_opencv.data.GrayscaleViewModel
import com.example.camerax_opencv.databinding.FragmentGrayscaleBinding
import com.example.camerax_opencv.util.CameraUtil
import com.example.camerax_opencv.util.ProcessImageAnalyzer

class GrayScaleFragment : Fragment() {
    private val viewModel: GrayscaleViewModel by viewModels()

    companion object {

        init {
            System.loadLibrary("opencv_java4")
        }
    }

    private var _binding: FragmentGrayscaleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGrayscaleBinding.inflate(inflater, container, false)
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

        return binding.root
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
