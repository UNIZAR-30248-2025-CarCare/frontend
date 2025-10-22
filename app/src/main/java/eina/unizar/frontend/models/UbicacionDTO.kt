package eina.unizar.frontend.models;

import kotlinx.serialization.Serializable;

@Serializable
data class UbicacionDTO(val latitud: Double, val longitud: Double)

fun Ubicacion?.toUbicacionDTO(): UbicacionDTO? {
    return this?.let { UbicacionDTO(latitud = it.latitud, longitud = it.longitud) }
}