package com.example.camerax_opencv.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.camerax_opencv.data.ThresholdViewModel
import com.example.camerax_opencv.databinding.FragmentThresholdBinding
import com.example.camerax_opencv.util.CameraUtil
import com.example.camerax_opencv.util.ProcessImageAnalyzer

class ThresholdFragment : Fragment() {
    private val viewModel by lazy { ViewModelProvider(this).get(ThresholdViewModel::class.java) }

    companion object {

        init {
            System.loadLibrary("opencv_java4")
        }
    }

    private var _binding: FragmentThresholdBinding? = null
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentThresholdBinding.inflate(inflater, container, false)
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
                }, binding.previewView,
                viewModel.params
            ), binding.previewView.surfaceProvider
        )

        binding.sliderThresh.addOnChangeListener { _, value, _ ->
            // Responds to when slider's value is changed
            val thresh = value.toDouble()
            viewModel.onThreshChange(thresh)
        }
        binding.sliderMaxVal.addOnChangeListener { _, value, _ ->
            // Responds to when slider's value is changed
            val maxVal = value.toDouble()
            viewModel.onMaxValChange(maxVal)
        }

        return binding.root
    }

    private fun Fragment?.runOnUiThread(action: () -> Unit) {
        this ?: return
        if (!isAdded) return
        activity?.runOnUiThread(action)
    }

}
