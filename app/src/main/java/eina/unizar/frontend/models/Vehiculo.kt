package eina.unizar.frontend.models

import eina.unizar.frontend.EstadoVehiculo
import eina.unizar.frontend.TipoVehiculo

data class Vehiculo(
    val id: String,
    val estado: EstadoVehiculo,
    val nombre: String,
    val matricula: String,
    val modelo: String,
    val fabricante: String,
    val antiguedad: Int,
    val tipo_combustible: String,
    val litros_combustible: Float,
    val consumo_medio: Float,
    val ubicacion_actual: Ubicacion?,
    val tipo: TipoVehiculo,
    val usuariosVinculados: List<String> = emptyList()
)

fun Vehiculo.toVehiculoDetalle(): VehiculoDetalle {
    return VehiculoDetalle(
        id = this.id,
        nombre = this.nombre,
        matricula = this.matricula,
        fabricante = this.fabricante,
        modelo = this.modelo,
        anio = 2024 - this.antiguedad, // Ajusta según tu lógica
        combustible = this.tipo_combustible,
        capacidadDeposito = this.litros_combustible.toInt(),
        consumoMedio = this.consumo_medio.toDouble(),
        tipo = this.tipo,
        estado = this.estado,
        usuariosVinculados = usuariosVinculados ?: emptyList()
    )
}