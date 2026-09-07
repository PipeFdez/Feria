package com.example.aplicacion_feria.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacion_feria.data.local.FeriaDatabase
import com.example.aplicacion_feria.data.local.entities.DetalleCompraEntity
import com.example.aplicacion_feria.data.local.entities.ProductoEntity
import com.example.aplicacion_feria.data.local.entities.SesionCompraEntity
import com.example.aplicacion_feria.data.local.entities.TipoVenta
import com.example.aplicacion_feria.util.CalculadoraFeria
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ItemCarrito(
    val producto: ProductoEntity,
    val precioCobrado: Int,
    val cantidad: Double,
    val subtotal: Int
)

data class CompraUiState(
    val carrito: List<ItemCarrito> = emptyList(),
    val total: Int = 0,
    val montoPagado: Int = 0,
    val vuelto: Int = 0,
    val presupuestoMaximo: Int = 25000
)

class CompraViewModel(application: Application) : AndroidViewModel(application) {
    private val db = FeriaDatabase.getDatabase(application)
    private val productoDao = db.productoDao()
    private val compraDao = db.compraDao()

    val productos: StateFlow<List<ProductoEntity>> = productoDao.getAllProductos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(CompraUiState())
    val uiState: StateFlow<CompraUiState> = _uiState.asStateFlow()

    init {
        precargarProductosSiEsNecesario()
    }

    private fun precargarProductosSiEsNecesario() {
        viewModelScope.launch(Dispatchers.IO) {
            val existentes = productoDao.getAllProductos().first()
            if (existentes.isEmpty()) {
                val listaBase = listOf(
                    ProductoEntity(nombre = "Tomates", precioBase = 1500, tipoVenta = TipoVenta.PESO),
                    ProductoEntity(nombre = "Papas", precioBase = 1000, tipoVenta = TipoVenta.PESO),
                    ProductoEntity(nombre = "Plátanos", precioBase = 1200, tipoVenta = TipoVenta.PESO),
                    ProductoEntity(nombre = "Cebollas", precioBase = 1000, tipoVenta = TipoVenta.PESO),
                    ProductoEntity(nombre = "Zanahorias", precioBase = 900, tipoVenta = TipoVenta.PESO),
                    ProductoEntity(nombre = "Paltas", precioBase = 4500, tipoVenta = TipoVenta.PESO)
                )
                listaBase.forEach { productoDao.insertProducto(it) }
            }
        }
    }

    fun agregarItemConPrecio(producto: ProductoEntity, precioManual: Int, cantidad: Double) {
        if (precioManual <= 0) return
        val subtotal = CalculadoraFeria.calcularSubtotal(precioManual, cantidad)
        val nuevoCarrito = _uiState.value.carrito + ItemCarrito(producto, precioManual, cantidad, subtotal)
        recalcularTotales(nuevoCarrito, _uiState.value.montoPagado)
    }

    fun removerItem(index: Int) {
        val nuevoCarrito = _uiState.value.carrito.toMutableList().apply { removeAt(index) }
        recalcularTotales(nuevoCarrito, _uiState.value.montoPagado)
    }

    fun establecerPago(monto: Int) {
        recalcularTotales(_uiState.value.carrito, monto)
    }

    private fun recalcularTotales(carrito: List<ItemCarrito>, pago: Int) {
        val total = carrito.sumOf { it.subtotal }
        val vuelto = if (pago > 0) CalculadoraFeria.calcularVuelto(total, pago) else 0
        _uiState.value = _uiState.value.copy(
            carrito = carrito,
            total = total,
            montoPagado = pago,
            vuelto = vuelto
        )
    }

    fun guardarCompraFinal(onSuccess: () -> Unit) {
        val estado = _uiState.value
        if (estado.carrito.isEmpty()) return

        viewModelScope.launch(Dispatchers.IO) {
            val sesionId = compraDao.insertSesion(
                SesionCompraEntity(
                    totalGastado = estado.total,
                    montoPagado = estado.montoPagado,
                    vuelto = estado.vuelto,
                    presupuestoLimite = estado.presupuestoMaximo
                )
            )

            val detalles = estado.carrito.map {
                DetalleCompraEntity(
                    sesionId = sesionId,
                    productoId = it.producto.id,
                    precioUnitarioCobrado = it.precioCobrado,
                    cantidad = it.cantidad,
                    subtotal = it.subtotal
                )
            }
            compraDao.insertDetalles(detalles)

            _uiState.value = CompraUiState()
            launch(Dispatchers.Main) {
                onSuccess()
            }
        }
    }

    fun sumarAlPago(monto: Int) {
        val nuevoMonto = _uiState.value.montoPagado + monto
        recalcularTotales(_uiState.value.carrito, nuevoMonto)
    }

    fun limpiarPago() {
        recalcularTotales(_uiState.value.carrito, 0)
    }

}