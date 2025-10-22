package eina.unizar.frontend.models

data class RegistrarVehiculoRequest(
    val usuarioId: Int,
    val nombre: String,
    val matricula: String,
    val modelo: String,
    val fabricante: String,
    val antiguedad: Int,
    val tipo_combustible: String,
    val litros_combustible: Number,
    val consumo_medio: Double,
    val ubicacion_actual: Ubicacion,
    val estado: String
)