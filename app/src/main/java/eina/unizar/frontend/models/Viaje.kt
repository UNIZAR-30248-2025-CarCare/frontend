package eina.unizar.frontend.models

import eina.unizar.frontend.Usuario
import java.time.LocalDate

data class Viaje(
    val id: String,
    val vehiculoId: String,
    val nombre: String,
    val descripcion: String,
    val fechaHoraInicio: String, // ISO 8601, se puede parsear a LocalDateTime si queremos
    val fechaHoraFin: String,
    val kmRealizados: Int,
    val consumoCombustible: Int,
    val ubicacionFinal: Ubicacion,
    val usuario: String
)