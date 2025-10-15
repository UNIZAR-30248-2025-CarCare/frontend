package eina.unizar.frontend.models

data class Usuario(
    val id: Int? = null,
    val nombre: String,
    val email: String,
    val contraseña: String,
    val fecha_nacimiento: String
)