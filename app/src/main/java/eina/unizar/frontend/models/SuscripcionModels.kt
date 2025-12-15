package eina.unizar.frontend.models

import com.google.gson.annotations.SerializedName

data class ProcesarPagoRequest(
    @SerializedName("tipo_suscripcion")
    val tipoSuscripcion: String, // "mensual" o "anual"

    @SerializedName("datos_tarjeta")
    val datosTarjeta: DatosTarjeta
)

data class DatosTarjeta(
    @SerializedName("numero")
    val numero: String,

    @SerializedName("cvv")
    val cvv: String,

    @SerializedName("fecha_expiracion")
    val fechaExpiracion: String // formato: "MM/YY"
)

data class PagoResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("suscripcion")
    val suscripcion: SuscripcionInfo
)

data class SuscripcionInfo(
    @SerializedName("tipo")
    val tipo: String,

    @SerializedName("precio")
    val precio: Double,

    @SerializedName("fecha_inicio")
    val fechaInicio: String,

    @SerializedName("fecha_fin")
    val fechaFin: String
)

data class EstadoSuscripcionResponse(
    @SerializedName("es_premium")
    val esPremium: Boolean,

    @SerializedName("tipo_suscripcion")
    val tipoSuscripcion: String?,

    @SerializedName("fecha_inicio")
    val fechaInicio: String?,

    @SerializedName("fecha_fin")
    val fechaFin: String?,

    @SerializedName("precios")
    val precios: Precios
)

data class Precios(
    @SerializedName("mensual")
    val mensual: Double,

    @SerializedName("anual")
    val anual: Double
)

data class VerificarAnuncioResponse(
    @SerializedName("mostrar_anuncio")
    val mostrarAnuncio: Boolean,

    @SerializedName("es_premium")
    val esPremium: Boolean,

    @SerializedName("tiempo_restante_segundos")
    val tiempoRestanteSegundos: Int? = null
)