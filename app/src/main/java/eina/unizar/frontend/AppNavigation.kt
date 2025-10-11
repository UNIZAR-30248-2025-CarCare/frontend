package eina.unizar.frontend

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // --- Datos de Ejemplo para IncidenciasScreen (SOLO PARA COMPILAR) ---
    val usuarioEjemplo = Usuario("1", "Juan Pérez", "juan@eina.com")
    val vehiculoEjemplo = Vehiculo("V01", "Furgoneta 1", "Z-1234-AZ", TipoVehiculo.FURGONETA)
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
                onTabSelected = { newTab ->
                    // Lógica para cambiar de pestaña si es necesario en el ViewModel
                }
            )
        }
        // ------------------------------------

    }
}
