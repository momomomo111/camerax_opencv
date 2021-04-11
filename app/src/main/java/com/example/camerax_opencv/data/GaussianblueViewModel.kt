package com.example.camerax_opencv.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GaussianblurViewModel : ViewModel() {
    private val _kSize = MutableLiveData(1.0)
    private val _sigmaX = MutableLiveData(0.0)
    private val _sigmaY = MutableLiveData(0.0)
    val kSize: LiveData<Double> = _kSize
    val sigmaX: LiveData<Double> = _sigmaX
    val sigmaY: LiveData<Double> = _sigmaY

    fun onKSizeChange(data: Double) {
        _kSize.value = data
    }

    fun onSigmaXChange(data: Double) {
        _sigmaX.value = data
    }

    fun onSigmaYChange(data: Double) {
        _sigmaY.value = data
    }
}