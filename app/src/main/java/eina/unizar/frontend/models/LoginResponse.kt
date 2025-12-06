package eina.unizar.frontend.models

/**
 * Modelo de datos para la respuesta del inicio de sesión.
 * 
 * Contiene la información devuelta por el backend tras una autenticación exitosa.
 *
 * @property token Token JWT o de sesión para autenticar futuras peticiones
 * @property userId Identificador único del usuario autenticado
 */
data class LoginResponse(
    val token: String,
    val userId: String,
    val foto_perfil_url: String? = null
)