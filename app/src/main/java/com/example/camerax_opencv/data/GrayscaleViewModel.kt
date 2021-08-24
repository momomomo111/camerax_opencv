package com.example.camerax_opencv.data

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GrayscaleViewModel : ViewModel() {
    private val _params = MutableStateFlow(Params.GrayScaleParams(null))
    val params: StateFlow<Params.GrayScaleParams> = _params
}
