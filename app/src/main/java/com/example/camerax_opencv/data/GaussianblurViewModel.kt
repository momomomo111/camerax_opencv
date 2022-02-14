package com.example.camerax_opencv.data

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GaussianblurViewModel : ViewModel() {
    private val _params = MutableStateFlow(Params.GaussianBlurParams(1.0, 0.0, 0.0))
    val params: StateFlow<Params.GaussianBlurParams> = _params

    fun onKSizeChange(data: Double) {
        _params.value = _params.value.copy(kSize = data)
    }

    fun onSigmaXChange(data: Double) {
        _params.value = _params.value.copy(sigmaX = data)
    }

    fun onSigmaYChange(data: Double) {
        _params.value = _params.value.copy(sigmaY = data)
    }
}
