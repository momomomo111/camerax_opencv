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
import com.example.camerax_opencv.data.GaussianblurViewModel
import com.example.camerax_opencv.databinding.FragmentGaussianblurBinding
import com.example.camerax_opencv.util.CameraUtil
import com.google.common.util.concurrent.ListenableFuture
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import java.util.concurrent.Executors

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
        if (CameraUtil.checkPermissions(requireContext())) {
            startCamera()
        } else {
            ActivityResultContracts.RequestPermission()
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
                val camera = cameraProvider.bindToLifecycle(
                    (context as LifecycleOwner),
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
                preview.setSurfaceProvider(binding.previewView.createSurfaceProvider(camera.cameraInfo))
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
            Imgproc.GaussianBlur(
                mat,
                matOutput,
                Size(viewModel.kSize.value ?: 1.0, viewModel.kSize.value ?: 1.0),
                viewModel.sigmaX.value ?: 0.0,
                viewModel.sigmaY.value ?: 0.0
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