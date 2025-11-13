package eina.unizar.frontend.models

import com.google.gson.annotations.SerializedName

/**
 * Modelo que representa el detalle completo de una incidencia.
 */
data class IncidenciaDetalle(
    @SerializedName("id")
    val id: String,

    @SerializedName("vehiculoId")
    val vehiculoId: Int,

    @SerializedName("usuarioId")
    val usuarioId: Int,

    @SerializedName("tipo")
    val tipo: String,

    @SerializedName("prioridad")
    val prioridad: String,

    @SerializedName("titulo")
    val titulo: String,

    @SerializedName("descripcion")
    val descripcion: String,

    @SerializedName("estado")
    val estado: String, // "Pendiente", "En progreso", "Resuelta", "Cancelada"

    @SerializedName("fechaCreacion")
    val fechaCreacion: String, // ISO 8601

    @SerializedName("fechaActualizacion")
    val fechaActualizacion: String?, // ISO 8601

    @SerializedName("fotos")
    val fotos: List<String>? = emptyList(),

    @SerializedName("compartidaConGrupo")
    val compartidaConGrupo: Boolean,

    // Información adicional opcional
    @SerializedName("nombreUsuario")
    val nombreUsuario: String? = null,

    @SerializedName("nombreVehiculo")
    val nombreVehiculo: String? = null,

    @SerializedName("matriculaVehiculo")
    val matriculaVehiculo: String? = null
)