package eina.unizar.frontend.models

import com.google.gson.annotations.SerializedName

// Respuesta del backend para listar reservas
data class ReservasListResponse(
    val reservas: List<ReservaDTO>
)

// Modelo que viene del backend
data class ReservaDTO(
    val id: Int,
    @SerializedName("Usuario")
    val Usuario: UsuarioReservaDTO,
    @SerializedName("Vehiculo")
    val Vehiculo: VehiculoReservaDTO,
    val fechaInicio: String,
    val fechaFin: String,
    val horaInicio: String,
    val horaFin: String,
    val motivo: String,
    val descripcion: String?
)

data class UsuarioReservaDTO(
    val id: Int,
    val nombre: String,
    val email: String
)

data class VehiculoReservaDTO(
    val id: String,
    val nombre: String,
    val matricula: String,
    val tipo: String
)

// Response al crear reserva
data class ReservaResponse(
    val id: Int,
    val motivo: String,
    val fechaInicio: String,
    val fechaFin: String,
    val UsuarioId: Int,
    val VehiculoId: String,
    val horaInicio: String,
    val horaFin: String,
    val descripcion: String?
)