package com.momomomo111.camerax_opencv.data

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RgbextractionViewModel : ViewModel() {
    private val _params =
        MutableStateFlow(Params.RgbExtractionParams(255.0, 255.0, 255.0, 0.0, 0.0, 0.0))
    val params: StateFlow<Params.RgbExtractionParams> = _params

    fun onUpperRChange(data: Double) {
        _params.value = _params.value.copy(upperR = data)
    }

    fun onUpperGChange(data: Double) {
        _params.value = _params.value.copy(upperG = data)
    }

    fun onUpperBChange(data: Double) {
        _params.value = _params.value.copy(upperB = data)
    }

    fun onLowerRChange(data: Double) {
        _params.value = _params.value.copy(lowerR = data)
    }

    fun onLowerGChange(data: Double) {
        _params.value = _params.value.copy(lowerG = data)
    }

    fun onLowerBChange(data: Double) {
        _params.value = _params.value.copy(lowerB = data)
    }
}
