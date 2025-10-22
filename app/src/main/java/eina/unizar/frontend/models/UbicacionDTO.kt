package eina.unizar.frontend.models;

import kotlinx.serialization.Serializable;

/**
 * Data Transfer Object para ubicaciones geográficas.
 * 
 * Versión serializable de Ubicacion para comunicación con el backend
 * utilizando kotlinx.serialization.
 *
 * @property latitud Latitud en grados decimales
 * @property longitud Longitud en grados decimales
 */
@Serializable
data class UbicacionDTO(val latitud: Double, val longitud: Double)


/**
 * Función de extensión para convertir una Ubicacion a UbicacionDTO.
 * 
 * Maneja correctamente valores nulos devolviendo null si la ubicación es null.
 *
 * @return UbicacionDTO con las mismas coordenadas, o null si la ubicación original es null
 */
fun Ubicacion?.toUbicacionDTO(): UbicacionDTO? {
    return this?.let { UbicacionDTO(latitud = it.latitud, longitud = it.longitud) }
}