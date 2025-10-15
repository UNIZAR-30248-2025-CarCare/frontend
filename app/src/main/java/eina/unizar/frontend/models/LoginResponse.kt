package eina.unizar.frontend.models

data class LoginResponse(
    val token: String,
    val userId: String
)