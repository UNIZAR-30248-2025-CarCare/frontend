package eina.unizar.frontend.models

import eina.unizar.frontend.TipoReserva
import java.time.LocalDate
import java.time.LocalTime

data class NuevaReservaData(
    val vehiculoId: String,
    val fechaInicio: LocalDate,
    val fechaFinal: LocalDate,
    val horaInicio: LocalTime,
    val horaFin: LocalTime,
    val tipo: TipoReserva,
    val notas: String
)
