package com.momomomo111.camerax_opencv.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object VibrationUtil {
    @SuppressLint("WrongConstant")
    fun setVibrator(context: Context?): Vibrator {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context?.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        return vibrator
    }

    fun effectSlide(vibrator: Vibrator) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.vibrate(effect)
        } else {
            vibrator.vibrate(10)
        }
    }
}
