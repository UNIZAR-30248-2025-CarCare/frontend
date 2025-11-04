package eina.unizar.frontend.models

data class NuevoViajeData(
    val usuarioId: String,
    val vehiculoId: String,
    val nombre: String,
    val descripcion: String,
    val fechaHoraInicio: String,
    val fechaHoraFin: String,
    val kmRealizados: Double,
    val consumoCombustible: Double,
    val ubicacionFinal: Ubicacion
)