package com.example.camerax_opencv.util

import android.widget.TextView
import androidx.annotation.StringRes
import androidx.databinding.BindingAdapter


@BindingAdapter("set_param", "set_type")
fun setParamText(view: TextView, text: String?, @StringRes id: Int) {
    // Some checks removed for clarity
    view.text = view.context.getString(id, text)
}

@BindingAdapter("set_param", "set_type")
fun setParamText(view: TextView, text: Double?, @StringRes id: Int) {
    // Some checks removed for clarity
    view.text = view.context.getString(id, text.toString())
}