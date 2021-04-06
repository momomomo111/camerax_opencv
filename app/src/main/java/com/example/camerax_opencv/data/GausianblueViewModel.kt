package com.example.camerax_opencv.data

import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.camerax_opencv.R

class GausianblurViewModel : ViewModel() {
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