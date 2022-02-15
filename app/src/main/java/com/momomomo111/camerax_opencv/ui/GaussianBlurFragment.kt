package com.momomomo111.camerax_opencv.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.momomomo111.camerax_opencv.R
import com.momomomo111.camerax_opencv.data.GaussianblurViewModel
import com.momomomo111.camerax_opencv.databinding.FragmentGaussianblurBinding
import com.momomomo111.camerax_opencv.util.CameraUtil
import com.momomomo111.camerax_opencv.util.ProcessImageAnalyzer

class GaussianBlurFragment : Fragment() {
    private val viewModel: GaussianblurViewModel by viewModels()

    private var _binding: FragmentGaussianblurBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGaussianblurBinding.inflate(inflater, container, false)
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

        binding.sliderKSize.addOnChangeListener { _, value, _ ->
            val kSize = value.toDouble()
            viewModel.onKSizeChange(kSize)
            binding.kSizeText.text = getString(R.string.k_size, kSize.toString())
        }
        binding.sliderSigmaX.addOnChangeListener { _, value, _ ->
            val sigmaX = value.toDouble()
            viewModel.onSigmaXChange(sigmaX)
            binding.sigmaXText.text = getString(R.string.sigma_x, sigmaX.toString())
        }
        binding.sliderSigmaY.addOnChangeListener { _, value, _ ->
            val sigmaY = value.toDouble()
            viewModel.onSigmaYChange(sigmaY)
            binding.sigmaYText.text = getString(R.string.sigma_y, sigmaY.toString())
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
