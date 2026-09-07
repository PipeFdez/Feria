package com.example.aplicacion_feria.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aplicacion_feria.data.local.entities.ProductoEntity
import com.example.aplicacion_feria.data.local.entities.TipoVenta
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompraScreen(viewModel: CompraViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val productos by viewModel.productos.collectAsState()
    var productoSeleccionado by remember { mutableStateOf<ProductoEntity?>(null) }

    val clpFormat = remember {
        NumberFormat.getCurrencyInstance(Locale("es", "CL")).apply {
            maximumFractionDigits = 0
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro Feria", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Selector de productos del catálogo
            Text("Productos frecuentes:", fontWeight = FontWeight.SemiBold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(productos) { producto ->
                    FilterChip(
                        selected = productoSeleccionado?.id == producto.id,
                        onClick = { productoSeleccionado = producto },
                        label = { Text("${producto.nombre} (${clpFormat.format(producto.precioBase)})") },
                        leadingIcon = { Icon(Icons.Default.ShoppingBag, contentDescription = null) }
                    )
                }
            }

            // 2. Botones de acción rápida según producto seleccionado
            productoSeleccionado?.let { prod ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Agregar ${prod.nombre}:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (prod.tipoVenta == TipoVenta.PESO) {
                                Button(onClick = { viewModel.agregarItem(prod, 0.25) }) { Text("1/4 kg") }
                                Button(onClick = { viewModel.agregarItem(prod, 0.5) }) { Text("1/2 kg") }
                                Button(onClick = { viewModel.agregarItem(prod, 1.0) }) { Text("1 kg") }
                                Button(onClick = { viewModel.agregarItem(prod, 2.0) }) { Text("2 kg") }
                            } else {
                                Button(onClick = { viewModel.agregarItem(prod, 1.0) }) { Text("1 un") }
                                Button(onClick = { viewModel.agregarItem(prod, 2.0) }) { Text("2 un") }
                                Button(onClick = { viewModel.agregarItem(prod, 3.0) }) { Text("3 un") }
                            }
                        }
                    }
                }
            }

            // 3. Lista de canasta actual
            Text("Canasta (${uiState.carrito.size} items):", fontWeight = FontWeight.SemiBold)
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                itemsIndexed(uiState.carrito) { index, item ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(item.producto.nombre, fontWeight = FontWeight.Bold)
                                val cantidadTexto = if (item.producto.tipoVenta == TipoVenta.PESO) {
                                    "${item.cantidad} kg"
                                } else {
                                    "${item.cantidad.toInt()} un"
                                }
                                Text("$cantidadTexto - ${clpFormat.format(item.subtotal)}")
                            }
                            IconButton(onClick = { viewModel.removerItem(index) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                            }
                        }
                    }
                }
            }

            // 4. Panel de Totales, Pago y Vuelto
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (uiState.total > uiState.presupuestoMaximo) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.secondaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total a pagar:", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(
                            clpFormat.format(uiState.total),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (uiState.total > uiState.presupuestoMaximo) Color.Red else Color.Unspecified
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Pagar con billete:", fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(5000, 10000, 20000).forEach { billete ->
                            OutlinedButton(onClick = { viewModel.establecerPago(billete) }) {
                                Text(clpFormat.format(billete))
                            }
                        }
                    }

                    if (uiState.montoPagado > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Tu vuelto:", fontWeight = FontWeight.SemiBold)
                            Text(
                                clpFormat.format(uiState.vuelto),
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.vuelto < 0) Color.Red else Color(0xFF2E7D32)
                            )
                        }
                    }
                }
            }

            // 5. Botón Guardar Compra
            Button(
                onClick = { viewModel.guardarCompraFinal {} },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.carrito.isNotEmpty()
            ) {
                Text("Finalizar y Guardar Compra")
            }
        }
    }
}