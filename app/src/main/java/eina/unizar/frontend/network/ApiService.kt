package eina.unizar.frontend.network

import eina.unizar.frontend.models.Usuario
import eina.unizar.frontend.models.LoginRequest
import eina.unizar.frontend.models.LoginResponse
import eina.unizar.frontend.models.UserNameResponse
import eina.unizar.frontend.models.ReservaRequest
import eina.unizar.frontend.models.ReservaResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("/usuario/sign-up")
    fun registrarUsuario(@Body usuario: Usuario): Call<Void>

    @POST("usuario/sign-in")
    fun iniciarSesion(@Body request: LoginRequest): Call<LoginResponse>

    @GET("usuario/obtenerNombreUsuario/{id}")
    fun obtenerNombreUsuario(
        @Path("id") id: String,
        @Header("Authorization") token: String
    ): Call<UserNameResponse>

    @POST("reserva")
    fun crearReserva(
        @Header("Authorization") token: String,
        @Body reserva: ReservaRequest
    ): Call<ReservaResponse>
}