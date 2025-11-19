package eina.unizar.frontend.models

import com.google.gson.annotations.SerializedName

/**
 * Modelo de un logro individual.
 */
data class LogroDTO(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val tipo: String,
    val criterio: Int,
    val icono: String,
    val puntos: Int,
    val progreso: Int,
    val desbloqueado: Boolean,
    val fechaObtenido: String?,
    val porcentaje: Int
)

/**
 * Estadísticas generales de logros del usuario.
 */
data class EstadisticasLogros(
    val totalLogros: Int,
    val desbloqueados: Int,
    val pendientes: Int,
    val puntosTotales: Int,
    val porcentajeCompletado: Int
)

/**
 * Respuesta del endpoint de logros del usuario.
 */
data class LogrosUsuarioResponse(
    val logros: List<LogroDTO>,
    val estadisticas: EstadisticasLogros
)

/**
 * Respuesta del endpoint de todos los logros disponibles.
 */
data class LogrosResponse(
    val logros: List<LogroSimpleDTO>
)

/**
 * Modelo simplificado de logro (sin progreso de usuario).
 */
data class LogroSimpleDTO(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val tipo: String,
    val criterio: Int,
    val icono: String,
    val puntos: Int,
    val activo: Boolean
)

/**
 * Logro recién desbloqueado.
 */
data class LogroDesbloqueadoDTO(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val icono: String,
    val puntos: Int,
    val progreso: Int
)

/**
 * Respuesta del endpoint de verificación de progreso.
 */
data class VerificarProgresoResponse(
    val mensaje: String,
    val nuevosLogros: List<LogroDesbloqueadoDTO>,
    val totalNuevos: Int
)