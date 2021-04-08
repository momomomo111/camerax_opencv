package com.example.camerax_opencv.util

import android.content.Context
import android.content.pm.PackageManager
import android.view.Surface
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc
import java.nio.ByteBuffer

private val REQUIRED_PERMISSIONS = arrayOf(
    "android.permission.CAMERA",
    "android.permission.WRITE_EXTERNAL_STORAGE"
)

fun getMatFromImage(image: ImageProxy): Mat {
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

fun fixMatRotation(matOrg: Mat, previewView: PreviewView?): Mat {
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

fun checkPermissions(context: Context?): Boolean {
    for (permission in REQUIRED_PERMISSIONS) {
        if (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    permission
                )
            } != PackageManager.PERMISSION_GRANTED) {
            return false
        }
    }
    return true
}