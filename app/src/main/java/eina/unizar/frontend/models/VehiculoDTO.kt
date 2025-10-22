package eina.unizar.frontend.models

import eina.unizar.frontend.EstadoVehiculo
import eina.unizar.frontend.TipoVehiculo
import kotlinx.serialization.Serializable

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
    val usuariosVinculados: List<String> = emptyList()
)

fun VehiculoDTO.toVehiculo(): Vehiculo {
    return Vehiculo(
        id = id.toString(),
        estado = when (estado) {
            "Activo" -> EstadoVehiculo.DISPONIBLE
            "En uso" -> EstadoVehiculo.EN_USO
            "En reparación" -> EstadoVehiculo.EN_REPARACION
            else -> EstadoVehiculo.DISPONIBLE
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
        usuariosVinculados = usuariosVinculados ?: emptyList()
    )
}

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
        usuariosVinculados = this.usuariosVinculados
    )
}


