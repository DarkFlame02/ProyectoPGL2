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

        // Crea un objeto DisplayMetrics para obtener las métricas de la pantalla
        val displayMetrics = DisplayMetrics()

        // Obtiene el servicio de DisplayManager y el display predeterminado
        val display = requireContext().getSystemService(DisplayManager::class.java)
            ?.getDisplay(Display.DEFAULT_DISPLAY)

        // Obtiene las métricas del display y las almacena en displayMetrics
        display?.getMetrics(displayMetrics)

        // Obtiene el ancho y alto de la pantalla en píxeles
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        binding.tvResolucion.text = "Resolución: ${width}x${height} px"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}