package eina.unizar.frontend

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.carcare.ui.UbicacionVehiculoScreen
import eina.unizar.frontend.viewmodels.AuthViewModel
import java.time.LocalDate
import java.time.YearMonth
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import eina.unizar.frontend.models.Ubicacion
import eina.unizar.frontend.models.Vehiculo
import eina.unizar.frontend.models.VehiculoDetalle
import eina.unizar.frontend.models.toVehiculo
import eina.unizar.frontend.models.toVehiculoDetalle
import eina.unizar.frontend.viewmodels.HomeViewModel
import eina.unizar.frontend.models.Parking
import android.content.Intent
import androidx.compose.runtime.setValue
import eina.unizar.frontend.models.SearchResult
import eina.unizar.frontend.viewmodels.SuscripcionViewModel
import eina.unizar.frontend.PersonalizarIconoScreen

/**
 * Composable principal que gestiona la navegación entre pantallas.
 *
 * - Usa NavController para manejar el flujo de pantallas principales.
 * - Obtiene el userId y token desde AuthViewModel o SharedPreferences.
 * - Mantiene la sesión activa mientras se navega entre secciones.
 *
 * También define la clase `NavTab`, que representa las pestañas principales
 * de la barra de navegación inferior:
 * - Inicio
 * - Mapa
 * - Incidencias
 * - Reservas
 *
 * La función `BottomNavigationBar()` dibuja la barra inferior con sus íconos
 * y etiquetas, destacando la pestaña activa.
 */

@SuppressLint("ContextCastToActivity")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(intent: Intent? = null) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val activity = context as ComponentActivity

    LaunchedEffect(intent) {
        intent?.let {
            val navigateTo = it.getStringExtra("navigate_to")
            Log.d("AppNavigation", "Navegación desde notificación: $navigateTo")

            when (navigateTo) {
                "reservation_detail" -> {
                    val reservationId = it.getIntExtra("reservation_id", -1)
                    if (reservationId != -1) {
                        Log.d("AppNavigation", "Navegando a reserva: $reservationId")
                        navController.navigate("home") {
                            popUpTo(0) { inclusive = true }
                        }
                        navController.navigate("reservas")
                    }
                }
                "maintenance_detail" -> {
                    val maintenanceId = it.getIntExtra("maintenance_id", -1)
                    if (maintenanceId != -1) {
                        Log.d("AppNavigation", "Navegando a mantenimiento: $maintenanceId")
                        navController.navigate("home") {
                            popUpTo(0) { inclusive = true }
                        }
                        navController.navigate("incidencias")
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        val sharedPrefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().clear().apply()
        Log.d("AppNavigation", "✅ SharedPreferences limpiadas - ID 6 eliminado")
    }
    
    val authViewModel: AuthViewModel = viewModel(viewModelStoreOwner = activity)
    
    
    // Asegúrate de que los valores se recolectan correctamente
    val userId by authViewModel.userId.collectAsState()
    val token by authViewModel.token.collectAsState()

    // Log para depuración
    Log.d("AppNavigation", "AuthViewModel userId: $userId, token: $token")

    // Revisa si hay datos almacenados en SharedPreferences
    val sharedPrefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    val storedUserId = sharedPrefs.getString("user_id", null)
    val storedToken = sharedPrefs.getString("token", null)
    Log.d("AppNavigation", "SharedPrefs userId: $storedUserId, token: $storedToken")

    // Usa los valores de SharedPreferences si los valores del ViewModel son nulos
    val efectiveUserId = userId ?: storedUserId
    val efectiveToken = token ?: storedToken
    val selectedVehiculo = remember { mutableStateOf<Vehiculo?>(null) }
    val selectedParking = remember { mutableStateOf<Parking?>(null) }

    // *** SISTEMA DE ANUNCIOS ***
    var mostrarAnuncio by remember { mutableStateOf(false) }
    val suscripcionViewModel: SuscripcionViewModel = viewModel(viewModelStoreOwner = activity)
    val estadoSuscripcion by suscripcionViewModel.estadoSuscripcion.collectAsState()

    // Cargar estado de suscripción al iniciar
    LaunchedEffect(efectiveUserId) {
        val token = efectiveToken
        Log.d("AnuncioDebug", "🔍 Token encontrado: ${token != null}")
        if (token != null) {
            suscripcionViewModel.obtenerEstadoSuscripcion(token)
            // Dar tiempo a que se cargue el estado
            kotlinx.coroutines.delay(500)
        }
    }

    // Observar cambios en el estado de suscripción
    LaunchedEffect(estadoSuscripcion) {
        Log.d("AnuncioDebug", "📊 Estado suscripción: ${estadoSuscripcion?.esPremium}")
    }

    // Función para registrar acciones y mostrar anuncio
    fun registrarAccion() {
        val esPremium = estadoSuscripcion?.esPremium ?: false
        Log.d("AnuncioDebug", "🎬 Acción registrada - esPremium: $esPremium")

        if (AnuncioManager.deberMostrarAnuncio(esPremium)) {
            Log.d("AnuncioDebug", "⚠️ MOSTRANDO ANUNCIO")
            mostrarAnuncio = true
        } else {
            Log.d("AnuncioDebug", "✅ Anuncio bloqueado (Premium o contador)")
        }
    }

    // --- Datos de Ejemplo para Home y otras pantallas ---
    val usuarioEjemplo = Usuario("1", "Juan Pérez", "jp", "juan@eina.com")
    val vehiculoEjemplo = Vehiculo(
        id = "V01",
        estado = EstadoVehiculo.INACTIVO,
        nombre = "Furgoneta 1",
        matricula = "Z-1234-AZ",
        tipo = TipoVehiculo.FURGONETA,
        fabricante = "Peugeot",
        modelo = "Boxer",
        antiguedad = 4,
        tipo_combustible = "Diésel",
        litros_combustible = 90.0f,
        consumo_medio = 7.5f,
        ubicacion_actual = Ubicacion(41.6488, -0.8891),
        usuariosVinculados = listOf("David Borrel"),
        usuarioActivoId = null
    )

    val vehiculoEjemplo2 = Vehiculo(
        id = "V02",
        estado = EstadoVehiculo.ACTIVO,
        nombre = "Camión 2",
        matricula = "B-5678-CX",
        tipo = TipoVehiculo.CAMION,
        fabricante = "Mercedes",
        modelo = "Actros",
        antiguedad = 2,
        tipo_combustible = "Diésel",
        litros_combustible = 300.0f,
        consumo_medio = 24.0f,
        ubicacion_actual = Ubicacion(41.6500, -0.8800),
        usuariosVinculados = listOf("Ana García"),
        usuarioActivoId = null
    )
    val vehiculosDisponibles = listOf(vehiculoEjemplo, vehiculoEjemplo2)
    val incidenciaActivaEjemplo = Incidencia(
        id = "I01",
        titulo = "Rueda pinchada",
        descripcion = "La rueda delantera derecha está pinchada.",
        tipo = TipoIncidencia.AVERIA,
        prioridad = PrioridadIncidencia.ALTA,
        reportadoPor = usuarioEjemplo,
        fecha = LocalDate.now(),
        vehiculo = vehiculoEjemplo,
        estado = EstadoIncidencia.ACTIVA
    )
    val incidenciaResueltaEjemplo = Incidencia(
        id = "I02",
        titulo = "Revisión 100k",
        descripcion = "Mantenimiento anual de la flota.",
        tipo = TipoIncidencia.MANTENIMIENTO,
        prioridad = PrioridadIncidencia.BAJA,
        reportadoPor = usuarioEjemplo,
        fecha = LocalDate.now().minusMonths(1),
        vehiculo = vehiculoEjemplo,
        estado = EstadoIncidencia.RESUELTA
    )



    val incidenciasActivas = listOf(incidenciaActivaEjemplo)
    val incidenciasResueltas = listOf(incidenciaResueltaEjemplo)
    // ---------------------------------------------------------------------

    // *** NUEVOS DATOS DE EJEMPLO PARA DetalleVehiculoScreen ***
        val vehiculoDetalleEjemplo = VehiculoDetalle(
            id = "V01",
            nombre = "Furgoneta 1",
            matricula = "Z-1234-AZ",
            fabricante = "Peugeot",
            modelo = "Boxer",
            anio = 2020,
            combustible = "Diésel",
            capacidadDeposito = 90,
            consumoMedio = 7.5,
            tipo = TipoVehiculo.FURGONETA,
            estado = EstadoVehiculo.INACTIVO,
            usuariosVinculados = listOf("David Borrel"),
            usuarioActivoId = null
        )

    // ---------------------------------------------------------------------


    NavHost(
        navController = navController,
        startDestination = if (efectiveUserId == null || efectiveToken == null) "inicio" else "home"
    ) {
        composable("inicio") {
            PantallaPrincipal {
                navController.navigate("eleccion")
            }
        }
        composable("eleccion") {
            PantallaEleccionInicio(
                onLoginClick = { navController.navigate("home") },
                onRegisterClick = {
                    navController.navigate("registro")
                },
                onForgotPasswordClick = { /* lógica extra */ }
            )
        }

        composable("registro") {
            RegistroUsuarioScreen(
                onBackClick = { navController.popBackStack() },
                onRegisterClick = { navController.navigate("eleccion") },
                onLoginClick = {
                    //Ir al login
                    navController.navigate("eleccion")
                }
            )
        }

        composable("home") {
            Log.d(
                "AppNavigation",
                "Accediendo a home userId: $efectiveUserId, token: $efectiveToken"
            )
            if (efectiveUserId != null && efectiveToken != null) {
                Log.d(
                    "AppNavigation",
                    "Navegando a Home con userId: $efectiveUserId y token: $efectiveToken"
                )
                HomeScreenWrapper(
                    userId = efectiveUserId,
                    token = efectiveToken,
                    vehiculos = vehiculosDisponibles,

                    // Callbacks de navegación de la pantalla:
                    onVehiculoClick = { vehiculoId: String ->
                        registrarAccion()
                        selectedVehiculo.value = vehiculosDisponibles.find { it.id == vehiculoId }
                        navController.navigate("vehiculo_detalle/$vehiculoId")
                    },
                    onAddVehiculoClick = {
                        navController.navigate("add_vehiculo/$efectiveUserId/$efectiveToken")
                    },
                    onMapaClick = {
                        navController.navigate("mapa")
                    },
                    onCalendarioClick = {
                        navController.navigate("reservas")
                    },
                    onIncidenciasClick = {
                        navController.navigate("incidencias")
                    },
                    selectedTab = 0,
                    onTabSelected = { /* lógica */ },
                    navController = navController,
                    authViewModel = authViewModel,
                    onViajesClick = {
                        navController.navigate("viajes")
                    },
                    onRepostajesClick = { navController.navigate("repostajes") },
                    onRevisionesClick = { navController.navigate("revisiones") },
                    onEstadisticasClick = { navController.navigate("estadisticas") },
                    onBusquedaClick = { navController.navigate("busqueda") },
                    onLogrosClick = { navController.navigate("logros") },
                    onParkingClick = { navController.navigate("parkings")
                    }
                )
            }
        }

        composable("editarFotoPerfil") {
            // Usa collectAsState() porque token es un StateFlow
            val token by authViewModel.token.collectAsState()

            if (token != null) {
                EditarFotoPerfilScreen(
                    navController = navController,
                    token = token!!,
                    perfilViewModel = viewModel() // Inyección simple con viewModel()
                )
            } else {
                // Redirigir a login si no hay token
                LaunchedEffect(Unit) {
                    navController.navigate("eleccion") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            }
        }

        composable("mapa") {
            registrarAccion()
            UbicacionVehiculoScreen(
                onBackClick = { navController.popBackStack() },
                navController = navController,
                efectiveUserId = efectiveUserId,
                efectiveToken = efectiveToken
            )

        }

        // ----------------------------------------------------------
        // *** RUTA PARA AÑADIR VEHÍCULO ***
        composable(
            route = "add_vehiculo/{userId}/{token}",
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType },
                navArgument("token") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val token = backStackEntry.arguments?.getString("token") ?: ""
            AddVehiculoScreen(
                userId = userId,
                token = token,
                onBackClick = { navController.popBackStack() },
                onAddClick = { navController.navigate("home") }
            )
        }
        // ----------------------------------------------------------

        // ----------------------------------------------------------
        // *** NUEVA RUTA DE DETALLE DE VEHÍCULO ***
        composable(
            route = "vehiculo_detalle/{vehiculoId}",
            arguments = listOf(navArgument("vehiculoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val vehiculoId = backStackEntry.arguments?.getString("vehiculoId") ?: ""
            val viewModel: HomeViewModel = viewModel()
            val vehiculos by viewModel.vehiculos.collectAsState()

            if (vehiculos.isEmpty()) {
                // Lanzar fetch si la lista está vacía
                LaunchedEffect(Unit) {
                    if (efectiveUserId != null) {
                        if (efectiveToken != null) {
                            viewModel.fetchVehiculos(efectiveUserId, efectiveToken)
                        }
                    } // Asegúrate de tener este método en tu ViewModel
                }
                Text("Cargando vehículos...")
            } else {
                val vehiculoSeleccionado =
                    vehiculos.map { it.toVehiculo() }.find { it.id == vehiculoId }
                if (vehiculoSeleccionado != null) {
                    DetalleVehiculoScreen(
                        vehiculo = vehiculoSeleccionado.toVehiculoDetalle(),
                        onBackClick = { navController.popBackStack() },
                        onVerMapaClick = { navController.navigate("mapa") },
                        onAddUsuarioClick = { /* lógica */ },
                        efectiveUserId = efectiveUserId,
                        efectiveToken = efectiveToken,
                        navController = navController
                    )
                } else {
                    Text("Vehículo no encontrado")
                }
            }
        }
        // ----------------------------------------------------------
        // --- RUTA PARA EDITAR UN VEHÍCULO ---
        composable(
            route = "editar_vehiculo/{vehiculoId}/{userId}/{token}",
            arguments = listOf(
                navArgument("vehiculoId") { type = NavType.StringType },
                navArgument("userId") { type = NavType.StringType },
                navArgument("token") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val vehiculoId = backStackEntry.arguments?.getString("vehiculoId") ?: ""
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val token = backStackEntry.arguments?.getString("token") ?: ""
            val viewModel: HomeViewModel = viewModel()
            val vehiculos by viewModel.vehiculos.collectAsState()

            val vehiculo = vehiculos.map { it.toVehiculo() }.find { it.id == vehiculoId }
            if (vehiculos.isEmpty()) {
                LaunchedEffect(Unit) {
                    if (userId.isNotEmpty() && token.isNotEmpty()) {
                        viewModel.fetchVehiculos(userId, token)
                    }
                }
                Text("Cargando vehículos...")
            } else if (vehiculo != null) {
                EditVehiculoScreen(
                    vehiculo = vehiculo,
                    userId = userId,
                    token = token,
                    onBackClick = { navController.popBackStack() },
                    onAddClick = { navController.navigate("home") }
                )
            } else {
                Text("Vehículo no encontrado")
            }
        }

        // --- NUEVA RUTA DE INVITACIONES
        composable("invitaciones") {
            registrarAccion()
            InvitacionesScreen(
                usuarioId = efectiveUserId ?: "",
                token = efectiveToken ?: "",
                navController = navController,
                currentRoute = "invitaciones",
                onBackClick = { navController.popBackStack() }
            )
        }


        // --- NUEVA RUTA DE INCIDENCIAS ---
        composable("incidencias") {
            registrarAccion()
            if (efectiveUserId != null && efectiveToken != null) {
                IncidenciasScreen(
                    userId = efectiveUserId,
                    token = efectiveToken,
                    onBackClick = { navController.popBackStack() },
                    onAddIncidenciaClick = {
                        navController.navigate("add_incidencia/$efectiveUserId/$efectiveToken")
                    },
                    navController = navController
                )
            } else {
                // Redirigir al login si no hay autenticación
                LaunchedEffect(Unit) {
                    navController.navigate("eleccion") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }

        // --- RUTA PARA CREAR INCIDENCIA ---
        composable(
            route = "add_incidencia/{userId}/{token}",
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType },
                navArgument("token") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val token = backStackEntry.arguments?.getString("token") ?: ""

            if (userId.isNotEmpty() && token.isNotEmpty()) {
                NuevaIncidenciaScreen(
                    userId = userId,
                    token = token,
                    onBackClick = { navController.popBackStack() },
                    onIncidenciaCreada = {
                        // Volver a la pantalla de incidencias
                        navController.popBackStack()
                    }
                )
            } else {
                // Si no hay userId o token, redirigir al login
                LaunchedEffect(Unit) {
                    navController.navigate("eleccion") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }

        // --- RUTA PARA DETALLE DE INCIDENCIA ---
        composable(
            route = "detalle_incidencia/{incidenciaId}",
            arguments = listOf(
                navArgument("incidenciaId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val incidenciaId = backStackEntry.arguments?.getInt("incidenciaId") ?: -1

            if (efectiveToken != null && incidenciaId != -1) {
                DetalleIncidenciaScreen(
                    incidenciaId = incidenciaId,
                    token = efectiveToken,
                    onBackClick = { navController.popBackStack() }
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate("eleccion") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }

        composable("reservas") {
            registrarAccion()
            val userId = authViewModel.userId.collectAsState().value ?: ""
            val token = authViewModel.token.collectAsState().value ?: ""

            CalendarioScreenWrapper(
                userId = userId,
                token = token,
                vehiculoSeleccionado = selectedVehiculo.value ?: vehiculoEjemplo, // fallback
                onBackClick = { navController.popBackStack() },
                onVehiculoClick = { nuevoVehiculo ->
                    selectedVehiculo.value = nuevoVehiculo
                },
                onAddReservaClick = { navController.navigate("nueva_reserva") },
                navController = navController
            )
        }


        // --- NUEVA RUTA DE RESERVA ---
        composable("nueva_reserva") {
            val userId = authViewModel.userId.collectAsState().value ?: ""
            val token = authViewModel.token.collectAsState().value ?: ""

            Log.d("AppNavigation", "Nueva Reserva - userId: $userId, token: $token")

            CrearReservaWrapper(
                userId = userId,
                token = token,
                onBackClick = {
                    navController.popBackStack()
                },
                onCrearReserva = { nuevaReservaData ->
                    Log.d("NuevaReserva", "Reserva creada: $nuevaReservaData")
                    navController.popBackStack()
                }
            )
        }
        // ------------------------------------
        // --- NUEVA RUTA DE EDICIÓN DE RESERVA ---
        composable(
            route = "editarReserva/{vehiculoId}/{reservaId}",
            arguments = listOf(
                navArgument("vehiculoId") { type = NavType.StringType },
                navArgument("reservaId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val vehiculoId = backStackEntry.arguments?.getString("vehiculoId") ?: ""
            val reservaId = backStackEntry.arguments?.getString("reservaId") ?: ""
            
            Log.d("AppNavigation", "Editar Reserva - vehiculoId: $vehiculoId, reservaId: $reservaId, token: $efectiveToken")
            
            if (efectiveToken != null) {
                EditarReservaScreen(
                    navController = navController,
                    vehiculoId = vehiculoId,
                    reservaId = reservaId,
                    token = efectiveToken
                )
            } else {
                // Redirigir al login si no hay token
                LaunchedEffect(Unit) {
                    navController.navigate("eleccion") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }

        // Ruta para viajes
        composable("viajes") {
            registrarAccion()
            if (efectiveUserId != null && efectiveToken != null) {
                ViajesScreen(
                    onBackClick = { navController.popBackStack() },
                    onViajeClick = { /* lógica */ },
                    onAddViajeClick = { navController.navigate("add_viaje") },
                    navController = navController,
                    efectiveUserId = efectiveUserId,
                    efectiveToken = efectiveToken
                )
            }
        }
        
        composable("logros") {
            registrarAccion()
            if (efectiveUserId != null && efectiveToken != null) {
                LogrosScreen(
                    navController = navController,
                    userId = efectiveUserId, 
                    token = efectiveToken    
                )
            }
        }

        // Ruta para crear viajes
        composable("add_viaje") {
            if (efectiveUserId != null && efectiveToken != null) {
                CrearViajeScreen(
                    onBackClick = { navController.popBackStack() },
                    onCrearViaje = { nuevoViajeData ->
                        // Lógica para enviar los datos al backend
                        println("Viaje creado: $nuevoViajeData")
                        navController.popBackStack() // Volver a la pantalla anterior
                    },
                    efectiveUserId = efectiveUserId,
                    efectiveToken = efectiveToken
                )
            }
        }

        // Ruta para repostajes
        composable("repostajes") {
            registrarAccion()
            if (efectiveUserId != null && efectiveToken != null) {
                RepostajesScreen(
                    onBackClick = { navController.popBackStack() },
                    onAddRepostajeClick = { navController.navigate("add_repostaje") },
                    navController = navController,
                    efectiveUserId = efectiveUserId,
                    efectiveToken = efectiveToken
                )
            }
        }

        // Ruta para crear repostajes
        composable("add_repostaje") {
            if (efectiveUserId != null && efectiveToken != null) {
                CrearRepostajeScreen(
                    onBackClick = { navController.popBackStack() },
                    onCrearRepostaje = { nuevoRepostajeData ->
                        // Lógica para enviar los datos al backend
                        println("Repostaje creado: $nuevoRepostajeData")
                        navController.popBackStack() // Volver a la pantalla anterior
                    },
                    efectiveUserId = efectiveUserId,
                    efectiveToken = efectiveToken
                )
            }
        }

        // Ruta para revisiones
        composable("revisiones") {
            registrarAccion()
            if (efectiveUserId != null && efectiveToken != null) {
                RevisionesScreen(
                    onBackClick = { navController.popBackStack() },
                    onAddRevisionClick = { navController.navigate("add_revision") },
                    navController = navController,
                    efectiveUserId = efectiveUserId,
                    efectiveToken = efectiveToken
                )
            }
        }

        // Ruta para crear revisiones
        composable("add_revision") {
            if (efectiveUserId != null && efectiveToken != null) {
                CrearRevisionScreen(
                    onBackClick = { navController.popBackStack() },
                    efectiveUserId = efectiveUserId,
                    efectiveToken = efectiveToken
                )
            }
        }

        // Ruta para estadisticas
        composable("estadisticas") {
            registrarAccion()
            if (efectiveUserId != null && efectiveToken != null) {
                EstadisticasScreen(
                    navController = navController,
                    efectiveUserId = efectiveUserId,
                    efectiveToken = efectiveToken
                )
            }
        }

        // Ruta para busquedas
        composable("busqueda") {
            registrarAccion()
            if (efectiveToken != null) {
                BusquedaScreen(
                    efectiveUserId = efectiveUserId ?: "",
                    efectiveToken = efectiveToken ?: "",
                    onBackClick = { navController.popBackStack() }
                )
            }
        }

        // Ruta para crear parking
        composable("add_parking") {
            if (efectiveUserId != null && efectiveToken != null) {
                CrearParkingScreen(
                    onBackClick = { navController.popBackStack() },
                    efectiveUserId = efectiveUserId,
                    efectiveToken = efectiveToken
                )
            } else {
                Text("Usuario o token no válido")
            }
        }

        // Ruta para ver lista de parkings
        composable("parkings") {
            if (efectiveUserId != null && efectiveToken != null) {
                ParkingsScreen(
                    userId = efectiveUserId,
                    token = efectiveToken,
                    onBackClick = { navController.popBackStack() },
                    onAddParkingClick = { navController.navigate("add_parking") },
                    onEditParkingClick = { parking ->
                        selectedParking.value = parking
                        navController.navigate("editar_parking")
                    }
                )
            } else {
                Text("Usuario o token no válido")
            }
        }

        // Ruta para editar parking
        composable("editar_parking") {
            if (efectiveUserId != null && efectiveToken != null && selectedParking.value != null) {
                EditarParkingScreen(
                    parking = selectedParking.value!!,
                    onBackClick = { navController.popBackStack() },
                    efectiveToken = efectiveToken,
                    onEditSuccess = {
                        navController.popBackStack()
                    }
                )
            } else {
                Text("Datos inválidos")
            }
        }

        composable("premium") { backStackEntry ->

            PremiumScreen(
                token = efectiveToken ?: "",
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            "personalizar_icono/{vehiculoId}/{vehiculoNombre}/{vehiculoTipo}",
            arguments = listOf(
                navArgument("vehiculoId") { type = NavType.StringType },
                navArgument("vehiculoNombre") { type = NavType.StringType },
                navArgument("vehiculoTipo") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val vehiculoId = backStackEntry.arguments?.getString("vehiculoId") ?: ""
            val vehiculoNombre = backStackEntry.arguments?.getString("vehiculoNombre") ?: ""
            val vehiculoTipo = backStackEntry.arguments?.getString("vehiculoTipo") ?: ""

            PersonalizarIconoScreen(
                vehiculoId = vehiculoId,
                vehiculoNombre = vehiculoNombre,
                vehiculoTipo = vehiculoTipo,
                navController = navController,
                token = efectiveToken ?: "",
                iconoActualUrl = null, // O la URL real si la tienes
                onIconoActualizado = { /* lógica si quieres actualizar la UI */ }
            )
        }
    }
    // *** DIALOG DE ANUNCIO ***
    if (mostrarAnuncio) {
        AnuncioDialog(
            onDismiss = {
                mostrarAnuncio = false
                AnuncioManager.resetearContador()
            },
            navController = navController
        )
    }
}
