package eina.unizar.frontend.models

data class BusquedaResponse(
    val viajes: List<Viaje> = emptyList(),
    //val repostajes: List<Repostaje> = emptyList(),
    val incidencias: List<IncidenciaDetalle> = emptyList(),
    val reservas: List<ReservaDTO> = emptyList(),
    val revisiones: List<RevisionDTO> = emptyList()
)