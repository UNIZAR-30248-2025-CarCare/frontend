package eina.unizar.frontend.models

/**
 * Modelo de datos que representa un usuario del sistema.
 * 
 * Contiene toda la información de perfil de un usuario.
 * El id es opcional (null) para usuarios que aún no han sido registrados.
 *
 * @property id Identificador único del usuario (null para nuevos usuarios)
 * @property nombre Nombre completo del usuario
 * @property email Correo electrónico del usuario (único en el sistema)
 * @property contraseña Contraseña del usuario
 * @property fecha_nacimiento Fecha de nacimiento en formato string
 */
data class Usuario(
    val id: Int? = null,
    val nombre: String,
    val email: String,
    val contraseña: String,
    val fecha_nacimiento: String
)