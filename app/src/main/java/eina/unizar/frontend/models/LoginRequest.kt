package eina.unizar.frontend.models

/**
 * Modelo de datos para la solicitud de inicio de sesión.
 * 
 * Este data class encapsula las credenciales necesarias para autenticar
 * a un usuario en el sistema.
 *
 * @property email Correo electrónico del usuario
 * @property contraseña Contraseña del usuario en texto plano (se enviará al backend para procesamiento seguro)
 */
data class LoginRequest(
    val email: String,
    val contraseña: String
)