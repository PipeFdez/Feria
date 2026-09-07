package com.example.aplicacion_feria.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.aplicacion_feria.data.local.dao.SesionConDetalles
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Estructura para agrupar las sesiones por día
data class GrupoComprasDia(
    val fechaTexto: String, // Ejemplo: "07 de septiembre de 2026"
    val totalDia: Int,
    val compras: List<SesionConDetalles>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(viewModel: HistorialViewModel) {
    val historial by viewModel.historial.collectAsState()

    val clpFormat = remember {
        NumberFormat.getCurrencyInstance(Locale("es", "CL")).apply {
            maximumFractionDigits = 0
        }
    }

    // Agrupamos el historial por fecha (Día, Mes, Año)
    val historialPorDia = remember(historial) {
        val formatoDia = SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy", Locale("es", "CL"))
        val formatoClave = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

        historial
            .groupBy { formatoClave.format(Date(it.sesion.fechaEpochMs)) }
            .map { (_, sesiones) ->
                val primerRegistro = sesiones.first()
                val fechaTitulo = formatoDia.format(Date(primerRegistro.sesion.fechaEpochMs))
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("es", "CL")) else it.toString() }
                val totalDia = sesiones.sumOf { it.sesion.totalGastado }
                GrupoComprasDia(
                    fechaTexto = fechaTitulo,
                    totalDia = totalDia,
                    compras = sesiones
                )
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Compras", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        if (historialPorDia.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Aún no tienes compras guardadas.",
                    color = MaterialTheme.colorScheme.outline
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(historialPorDia) { grupo ->
                    TarjetaDiaHistorial(grupo = grupo, clpFormat = clpFormat)
                }
            }
        }
    }
}

@Composable
fun TarjetaDiaHistorial(grupo: GrupoComprasDia, clpFormat: NumberFormat) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Cabecera del Día: Fecha y Total Gastado en la jornada
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = grupo.fechaTexto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Total día:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = clpFormat.format(grupo.totalDia),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Lista de compras efectuadas en este día
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                grupo.compras.forEachIndexed { index, compra ->
                    TarjetaSesionIndividual(
                        numeroCompra = grupo.compras.size - index,
                        item = compra,
                        clpFormat = clpFormat
                    )
                }
            }
        }
    }
}

@Composable
fun TarjetaSesionIndividual(
    numeroCompra: Int,
    item: SesionConDetalles,
    clpFormat: NumberFormat
) {
    var expandido by remember { mutableStateOf(false) }

    val horaFormateada = remember(item.sesion.fechaEpochMs) {
        val sdf = SimpleDateFormat("HH:mm 'hrs'", Locale("es", "CL"))
        sdf.format(Date(item.sesion.fechaEpochMs))
    }

    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expandido = !expandido }
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Fila de la compra individual dentro del día
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Compra #$numeroCompra • $horaFormateada",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = clpFormat.format(item.sesion.totalGastado),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
                Icon(
                    imageVector = if (expandido) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expandido) "Ocultar" else "Ver productos",
                    modifier = Modifier.size(20.dp)
                )
            }

            // Desplegable de productos pesados y pagos
            AnimatedVisibility(visible = expandido) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    HorizontalDivider(thickness = 0.6.dp, color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(6.dp))

                    item.detalles.forEach { detalleConProd ->
                        val prod = detalleConProd.producto
                        val detalle = detalleConProd.detalle

                        val porcionTexto = when (detalle.cantidad) {
                            0.25 -> "1/4 kg"
                            0.5 -> "1/2 kg"
                            1.0 -> "1 kg"
                            else -> "${detalle.cantidad} kg"
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (prod.imagenUri != null) {
                                AsyncImage(
                                    model = prod.imagenUri,
                                    contentDescription = prod.nombre,
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(prod.nombre, fontWeight = FontWeight.Medium, fontSize = 12.sp)
                                Text(
                                    text = "$porcionTexto a ${clpFormat.format(detalle.precioUnitarioCobrado)}/kg",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }

                            Text(
                                text = clpFormat.format(detalle.subtotal),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    if (item.sesion.montoPagado > 0) {
                        Spacer(modifier = Modifier.height(6.dp))
                        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Pagado:", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                            Text(text = clpFormat.format(item.sesion.montoPagado), fontSize = 11.sp)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Vuelto:", fontSize = 11.sp, color = Color(0xFF2E7D32))
                            Text(
                                text = clpFormat.format(item.sesion.vuelto),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                }
            }
        }
    }
}