package com.example.aplicacion_feria.util

import kotlin.math.roundToInt

object CalculadoraFeria {
    const val FRACCION_CUARTO = 0.25
    const val FRACCION_MEDIO = 0.50
    const val FRACCION_TRES_CUARTOS = 0.75
    const val KILO_ENTERO = 1.00

    fun calcularSubtotal(precioPorKiloOUnidad: Int, cantidad: Double): Int {
        return (precioPorKiloOUnidad * cantidad).roundToInt()
    }

    fun calcularVuelto(totalCompra: Int, montoPagado: Int): Int {
        return montoPagado - totalCompra
    }

    fun estaSobrePresupuesto(totalCompra: Int, presupuestoLimite: Int?): Boolean {
        return presupuestoLimite != null && totalCompra > presupuestoLimite
    }
}