package com.example.proyectopgl2.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ProductoDao {
    @Query("SELECT * FROM productos WHERE usuarioEmail = :email")
    suspend fun getProductosByUser(email: String): List<Producto>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducto(producto: Producto): Long

    @Update
    suspend fun updateProducto(producto: Producto)

    @Query("DELETE FROM productos WHERE id = :id")
    suspend fun deleteProductoById(id: Int)
}

