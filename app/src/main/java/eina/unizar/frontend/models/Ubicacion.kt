package eina.unizar.frontend.models

/**
 * Modelo de datos para representar una ubicación geográfica.
 * 
 * Utiliza coordenadas geográficas estándar para localizar vehículos.
 *
 * @property latitud Latitud en grados decimales (-90 a 90)
 * @property longitud Longitud en grados decimales (-180 a 180)
 */
data class Ubicacion(
    val latitud: Double,
    val longitud: Double
)