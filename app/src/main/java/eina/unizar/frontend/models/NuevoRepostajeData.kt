package eina.unizar.frontend.models

data class NuevoRepostajeData(
    val usuarioId: String,
    val vehiculoId: String,
    val litros: Double,
    val precioPorLitro: Double,
    val precioTotal: Double,
    val fecha: String
)