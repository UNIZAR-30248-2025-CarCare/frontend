package eina.unizar.frontend.models

import com.google.gson.annotations.SerializedName


/**
 * Modelo de datos para la solicitud de creación de una reserva.
 * 
 * Utiliza anotaciones de Gson para mapear correctamente los campos
 * con el formato esperado por la API backend. Las fechas y horas
 * se envían como strings en formato específico.
 *
 * @property vehiculoId Identificador del vehículo a reservar
 * @property fechaInicio Fecha de inicio de la reserva en formato "yyyy-MM-dd"
 * @property fechaFinal Fecha de finalización de la reserva en formato "yyyy-MM-dd"
 * @property horaInicio Hora de inicio en formato "HH:mm"
 * @property horaFin Hora de finalización en formato "HH:mm"
 * @property tipo Tipo de reserva: "TRABAJO" o "PERSONAL"
 * @property notas Observaciones opcionales sobre la reserva
 */
data class ReservaRequest(
    @SerializedName("vehiculoId")
    val vehiculoId: String,

    @SerializedName("fechaInicio")
    val fechaInicio: String, // Formato: "yyyy-MM-dd"

    @SerializedName("fechaFinal")
    val fechaFinal: String, // Formato: "yyyy-MM-dd"

    @SerializedName("horaInicio")
    val horaInicio: String, // Formato: "HH:mm"

    @SerializedName("horaFin")
    val horaFin: String, // Formato: "HH:mm"

    @SerializedName("tipo")
    val tipo: String, // "TRABAJO" o "PERSONAL"

    @SerializedName("notas")
    val notas: String?
)