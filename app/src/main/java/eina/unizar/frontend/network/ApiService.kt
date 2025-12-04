package eina.unizar.frontend.network

import eina.unizar.frontend.models.BusquedaResponse
import eina.unizar.frontend.models.ActualizarEstadoRequest
import eina.unizar.frontend.models.CrearIncidenciaRequest
import eina.unizar.frontend.models.CrearIncidenciaResponse
import eina.unizar.frontend.models.IncidenciaResponse
import eina.unizar.frontend.models.EstadisticasData
import eina.unizar.frontend.models.EstadoSuscripcionResponse
import eina.unizar.frontend.models.FotoPerfilResponse
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
import eina.unizar.frontend.models.ListaIncidenciasResponse
import eina.unizar.frontend.models.NuevoRepostajeData
import eina.unizar.frontend.models.NuevoViajeData
import eina.unizar.frontend.models.ProximoRepostajeResponse
import eina.unizar.frontend.models.ViajesResponse
import eina.unizar.frontend.models.ResumenRepostajesResponse
import eina.unizar.frontend.models.RevisionRequest
import eina.unizar.frontend.models.RevisionResponse
import eina.unizar.frontend.models.RevisionesListResponse
import eina.unizar.frontend.models.LogrosResponse
import eina.unizar.frontend.models.LogrosUsuarioResponse
import eina.unizar.frontend.models.PagoResponse
import eina.unizar.frontend.models.ProcesarPagoRequest
import eina.unizar.frontend.models.VerificarAnuncioResponse
import eina.unizar.frontend.models.VerificarProgresoResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
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
    data class IconoResponse(val iconoUrl: String, val message: String)

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

    /**
     * Obtiene las revisiones de un vehículo.
     *
     * Endpoint: GET /revision/obtenerRevisiones/{vehiculoId}
     * Requiere autenticación.
     *
     * @param vehiculoId Identificador del vehículo
     * @param tipo Tipo de revisión (opcional)
     * @param token Token de autenticación JWT (formato: "Bearer {token}")
     * @return Call<RevisionesListResponse> Respuesta con la lista de revisiones
     */
    @GET("revision/obtenerRevisiones/{vehiculoId}")
    fun obtenerRevisiones(
        @Path("vehiculoId") vehiculoId: String,
        @Query("tipo") tipo: String?,
        @Header("Authorization") token: String
    ): Call<RevisionesListResponse>

    /**
     * Registra una nueva revisión para un vehículo.
     *
     * Endpoint: POST /revision/registrar
     * Requiere autenticación.
     *
     * @param token Token de autenticación JWT (formato: "Bearer {token}")
     * @param revision Datos de la revisión a registrar
     * @return Call<RevisionResponse> Respuesta con los datos de la revisión registrada
     */
    @POST("revision/registrar")
    fun registrarRevision(
        @Header("Authorization") token: String,
        @Body revision: RevisionRequest
    ): Call<RevisionResponse>

    // ========== INCIDENCIAS ==========

    /**
     * Crear una nueva incidencia.
     * POST /api/incidencias/crear
     */
    @POST("incidencia/crear")
    suspend fun crearIncidencia(
        @Header("Authorization") token: String,
        @Body request: CrearIncidenciaRequest
    ): Response<CrearIncidenciaResponse>

    /**
     * Obtener todas las incidencias de un vehículo específico.
     * GET /api/incidencias/vehiculo/:vehiculoId
     */
    @GET("incidencia/vehiculo/{vehiculoId}")
    suspend fun obtenerIncidenciasVehiculo(
        @Header("Authorization") token: String,
        @Path("vehiculoId") vehiculoId: String
    ): Response<ListaIncidenciasResponse>

    /**
     * Obtener todas las incidencias de los vehículos del usuario.
     * GET /api/incidencias/usuario
     */
    @GET("incidencia/usuario")
    suspend fun obtenerIncidenciasUsuario(
        @Header("Authorization") token: String
    ): Response<ListaIncidenciasResponse>

    /**
     * Obtener una incidencia específica.
     * GET /api/incidencias/:incidenciaId
     */
    @GET("incidencia/{incidenciaId}")
    suspend fun obtenerIncidencia(
        @Header("Authorization") token: String,
        @Path("incidenciaId") incidenciaId: Int
    ): Response<IncidenciaResponse>

    /**
     * Actualizar solo el estado de una incidencia.
     * PATCH /api/incidencias/:incidenciaId/estado
     */
    @PATCH("incidencia/{incidenciaId}/estado")
    suspend fun actualizarEstadoIncidencia(
        @Header("Authorization") token: String,
        @Path("incidenciaId") incidenciaId: Int,
        @Body request: ActualizarEstadoRequest
    ): Response<CrearIncidenciaResponse>

    /**
     * Actualizar una incidencia completa.
     * PUT /api/incidencias/:incidenciaId
     */
    @PUT("incidencia/{incidenciaId}")
    suspend fun actualizarIncidencia(
        @Header("Authorization") token: String,
        @Path("incidenciaId") incidenciaId: Int,
        @Body request: CrearIncidenciaRequest
    ): Response<CrearIncidenciaResponse>

    /**
     * Eliminar una incidencia.
     * DELETE /api/incidencias/:incidenciaId
     */
    @DELETE("incidencia/{incidenciaId}")
    suspend fun eliminarIncidencia(
        @Header("Authorization") token: String,
        @Path("incidenciaId") incidenciaId: Int
    ): Response<MensajeResponse>

    /**
     * Obtiene las estadísticas de un vehículo para un mes y año específicos.
     *
     * Endpoint: GET /estadisticas/{vehiculoId}
     * Requiere autenticación.
     * Método suspendido para uso con coroutines.
     *
     * @param vehiculoId Identificador del vehículo
     * @param mes Mes para el cual se solicitan las estadísticas
     * @param ano Año para el cual se solicitan las estadísticas
     * @param token Token de autenticación JWT (formato: "Bearer {token}")
     * @return Response<EstadisticasData> Respuesta con los datos estadísticos
     */
    @GET("estadisticas/{vehiculoId}")
    suspend fun getEstadisticas(
        @Path("vehiculoId") vehiculoId: String,
        @Query("mes") mes: Int,
        @Query("ano") ano: Int,
        @Header("Authorization") token: String
    ): Response<EstadisticasData>

    /**
     * Realiza una búsqueda global en los datos asociados a un vehículo.
     *
     * Endpoint: GET /busqueda/global/{vehiculoId}
     * Requiere autenticación.
     * Método suspendido para uso con coroutines.
     *
     * @param vehiculoId Identificador del vehículo
     * @param query Término de búsqueda
     * @param token Token de autenticación JWT (formato: "Bearer {token}")
     * @return Response<BusquedaResponse> Respuesta con los resultados de la búsqueda
     */
    @GET("busqueda/global/{vehiculoId}")
    suspend fun busquedaGlobal(
        @Header("Authorization") token: String,
        @Path("vehiculoId") vehiculoId: String,
        @Query("query") query: String
    ): Response<BusquedaResponse>

    /**
     * Obtener todos los logros disponibles (activos).
     */
    @GET("logro")
    fun obtenerTodosLosLogros(
        @Header("Authorization") token: String
    ): Call<LogrosResponse>

    /**
     * Obtener logros de un usuario específico con su progreso.
     */
    @GET("logro/usuario/{usuarioId}")
    fun obtenerLogrosUsuario(
        @Header("Authorization") token: String,
        @Path("usuarioId") usuarioId: Int
    ): Call<LogrosUsuarioResponse>

    /**
     * Verificar y actualizar el progreso de logros de un usuario.
     * Retorna los logros recién desbloqueados.
     */
    @POST("logro/verificar/{usuarioId}")
    fun verificarProgresoLogros(
        @Header("Authorization") token: String,
        @Path("usuarioId") usuarioId: Int
    ): Call<VerificarProgresoResponse>

    @Multipart
    @POST("/vehiculo/{vehiculoId}/icono")
    suspend fun subirIconoVehiculo(
        @Header("Authorization") token: String,
        @Path("vehiculoId") vehiculoId: String,
        @Part icono: MultipartBody.Part
    ): Response<IconoResponse>

    @GET("/vehiculo/{vehiculoId}/icono")
    suspend fun obtenerIconoVehiculo(
        @Path("vehiculoId") vehiculoId: String,
        @Header("Authorization") token: String
    ): retrofit2.Response<IconoResponse>

    @DELETE("/vehiculo/{vehiculoId}/icono")
    suspend fun eliminarIconoVehiculo(
        @Header("Authorization") token: String,
        @Path("vehiculoId") vehiculoId: String
    ): Response<Void>

    // Suscripciones
    /**
     * Procesa el pago de una suscripción.
     * POST /api/suscripciones/procesar-pago
     */
    @POST("suscripcion/procesar-pago")
    suspend fun procesarPago(
        @Header("Authorization") token: String,
        @Body request: ProcesarPagoRequest
    ): Response<PagoResponse>

    /**
     * Obtiene el estado de la suscripción del usuario.
     * GET /api/suscripciones/estado
     */
    @GET("suscripcion/estado")
    suspend fun obtenerEstadoSuscripcion(
        @Header("Authorization") token: String
    ): Response<EstadoSuscripcionResponse>

    /**
     * Verifica si hay anuncios disponibles para el usuario.
     * GET /api/suscripciones/verificar-anuncio
     */
    @GET("suscripcion/verificar-anuncio")
    suspend fun verificarAnuncio(
        @Header("Authorization") token: String
    ): Response<VerificarAnuncioResponse>

    /**
     * Actualiza el estado de un vehículo.
     *
     * Endpoint: PATCH /api/vehiculos/{id}/estado
     * Requiere autenticación.
     * Método suspendido para uso con coroutines.
     *
     * @param token Token de autenticación JWT (formato: "Bearer {token}")
     * @param vehiculoId Identificador del vehículo
     * @param requestBody Cuerpo de la solicitud con el nuevo estado
     * @return Response<ResponseBody> Respuesta HTTP con código de estado
     */
    @PATCH("vehiculo/{id}/estado")
    suspend fun actualizarEstadoVehiculo(
        @Header("Authorization") token: String,
        @Path("id") vehiculoId: String,
        @Body requestBody: Map<String, String> // Body: { "estado": "activo" | "inactivo" | "en_mantenimiento" }
    ): Response<ResponseBody>


    /**
     * Sube o actualiza la foto de perfil del usuario.
     * * Endpoint: PUT /usuario/perfil/foto
     * Requiere autenticación y el envío de un archivo Multipart.
     * * @param token Token de autenticación JWT (formato: "Bearer {token}")
     * @param foto Parte del cuerpo multipart que contiene el archivo de imagen.
     * @return Response<FotoPerfilResponse> Respuesta con la URL de la nueva foto.
     */
    @Multipart
    @PUT("usuario/perfil/foto") // La ruta definida en el backend
    suspend fun actualizarFotoPerfil(
        @Header("Authorization") token: String,
        @Part foto: MultipartBody.Part // El nombre del campo debe ser 'foto'
    ): Response<FotoPerfilResponse>

}