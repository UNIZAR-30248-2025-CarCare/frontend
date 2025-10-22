package eina.unizar.frontend.models

import kotlinx.serialization.Serializable

/**
 * Modelo de datos para la respuesta que contiene una lista de vehículos.
 * 
 * Wrapper utilizado cuando el backend devuelve múltiples vehículos,
 * típicamente en endpoints de listado o búsqueda.
 *
 * @property vehiculos Lista de vehículos en formato DTO
 */
@Serializable
data class VehiculoResponse(val vehiculos: List<VehiculoDTO>)