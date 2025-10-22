package eina.unizar.frontend.models

import kotlinx.serialization.Serializable

@Serializable
data class VehiculoResponse(val vehiculos: List<VehiculoDTO>)