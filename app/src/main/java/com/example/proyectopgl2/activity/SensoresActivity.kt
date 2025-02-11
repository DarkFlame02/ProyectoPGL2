package com.example.proyectopgl2.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.proyectopgl2.R
import com.example.proyectopgl2.databinding.ActivitySensoresBinding
import com.example.proyectopgl2.fragments.FuenteAlimentacionFragment
import com.example.proyectopgl2.fragments.LuzAmbientalFragment
import com.example.proyectopgl2.fragments.ResolucionFragment
import com.example.proyectopgl2.fragments.UbicacionFragment
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.system.exitProcess

class SensoresActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySensoresBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySensoresBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_info -> {
                    mostrarInformacionApp()
                }
                R.id.nav_logout -> {
                    logout()
                }
                R.id.nav_exit -> {
                    mostrarConfirmacionSalida()
                }
            }
            true
        }

        // Adaptador para manejar los fragmentos en ViewPager2
        val adapter = object : FragmentStateAdapter(this) {
            private val fragments = listOf(
                ResolucionFragment(),
                FuenteAlimentacionFragment(),
                LuzAmbientalFragment(),
                UbicacionFragment()
            )

            override fun getItemCount(): Int = fragments.size

            override fun createFragment(position: Int) = fragments[position]
        }
        // Asigna el adaptador al ViewPager y bindeo con el tablayout
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Resolución"
                1 -> "Fuente de Alimentación"
                2 -> "Luz Ambiental"
                else -> "Ubicación"
            }
        }.attach()
    }

    // Muestra el cuadro de dialogo con informacion de la aplicacion
    private fun mostrarInformacionApp() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.informacion))
        builder.setMessage(getString(R.string.descripcion_app))
        builder.setPositiveButton(getString(R.string.cerrar)) { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }
    // Cierra la sesion del usuario
    private fun logout() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.cerrar_sesion))
        builder.setMessage(getString(R.string.estas_seguro_que_deseas_cerrar_sesion))
        builder.setPositiveButton(getString(R.string.si)) { dialog, _ ->
            startActivity(Intent(this, LoginActivity::class.java))
            Toast.makeText(this, getString(R.string.sesion_cerrada), Toast.LENGTH_SHORT).show()
            finish()
        }
        builder.setNegativeButton(getString(R.string.no)) { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }
    // Muestra el cuadro de dialogo para confirmar la salida de la aplicacion
    private fun mostrarConfirmacionSalida() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.confirmar_salida))
        builder.setMessage(getString(R.string.estas_seguro_que_deseas_salir))
        builder.setPositiveButton(getString(R.string.si)) { dialog, _ ->
            finishAffinity()
            exitProcess(0)
        }
        builder.setNegativeButton(getString(R.string.no)) { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }
}