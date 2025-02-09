package com.example.proyectopgl2.fragments

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.proyectopgl2.R
import com.example.proyectopgl2.databinding.FragmentLuzAmbientalBinding

class LuzAmbientalFragment : Fragment(R.layout.fragment_luz_ambiental) {
    private var _binding: FragmentLuzAmbientalBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLuzAmbientalBinding.bind(view)

        // Obtiene el servicio de sensores del sistema
        val sensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        // Obtiene el sensor de luz
        val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        // Registra un listener para el sensor de luz
        sensorManager.registerListener(object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    // Obtiene el valor de luz del evento
                    val luz = it.values[0]
                    // Determina el estado de la luz basado en el valor
                    val status = when {
                        luz < 100 -> "Oscuro"
                        luz in 100.0..2000.0 -> "Normal"
                        else -> "Brillante"
                    }
                    binding.tvLuz.text = "Luz: $status ($luz lx)"
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }, lightSensor, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}