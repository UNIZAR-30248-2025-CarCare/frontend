package eina.unizar.frontend

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import eina.unizar.frontend.models.Vehiculo


@SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
@RunWith(AndroidJUnit4::class)
class ReservaScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val vehiculosPrueba = listOf(
        Vehiculo(
            id = "v1",
            nombre = "Coche de Prueba",
            matricula = "1234ABC",
            tipo = TipoVehiculo.COCHE,
            estado = EstadoVehiculo.DISPONIBLE,
            modelo = "Model 3",
            fabricante = "Tesla",
            antiguedad = 2,
            tipo_combustible = "Eléctrico",
            litros_combustible = 0.0f,
            consumo_medio = 0.15f,
            ubicacion_actual = null,
            usuariosVinculados = emptyList()
        )
    )

    /**
     * Test 1: Happy Path - Verificar que todos los campos se pueden llenar correctamente
     * NOTA: Este test NO hace click en "Crear Reserva" porque eso requiere una llamada API real.
     * Solo verifica que la UI funciona correctamente.
     */
    @Test
    fun testReservaExitosa_DatosValidos() {
        composeTestRule.setContent {
            NuevaReservaScreen(
                vehiculos = vehiculosPrueba,
                onBackClick = {},
                onCrearReserva = {}
            )
        }

        // PASO 1: Verificar que el vehículo está seleccionado
        composeTestRule.onNodeWithText("Coche de Prueba - 1234ABC")
            .assertIsDisplayed()

        // PASO 2: Verificar tipo de reserva (Trabajo está por defecto)
        composeTestRule.onNodeWithText("Trabajo")
            .assertIsDisplayed()

        // PASO 3: Modificar hora de inicio
        composeTestRule.onAllNodes(hasSetTextAction())[0]
            .performTextClearance()
        composeTestRule.onAllNodes(hasSetTextAction())[0]
            .performTextInput("10:00")

        composeTestRule.waitForIdle()

        // PASO 4: Modificar hora de fin
        composeTestRule.onAllNodes(hasSetTextAction())[1]
            .performTextClearance()
        composeTestRule.onAllNodes(hasSetTextAction())[1]
            .performTextInput("12:00")

        composeTestRule.waitForIdle()

        // PASO 5: Añadir notas
        composeTestRule.onNode(hasSetTextAction() and hasText("Añade detalles sobre tu reserva..."))
            .performTextInput("Revisión de la bomba de agua")

        composeTestRule.waitForIdle()

        // PASO 6: Verificar que todos los datos se llenaron correctamente
        composeTestRule.onNodeWithText("10:00").assertExists()
        composeTestRule.onNodeWithText("12:00").assertExists()
        composeTestRule.onNodeWithText("Revisión de la bomba de agua").assertExists()

        // PASO 7: Verificar que el botón está habilitado
        composeTestRule.onNodeWithText("Crear Reserva")
            .assertIsEnabled()

        // NOTA: NO hacemos click en "Crear Reserva" porque requiere una API real
        // En su lugar, verificamos que el formulario está completo y el botón habilitado
    }

    /**
     * Test 2: Cambiar tipo de reserva a Personal
     */
    @Test
    fun testCambiarTipoReserva_Personal() {
        composeTestRule.setContent {
            NuevaReservaScreen(
                vehiculos = vehiculosPrueba,
                onBackClick = {},
                onCrearReserva = {}
            )
        }

        // PASO 1: Trabajo está seleccionado por defecto
        composeTestRule.onNodeWithText("Trabajo")
            .assertIsDisplayed()

        // PASO 2: Cambiar a Personal
        composeTestRule.onNodeWithText("Personal")
            .performClick()

        composeTestRule.waitForIdle()

        // PASO 3: Verificar cambio
        composeTestRule.onNodeWithText("Personal")
            .assertIsDisplayed()
    }

    /**
     * Test 3: Verificar horas por defecto
     */
    @Test
    fun testHoras_ValoresPorDefecto() {
        composeTestRule.setContent {
            NuevaReservaScreen(
                vehiculos = vehiculosPrueba,
                onBackClick = {},
                onCrearReserva = {}
            )
        }

        // Verificar horas predeterminadas (09:00 - 14:00)
        composeTestRule.onAllNodesWithText("09:00")[0].assertIsDisplayed()
        composeTestRule.onAllNodesWithText("14:00")[0].assertIsDisplayed()
    }

    /**
     * Test 4: Verificar disponibilidad inicial
     */
    @Test
    fun testDisponibilidad_MostrarEstadoInicial() {
        composeTestRule.setContent {
            NuevaReservaScreen(
                vehiculos = vehiculosPrueba,
                onBackClick = {},
                onCrearReserva = {}
            )
        }

        // Debería mostrar disponible por defecto
        composeTestRule.onNodeWithText("Horario disponible")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("No hay conflictos con otras reservas")
            .assertIsDisplayed()
    }

    /**
     * Test 5: Verificar botón deshabilitado sin vehículos
     */
    @Test
    fun testCrearReserva_SinVehiculos() {
        composeTestRule.setContent {
            NuevaReservaScreen(
                vehiculos = emptyList(),
                onBackClick = {},
                onCrearReserva = {}
            )
        }

        // El botón debería estar deshabilitado
        composeTestRule.onNodeWithText("Crear Reserva")
            .assertIsNotEnabled()
    }

    /**
     * Test 6: Navegación Volver
     */
    @Test
    fun testNavegacion_BotonVolver() {
        var volverPresionado = false

        composeTestRule.setContent {
            NuevaReservaScreen(
                vehiculos = vehiculosPrueba,
                onBackClick = { volverPresionado = true },
                onCrearReserva = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("Volver").performClick()

        assertTrue("Debería navegar hacia atrás", volverPresionado)
    }

    /**
     * Test 7: Verificar que todos los campos están presentes
     */
    @Test
    fun testPantalla_TodosLosCamposPresentes() {
        composeTestRule.setContent {
            NuevaReservaScreen(
                vehiculos = vehiculosPrueba,
                onBackClick = {},
                onCrearReserva = {}
            )
        }

        // Verificar título
        composeTestRule.onNodeWithText("Nueva Reserva").assertIsDisplayed()

        // Verificar campos
        composeTestRule.onNodeWithText("Vehículo").assertExists()
        composeTestRule.onNodeWithText("Fecha de inicio").assertExists()
        composeTestRule.onNodeWithText("Fecha de fin").assertExists()
        composeTestRule.onNodeWithText("Hora de inicio").assertExists()
        composeTestRule.onNodeWithText("Hora de fin").assertExists()
        composeTestRule.onNodeWithText("Tipo de reserva").assertExists()
        composeTestRule.onNodeWithText("Notas (opcional)").assertExists()

        // Verificar botón
        composeTestRule.onNodeWithText("Crear Reserva").assertExists()
    }
}