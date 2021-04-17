package com.example.camerax_opencv.params

import kotlinx.coroutines.flow.StateFlow

sealed class Params {
    data class GaussianBlurParams(val kSize: Double, val sigmaX: Double, val sigmaY: Double) : Params()
    data class ThresholdParams(val thresh: Double, val maxVal: Double) : Params()
}


