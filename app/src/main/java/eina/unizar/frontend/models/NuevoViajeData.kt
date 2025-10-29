package eina.unizar.frontend.models

data class NuevoViajeData(
    val usuarioId: String,
    val vehiculoId: String,
    val nombre: String,
    val descripcion: String,
    val fechaHoraInicio: String,
    val fechaHoraFin: String,
    val kmRealizados: Int,
    val consumoCombustible: Int,
    val ubicacionFinal: Ubicacion
)