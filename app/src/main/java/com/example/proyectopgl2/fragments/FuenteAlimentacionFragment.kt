package com.example.proyectopgl2.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.proyectopgl2.R
import com.example.proyectopgl2.databinding.FragmentFuenteAlimentacionBinding

class FuenteAlimentacionFragment : Fragment(R.layout.fragment_fuente_alimentacion) {
    private var receiver: BroadcastReceiver? = null
    private var isReceiverRegistered = false
    private var _binding: FragmentFuenteAlimentacionBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFuenteAlimentacionBinding.bind(view)

        // Inicializamos el BroadcastReceiver
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (Intent.ACTION_POWER_CONNECTED == action) {
                    Toast.makeText(context, "Fuente de alimentación conectada", Toast.LENGTH_SHORT).show()
                } else if (Intent.ACTION_POWER_DISCONNECTED == action) {
                    Toast.makeText(context, "Fuente de alimentación desconectada", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Botón para activar o desactivar la monitorización de la fuente de alimentación
        binding.btnActivarReceiver.setOnClickListener {
            if (!isReceiverRegistered) {
                val intentFilter = IntentFilter().apply {
                    addAction(Intent.ACTION_POWER_CONNECTED)
                    addAction(Intent.ACTION_POWER_DISCONNECTED)
                }
                // Registramos el receptor solo si no está registrado
                requireContext().registerReceiver(receiver, intentFilter)
                isReceiverRegistered = true
                binding.btnActivarReceiver.text = "Desactivar monitorización"
            } else {
                // Desregistramos el receptor solo si está registrado
                try {
                    requireContext().unregisterReceiver(receiver)
                    isReceiverRegistered = false
                    binding.btnActivarReceiver.text = "Activar monitorización"
                } catch (e: IllegalArgumentException) {
                    // En caso de que el receptor no estuviera registrado, no hacemos nada
                    e.printStackTrace()
                }
            }
        }
    }

    // En el ciclo de vida del fragmento, desregistramos el receptor si está registrado
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        // Desregistramos el receptor solo si está registrado
        if (isReceiverRegistered) {
            try {
                context?.unregisterReceiver(receiver)
            } catch (e: IllegalArgumentException) {
                // Si el receptor no estaba registrado, no hacemos nada
                e.printStackTrace()
            }
        }
    }
}
