package com.example.camerax_opencv.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ThresholdViewModel : ViewModel() {
    private val _thresh = MutableLiveData(128.0)
    private val _maxVal = MutableLiveData(255.0)
    val thresh: LiveData<Double> = _thresh
    val maxVal: LiveData<Double> = _maxVal

    fun onThreshChange(data: Double) {
        _thresh.value = data
    }

    fun onMaxValChange(data: Double) {
        _maxVal.value = data
    }
}