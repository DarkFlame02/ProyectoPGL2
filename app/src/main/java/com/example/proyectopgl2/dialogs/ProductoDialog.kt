package com.example.proyectopgl2.dialogs

import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.proyectopgl2.R
import com.example.proyectopgl2.adapters.ProductAdapter
import com.example.proyectopgl2.databinding.NuevoProductoDialogBinding
import com.example.proyectopgl2.models.ProductosRecycler
import com.example.proyectopgl2.room.DatabaseProvider
import com.example.proyectopgl2.room.Producto
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductoDialog : DialogFragment() {

    private lateinit var binding: NuevoProductoDialogBinding
    lateinit var adaptador: ProductAdapter

    private var productoParaEditar: ProductosRecycler? = null  // Variable para almacenar el producto a editar

    // Método para pasar un producto a editar
    fun setProducto(producto: ProductosRecycler) {
        this.productoParaEditar = producto
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())

        // Inflamos la vista del diálogo usando ViewBinding
        binding = NuevoProductoDialogBinding.inflate(layoutInflater)
        val view = binding.root

        // Si hay un producto para editar, cargar sus datos en los campos
        productoParaEditar?.let {
            binding.nombreProd.setText(it.nombre)
            binding.cantidadProd.setText(it.cantidad.toString())
        }

        // Seteamos diálogo
        builder.setTitle(getString(R.string.dialogo_nuevo_producto))
            .setIcon(R.mipmap.ic_launcher_round)
            .setView(view)
            .setCancelable(false)
            .setPositiveButton(R.string.btn_positivo_dialogo) { _, _ ->
                val user = FirebaseAuth.getInstance().currentUser
                if (user == null) {
                    Toast.makeText(activity, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val emailUsuario = user.email ?: ""
                val db = DatabaseProvider.getDatabase(requireContext())
                val productoDao = db.productoDao()
                val nombreText = binding.nombreProd.text.toString()
                val cantidadText = binding.cantidadProd.text.toString()

                if (nombreText.isEmpty() || cantidadText.isEmpty()) {
                    Toast.makeText(activity, getString(R.string.txt_campos_oblig), Toast.LENGTH_SHORT).show()
                } else {
                    val cantidad = cantidadText.toIntOrNull()
                    if (cantidad == null) {
                        Toast.makeText(activity, getString(R.string.error_cantidad_no_valida), Toast.LENGTH_SHORT).show()
                    } else {
                        lifecycleScope.launch {
                            if (productoParaEditar == null) {
                                val id = productoDao.insertProducto(
                                    Producto(nombreProducto = nombreText, cantidadProducto = cantidad, usuarioEmail = emailUsuario)
                                )
                                val nuevoProducto = ProductosRecycler(nombreText, cantidad, id.toInt())

                                withContext(Dispatchers.Main) {
                                    adaptador.addItem(nuevoProducto)
                                }
                            } else {
                                val productoEditado = Producto(
                                    id = productoParaEditar!!.id,
                                    nombreProducto = nombreText,
                                    cantidadProducto = cantidad,
                                    usuarioEmail = emailUsuario
                                )
                                productoDao.updateProducto(productoEditado)

                                val productoActualizado = ProductosRecycler(nombreText, cantidad, productoParaEditar!!.id)
                                withContext(Dispatchers.Main) {
                                    adaptador.updateItem(productoActualizado)
                                }
                            }
                        }
                    }
                }
            }
            .setNegativeButton(R.string.btn_negativo_dialogo) { dialog, _ ->
                dialog.cancel()
            }

        return builder.create()
    }
}

