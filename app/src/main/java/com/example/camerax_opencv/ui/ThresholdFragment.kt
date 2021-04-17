package com.example.camerax_opencv.ui

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.example.camerax_opencv.data.ThresholdViewModel
import com.example.camerax_opencv.databinding.FragmentThresholdBinding
import com.example.camerax_opencv.util.CameraUtil
import com.google.common.util.concurrent.ListenableFuture
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import java.util.concurrent.Executors

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
        if (CameraUtil.checkPermissions(requireContext())) {
            startCamera()
        } else {
            ActivityResultContracts.RequestPermission()
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

    private fun startCamera() {
        val context: Context = requireContext()
        val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
            ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build()
                val imageAnalysis = ImageAnalysis.Builder().build()
                imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), MyImageAnalyzer())
                val cameraSelector =
                    CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    (context as LifecycleOwner),
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
                preview.setSurfaceProvider(binding.previewView.surfaceProvider)
            } catch (e: Exception) {
                Log.e("error", "[startCamera] Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    private inner class MyImageAnalyzer : ImageAnalysis.Analyzer {
        override fun analyze(image: ImageProxy) {
            /* Create cv::mat(RGB888) from image(NV21) */
            val matOrg: Mat = CameraUtil.getMatFromImage(image)

            /* Fix image rotation (it looks image in PreviewView is automatically fixed by CameraX???) */
            val mat: Mat = CameraUtil.fixMatRotation(matOrg, binding.previewView)

            /* Do some image processing */
            val matOutput = Mat(mat.rows(), mat.cols(), mat.type())
            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY)
            Imgproc.threshold(
                mat,
                matOutput,
                viewModel.thresh.value ?: 128.0,
                viewModel.maxVal.value ?: 255.0,
                0
            )

            /* Convert cv::mat to bitmap for drawing */
            val bitmap: Bitmap =
                Bitmap.createBitmap(matOutput.cols(), matOutput.rows(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(matOutput, bitmap)

            /* Display the result onto ImageView */
            runOnUiThread { binding.imageView.setImageBitmap(bitmap) }

            /* Close the image otherwise, this function is not called next time */
            image.close()
        }

        private fun Fragment?.runOnUiThread(action: () -> Unit) {
            this ?: return
            if (!isAdded) return
            activity?.runOnUiThread(action)
        }

    }
}
