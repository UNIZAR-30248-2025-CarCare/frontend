package eina.unizar.frontend.models

import com.google.gson.annotations.SerializedName

data class ReservaRequest(
    @SerializedName("vehiculoId")
    val vehiculoId: String,

    @SerializedName("fechaInicio")
    val fechaInicio: String, // Formato: "yyyy-MM-dd"

    @SerializedName("fechaFinal")
    val fechaFinal: String, // Formato: "yyyy-MM-dd"

    @SerializedName("horaInicio")
    val horaInicio: String, // Formato: "HH:mm"

    @SerializedName("horaFin")
    val horaFin: String, // Formato: "HH:mm"

    @SerializedName("tipo")
    val tipo: String, // "TRABAJO" o "PERSONAL"

    @SerializedName("notas")
    val notas: String?
)