package eina.unizar.frontend.models

data class InvitacionRecibida(
    val id: Int,
    val vehiculoId: Int,
    val creadoPorId: Int,
    val usuarioInvitadoId: Int,
    val codigo: String,
    val fechaCreacion: String,
    val fechaExpiracion: String?,
    val usado: Boolean,
    val Vehiculo: VehiculoDetalle
)