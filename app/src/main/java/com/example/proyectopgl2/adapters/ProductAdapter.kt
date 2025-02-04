package com.example.proyectopgl2.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectopgl2.R
import com.example.proyectopgl2.activity.MainActivity
import com.example.proyectopgl2.databinding.ProductCardBinding
import com.example.proyectopgl2.dialogs.ProductoDialog
import com.example.proyectopgl2.models.ProductosRecycler
import com.example.proyectopgl2.room.DatabaseProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ProductAdapter(val items: MutableList<ProductosRecycler>, private val coroutineScope: CoroutineScope) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    private lateinit var ctx: Context
    private lateinit var itemLista: ProductosRecycler

    class ViewHolder(val binding: ProductCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        ctx = viewGroup.context
        val binding = ProductCardBinding.inflate(LayoutInflater.from(ctx), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val item = items[i]
        viewHolder.binding.nombreProducto.text = item.nombre
        viewHolder.binding.cantidadProducto.text = item.cantidad.toString()

        viewHolder.binding.buttonEliminar.setOnClickListener {
            val builder = AlertDialog.Builder(ctx)
            builder.setMessage(ctx.getString(R.string.dialogo_eliminar_mensaje))
                .setTitle(ctx.getString(R.string.dialogo_eliminar_titulo))
                .setIcon(R.mipmap.ic_launcher_round)
                .setPositiveButton(ctx.getString(R.string.btn_positivo_dialogo)) { _, _ ->
                    val db = DatabaseProvider.getDatabase(ctx)
                    val comentarioDao = db.productoDao()
                    itemLista = MainActivity.datos[i]
                    coroutineScope.launch {
                        try {
                            comentarioDao.deleteProductoById(item.id)
                            deleteItem(i)
                        } catch (e: Exception) {
                            Toast.makeText(ctx, ctx.getString(R.string.error_eliminar), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .setNegativeButton(ctx.getString(R.string.btn_negativo_dialogo)) { dialog, _ ->
                    dialog.dismiss()
                }
            val dialogo = builder.create()
            dialogo.show()
        }

        viewHolder.binding.buttonModificar.setOnClickListener {
            val producto = items[i]
            val dialogo = ProductoDialog()
            dialogo.setProducto(producto)
            dialogo.adaptador = this@ProductAdapter
            dialogo.show((ctx as AppCompatActivity).supportFragmentManager, "ProductoDialog")
        }
    }

    // Método para añadir un item al adaptador
    fun addItem(item: ProductosRecycler) {
        items.add(item)
        notifyDataSetChanged()
    }

    // Método para eliminar un item del adaptador
    fun deleteItem(index: Int) {
        items.removeAt(index)
        notifyDataSetChanged()
    }

    // Método para actualizar un item en el adaptador
    fun updateItem(updatedItem: ProductosRecycler) {
        val index = items.indexOfFirst { it.id == updatedItem.id }
        if (index != -1) {
            items[index] = updatedItem
            notifyItemChanged(index)
        }
    }


}