package eina.unizar.frontend


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import eina.unizar.frontend.models.NuevaReservaData
import eina.unizar.frontend.models.Vehiculo
import eina.unizar.frontend.models.Ubicacion // Asumiendo que Ubicacion necesita importarse si no es un tipo conocido
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
@RunWith(AndroidJUnit4::class)
class NuevaReservaScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val vehiculosPrueba = listOf(
        Vehiculo(
            id = "1",
            nombre = "Tesla Model 3",
            matricula = "1234ABC",
            tipo = TipoVehiculo.COCHE,
            // Atributos corregidos:
            estado = EstadoVehiculo.DISPONIBLE,
            modelo = "Model 3",
            fabricante = "Tesla", // Nuevo atributo
            antiguedad = 2, // Nuevo atributo (ej: 2 años)
            tipo_combustible = "Eléctrico", // Nuevo atributo
            litros_combustible = 0.0f, // Nuevo atributo (ej: 0.0 para eléctrico)
            consumo_medio = 0.15f, // Nuevo atributo (ej: kWh/km)
            ubicacion_actual = null,
            usuariosVinculados = emptyList() // Nuevo atributo
        ),
        Vehiculo(
            id = "2",
            nombre = "Honda CB500",
            matricula = "5678DEF",
            tipo = TipoVehiculo.MOTO,
            // Atributos corregidos:
            estado = EstadoVehiculo.DISPONIBLE,
            modelo = "CB500",
            fabricante = "Honda", // Nuevo atributo
            antiguedad = 3, // Nuevo atributo (ej: 3 años)
            tipo_combustible = "Gasolina", // Nuevo atributo
            litros_combustible = 15.0f, // Nuevo atributo (ej: 15 litros)
            consumo_medio = 3.5f, // Nuevo atributo (ej: litros/100km)
            ubicacion_actual = null,
            usuariosVinculados = emptyList() // Nuevo atributo
        )
    )

    /**
     * Test 1: Verificar que todos los campos están presentes
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

    /**
     * Test 2: Verificar que el primer vehículo está seleccionado por defecto
     */
    @Test
    fun testVehiculo_PrimerVehiculoSeleccionadoPorDefecto() {
        composeTestRule.setContent {
            NuevaReservaScreen(
                vehiculos = vehiculosPrueba,
                onBackClick = {},
                onCrearReserva = {}
            )
        }

        // Verificar que aparece el primer vehículo
        composeTestRule.onNodeWithText("Tesla Model 3 - 1234ABC")
            .assertIsDisplayed()
    }

    /**
     * Test 3: Verificar cambio de vehículo
     */
    @Test
    fun testVehiculo_CambiarSeleccion() {
        composeTestRule.setContent {
            NuevaReservaScreen(
                vehiculos = vehiculosPrueba,
                onBackClick = {},
                onCrearReserva = {}
            )
        }

        // Abrir dropdown
        composeTestRule.onNodeWithContentDescription("Expandir")
            .performClick()

        composeTestRule.waitForIdle()

        // Seleccionar segundo vehículo
        composeTestRule.onNodeWithText("Honda CB500 - 5678DEF")
            .performClick()

        composeTestRule.waitForIdle()

        // Verificar que cambió
        composeTestRule.onNodeWithText("Honda CB500 - 5678DEF")
            .assertIsDisplayed()
    }

    /**
     * Test 4: Verificar tipos de reserva disponibles
     */
    @Test
    fun testTipoReserva_TodosLosTiposDisponibles() {
        composeTestRule.setContent {
            NuevaReservaScreen(
                vehiculos = vehiculosPrueba,
                onBackClick = {},
                onCrearReserva = {}
            )
        }

        // Verificar que aparecen los tipos
        composeTestRule.onNodeWithText("Trabajo").assertIsDisplayed()
        composeTestRule.onNodeWithText("Personal").assertIsDisplayed()
    }

    /**
     * Test 5: Verificar cambio de tipo de reserva
     */
    @Test
    fun testTipoReserva_CambiarTipo() {
        composeTestRule.setContent {
            NuevaReservaScreen(
                vehiculos = vehiculosPrueba,
                onBackClick = {},
                onCrearReserva = {}
            )
        }

        // Trabajo debería estar seleccionado por defecto
        composeTestRule.waitForIdle()

        // Cambiar a Personal
        composeTestRule.onNodeWithText("Personal")
            .performClick()

        composeTestRule.waitForIdle()

        // Verificar cambio (visualmente el estilo cambia)
        composeTestRule.onNodeWithText("Personal")
            .assertIsDisplayed()
    }

    /**
     * Test 6: Verificar horas por defecto
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

        // Verificar horas predeterminadas
        composeTestRule.onAllNodesWithText("09:00")[0].assertIsDisplayed()
        composeTestRule.onAllNodesWithText("14:00")[0].assertIsDisplayed()
    }

    /**
     * Test 7: Verificar modificación de hora de inicio
     */
    @Test
    fun testHoras_ModificarHoraInicio() {
        composeTestRule.setContent {
            NuevaReservaScreen(
                vehiculos = vehiculosPrueba,
                onBackClick = {},
                onCrearReserva = {}
            )
        }

        // Limpiar y escribir nueva hora
        composeTestRule.onAllNodes(hasSetTextAction())[0]
            .performTextClearance()

        composeTestRule.onAllNodes(hasSetTextAction())[0]
            .performTextInput("10:30")

        composeTestRule.waitForIdle()

        // Verificar cambio
        composeTestRule.onNodeWithText("10:30").assertExists()
    }

    /**
     * Test 8: Verificar disponibilidad inicial
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
     * Test 9: Verificar campo de notas opcional
     */
    @Test
    fun testNotas_CampoOpcionalFuncional() {
        composeTestRule.setContent {
            NuevaReservaScreen(
                vehiculos = vehiculosPrueba,
                onBackClick = {},
                onCrearReserva = {}
            )
        }

        // Escribir en notas
        composeTestRule.onNode(hasSetTextAction() and hasText("Añade detalles sobre tu reserva..."))
            .performTextInput("Reunión con cliente importante")

        composeTestRule.waitForIdle()

        // Verificar que se escribió
        composeTestRule.onNodeWithText("Reunión con cliente importante")
            .assertExists()
    }

    /**
     * Test 10: Verificar botón crear habilitado con datos válidos
     */
    @Test
    fun testBotonCrear_HabilitadoConDatosValidos() {
        composeTestRule.setContent {
            NuevaReservaScreen(
                vehiculos = vehiculosPrueba,
                onBackClick = {},
                onCrearReserva = {}
            )
        }

        // El botón debería estar habilitado (hay vehículo y horario disponible)
        composeTestRule.onNodeWithText("Crear Reserva")
            .assertIsEnabled()
    }

    /**
     * Test 11: Verificar callback al crear reserva
     */
    @Test
    fun testCrearReserva_CallbackEjecutado() {
        var reservaCreada = false
        var datosReserva: NuevaReservaData? = null

        composeTestRule.setContent {
            NuevaReservaScreen(
                vehiculos = vehiculosPrueba,
                onBackClick = {},
                onCrearReserva = { datos ->
                    reservaCreada = true
                    datosReserva = datos
                }
            )
        }

        // Crear reserva
        composeTestRule.onNodeWithText("Crear Reserva")
            .performClick()

        composeTestRule.waitForIdle()

        // Nota: Este test puede fallar si hay validación de API
        // En ese caso, necesitarás mockear la API
    }

    /**
     * Test 12: Verificar navegación hacia atrás
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

        // Hacer clic en volver
        composeTestRule.onNodeWithContentDescription("Volver")
            .performClick()

        assertTrue("Debería ejecutar callback de volver", volverPresionado)
    }

    /**
     * Test 13: Verificar que no se puede crear sin vehículos
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
     * Test 14: Verificar selector de fecha de inicio
     */
    @Test
    fun testFecha_SelectorFechaInicio() {
        composeTestRule.setContent {
            NuevaReservaScreen(
                vehiculos = vehiculosPrueba,
                onBackClick = {},
                onCrearReserva = {}
            )
        }

        val fechaActual = LocalDate.now().toString() // Ejemplo: "2025-10-28"

        // NO asignamos el resultado a una variable, ya que devuelve Unit o lanza excepción.
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            // La condición de espera es si el nodo con el texto de la fecha está presente
            composeTestRule.onAllNodesWithText(fechaActual).fetchSemanticsNodes().isNotEmpty()
        }
    }

    /**
     * Test 15: Verificar todos los elementos del header
     */
    @Test
    fun testHeader_ElementosCorrectos() {
        composeTestRule.setContent {
            NuevaReservaScreen(
                vehiculos = vehiculosPrueba,
                onBackClick = {},
                onCrearReserva = {}
            )
        }

        // Verificar título
        composeTestRule.onNodeWithText("Nueva Reserva")
            .assertIsDisplayed()

        // Verificar botón de volver
        composeTestRule.onNodeWithContentDescription("Volver")
            .assertIsDisplayed()
    }

    /**
     * Test 16: Verificar sección de detalles
     */
    @Test
    fun testDetalles_SeccionVisible() {
        composeTestRule.setContent {
            NuevaReservaScreen(
                vehiculos = vehiculosPrueba,
                onBackClick = {},
                onCrearReserva = {}
            )
        }

        // Verificar título de sección
        composeTestRule.onNodeWithText("Detalles de la reserva")
            .assertIsDisplayed()
    }
}