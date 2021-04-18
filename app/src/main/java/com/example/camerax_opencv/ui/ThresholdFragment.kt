package com.example.camerax_opencv.ui

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
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
        val seekBarThresh = binding.seekBarThresh
        val seekBarMaxVal = binding.seekBarMaxVal

        seekBarThresh.max = 255
        seekBarThresh.min = 0
        seekBarThresh.progress = 128
        seekBarMaxVal.max = 255
        seekBarMaxVal.min = 0
        seekBarMaxVal.progress = 255
        val context: Context = requireContext()
        if (CameraUtil.checkPermissions(context)) {
            CameraUtil.startCamera(
                context,
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
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                2000
            )
        }

        fun onThreshChange(data: Double) {
            viewModel.onThreshChange(data)
        }

        fun onMaxValChange(data: Double) {
            viewModel.onMaxValChange(data)
        }

        seekBarThresh.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val thresh = progress.toDouble()
                onThreshChange(thresh)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        seekBarMaxVal.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val maxVal = progress.toDouble()
                onMaxValChange(maxVal)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        return binding.root
    }

    private fun Fragment?.runOnUiThread(action: () -> Unit) {
        this ?: return
        if (!isAdded) return
        activity?.runOnUiThread(action)
    }

}
