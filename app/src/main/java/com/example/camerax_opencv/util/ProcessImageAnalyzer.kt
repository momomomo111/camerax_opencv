package com.example.camerax_opencv.util

import android.graphics.Bitmap
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import com.example.camerax_opencv.data.Params
import kotlinx.coroutines.flow.StateFlow
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

class ProcessImageAnalyzer(
    val runOnUiThread: (Bitmap) -> Unit,
    val previewView: PreviewView?,
    val params: StateFlow<Params>
) : ImageAnalysis.Analyzer {
    override fun analyze(image: ImageProxy) {
        val matOrg: Mat = CameraUtil.getMatFromImage(image)
        val mat: Mat = CameraUtil.fixMatRotation(matOrg, previewView)
        val matMask = Mat(mat.rows(), mat.cols(), 0)
        val matOutput = Mat(mat.rows(), mat.cols(), mat.type())

        when (val params = params.value) {
            is Params.GaussianBlurParams -> {
                Imgproc.GaussianBlur(
                    mat,
                    matOutput,
                    Size(params.kSize, params.kSize),
                    params.sigmaX,
                    params.sigmaY
                )
            }
            is Params.ThresholdParams -> {
                Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY)
                Imgproc.threshold(
                    mat,
                    matOutput,
                    params.thresh,
                    params.maxVal,
                    0
                )
            }
            is Params.CannyParams -> {
                Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY)
                Imgproc.Canny(
                    mat,
                    matOutput,
                    params.threshold1,
                    params.threshold2
                )
            }
            is Params.GrayScaleParams -> {
                Imgproc.cvtColor(mat, matOutput, Imgproc.COLOR_RGB2GRAY)
            }
            is Params.RgbExtractionParams -> {
                val sMin = Scalar(params.lowerR, params.lowerG, params.lowerB)
                val sMax = Scalar(params.upperR, params.upperG, params.upperB)
                Core.inRange(mat, sMin, sMax, matMask)
                Core.bitwise_and(mat, mat, matOutput, matMask)
            }
        }
        val bitmap: Bitmap =
            Bitmap.createBitmap(matOutput.cols(), matOutput.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(matOutput, bitmap)
        runOnUiThread(bitmap)
        image.close()
    }

}