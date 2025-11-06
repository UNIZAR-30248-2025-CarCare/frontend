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
import eina.unizar.frontend.models.NuevoRepostajeData
import eina.unizar.frontend.models.NuevoViajeData
import eina.unizar.frontend.models.ProximoRepostajeResponse
import eina.unizar.frontend.models.ViajesResponse
import eina.unizar.frontend.models.ResumenRepostajesResponse
import eina.unizar.frontend.models.RevisionRequest
import eina.unizar.frontend.models.RevisionResponse
import eina.unizar.frontend.models.RevisionesListResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

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
     * Edita un vehículo existente en el sistema.
     *
     * Endpoint: PUT /api/vehiculos/{id}
     * Requiere autenticación.
     * Método suspendido para uso con coroutines.
     *
     * @param token Token de autenticación JWT (formato: "Bearer {token}")
     * @param vehiculoId Identificador del vehículo a editar
     * @param request Datos actualizados del vehículo
     * @return Response<Any> Respuesta HTTP con código de estado
     */
    @PUT("/vehiculo/editar/{id}")
    suspend fun editarVehiculo(
        @Header("Authorization") token: String,
        @Path("id") vehiculoId: String,
        @Body request: RegistrarVehiculoRequest
    ): Response<Any>

    /**
     * Elimina un vehículo del sistema.
     *
     * Endpoint: DELETE /api/vehiculos/{id}
     * Requiere autenticación.
     * Método suspendido para uso con coroutines.
     *
     * @param auth Token de autenticación JWT (formato: "Bearer {token}")
     * @param vehiculoId Identificador del vehículo a eliminar
     * @return Response<MensajeResponse> Respuesta con mensaje de confirmación
     */
    @DELETE("/vehiculo/eliminar/{id}")
    suspend fun eliminarVehiculo(
        @Header("Authorization") auth: String,
        @Path("id") vehiculoId: String
    ): Response<MensajeResponse>

    data class MensajeResponse(val message: String)

    /**
     * Elimina un usuario vinculado a un vehículo.
     *
     * Endpoint: POST /vehiculo/eliminarUsuario/{vehiculoId}
     * Requiere autenticación.
     * Método suspendido para uso con coroutines.
     *
     * @param token Token de autenticación JWT (formato: "Bearer {token}")
     * @param vehiculoId Identificador del vehículo
     * @param body Datos necesarios para eliminar el usuario vinculado
     * @return Response<MensajeResponse> Respuesta con mensaje de confirmación
     */
    @POST("/vehiculo/eliminarUsuario/{vehiculoId}")
    suspend fun eliminarUsuarioVinculado(
        @Header("Authorization") token: String,
        @Path("vehiculoId") vehiculoId: String,
        @Body body: Map<String, String>
    ): Response<MensajeResponse>

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

    /**
     * Genera una invitación para un vehículo.
     *
     * Endpoint: POST /invitacion/generarInvitacion/{vehiculoId}
     * Requiere autenticación.
     * Método suspendido para uso con coroutines.
     *
     * @param vehiculoId Identificador del vehículo
     * @param token Token de autenticación JWT (formato: "Bearer {token}")
     * @param body Datos de la invitación
     * @return Response<InvitacionResponse> Respuesta con los datos de la invitación generada
     */
    @POST("invitacion/generarInvitacion/{vehiculoId}")
    suspend fun generarInvitacion(
        @Path("vehiculoId") vehiculoId: String,
        @Header("Authorization") token: String,
        @Body body: InvitacionBody
    ): Response<InvitacionResponse>

    /**
     * Obtiene las invitaciones recibidas por un usuario.
     *
     * Endpoint: GET /invitacion/invitacionesRecibidas/{usuarioId}
     * Requiere autenticación.
     * Método suspendido para uso con coroutines.
     *
     * @param usuarioId Identificador del usuario
     * @param token Token de autenticación JWT (formato: "Bearer {token}")
     * @return Response<InvitacionesRecibidasResponse> Respuesta con la lista de invitaciones recibidas
     */
    @GET("invitacion/invitacionesRecibidas/{usuarioId}")
    suspend fun getInvitacionesRecibidas(
        @Path("usuarioId") usuarioId: String,
        @Header("Authorization") token: String
    ): Response<InvitacionesRecibidasResponse>

    /**
     * Acepta una invitación recibida.
     *
     * Endpoint: POST /invitacion/aceptarInvitacion
     * Requiere autenticación.
     * Método suspendido para uso con coroutines.
     *
     * @param token Token de autenticación JWT (formato: "Bearer {token}")
     * @param body Datos necesarios para aceptar la invitación
     * @return Response<InvitacionResponse> Respuesta con los datos de la invitación aceptada
     */
    @POST("invitacion/aceptarInvitacion")
    suspend fun aceptarInvitacion(
        @Header("Authorization") token: String,
        @Body body: Map<String, String>
    ): Response<InvitacionResponse>

    /**
     * Rechaza una invitación recibida.
     *
     * Endpoint: POST /invitacion/rechazarInvitacion
     * Requiere autenticación.
     * Método suspendido para uso con coroutines.
     *
     * @param token Token de autenticación JWT (formato: "Bearer {token}")
     * @param body Datos necesarios para rechazar la invitación
     * @return Response<InvitacionResponse> Respuesta con los datos de la invitación rechazada
     */
    @POST("invitacion/rechazarInvitacion")
    suspend fun rechazarInvitacion(
        @Header("Authorization") token: String,
        @Body body: Map<String, Int>
    ): Response<InvitacionResponse>

    /**
     * Obtiene los viajes asociados a un vehículo.
     *
     * Endpoint: GET /viaje/obtenerViajes/{vehiculoId}
     * Requiere autenticación.
     * Método suspendido para uso con coroutines.
     *
     * @param vehiculoId Identificador del vehículo
     * @param authHeader Token de autenticación JWT (formato: "Bearer {token}")
     * @return Response<ViajesResponse> Respuesta con la lista de viajes
     */
    @GET("viaje/obtenerViajes/{vehiculoId}")
    suspend fun obtenerViajes(
        @Header("Authorization") authHeader: String,
        @Path("vehiculoId") vehiculoId: String
    ): Response<ViajesResponse>

    /**
     * Crea un nuevo viaje para un vehículo.
     *
     * Endpoint: POST /viaje/crearViaje
     * Requiere autenticación.
     * Método suspendido para uso con coroutines.
     *
     * @param token Token de autenticación JWT (formato: "Bearer {token}")
     * @param nuevoViaje Datos del viaje a crear
     * @return Response<Unit> Respuesta HTTP con código de estado
     */
    @POST("viaje/crearViaje")
    suspend fun crearViaje(
        @Header("Authorization") token: String,
        @Body nuevoViaje: NuevoViajeData
    ): Response<Unit>

    /**
     * Obtiene el resumen de repostajes de un vehículo.
     *
     * Endpoint: GET /repostaje/obtenerRepostajesVehiculo/{vehiculoId}
     * Requiere autenticación.
     * Método suspendido para uso con coroutines.
     *
     * @param vehiculoId Identificador del vehículo
     * @param token Token de autenticación JWT (formato: "Bearer {token}")
     * @return Response<ResumenRepostajesResponse> Respuesta con el resumen de repostajes
     */
    @GET("repostaje/obtenerRepostajesVehiculo/{vehiculoId}")
    suspend fun obtenerRepostajesVehiculo(
        @Header("Authorization") token: String,
        @Path("vehiculoId") vehiculoId: String
    ): Response<ResumenRepostajesResponse>

    /**
     * Calcula el próximo repostaje recomendado para un vehículo.
     *
     * Endpoint: GET /repostaje/calcularProximoRepostaje/{vehiculoId}
     * Requiere autenticación.
     * Método suspendido para uso con coroutines.
     *
     * @param vehiculoId Identificador del vehículo
     * @param token Token de autenticación JWT (formato: "Bearer {token}")
     * @return Response<ProximoRepostajeResponse> Respuesta con los datos del próximo repostaje
     */
    @GET("repostaje/calcularProximoRepostaje/{vehiculoId}")
    suspend fun calcularProximoRepostaje(
        @Header("Authorization") token: String,
        @Path("vehiculoId") vehiculoId: String
    ): Response<ProximoRepostajeResponse>

    /**
     * Crea un nuevo repostaje para un vehículo.
     *
     * Endpoint: POST /repostaje/crearRepostaje
     * Requiere autenticación.
     * Método suspendido para uso con coroutines.
     *
     * @param token Token de autenticación JWT (formato: "Bearer {token}")
     * @param nuevoRepostaje Datos del repostaje a crear
     * @return Response<Unit> Respuesta HTTP con código de estado
     */
    @POST("repostaje/crearRepostaje")
    suspend fun crearRepostaje(
        @Header("Authorization") token: String,
        @Body nuevoRepostaje: NuevoRepostajeData
    ): Response<Unit>

    @GET("revision/obtenerRevisiones/{vehiculoId}")
    fun obtenerRevisiones(
        @Path("vehiculoId") vehiculoId: String,
        @Query("tipo") tipo: String?,
        @Header("Authorization") token: String
    ): Call<RevisionesListResponse>

    @POST("revision/registrar")
    fun registrarRevision(
        @Header("Authorization") token: String,
        @Body revision: RevisionRequest
    ): Call<RevisionResponse>
}