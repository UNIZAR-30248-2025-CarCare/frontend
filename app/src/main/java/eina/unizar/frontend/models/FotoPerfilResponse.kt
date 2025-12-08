package eina.unizar.frontend.models

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object para la respuesta de subida de foto de perfil.
 * * @property message Mensaje de confirmación del backend.
 * @property foto_perfil URL relativa de la foto recién subida.
 */
@Serializable
data class FotoPerfilResponse(
    val message: String,
    @SerializedName("foto_perfil_url")
    val foto_perfil: String
)