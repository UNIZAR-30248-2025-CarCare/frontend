package eina.unizar.frontend.models

import com.google.gson.annotations.SerializedName

/**
 * Respuesta del backend al crear una incidencia.
 */
data class CrearIncidenciaResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("incidencia")
    val incidencia: IncidenciaDetalle
)