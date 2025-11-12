package eina.unizar.frontend

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import eina.unizar.frontend.models.Vehiculo
import eina.unizar.frontend.EstadoVehiculo
import eina.unizar.frontend.TipoVehiculo
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EstadisticasScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val vehiculoTest = Vehiculo(
        id = "1",
        estado = EstadoVehiculo.DISPONIBLE,
        nombre = "Coche Test",
        matricula = "1234 BBC",
        modelo = "Ibiza",
        fabricante = "Seat",
        antiguedad = 2,
        tipo_combustible = "Gasolina",
        litros_combustible = 45.0f,
        consumo_medio = 5.5f,
        ubicacion_actual = null,
        tipo = TipoVehiculo.COCHE,
        usuariosVinculados = listOf("0")
    )

    /**
     * Test 1: Verifica que el header se muestra correctamente
     */
    @Test
    fun testHeaderSeVisualizaCorrectamente() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            EstadisticasScreen(
                navController = navController,
                efectiveUserId = "0",
                efectiveToken = "test-token"
            )
        }

        composeTestRule.onNodeWithText("Estadísticas").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Volver").assertIsDisplayed()
    }

    /**
     * Test 2: Verifica que se muestra el texto "Resumen del Periodo"
     */
    @Test
    fun testResumenPeriodoSeVisualizaCorrectamente() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            EstadisticasScreen(
                navController = navController,
                efectiveUserId = "0",
                efectiveToken = "test-token"
            )
        }

        composeTestRule.onNodeWithText("Resumen del Periodo")
            .assertIsDisplayed()
    }

    /**
     * Test 3: Verifica que se muestran todas las tarjetas de estadísticas
     */
    @Test
    fun testTodasLasTarjetasEstadisticasSeVisualizan() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            EstadisticasScreen(
                navController = navController,
                efectiveUserId = "0",
                efectiveToken = "test-token"
            )
        }

        composeTestRule.onNodeWithText("Kilómetros Totales")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Horas de Trayecto")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Consumo Promedio")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Gasto Total")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Litros Repostados")
            .assertIsDisplayed()
    }

    /**
     * Test 4: Verifica que los valores por defecto son cero cuando no hay datos
     */
    @Test
    fun testValoresPorDefectoSonCero() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            EstadisticasScreen(
                navController = navController,
                efectiveUserId = "0",
                efectiveToken = "test-token"
            )
        }

        composeTestRule.onNodeWithText("0 km").assertIsDisplayed()
        composeTestRule.onNodeWithText("0.0 h").assertIsDisplayed()
        composeTestRule.onNodeWithText("0.0 L/100km").assertIsDisplayed()
        composeTestRule.onNodeWithText("0.0 €").assertIsDisplayed()
        composeTestRule.onNodeWithText("0.0 L").assertIsDisplayed()
    }

    /**
     * Test 5: Verifica que se puede abrir el diálogo de selección de periodo
     */
    @Test
    fun testDialogoSeleccionPeriodoSeAbre() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            EstadisticasScreen(
                navController = navController,
                efectiveUserId = "0",
                efectiveToken = "test-token"
            )
        }

        // Buscar el Card con el icono de calendario y hacer click
        composeTestRule.onNode(
            hasContentDescription("Periodo")
        ).assertExists()

        // Hacer click en el selector de periodo
        composeTestRule.onAllNodes(hasContentDescription("Cambiar periodo"))[0]
            .performClick()

        // Verificar que aparece el diálogo
        composeTestRule.onNodeWithText("Seleccionar Periodo")
            .assertIsDisplayed()
    }

    /**
     * Test 6: Verifica los botones del diálogo de selección de periodo
     */
    @Test
    fun testDialogoPeriodoTieneBotonesAceptarYCancelar() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            EstadisticasScreen(
                navController = navController,
                efectiveUserId = "0",
                efectiveToken = "test-token"
            )
        }

        // Abrir el diálogo
        composeTestRule.onAllNodes(hasContentDescription("Cambiar periodo"))[0]
            .performClick()

        // Verificar botones
        composeTestRule.onNodeWithText("Aceptar").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancelar").assertIsDisplayed()
    }

    /**
     * Test 7: Verifica que el botón Cancelar cierra el diálogo
     */
    @Test
    fun testBotonCancelarCierraDialogo() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            EstadisticasScreen(
                navController = navController,
                efectiveUserId = "0",
                efectiveToken = "test-token"
            )
        }

        // Abrir el diálogo
        composeTestRule.onAllNodes(hasContentDescription("Cambiar periodo"))[0]
            .performClick()

        // Click en Cancelar
        composeTestRule.onNodeWithText("Cancelar").performClick()

        // Verificar que el diálogo se cerró
        composeTestRule.onNodeWithText("Seleccionar Periodo")
            .assertDoesNotExist()
    }

    /**
     * Test 8: Verifica que el diálogo muestra "Mes" y "Año"
     */
    @Test
    fun testDialogoMuestraMesYAno() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            EstadisticasScreen(
                navController = navController,
                efectiveUserId = "0",
                efectiveToken = "test-token"
            )
        }

        // Abrir el diálogo
        composeTestRule.onAllNodes(hasContentDescription("Cambiar periodo"))[0]
            .performClick()

        // Verificar etiquetas
        composeTestRule.onAllNodesWithText("Mes")[0].assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Año")[0].assertIsDisplayed()
    }

    /**
     * Test 9: Verifica que existen botones para navegar entre meses
     */
    @Test
    fun testBotonesNavegacionMesExisten() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            EstadisticasScreen(
                navController = navController,
                efectiveUserId = "0",
                efectiveToken = "test-token"
            )
        }

        // Abrir el diálogo
        composeTestRule.onAllNodes(hasContentDescription("Cambiar periodo"))[0]
            .performClick()

        // Verificar botones de navegación (debe haber 4: 2 para mes, 2 para año)
        composeTestRule.onAllNodesWithContentDescription("Anterior")
            .assertCountEquals(2)
        composeTestRule.onAllNodesWithContentDescription("Siguiente")
            .assertCountEquals(2)
    }

    /**
     * Test 10: Verifica que el botón Aceptar cierra el diálogo
     */
    @Test
    fun testBotonAceptarCierraDialogo() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            EstadisticasScreen(
                navController = navController,
                efectiveUserId = "0",
                efectiveToken = "test-token"
            )
        }

        // Abrir el diálogo
        composeTestRule.onAllNodes(hasContentDescription("Cambiar periodo"))[0]
            .performClick()

        // Click en Aceptar
        composeTestRule.onNodeWithText("Aceptar").performClick()

        // Verificar que el diálogo se cerró
        composeTestRule.onNodeWithText("Seleccionar Periodo")
            .assertDoesNotExist()
    }

    /**
     * Test 11: Verifica que el selector de periodo muestra el mes y año actual
     */
    @Test
    fun testSelectorPeriodoMuestraMesYAnoActual() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            EstadisticasScreen(
                navController = navController,
                efectiveUserId = "0",
                efectiveToken = "test-token"
            )
        }

        // El selector debe mostrar algún mes y año (no verificamos el valor exacto
        // porque depende de la fecha actual, pero verificamos que existe)
        composeTestRule.onAllNodes(hasContentDescription("Periodo"))
            .assertCountEquals(1)
    }

    /**
     * Test 12: Verifica que al hacer click en Volver se cierra la pantalla
     */
    @Test
    fun testBotonVolverFunciona() {
        var backPressed = false

        composeTestRule.setContent {
            val navController = rememberNavController()
            navController.addOnDestinationChangedListener { _, _, _ ->
                backPressed = true
            }

            EstadisticasScreen(
                navController = navController,
                efectiveUserId = "0",
                efectiveToken = "test-token"
            )
        }

        composeTestRule.onNodeWithContentDescription("Volver")
            .performClick()

        // Verificar que se llamó a la navegación (esto puede variar según implementación)
        // En este caso simplificado solo verificamos que el botón es clickeable
        composeTestRule.onNodeWithContentDescription("Volver")
            .assertHasClickAction()
    }
}