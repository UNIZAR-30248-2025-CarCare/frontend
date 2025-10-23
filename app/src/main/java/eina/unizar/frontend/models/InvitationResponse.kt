package eina.unizar.frontend.models

data class InvitacionResponse(
    val message: String,
    val codigo: String?,
    val vehiculo: VehiculoDetalle?
)