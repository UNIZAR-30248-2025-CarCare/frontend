package eina.unizar.frontend.models

import eina.unizar.frontend.EstadoVehiculo
import eina.unizar.frontend.TipoVehiculo
import eina.unizar.frontend.Usuario

/**
 * Modelo de datos con información detallada de un vehículo.
 * 
 * Versión extendida del modelo Vehiculo, optimizada para mostrar
 * información completa en pantallas de detalle. Incluye el año
 * en lugar de la antigüedad para mejor legibilidad.
 *
 * @property id Identificador único del vehículo
 * @property nombre Nombre descriptivo del vehículo
 * @property matricula Matrícula del vehículo
 * @property fabricante Marca o fabricante
 * @property modelo Modelo del vehículo
 * @property anio Año de fabricación
 * @property combustible Tipo de combustible
 * @property capacidadDeposito Capacidad del depósito en litros
 * @property consumoMedio Consumo medio en litros/100km
 * @property tipo Tipo de vehículo (enum TipoVehiculo)
 * @property estado Estado actual (enum EstadoVehiculo)
 * @property usuariosVinculados Lista de IDs de usuarios con acceso
 */
data class VehiculoDetalle(
    val id: String,
    val nombre: String,
    val matricula: String,
    val fabricante: String,
    val modelo: String,
    val anio: Int,
    val combustible: String,
    val capacidadDeposito: Int,
    val consumoMedio: Double,
    val tipo: TipoVehiculo,
    val estado: EstadoVehiculo,
    val usuariosVinculados: List<String>,
    val usuarioActivoId: String?,
    val icono_url: String? = null
)