package com.example.camerax_opencv.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CannyViewModel : ViewModel() {
    private val _params = MutableStateFlow(Params.CannyParams(255.0, 255.0))
    val params: StateFlow<Params.CannyParams> = _params
    val liveParams: LiveData<Params.CannyParams> = _params.asLiveData()

    fun onThreshold1Change(data: Double) {
        _params.value = _params.value.copy(threshold1 = data)
    }

    fun onThreshold2Change(data: Double) {
        _params.value = _params.value.copy(threshold2 = data)
    }
}