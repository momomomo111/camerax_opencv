package com.example.camerax_opencv.util

import android.widget.TextView
import androidx.databinding.BindingAdapter


@BindingAdapter("android:text_link_ksize")
fun setKSizeText(view: TextView, text: CharSequence?) {
    // Some checks removed for clarity
    view.text = "Ksize: $text"
}

@BindingAdapter("android:text_link_sigmaX")
fun setSigmaXText(view: TextView, text: CharSequence?) {
    // Some checks removed for clarity
    view.text = "SigmaX: $text"
}

@BindingAdapter("android:text_link_sigmaY")
fun setSigmaYText(view: TextView, text: CharSequence?) {
    // Some checks removed for clarity
    view.text = "SigmaY: $text"
}