package eina.unizar.frontend.models

import com.google.gson.annotations.SerializedName

/**
 * Respuesta del backend al obtener una incidencia específica.
 */
data class IncidenciaResponse(
    @SerializedName("incidencia")
    val incidencia: IncidenciaDetalle
)