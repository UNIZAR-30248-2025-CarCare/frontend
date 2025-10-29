package eina.unizar.frontend.models

data class Repostaje(
    val id: Int,
    val usuarioId: Int,
    val usuarioNombre: String,
    val vehiculoId: Int,
    val fecha: String,
    val litros: Double,
    val precioPorLitro: Double,
    val precioTotal: Double
)