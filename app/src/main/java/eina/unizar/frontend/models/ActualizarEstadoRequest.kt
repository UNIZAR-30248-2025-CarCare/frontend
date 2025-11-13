package eina.unizar.frontend.models

import com.google.gson.annotations.SerializedName

/**
 * Modelo para actualizar solo el estado de una incidencia.
 */
data class ActualizarEstadoRequest(
    @SerializedName("estado")
    val estado: String // "Pendiente", "En progreso", "Resuelta", "Cancelada"
)