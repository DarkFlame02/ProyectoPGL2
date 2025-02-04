package com.example.proyectopgl2.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class Producto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombreProducto: String,
    val cantidadProducto: Int,
    val usuarioEmail: String
)
