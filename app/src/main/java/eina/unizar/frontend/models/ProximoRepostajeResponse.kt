package eina.unizar.frontend.models

data class ProximoRepostajeResponse(
    val proximoUsuario: ProximoUsuario,
    val saldoPorUsuario: Map<String, Double>,
    val importeEstimado: Double
)