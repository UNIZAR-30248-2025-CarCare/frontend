package eina.unizar.frontend.models

data class ReservaResponse(
    val id: String,
    val vehiculoId: String,
    val usuarioId: String,
    val fechaInicio: String,
    val fechaFinal: String,
    val horaInicio: String,
    val horaFin: String,
    val tipo: String,
    val notas: String?,
    val estado: String
)