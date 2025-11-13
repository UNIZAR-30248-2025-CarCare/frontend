package eina.unizar.frontend.models

import com.google.gson.annotations.SerializedName

/**
 * Respuesta del backend al obtener una lista de incidencias.
 */
data class ListaIncidenciasResponse(
    @SerializedName("incidencias")
    val incidencias: List<IncidenciaDetalle>
)