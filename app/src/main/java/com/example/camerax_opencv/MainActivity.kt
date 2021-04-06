package com.example.camerax_opencv

import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.camerax_opencv.data.GausianblurViewModel
import com.example.camerax_opencv.databinding.ActivityMainBinding

import com.google.common.util.concurrent.ListenableFuture

import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc

import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {
    private val viewModel by lazy { ViewModelProvider(this).get(GausianblurViewModel::class.java) }
    private val REQUEST_CODE_FOR_PERMISSIONS = 1234
    private val REQUIRED_PERMISSIONS = arrayOf("android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE")

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
        /*** Fixed values  */
        private const val TAG = "MyApp"

        init {
            System.loadLibrary("opencv_java4")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding : ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
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
        if (checkPermissions()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_FOR_PERMISSIONS)
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

            // 値が変更された時に呼ばれる
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                kSize = progress.toDouble()
                if (progress.toDouble() % 2 == 0.0) {
                    kSize += 1
                }
                onKSizeChange(kSize)
            }

            // つまみがタッチされた時に呼ばれる
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            // つまみが離された時に呼ばれる
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        seekBarSigmaX.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            // 値が変更された時に呼ばれる
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                sigmaX = progress.toDouble()
                onSigmaXChange(sigmaX)
            }

            // つまみがタッチされた時に呼ばれる
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            // つまみが離された時に呼ばれる
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        seekBarSigmaY.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            // 値が変更された時に呼ばれる
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                sigmaY = progress.toDouble()
                onSigmaYChange(sigmaY)
            }

            // つまみがタッチされた時に呼ばれる
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            // つまみが離された時に呼ばれる
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    private fun startCamera() {
        val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> = ProcessCameraProvider.getInstance(this)
        val context: Context = this
        cameraProviderFuture.addListener(Runnable {
            try {
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                preview = Preview.Builder().build()
                imageAnalysis = ImageAnalysis.Builder().build()
                imageAnalysis!!.setAnalyzer(cameraExecutor, MyImageAnalyzer())
                val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle((context as LifecycleOwner), cameraSelector, preview, imageAnalysis)
                preview!!.setSurfaceProvider(previewView!!.createSurfaceProvider(camera!!.getCameraInfo()))
            } catch (e: Exception) {
                Log.e(TAG, "[startCamera] Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private inner class MyImageAnalyzer : ImageAnalysis.Analyzer {
        override fun analyze(image: ImageProxy) {
            /* Create cv::mat(RGB888) from image(NV21) */
            val matOrg: Mat = getMatFromImage(image)

            /* Fix image rotation (it looks image in PreviewView is automatically fixed by CameraX???) */
            val mat: Mat = fixMatRotation(matOrg)
            Log.i(TAG, "[analyze] width = " + image.width + ", height = " + image.height + "Rotation = " + previewView!!.display.rotation)
            Log.i(TAG, "[analyze] mat width = " + matOrg.cols() + ", mat height = " + matOrg.rows())

            /* Do some image processing */
            val matOutput = Mat(mat.rows(), mat.cols(), mat.type())
            Imgproc.GaussianBlur(mat, matOutput, Size(kSize, kSize), sigmaX, sigmaY)


            /* Convert cv::mat to bitmap for drawing */
            val bitmap: Bitmap = Bitmap.createBitmap(matOutput.cols(), matOutput.rows(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(matOutput, bitmap)

            /* Display the result onto ImageView */runOnUiThread { imageView?.setImageBitmap(bitmap) }

            /* Close the image otherwise, this function is not called next time */
            image.close()
        }

        private fun getMatFromImage(image: ImageProxy): Mat {
            /* https://stackoverflow.com/questions/30510928/convert-android-camera2-api-yuv-420-888-to-rgb */
            val yBuffer: ByteBuffer = image.planes[0].buffer
            val uBuffer: ByteBuffer = image.planes[1].buffer
            val vBuffer: ByteBuffer = image.planes[2].buffer
            val ySize: Int = yBuffer.remaining()
            val uSize: Int = uBuffer.remaining()
            val vSize: Int = vBuffer.remaining()
            val nv21 = ByteArray(ySize + uSize + vSize)
            yBuffer.get(nv21, 0, ySize)
            vBuffer.get(nv21, ySize, vSize)
            uBuffer.get(nv21, ySize + vSize, uSize)
            val yuv = Mat(image.height + image.height / 2, image.width, CvType.CV_8UC1)
            yuv.put(0, 0, nv21)
            val mat = Mat()
            Imgproc.cvtColor(yuv, mat, Imgproc.COLOR_YUV2RGB_NV21, 3)
            return mat
        }

        private fun fixMatRotation(matOrg: Mat): Mat {
            val mat: Mat
            when (previewView!!.display.rotation) {
                Surface.ROTATION_0 -> {
                    mat = Mat(matOrg.cols(), matOrg.rows(), matOrg.type())
                    Core.transpose(matOrg, mat)
                    Core.flip(mat, mat, 1)
                }
                Surface.ROTATION_90 -> mat = matOrg
                Surface.ROTATION_270 -> {
                    mat = matOrg
                    Core.flip(mat, mat, -1)
                }
                else -> {
                    mat = Mat(matOrg.cols(), matOrg.rows(), matOrg.type())
                    Core.transpose(matOrg, mat)
                    Core.flip(mat, mat, 1)
                }
            }
            return mat
        }
    }

    private fun checkPermissions(): Boolean {
        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_FOR_PERMISSIONS) {
            if (checkPermissions()) {
                startCamera()
            } else {
                Log.i(TAG, "[onRequestPermissionsResult] Failed to get permissions")
                finish()
            }
        }
    }
}