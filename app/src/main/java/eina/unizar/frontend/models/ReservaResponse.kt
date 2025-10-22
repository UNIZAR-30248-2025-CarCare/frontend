package eina.unizar.frontend.models


/**
 * Modelo de datos para la respuesta de una reserva desde el backend.
 * 
 * Representa una reserva ya creada en el sistema con todos sus datos,
 * incluyendo el identificador único generado por el backend.
 *
 * @property id Identificador único de la reserva
 * @property vehiculoId Identificador del vehículo reservado
 * @property usuarioId Identificador del usuario que realizó la reserva
 * @property fechaInicio Fecha de inicio de la reserva
 * @property fechaFinal Fecha de finalización de la reserva
 * @property horaInicio Hora de inicio de la reserva
 * @property horaFin Hora de finalización de la reserva
 * @property tipo Tipo de reserva (TRABAJO/PERSONAL)
 * @property notas Observaciones adicionales (puede ser null)
 * @property estado Estado actual de la reserva (pendiente, confirmada, cancelada, etc.)
 */
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