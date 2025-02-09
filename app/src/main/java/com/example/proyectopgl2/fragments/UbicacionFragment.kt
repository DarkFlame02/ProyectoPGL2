package com.example.proyectopgl2.fragments

import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.proyectopgl2.R
import com.example.proyectopgl2.databinding.FragmentUbicacionBinding

class UbicacionFragment : Fragment(R.layout.fragment_ubicacion) {
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private var _binding: FragmentUbicacionBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentUbicacionBinding.bind(view)
        // Verifica si los permisos de ubicación están concedidos
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Solicita los permisos de ubicación si no están concedidos
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            // Si los permisos ya están concedidos, obtiene la ubicación
            getLocation()
        }
    }

    private fun getLocation() {
        val locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // Verifica nuevamente los permisos antes de acceder a la ubicación
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Obtiene la última ubicación conocida del proveedor de red
            val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            location?.let {
                // Actualiza la interfaz de usuario con los datos de ubicación
                val lat = it.latitude
                val lon = it.longitude
                binding.tvUbicacion.text = "Lat: $lat, Lon: $lon"
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Si los permisos son concedidos, obtiene la ubicación
                getLocation()
            } else {
                // Si los permisos son denegados, actualiza la interfaz de usuario en consecuencia
                binding.tvUbicacion.text = "Location permissions are not granted"
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}