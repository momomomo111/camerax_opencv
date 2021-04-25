package com.example.camerax_opencv.util

import android.graphics.Bitmap
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import com.example.camerax_opencv.data.Params
import kotlinx.coroutines.flow.StateFlow
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

class ProcessImageAnalyzer(
    val runOnUiThread: (Bitmap) -> Unit,
    val previewView: PreviewView?,
    val params: StateFlow<Params>
) : ImageAnalysis.Analyzer {
    override fun analyze(image: ImageProxy) {
        /* Create cv::mat(RGB888) from image(NV21) */
        val matOrg: Mat = CameraUtil.getMatFromImage(image)

        /* Fix image rotation (it looks image in PreviewView is automatically fixed by CameraX???) */
        val mat: Mat = CameraUtil.fixMatRotation(matOrg, previewView)

        /* Do some image processing */
        val matOutput = Mat(mat.rows(), mat.cols(), mat.type())
        val params = params.value
        if (params is Params.GaussianBlurParams) {
            Imgproc.GaussianBlur(
                mat,
                matOutput,
                Size(params.kSize, params.kSize),
                params.sigmaX,
                params.sigmaY
            )
        } else if (params is Params.ThresholdParams) {
            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY)
            Imgproc.threshold(
                mat,
                matOutput,
                params.thresh,
                params.maxVal,
                0
            )
        } else if (params is Params.CannyParams) {
            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY)
            Imgproc.Canny(
                mat,
                matOutput,
                params.threshold1,
                params.threshold2
            )
        } else if (params is Params.GrayScaleParams) {
            Imgproc.cvtColor(mat, matOutput, Imgproc.COLOR_RGB2GRAY)
        }
        /* Convert cv::mat to bitmap for drawing */
        val bitmap: Bitmap =
            Bitmap.createBitmap(matOutput.cols(), matOutput.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(matOutput, bitmap)

        /* Display the result onto ImageView */
        runOnUiThread(bitmap)

        /* Close the image otherwise, this function is not called next time */
        image.close()
    }

}