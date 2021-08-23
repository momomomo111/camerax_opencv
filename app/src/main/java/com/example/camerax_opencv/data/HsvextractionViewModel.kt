package com.example.camerax_opencv.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HsvextractionViewModel : ViewModel() {
    private val _params =
        MutableStateFlow(Params.HsvExtractionParams(255.0, 255.0, 255.0, 0.0, 0.0, 0.0))
    val params: StateFlow<Params.HsvExtractionParams> = _params
    val liveParams: LiveData<Params.HsvExtractionParams> = _params.asLiveData()

    fun onUpperHChange(data: Double) {
        _params.value = _params.value.copy(upperH = data)
    }

    fun onUpperSChange(data: Double) {
        _params.value = _params.value.copy(upperS = data)
    }

    fun onUpperVChange(data: Double) {
        _params.value = _params.value.copy(upperV = data)
    }

    fun onLowerHChange(data: Double) {
        _params.value = _params.value.copy(lowerH = data)
    }

    fun onLowerSChange(data: Double) {
        _params.value = _params.value.copy(lowerS = data)
    }

    fun onLowerVChange(data: Double) {
        _params.value = _params.value.copy(lowerV = data)
    }

}