package com.example.camerax_opencv.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.camerax_opencv.R
import com.example.camerax_opencv.data.ErodeViewModel
import com.example.camerax_opencv.databinding.FragmentErodeBinding
import com.example.camerax_opencv.util.CameraUtil
import com.example.camerax_opencv.util.ProcessImageAnalyzer

class ErodeFragment : Fragment() {
    private val viewModel: ErodeViewModel by viewModels()

    companion object {

        init {
            System.loadLibrary("opencv_java4")
        }
    }

    private var _binding: FragmentErodeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentErodeBinding.inflate(inflater, container, false)
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
            val kSize = value.toInt()
            viewModel.onKSizeChange(kSize)
            binding.kSizeText.text = getString(R.string.k_size, kSize.toString())
        }
        binding.sliderIterations.addOnChangeListener { _, value, _ ->
            val iterations = value.toInt()
            viewModel.onIterationsChange(iterations)
            binding.iterationsText.text = getString(R.string.iterations, iterations.toString())
        }

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
