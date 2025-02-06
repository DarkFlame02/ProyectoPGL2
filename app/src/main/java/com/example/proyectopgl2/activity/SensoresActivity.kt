package com.example.proyectopgl2.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.proyectopgl2.databinding.ActivitySensoresBinding
import com.example.proyectopgl2.fragments.FuenteAlimentacionFragment
import com.example.proyectopgl2.fragments.LuzAmbientalFragment
import com.example.proyectopgl2.fragments.ResolucionFragment
import com.example.proyectopgl2.fragments.UbicacionFragment
import com.google.android.material.tabs.TabLayoutMediator

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
}