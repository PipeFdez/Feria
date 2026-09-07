package com.example.aplicacion_feria.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.Close
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.aplicacion_feria.data.local.entities.ProductoEntity
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompraScreen(viewModel: CompraViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val productos by viewModel.productos.collectAsState()

    var productoSeleccionado by remember { mutableStateOf<ProductoEntity?>(null) }
    var porcionBaseSeleccionada by remember { mutableStateOf(1.0) } // 0.25, 0.5 o 1.0
    var precioIngresado by remember { mutableStateOf("") }
    var cantidadPorciones by remember { mutableStateOf(1) }

    val clpFormat = remember {
        NumberFormat.getCurrencyInstance(Locale("es", "CL")).apply {
            maximumFractionDigits = 0
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Feria: Compras y Precios", fontWeight = FontWeight.Bold) },
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
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Panel de Selección y Registro Paso a Paso
            productoSeleccionado?.let { prod ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Cabecera con foto, nombre y BOTÓN DE CANCELAR (X)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (prod.imagenUri != null) {
                                AsyncImage(
                                    model = prod.imagenUri,
                                    contentDescription = prod.nombre,
                                    modifier = Modifier.size(44.dp).clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.LocalMall, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(prod.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Paso 1: Elige porción base", fontSize = 12.sp)
                            }

                            // Botón "X" para descartar/cerrar la selección si se tocó por error
                            IconButton(
                                onClick = {
                                    productoSeleccionado = null
                                    precioIngresado = ""
                                    cantidadPorciones = 1
                                    porcionBaseSeleccionada = 1.0
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Cancelar selección",
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Paso 1: Porción base (1/4, 1/2, 1 kg)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf(0.25 to "1/4 kg", 0.5 to "1/2 kg", 1.0 to "1 kg").forEach { (valor, texto) ->
                                val activo = porcionBaseSeleccionada == valor
                                Button(
                                    onClick = { porcionBaseSeleccionada = valor },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (activo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = if (activo) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                ) {
                                    Text(texto)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Paso 2: Precio de esa porción base
                        val etiquetaPorcion = when (porcionBaseSeleccionada) {
                            0.25 -> "el 1/4 kg"
                            0.5 -> "el 1/2 kg"
                            else -> "el kilo"
                        }

                        OutlinedTextField(
                            value = precioIngresado,
                            onValueChange = { input ->
                                if (input.all { it.isDigit() }) precioIngresado = input
                            },
                            label = { Text("¿Cuánto sale $etiquetaPorcion?") },
                            prefix = { Text("$ ") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Paso 3: Cantidad de porciones a llevar
                        Text("¿Cuántos vas a llevar?", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilledTonalIconButton(
                                    onClick = { if (cantidadPorciones > 1) cantidadPorciones-- }
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Restar")
                                }

                                Text(
                                    text = "$cantidadPorciones",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )

                                FilledTonalIconButton(
                                    onClick = { cantidadPorciones++ }
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Sumar")
                                }
                            }

                            val precioInt = precioIngresado.toIntOrNull() ?: 0
                            val subtotalTemporal = precioInt * cantidadPorciones
                            Text(
                                text = "Subtotal: ${clpFormat.format(subtotalTemporal)}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val precioValido = (precioIngresado.toIntOrNull() ?: 0) > 0

                        Button(
                            onClick = {
                                val totalKilos = porcionBaseSeleccionada * cantidadPorciones
                                val precioPorKiloEquivalente = (precioIngresado.toInt() / porcionBaseSeleccionada).toInt()

                                viewModel.agregarItemConPrecio(prod, precioPorKiloEquivalente, totalKilos)

                                // Limpiar estados
                                precioIngresado = ""
                                cantidadPorciones = 1
                                porcionBaseSeleccionada = 1.0
                                productoSeleccionado = null
                            },
                            enabled = precioValido,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Agregar a la canasta")
                        }
                    }
                }
            }

            // Lista Vertical de Productos Disponibles
            Text("Productos disponibles:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            LazyColumn(
                modifier = Modifier
                    .weight(0.65f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(productos) { prod ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                productoSeleccionado = prod
                                precioIngresado = ""
                                cantidadPorciones = 1
                                porcionBaseSeleccionada = 1.0
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (productoSeleccionado?.id == prod.id)
                                MaterialTheme.colorScheme.tertiaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (prod.imagenUri != null) {
                                AsyncImage(
                                    model = prod.imagenUri,
                                    contentDescription = prod.nombre,
                                    modifier = Modifier.size(42.dp).clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.LocalMall, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                }
                            }

                            Text(
                                text = prod.nombre,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Text("Seleccionar", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }

            // 3. Canasta de Compras (solo aparece si hay cosas agregadas para no robar pantalla)
            if (uiState.carrito.isNotEmpty()) {
                Text(
                    text = "Canasta (${uiState.carrito.size} compras):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                LazyColumn(
                    modifier = Modifier
                        .weight(0.35f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    itemsIndexed(uiState.carrito) { index, item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.producto.nombre, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    val porcionTexto = when (item.cantidad) {
                                        0.25 -> "1/4 kg"
                                        0.5 -> "1/2 kg"
                                        1.0 -> "1 kg"
                                        else -> "${item.cantidad} kg"
                                    }
                                    Text(
                                        "$porcionTexto = ${clpFormat.format(item.subtotal)}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                                IconButton(onClick = { viewModel.removerItem(index) }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Eliminar",
                                        tint = Color.Red,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. Panel de Totales, Monedas/Billetes Acumulativos y Vuelto
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (uiState.total > uiState.presupuestoMaximo) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total canasta:", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(
                            clpFormat.format(uiState.total),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = if (uiState.total > uiState.presupuestoMaximo) Color.Red else Color.Unspecified
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Pagas con: ${clpFormat.format(uiState.montoPagado)}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        if (uiState.montoPagado > 0) {
                            TextButton(
                                onClick = { viewModel.limpiarPago() },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                            ) {
                                Text("Limpiar (C)", color = Color.Red, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    val denominaciones = listOf(50, 100, 500, 1000, 2000, 5000)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(denominaciones) { valor ->
                            FilledTonalButton(
                                onClick = { viewModel.sumarAlPago(valor) },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("$$valor", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (uiState.montoPagado > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Vuelto a recibir:", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                            Text(
                                text = if (uiState.vuelto < 0) "Faltan ${clpFormat.format(-uiState.vuelto)}" else clpFormat.format(uiState.vuelto),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = if (uiState.vuelto < 0) Color.Red else Color(0xFF2E7D32)
                            )
                        }
                    }
                }
            }

            // 5. Botón Finalizar
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