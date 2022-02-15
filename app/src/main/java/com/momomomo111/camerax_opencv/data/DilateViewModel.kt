package com.momomomo111.camerax_opencv.data

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DilateViewModel : ViewModel() {
    private val _params = MutableStateFlow(Params.DilateParams(1, 1))
    val params: StateFlow<Params.DilateParams> = _params

    fun onKSizeChange(data: Int) {
        _params.value = _params.value.copy(kSize = data)
    }

    fun onIterationsChange(data: Int) {
        _params.value = _params.value.copy(iterations = data)
    }
}
