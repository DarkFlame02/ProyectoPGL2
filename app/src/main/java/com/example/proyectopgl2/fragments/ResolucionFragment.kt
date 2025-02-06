package com.example.proyectopgl2.fragments

import android.hardware.display.DisplayManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Display
import android.view.View
import androidx.fragment.app.Fragment
import com.example.proyectopgl2.R
import com.example.proyectopgl2.databinding.FragmentResolucionBinding

class ResolucionFragment : Fragment(R.layout.fragment_resolucion) {
    private var _binding: FragmentResolucionBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentResolucionBinding.bind(view)

        val displayMetrics = DisplayMetrics()

        val display = requireContext().getSystemService(DisplayManager::class.java)
            ?.getDisplay(Display.DEFAULT_DISPLAY)

        display?.getMetrics(displayMetrics)

        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        binding.tvResolucion.text = "Resolución: ${width}x${height} px"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}