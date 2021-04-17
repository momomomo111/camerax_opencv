package com.example.camerax_opencv.ui

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.camerax_opencv.data.GaussianblurViewModel
import com.example.camerax_opencv.databinding.FragmentGaussianblurBinding
import com.example.camerax_opencv.params.Params
import com.example.camerax_opencv.process.GaussianImageAnalyzer
import com.example.camerax_opencv.util.CameraUtil
import com.example.camerax_opencv.util.CameraUtil.startCamera
import kotlinx.coroutines.flow.StateFlow
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

class GaussianBlurFragment : Fragment() {
    private val viewModel by lazy { ViewModelProvider(this).get(GaussianblurViewModel::class.java) }

    companion object {

        init {
            System.loadLibrary("opencv_java4")
        }
    }

    private var _binding: FragmentGaussianblurBinding? = null
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGaussianblurBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        val seekBarKSize = binding.seekBarKSize
        val seekBarSigmaX = binding.seekBarSigmaX
        val seekBarSigmaY = binding.seekBarSigmaY
        seekBarKSize.max = 51
        seekBarKSize.min = 1
        seekBarSigmaX.max = 50
        seekBarSigmaX.min = 0
        seekBarSigmaY.max = 50
        seekBarSigmaY.min = 0
        val context: Context = requireContext()
        if (CameraUtil.checkPermissions(context)) {
            startCamera(
                context,
                GaussianImageAnalyzer({
                    runOnUiThread({
                        binding.imageView.setImageBitmap(
                            it
                        )
                    })
                }, binding.previewView,
                    viewModel.params
                ),binding.previewView.surfaceProvider)
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                2000
            )
        }

        fun onKSizeChange(data: Double) {
            viewModel.onKSizeChange(data)
        }

        fun onSigmaXChange(data: Double) {
            viewModel.onSigmaXChange(data)
        }

        fun onSigmaYChange(data: Double) {
            viewModel.onSigmaYChange(data)
        }
        seekBarKSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val kSize = progress.toDouble()
                if (kSize % 2 != 0.0) {
                    onKSizeChange(kSize)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        seekBarSigmaX.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val sigmaX = progress.toDouble()
                onSigmaXChange(sigmaX)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        seekBarSigmaY.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val sigmaY = progress.toDouble()
                onSigmaYChange(sigmaY)
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