package eina.unizar.frontend.models


/**
 * Modelo de datos para la respuesta con información básica del usuario.
 * 
 * Se utiliza cuando solo se necesita el identificador y nombre del usuario,
 * sin incluir información sensible o completa del perfil.
 *
 * @property id Identificador único del usuario
 * @property nombre Nombre completo del usuario
 */
data class UserNameResponse(
    val id: String,
    val nombre: String,
    val foto_perfil_url: String? = null
)