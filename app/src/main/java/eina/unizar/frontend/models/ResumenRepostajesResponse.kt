package eina.unizar.frontend.models

data class ResumenRepostajesResponse(
    val repostajes: List<Repostaje>,
    val totalLitros: Double,
    val totalPrecio: Double
)