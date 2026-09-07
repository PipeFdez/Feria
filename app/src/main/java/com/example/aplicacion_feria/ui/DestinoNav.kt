package com.example.aplicacion_feria.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class DestinoNav(val ruta: String, val titulo: String, val icono: ImageVector) {
    object Compra : DestinoNav("compra", "Comprar", Icons.Default.ShoppingCart)
    object Historial : DestinoNav("historial", "Historial", Icons.Default.History)
}