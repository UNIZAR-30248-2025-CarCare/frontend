package eina.unizar.frontend.models

import com.google.gson.annotations.SerializedName

/**
 * Modelo para crear o actualizar una incidencia.
 */
data class CrearIncidenciaRequest(
    @SerializedName("vehiculoId")
    val vehiculoId: String,

    @SerializedName("tipo")
    val tipo: String, // "Mecánica", "Eléctrica", "Neumático", "Carrocería", "Otros"

    @SerializedName("prioridad")
    val prioridad: String, // "Alta", "Media", "Baja"

    @SerializedName("titulo")
    val titulo: String,

    @SerializedName("descripcion")
    val descripcion: String,

    @SerializedName("fotos")
    val fotos: List<String>, // URLs o base64 de las fotos

    @SerializedName("compartirConGrupo")
    val compartirConGrupo: Boolean
)