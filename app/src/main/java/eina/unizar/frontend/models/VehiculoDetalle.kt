package eina.unizar.frontend.models

import eina.unizar.frontend.EstadoVehiculo
import eina.unizar.frontend.TipoVehiculo
import eina.unizar.frontend.Usuario

data class VehiculoDetalle(
    val id: String,
    val nombre: String,
    val matricula: String,
    val fabricante: String,
    val modelo: String,
    val anio: Int,
    val combustible: String,
    val capacidadDeposito: Int,
    val consumoMedio: Double,
    val tipo: TipoVehiculo,
    val estado: EstadoVehiculo,
    val usuariosVinculados: List<String>
)