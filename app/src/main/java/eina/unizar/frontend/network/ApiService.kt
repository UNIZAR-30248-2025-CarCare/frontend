package eina.unizar.frontend.network

import eina.unizar.frontend.models.Usuario
import eina.unizar.frontend.models.LoginRequest
import eina.unizar.frontend.models.LoginResponse
import eina.unizar.frontend.models.UserNameResponse
import eina.unizar.frontend.models.ReservaRequest
import eina.unizar.frontend.models.ReservaResponse
import eina.unizar.frontend.models.ReservasListResponse
import eina.unizar.frontend.models.RegistrarVehiculoRequest
import eina.unizar.frontend.models.VehiculoResponse
import eina.unizar.frontend.models.InvitacionBody
import eina.unizar.frontend.models.InvitacionResponse
import eina.unizar.frontend.models.InvitacionesRecibidasResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path


/**
 * Interfaz que define los endpoints de la API REST del backend.
 * 
 * Utiliza Retrofit para gestionar las peticiones HTTP. Cada función
 * representa un endpoint específico con su método HTTP correspondiente.
 * Los métodos que requieren autenticación incluyen el parámetro de header
 * "Authorization" para enviar el token JWT.
 */
interface ApiService {

    /**
     * Registra un nuevo usuario en el sistema.
     * 
     * Endpoint: POST /usuario/sign-up
     * 
     * @param usuario Datos del usuario a registrar
     * @return Call<Void> Respuesta sin contenido en caso de éxito
     */
    @POST("/usuario/sign-up")
    fun registrarUsuario(@Body usuario: Usuario): Call<Void>

    /**
     * Inicia sesión con credenciales de usuario.
     * 
     * Endpoint: POST /usuario/sign-in
     * 
     * @param request Credenciales del usuario (email y contraseña)
     * @return Call<LoginResponse> Respuesta con token y userId
     */
    @POST("usuario/sign-in")
    fun iniciarSesion(@Body request: LoginRequest): Call<LoginResponse>

    /**
     * Obtiene el nombre de un usuario por su ID.
     * 
     * Endpoint: GET /usuario/obtenerNombreUsuario/{id}
     * Requiere autenticación.
     * 
     * @param id Identificador del usuario
     * @param token Token de autenticación JWT (formato: "Bearer {token}")
     * @return Call<UserNameResponse> Respuesta con id y nombre del usuario
     */
    @GET("usuario/obtenerNombreUsuario/{id}")
    fun obtenerNombreUsuario(
        @Path("id") id: String,
        @Header("Authorization") token: String
    ): Call<UserNameResponse>

    /**
     * Crea una nueva reserva de vehículo.
     * 
     * Endpoint: POST /reserva
     * Requiere autenticación.
     * 
     * @param token Token de autenticación JWT (formato: "Bearer {token}")
     * @param reserva Datos de la reserva a crear
     * @return Call<ReservaResponse> Respuesta con los datos de la reserva creada
     */
    @POST("reserva")
    fun crearReserva(
        @Header("Authorization") token: String,
        @Body reserva: ReservaRequest
    ): Call<ReservaResponse>

    @GET("reserva")
    fun obtenerReservas(
        @Header("Authorization") token: String
    ): Call<ReservasListResponse>

    /**
     * Elimina una reserva por su ID.
     * 
     * Endpoint: DELETE /reserva/{id}
     * Requiere autenticación.
     * 
     * @param id Identificador de la reserva
     * @param token Token de autenticación JWT (formato: "Bearer {token}")
     * @return Call<Void> Respuesta sin contenido en caso de éxito
     */
    @DELETE("reserva/{id}")
    fun eliminarReserva(
        @Path("id") id: String,
        @Header("Authorization") token: String
    ): Call<Void>

    /**
     * Registra un nuevo vehículo en el sistema.
     * 
     * Endpoint: POST /vehiculo/registrar
     * Requiere autenticación.
     * Método suspendido para uso con coroutines.
     * 
     * @param token Token de autenticación JWT (formato: "Bearer {token}")
     * @param request Datos del vehículo a registrar
     * @return Response<Void> Respuesta HTTP con código de estado
     */
    @POST("/vehiculo/registrar")
    suspend fun registrarVehiculo(
        @Header("Authorization") token: String,
        @Body request: RegistrarVehiculoRequest
    ): Response<Void>

    /**
     * Obtiene la lista de vehículos asociados a un usuario.
     * 
     * Endpoint: GET /vehiculo/obtenerVehiculos/{userId}
     * Requiere autenticación.
     * 
     * @param userId Identificador del usuario
     * @param token Token de autenticación JWT (formato: "Bearer {token}")
     * @return Call<VehiculoResponse> Respuesta con la lista de vehículos
     */
    @GET("/vehiculo/obtenerVehiculos/{userId}")
    fun obtenerVehiculos(
        @Path("userId") userId: String,
        @Header("Authorization") token: String
    ): Call<VehiculoResponse>

    @POST("invitacion/generarInvitacion/{vehiculoId}")
    suspend fun generarInvitacion(
        @Path("vehiculoId") vehiculoId: String,
        @Header("Authorization") token: String,
        @Body body: InvitacionBody
    ): Response<InvitacionResponse>

    @GET("invitacion/invitacionesRecibidas/{usuarioId}")
    suspend fun getInvitacionesRecibidas(
        @Path("usuarioId") usuarioId: String,
        @Header("Authorization") token: String
    ): Response<InvitacionesRecibidasResponse>

    @POST("invitacion/aceptarInvitacion")
    suspend fun aceptarInvitacion(
        @Header("Authorization") token: String,
        @Body body: Map<String, String>
    ): Response<InvitacionResponse>

    @POST("invitacion/rechazarInvitacion")
    suspend fun rechazarInvitacion(
        @Header("Authorization") token: String,
        @Body body: Map<String, Int>
    ): Response<InvitacionResponse>
}