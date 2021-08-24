package com.example.camerax_opencv.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ThresholdViewModel : ViewModel() {
    private val _params = MutableStateFlow(Params.ThresholdParams(128.0, 255.0))
    val params: StateFlow<Params.ThresholdParams> = _params
    val liveParams: LiveData<Params.ThresholdParams> = _params.asLiveData()

    fun onThreshChange(data: Double) {
        _params.value = _params.value.copy(thresh = data)
    }

    fun onMaxValChange(data: Double) {
        _params.value = _params.value.copy(maxVal = data)
    }
}
