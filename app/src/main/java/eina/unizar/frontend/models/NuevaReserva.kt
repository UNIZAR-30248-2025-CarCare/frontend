package eina.unizar.frontend.models

import eina.unizar.frontend.TipoReserva
import java.time.LocalDate
import java.time.LocalTime

/**
 * Modelo de datos para crear una nueva reserva de vehículo.
 * 
 * Encapsula toda la información necesaria para registrar una reserva
 * en el sistema de gestión de vehículos.
 *
 * @property vehiculoId Identificador único del vehículo a reservar
 * @property fechaInicio Fecha de inicio de la reserva
 * @property fechaFinal Fecha de finalización de la reserva
 * @property horaInicio Hora de inicio de la reserva
 * @property horaFin Hora de finalización de la reserva
 * @property tipo Tipo de reserva (enum TipoReserva)
 * @property notas Observaciones o comentarios adicionales sobre la reserva
 */
data class NuevaReservaData(
    val vehiculoId: String,
    val fechaInicio: LocalDate,
    val fechaFinal: LocalDate,
    val horaInicio: LocalTime,
    val horaFin: LocalTime,
    val tipo: TipoReserva,
    val notas: String
)
