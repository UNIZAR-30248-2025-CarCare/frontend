package eina.unizar.frontend

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import java.time.LocalDate
import java.time.YearMonth

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()


    // --- Datos de Ejemplo para Home y otras pantallas ---
    val usuarioEjemplo = Usuario("1", "Juan Pérez", "jp", "juan@eina.com")
    val vehiculoEjemplo = Vehiculo("V01", EstadoVehiculo.DISPONIBLE,"Furgoneta 1", "Z-1234-AZ", TipoVehiculo.FURGONETA) // Añadido EstadoVehiculo para HomeScreen
    val vehiculoEjemplo2 = Vehiculo("V02", EstadoVehiculo.EN_USO, "Camión 2", "B-5678-CX", TipoVehiculo.CAMION) // Añadido EstadoVehiculo
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
            estado = EstadoVehiculo.DISPONIBLE,
            usuariosVinculados = listOf(usuarioEjemplo, usuarioEjemplo)
        )

    // --- Datos de Ejemplo (Mantenerlos para que las pantallas compilen) ---
    val reservasEjemplo = listOf(
        Reserva(
            id = "R01",
            usuario = usuarioEjemplo,
            vehiculo = vehiculoEjemplo,
            fecha = LocalDate.now(), // Para que aparezca en el día actual
            horaInicio = "09:00",
            horaFin = "11:00",
            tipo = TipoReserva.TRABAJO,
            estado = EstadoReserva.CONFIRMADA
        )
    )
    // ---------------------------------------------------------------------


    NavHost(
        navController = navController,
        startDestination = "inicio"
    ) {
        composable("inicio") {
            PantallaPrincipal {
                navController.navigate("eleccion")
            }
        }
        composable("eleccion") {
            PantallaEleccionInicio(
                onLoginClick = { /* lógica de login */ },
                onRegisterClick = {
                    navController.navigate("registro")
                },
                onForgotPasswordClick = { /* lógica extra */ }
            )
        }

        composable("registro") {
            RegistroUsuarioScreen(
                onBackClick = { navController.popBackStack() },
                onRegisterClick = { /* acción de registro */ },
                onLoginClick = {
                    //Ir al login
                    navController.navigate("home")
                }
            )
        }

        composable("home") {
            HomeScreen(
                userName = usuarioEjemplo.nombre, // Usamos el nombre del usuario de ejemplo
                vehiculos = vehiculosDisponibles, // Pasamos la lista de vehículos

                // Callbacks de navegación de la pantalla:
                onVehiculoClick = { vehiculoId ->
                    navController.navigate("vehiculo_detalle/$vehiculoId")
                },
                onAddVehiculoClick = {
                    navController.navigate("add_vehiculo")
                },
                onMapaClick = {
                    // navController.navigate("mapa")
                },
                onCalendarioClick = {
                    navController.navigate("reservas")
                },
                onIncidenciasClick = {
                    navController.navigate("incidencias")
                },

                // Bottom Navigation Logic:
                selectedTab = 0,
                onTabSelected = {

                }
            )
        }

        // ----------------------------------------------------------
        // *** RUTA PARA AÑADIR VEHÍCULO ***
        composable("add_vehiculo") {
            AddVehiculoScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onAddClick = { vehiculoData ->
                    // Lógica para enviar 'vehiculoData' al backend o ViewModel
                    println("Vehículo a añadir: $vehiculoData")
                    // Después de un guardado exitoso, vuelve a la pantalla anterior
                    navController.popBackStack()
                }
            )
        }
        // ----------------------------------------------------------

        // ----------------------------------------------------------
        // *** NUEVA RUTA DE DETALLE DE VEHÍCULO ***
        composable(
            route = "vehiculo_detalle/{vehiculoId}",
            arguments = listOf(navArgument("vehiculoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val vehiculoId = backStackEntry.arguments?.getString("vehiculoId") ?: "V01" // Default por si acaso

            // Simular obtener los datos del vehículo por ID
            val vehiculoDetalle = vehiculoDetalleEjemplo

            DetalleVehiculoScreen(
                vehiculo = vehiculoDetalle,
                onBackClick = { navController.popBackStack() },
                onVerMapaClick = {
                    // Aquí iría la navegación a la pantalla de mapa
                    println("Navegar a mapa para el vehículo: $vehiculoId")
                },
                onAddUsuarioClick = {
                    // Aquí iría la navegación a la pantalla para vincular usuarios
                    println("Navegar a vincular usuario para el vehículo: $vehiculoId")
                }
            )
        }
        // ----------------------------------------------------------


        // --- NUEVA RUTA DE INCIDENCIAS ---
        composable("incidencias") {
            IncidenciasScreen(
                vehiculoSeleccionado = vehiculoEjemplo,
                incidenciasActivas = incidenciasActivas,
                incidenciasResueltas = incidenciasResueltas,
                onBackClick = { navController.popBackStack() },
                onVehiculoClick = { /* Lógica para cambiar vehículo */ },
                onIncidenciaClick = { incidenciaId ->
                    // Lógica para ver detalles
                    println("Ver incidencia: $incidenciaId")
                },
                onAddIncidenciaClick = { navController.navigate("add_incidencia") },
                selectedTab = 0,
                onTabSelected = { navController.navigate("reservas") }
                    // Lógica para cambiar de pestaña si es necesario en el ViewModel
            )
        }
        // ------------------------------------

        // === NUEVA RUTA PARA REPORTAR INCIDENCIA ===
        composable("add_incidencia") {
            NuevaIncidenciaScreen(
                vehiculos = vehiculosDisponibles, // Puedes usar la misma lista de ejemplo
                onBackClick = { navController.popBackStack() },
                onReportarIncidencia = { nuevaIncidenciaData ->
                    // Lógica para enviar los datos al backend
                    println("Incidencia reportada: $nuevaIncidenciaData")
                    navController.popBackStack() // Volver a la pantalla anterior
                }
            )
        }
        // ===========================================

        // --- RUTA DE RESERVAS/CALENDARIO  ---
        composable("reservas") { // "reservas"
            CalendarioScreen(
                vehiculoSeleccionado = vehiculoEjemplo,
                reservas = reservasEjemplo,
                mesActual = YearMonth.now(),
                diaSeleccionado = LocalDate.now(),
                onBackClick = { navController.popBackStack() },
                onVehiculoClick = { /* Lógica */ },
                onMesAnterior = { /* Lógica */ },
                onMesSiguiente = { /* Lógica */ },
                onDiaClick = { /* Lógica */ },
                onAddReservaClick = {
                    navController.navigate("nueva_reserva")
                },
                selectedTab = 2, // Índice de la pestaña 'Reservas'
                onTabSelected = {  } // Usar la función de navegación
            )
        }

        // --- NUEVA RUTA DE RESERVA ---
        composable("nueva_reserva") {
            // Asegúrate de usar @RequiresApi si es necesario
            NuevaReservaScreen(
                vehiculos = vehiculosDisponibles, // Pasar los vehículos disponibles
                onBackClick = { navController.popBackStack() },
                onCrearReserva = { nuevaReservaData ->
                    // Aquí iría la lógica para enviar los datos al backend
                    println("Reserva creada: $nuevaReservaData")
                    navController.popBackStack() // Volver al calendario tras crear
                }
            )
        }

    }
}
