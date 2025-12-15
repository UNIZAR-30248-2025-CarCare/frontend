package eina.unizar.frontend.models

import com.google.gson.annotations.SerializedName

data class Parking(
    val id: Int,
    val nombre: String,
    val ubicacion: UbicacionParking,
    val notas: String?,
    val usuarioId: Int,
    val createdAt: String?,
    val updatedAt: String?
)

data class UbicacionParking(
    val lat: Double,
    val lng: Double
)

data class NuevoParkingData(
    val nombre: String,
    val ubicacion: UbicacionParking,
    val notas: String? = null
)

data class ParkingResponse(
    val message: String,
    val parking: Parking
)

data class ParkingsResponse(
    val parkings: List<Parking>
)