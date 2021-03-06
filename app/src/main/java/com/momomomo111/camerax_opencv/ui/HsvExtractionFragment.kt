package com.momomomo111.camerax_opencv.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.momomomo111.camerax_opencv.R
import com.momomomo111.camerax_opencv.data.HsvextractionViewModel
import com.momomomo111.camerax_opencv.databinding.FragmentHsvextractionBinding
import com.momomomo111.camerax_opencv.util.CameraUtil
import com.momomomo111.camerax_opencv.util.ProcessImageAnalyzer

class HsvExtractionFragment : Fragment() {
    private val viewModel: HsvextractionViewModel by viewModels()

    private var _binding: FragmentHsvextractionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHsvextractionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CameraUtil.startCamera(
            requireContext(),
            ProcessImageAnalyzer(
                {
                    runOnUiThread {
                        _binding?.imageView?.setImageBitmap(
                            it
                        )
                    }
                },
                viewModel.params
            )
        )

        val sliderH = binding.sliderH
        sliderH.setValues(0.0F, 255.0F)
        sliderH.addOnChangeListener { _, _, _ ->
            val values = sliderH.values
            val upperH = values[1].toDouble()
            val lowerH = values[0].toDouble()
            viewModel.onUpperHChange(upperH)
            viewModel.onLowerHChange(lowerH)
            binding.UpperHText.text = getString(R.string.upper_h, upperH.toString())
            binding.LowerHText.text = getString(R.string.lower_h, lowerH.toString())
        }

        val sliderS = binding.sliderS
        sliderS.setValues(0.0F, 255.0F)
        sliderS.addOnChangeListener { _, _, _ ->
            val values = sliderS.values
            val upperS = values[1].toDouble()
            val lowerS = values[0].toDouble()
            viewModel.onUpperSChange(upperS)
            viewModel.onLowerSChange(lowerS)
            binding.UpperSText.text = getString(R.string.upper_s, upperS.toString())
            binding.LowerSText.text = getString(R.string.lower_s, lowerS.toString())
        }

        val sliderV = binding.sliderV
        sliderV.setValues(0.0F, 255.0F)
        sliderV.addOnChangeListener { _, _, _ ->
            val values = sliderV.values
            val upperV = values[1].toDouble()
            val lowerV = values[0].toDouble()
            viewModel.onUpperVChange(upperV)
            viewModel.onLowerVChange(lowerV)
            binding.UpperVText.text = getString(R.string.upper_v, upperV.toString())
            binding.LowerVText.text = getString(R.string.lower_v, lowerV.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun Fragment.runOnUiThread(action: () -> Unit) {
        if (!isAdded) return
        activity?.runOnUiThread(action)
    }
}
