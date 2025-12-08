package eina.unizar.frontend.models

import eina.unizar.frontend.EstadoVehiculo
import eina.unizar.frontend.TipoVehiculo

/**
 * Modelo de datos que representa un vehículo en el sistema.
 * 
 * Contiene toda la información relevante de un vehículo, incluyendo
 * características técnicas, estado actual y usuarios vinculados.
 *
 * @property id Identificador único del vehículo
 * @property estado Estado actual del vehículo (enum EstadoVehiculo)
 * @property nombre Nombre descriptivo o alias del vehículo
 * @property matricula Matrícula del vehículo
 * @property modelo Modelo del vehículo
 * @property fabricante Marca o fabricante
 * @property antiguedad Antigüedad en años
 * @property tipo_combustible Tipo de combustible (gasolina, diésel, eléctrico, etc.)
 * @property litros_combustible Capacidad del depósito en litros
 * @property consumo_medio Consumo medio en litros/100km
 * @property ubicacion_actual Ubicación geográfica actual (puede ser null)
 * @property tipo Tipo de vehículo (enum TipoVehiculo)
 * @property usuariosVinculados Lista de IDs de usuarios con acceso al vehículo
 */
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
    val usuariosVinculados: List<String> = emptyList(),
    val usuarioActivoId: String?
    val icono_url: String? = null
)

/**
 * Función de extensión para convertir un Vehiculo a VehiculoDetalle.
 * 
 * Transforma la representación básica del vehículo a un formato más
 * adecuado para mostrar en pantallas de detalle, calculando el año
 * a partir de la antigüedad.
 *
 * @return VehiculoDetalle con los datos transformados
 */
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
        usuariosVinculados = usuariosVinculados ?: emptyList(),
        usuarioActivoId = this.usuarioActivoId
        icono_url = this.icono_url
    )
}