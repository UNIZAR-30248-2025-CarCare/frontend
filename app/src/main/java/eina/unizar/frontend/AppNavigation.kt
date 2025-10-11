package eina.unizar.frontend

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.time.LocalDate
import java.time.YearMonth

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()


    // --- Datos de Ejemplo para IncidenciasScreen (SOLO PARA COMPILAR) ---
    val usuarioEjemplo = Usuario("1", "Juan Pérez", "juan@eina.com")
    val vehiculoEjemplo = Vehiculo("V01", "Furgoneta 1", "Z-1234-AZ", TipoVehiculo.FURGONETA)
    val vehiculoEjemplo2 = Vehiculo("V02", "Camión 2", "B-5678-CX", TipoVehiculo.CAMION)
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
                    navController.navigate("incidencias")
                }
            )
        }
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
                onAddIncidenciaClick = { /* Lógica para añadir */ },
                selectedTab = 0,
                onTabSelected = { navController.navigate("reservas") }
                    // Lógica para cambiar de pestaña si es necesario en el ViewModel
            )
        }
        // ------------------------------------

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
