package eina.unizar.frontend.models


/**
 * Modelo de datos para la solicitud de registro de un nuevo vehículo.
 * 
 * Contiene toda la información necesaria para dar de alta un vehículo
 * en el sistema de gestión de flota.
 *
 * @property usuarioId Identificador del usuario que registra el vehículo
 * @property nombre Nombre descriptivo del vehículo
 * @property matricula Matrícula del vehículo
 * @property modelo Modelo del vehículo
 * @property fabricante Marca o fabricante del vehículo
 * @property antiguedad Antigüedad del vehículo en años
 * @property tipo_combustible Tipo de combustible que utiliza (gasolina, diésel, eléctrico, etc.)
 * @property litros_combustible Capacidad del depósito en litros
 * @property consumo_medio Consumo medio del vehículo (litros/100km)
 * @property ubicacion_actual Ubicación geográfica actual del vehículo
 * @property estado Estado actual del vehículo (disponible, en uso, en reparación, etc.)
 */
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
    val estado: String,
    val tipo: String
)