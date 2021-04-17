package com.example.camerax_opencv.process

import android.graphics.Bitmap
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import com.example.camerax_opencv.params.Params
import com.example.camerax_opencv.util.CameraUtil
import kotlinx.coroutines.flow.StateFlow
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

class GaussianImageAnalyzer(
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
        if (params is Params.GaussianBlurParams){
        Imgproc.GaussianBlur(
            mat,
            matOutput,
            Size(params.kSize, params.kSize),
            params.sigmaX,
            params.sigmaY
        )} else if (params is Params.ThresholdParams){
            Imgproc.threshold(
                mat,
                matOutput,
                params.thresh,
                params.maxVal,
                0
            )
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