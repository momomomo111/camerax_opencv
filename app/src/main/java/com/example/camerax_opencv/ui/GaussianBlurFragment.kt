package com.example.camerax_opencv.ui

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.example.camerax_opencv.data.GausianblurViewModel
import com.example.camerax_opencv.databinding.FragmentGaussianblurBinding
import com.example.camerax_opencv.util.checkPermissions
import com.example.camerax_opencv.util.fixMatRotation
import com.example.camerax_opencv.util.getMatFromImage
import com.google.common.util.concurrent.ListenableFuture
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class GaussianBlurFragment : Fragment() {
    private val viewModel by lazy { ViewModelProvider(this).get(GausianblurViewModel::class.java) }

    /*** Views  */
    private var previewView: PreviewView? = null
    private var imageView: ImageView? = null

    /*** For CameraX  */
    private var camera: Camera? = null
    private var preview: Preview? = null
    private var imageAnalysis: ImageAnalysis? = null
    private val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()


    /*** Params */
    private var kSize: Double = 1.0
    private var sigmaX: Double = 0.0
    private var sigmaY: Double = 0.0

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
        previewView = binding.previewView
        imageView = binding.imageView
        val seekBarKSize = binding.seekBarKSize
        val seekBarSigmaX = binding.seekBarSigmaX
        val seekBarSigmaY = binding.seekBarSigmaY
        seekBarKSize.max = 51
        seekBarKSize.min = 1
        seekBarSigmaX.max = 50
        seekBarSigmaX.min = 0
        seekBarSigmaY.max = 50
        seekBarSigmaY.min = 0
        if (checkPermissions(context)) {
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
                kSize = progress.toDouble()
                if (progress.toDouble() % 2 == 0.0) {
                    kSize += 1
                }
                onKSizeChange(kSize)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        seekBarSigmaX.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                sigmaX = progress.toDouble()
                onSigmaXChange(sigmaX)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        seekBarSigmaY.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                sigmaY = progress.toDouble()
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
        val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
            ProcessCameraProvider.getInstance(
                requireContext()
            )
        val context: Context? = context
        cameraProviderFuture.addListener({
            try {
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                preview = Preview.Builder().build()
                imageAnalysis = ImageAnalysis.Builder().build()
                imageAnalysis!!.setAnalyzer(cameraExecutor, MyImageAnalyzer())
                val cameraSelector =
                    CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    (context as LifecycleOwner),
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
                preview!!.setSurfaceProvider(previewView!!.createSurfaceProvider(camera!!.cameraInfo))
            } catch (e: Exception) {
                Log.e("error", "[startCamera] Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(getContext()))
    }

    private inner class MyImageAnalyzer : ImageAnalysis.Analyzer {
        override fun analyze(image: ImageProxy) {
            /* Create cv::mat(RGB888) from image(NV21) */
            val matOrg: Mat = getMatFromImage(image)

            /* Fix image rotation (it looks image in PreviewView is automatically fixed by CameraX???) */
            val mat: Mat = fixMatRotation(matOrg, previewView)

            /* Do some image processing */
            val matOutput = Mat(mat.rows(), mat.cols(), mat.type())
            Imgproc.GaussianBlur(mat, matOutput, Size(kSize, kSize), sigmaX, sigmaY)


            /* Convert cv::mat to bitmap for drawing */
            val bitmap: Bitmap =
                Bitmap.createBitmap(matOutput.cols(), matOutput.rows(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(matOutput, bitmap)

            /* Display the result onto ImageView */
            runOnUiThread { imageView?.setImageBitmap(bitmap) }

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
