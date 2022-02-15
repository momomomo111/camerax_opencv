package com.momomomo111.camerax_opencv.data

import androidx.annotation.Nullable

sealed class Params {
    data class GaussianBlurParams(val kSize: Double, val sigmaX: Double, val sigmaY: Double) :
        Params()

    data class ThresholdParams(val thresh: Double, val maxVal: Double) : Params()
    data class CannyParams(val threshold1: Double, val threshold2: Double) : Params()
    data class GrayScaleParams(val param: Nullable?) : Params()
    data class RgbExtractionParams(
        val upperR: Double,
        val upperG: Double,
        val upperB: Double,
        val lowerR: Double,
        val lowerG: Double,
        val lowerB: Double
    ) : Params()
    data class HsvExtractionParams(
        val upperH: Double,
        val upperS: Double,
        val upperV: Double,
        val lowerH: Double,
        val lowerS: Double,
        val lowerV: Double
    ) : Params()
    data class ErodeParams(val kSize: Int, val iterations: Int) :
        Params()
    data class DilateParams(val kSize: Int, val iterations: Int) :
        Params()
}
