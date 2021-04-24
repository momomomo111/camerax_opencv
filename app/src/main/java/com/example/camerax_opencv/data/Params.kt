package com.example.camerax_opencv.data

sealed class Params {
    data class GaussianBlurParams(val kSize: Double, val sigmaX: Double, val sigmaY: Double) :
        Params()

    data class ThresholdParams(val thresh: Double, val maxVal: Double) : Params()
    data class CannyParams(val threshold1: Double, val threshold2: Double) : Params()
}