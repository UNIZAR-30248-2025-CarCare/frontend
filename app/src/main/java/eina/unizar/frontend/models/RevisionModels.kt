package eina.unizar.frontend.models

import com.google.gson.annotations.SerializedName

/**
 * Respuesta del backend para listar revisiones
 */
data class RevisionesListResponse(
    val revisiones: List<RevisionDTO>
)

/**
 * Modelo que viene del backend para una revisión
 */
data class RevisionDTO(
    val id: Int,
    @SerializedName("vehiculoId")
    val vehiculoId: Int,
    val fecha: String,
    val tipo: String,
    val kilometraje: Int,
    val observaciones: String,
    @SerializedName("proximaRevision")
    val proximaRevision: String?,
    val taller: String?,
    val usuario: String? // nombre del usuario que registró la revisión
)

/**
 * Response simple al crear/actualizar revisión
 */
data class RevisionResponse(
    val message: String,
    val revision: RevisionDTO?
)

/**
 * Tipos de revisión disponibles (deben coincidir con el ENUM del backend)
 */
object TiposRevision {
    const val ACEITE = "Aceite"
    const val FILTROS = "Filtros"
    const val FRENOS = "Frenos"
    const val NEUMATICOS = "Neumáticos"
    const val MOTOR = "Motor"
    const val TRANSMISION = "Transmisión"
    const val SUSPENSION = "Suspensión"
    const val ELECTRICO = "Eléctrico"
    const val CLIMATIZACION = "Climatización"
    const val GENERAL = "General"

    val TODOS = listOf(
        ACEITE, FILTROS, FRENOS, NEUMATICOS, MOTOR,
        TRANSMISION, SUSPENSION, ELECTRICO, CLIMATIZACION, GENERAL
    )
}