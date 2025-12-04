package eina.unizar.frontend.models

import kotlinx.serialization.Serializable

/**
 * Data Transfer Object para la respuesta de subida de foto de perfil.
 * * @property message Mensaje de confirmación del backend.
 * @property foto_perfil URL relativa de la foto recién subida.
 */
@Serializable
data class FotoPerfilResponse(
    val message: String,
    val foto_perfil: String
)