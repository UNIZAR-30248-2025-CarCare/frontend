package eina.unizar.frontend.models

import eina.unizar.frontend.EstadoVehiculo
import eina.unizar.frontend.TipoVehiculo
import kotlinx.serialization.Serializable


/**
 * Data Transfer Object para vehículos.
 * 
 * Versión serializable del modelo Vehiculo para comunicación con el backend.
 * Utiliza tipos primitivos y strings para facilitar la serialización JSON.
 *
 * @property id Identificador único del vehículo
 * @property nombre Nombre del vehículo
 * @property matricula Matrícula
 * @property modelo Modelo del vehículo
 * @property fabricante Fabricante
 * @property antiguedad Antigüedad en años
 * @property tipo_combustible Tipo de combustible
 * @property litros_combustible Capacidad del depósito
 * @property consumo_medio Consumo medio
 * @property ubicacion_actual Ubicación como DTO (puede ser null)
 * @property estado Estado como string
 * @property tipo Tipo de vehículo como string
 * @property usuariosVinculados Lista de IDs de usuarios
 */
@Serializable
data class VehiculoDTO(
    val id: String,
    val nombre: String,
    val matricula: String,
    val modelo: String,
    val fabricante: String,
    val antiguedad: Int,
    val tipo_combustible: String,
    val litros_combustible: Float,
    val consumo_medio: Float,
    val ubicacion_actual: UbicacionDTO?,
    val estado: String,
    val tipo: String,
    val usuariosVinculados: List<String> = emptyList(),
    val usuarioActivoId: String?,
    val icono_url: String? = null
)

/**
 * Convierte un VehiculoDTO a Vehiculo.
 * 
 * Transforma la representación DTO (con strings) al modelo de dominio
 * (con enums y tipos específicos). Mapea los valores string de estado
 * y tipo a sus correspondientes enums.
 *
 * @return Vehiculo con los datos convertidos al modelo de dominio
 */
fun VehiculoDTO.toVehiculo(): Vehiculo {
    return Vehiculo(
        id = id.toString(),
        estado = when (estado) {
            "Inactivo" -> EstadoVehiculo.INACTIVO
            "Activo" -> EstadoVehiculo.ACTIVO
            "Mantenimiento" -> EstadoVehiculo.MANTENIMIENTO
            else -> EstadoVehiculo.INACTIVO
        },
        nombre = nombre,
        matricula = matricula,
        modelo = modelo,
        fabricante = fabricante,
        antiguedad = antiguedad,
        tipo_combustible = tipo_combustible,
        litros_combustible = litros_combustible,
        consumo_medio = consumo_medio,
        ubicacion_actual = ubicacion_actual?.let { Ubicacion(it.latitud, it.longitud) },
        tipo = when (tipo) {
            "Coche" -> TipoVehiculo.COCHE
            "Moto" -> TipoVehiculo.MOTO
            "Furgoneta" -> TipoVehiculo.FURGONETA
            "Camion" -> TipoVehiculo.CAMION
            else -> TipoVehiculo.OTRO
        },
        usuarioActivoId = null,
        usuariosVinculados = usuariosVinculados ?: emptyList()
    )
}


/**
 * Convierte un Vehiculo a VehiculoDTO.
 * 
 * Transforma el modelo de dominio al formato DTO para enviarlo al backend.
 * Convierte los enums a strings y la ubicación a su versión DTO.
 *
 * @return VehiculoDTO listo para serialización y envío al backend
 */
fun Vehiculo.toVehiculoDTO(): VehiculoDTO {
    return VehiculoDTO(
        id = this.id,
        estado = this.estado.texto,
        nombre = this.nombre,
        matricula = this.matricula,
        tipo = this.tipo.toString(),
        fabricante = this.fabricante,
        modelo = this.modelo,
        antiguedad = this.antiguedad,
        tipo_combustible = this.tipo_combustible,
        litros_combustible = this.litros_combustible,
        consumo_medio = this.consumo_medio,
        ubicacion_actual = this.ubicacion_actual.toUbicacionDTO(),
        usuarioActivoId = this.usuarioActivoId,
        usuariosVinculados = this.usuariosVinculados
    )
}


