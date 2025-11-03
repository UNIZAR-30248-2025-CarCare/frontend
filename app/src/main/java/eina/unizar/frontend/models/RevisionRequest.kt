package eina.unizar.frontend.models

import com.google.gson.annotations.SerializedName

/**
 * Modelo de datos para la solicitud de creación de una revisión.
 *
 * Utiliza anotaciones de Gson para mapear correctamente los campos
 * con el formato esperado por la API backend. Las fechas se envían
 * como strings en formato "yyyy-MM-dd".
 */
data class RevisionRequest(
    @SerializedName("usuarioId")
    val usuarioId: Int,

    @SerializedName("vehiculoId")
    val vehiculoId: Int,

    @SerializedName("fecha")
    val fecha: String, // Formato: "yyyy-MM-dd"

    @SerializedName("tipo")
    val tipo: String, // Debe ser uno de los valores del ENUM del backend

    @SerializedName("kilometraje")
    val kilometraje: Int,

    @SerializedName("observaciones")
    val observaciones: String,

    @SerializedName("proximaRevision")
    val proximaRevision: String?, // Formato: "yyyy-MM-dd" (opcional)

    @SerializedName("taller")
    val taller: String? // Opcional
)