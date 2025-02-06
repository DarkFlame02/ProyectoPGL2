package com.example.proyectopgl2.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectopgl2.R
import com.example.proyectopgl2.adapters.ProductAdapter
import com.example.proyectopgl2.databinding.ActivityMainBinding
import com.example.proyectopgl2.dialogs.ProductoDialog
import com.example.proyectopgl2.models.ProductosRecycler
import com.example.proyectopgl2.room.DatabaseProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var adaptadorReciclador: RecyclerView.Adapter<*>
    private lateinit var layManagerReciclador: RecyclerView.LayoutManager

    companion object {
        var datos: MutableList<ProductosRecycler> = mutableListOf()
    }

    lateinit var itemLista: ProductosRecycler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            cargarDatosUsuario(user.email ?: "")
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        binding.listaRecyclerView.setHasFixedSize(true)
        layManagerReciclador = LinearLayoutManager(this)
        binding.listaRecyclerView.layoutManager = layManagerReciclador

        adaptadorReciclador = ProductAdapter(datos, lifecycleScope)
        binding.listaRecyclerView.adapter = adaptadorReciclador

        binding.fab.setOnClickListener {
            val dialogo = ProductoDialog()
            dialogo.adaptador = adaptadorReciclador as ProductAdapter
            dialogo.show(supportFragmentManager, "ProductoDialog")
        }

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
    }

    private fun cargarDatosUsuario(emailUsuario: String) {
        val db = DatabaseProvider.getDatabase(this)
        val productoDao = db.productoDao()
        lifecycleScope.launch {
            datos.clear()
            val productos = productoDao.getProductosByUser(emailUsuario)
            datos.addAll(productos.map { producto ->
                ProductosRecycler(producto.nombreProducto, producto.cantidadProducto, producto.id)
            })
            adaptadorReciclador.notifyDataSetChanged()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.grabadora -> {
                startActivity(Intent(this, GrabadoraActivity::class.java))
                true
            }
            R.id.sensores -> {
                startActivity(Intent(this, SensoresActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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