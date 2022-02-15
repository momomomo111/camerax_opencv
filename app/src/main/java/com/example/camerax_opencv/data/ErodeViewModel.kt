package com.example.camerax_opencv.data

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ErodeViewModel : ViewModel() {
    private val _params = MutableStateFlow(Params.ErodeParams(1, 1))
    val params: StateFlow<Params.ErodeParams> = _params

    fun onKSizeChange(data: Int) {
        _params.value = _params.value.copy(kSize = data)
    }

    fun onIterationsChange(data: Int) {
        _params.value = _params.value.copy(iterations = data)
    }
}
