package eina.unizar.frontend

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Before

/**
 * Tests de aceptación para la funcionalidad de Estadísticas
 * Criterios de aceptación:
 * 1. El usuario puede acceder a Estadísticas desde el menú principal
 * 2. Se muestran estadísticas resumidas de vehículos
 * 3. El usuario puede filtrar por mes y año
 * 4. Se muestran: km totales, horas de trayecto, consumo promedio, gasto total y litros totales
 */
@RunWith(AndroidJUnit4::class)
class EstadisticasAcceptanceTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val userEmail = "juan.perez@email.com"
    private val userPassword = "password123"

    @Before
    fun setup() {
        composeTestRule.waitForIdle()
    }

    /**
     * Test 1: Happy Path - Acceder a estadísticas y verificar que se muestran todos los elementos
     * Criterios verificados:
     * - Acceso desde menú principal
     * - Visualización de todas las estadísticas
     */
    @Test
    fun happyPath_accederEstadisticasYVerificarElementos() {
        // PASO 1: Iniciar sesión
        iniciarSesion()

        // PASO 2: Navegar a Estadísticas desde el menú principal
        composeTestRule.onNodeWithContentDescription("Estadísticas")
            .assertIsDisplayed()
            .performClick()

        // Esperar a que cargue la pantalla
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Estadísticas")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // PASO 3: Verificar que aparece el header
        composeTestRule.onNodeWithText("Estadísticas")
            .assertIsDisplayed()

        // PASO 4: Verificar que aparece el texto "Resumen del Periodo"
        composeTestRule.onNodeWithText("Resumen del Periodo")
            .assertIsDisplayed()

        // PASO 5: Verificar que aparecen todas las tarjetas de estadísticas
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

        // PASO 6: Cerrar sesión
        cerrarSesion()
    }

    /**
     * Test 2: Verificar que se muestran valores numéricos en las estadísticas
     * Criterio verificado:
     * - Las estadísticas muestran valores (aunque sean 0)
     */
    @Test
    fun verificarValoresEstadisticasSeMuestran() {
        iniciarSesion()

        // Navegar a Estadísticas
        composeTestRule.onNodeWithContentDescription("Estadísticas")
            .performClick()

        // Esperar carga
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Estadísticas")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Verificar que los valores se muestran (buscar patrones de km, h, L/100km, €, L)
        composeTestRule.onAllNodes(hasTextThat(contains = "km"))
            .assertCountEquals(2)

        composeTestRule.onAllNodes(hasTextThat(contains = "h"))
            .assertCountEquals(2)

        composeTestRule.onAllNodes(hasTextThat(contains = "L/100km"))
            .onFirst()
            .assertIsDisplayed()

        composeTestRule.onAllNodes(hasTextThat(contains = "€"))
            .onFirst()
            .assertIsDisplayed()

        composeTestRule.onAllNodes(hasTextThat(contains = "L"))
            .assertCountEquals(6)

        cerrarSesion()
    }

    /**
     * Test 3: Cambiar el periodo de consulta (mes y año)
     * Criterio verificado:
     * - El usuario puede filtrar estadísticas por mes y año
     */
    @Test
    fun cambiarPeriodoConsulta() {
        iniciarSesion()

        // Navegar a Estadísticas
        composeTestRule.onNodeWithContentDescription("Estadísticas")
            .performClick()

        // Esperar carga
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Estadísticas")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // PASO 1: Abrir el selector de periodo
        composeTestRule.onAllNodes(hasContentDescription("Cambiar periodo"))
            .onFirst()
            .performClick()

        // PASO 2: Verificar que aparece el diálogo
        composeTestRule.onNodeWithText("Seleccionar Periodo")
            .assertIsDisplayed()

        // PASO 3: Verificar que aparecen las etiquetas Mes y Año
        composeTestRule.onAllNodesWithText("Mes")
            .onFirst()
            .assertIsDisplayed()

        composeTestRule.onAllNodesWithText("Año")
            .onFirst()
            .assertIsDisplayed()

        // PASO 4: Cambiar el mes (hacer click en la flecha derecha)
        composeTestRule.onAllNodesWithContentDescription("Siguiente")
            .onFirst()
            .performClick()

        // PASO 5: Cambiar el año (hacer click en la flecha izquierda del año)
        composeTestRule.onAllNodesWithContentDescription("Anterior")
            .onLast()
            .performClick()

        // PASO 6: Confirmar la selección
        composeTestRule.onNodeWithText("Aceptar")
            .performClick()

        // PASO 7: Verificar que el diálogo se cerró
        composeTestRule.onNodeWithText("Seleccionar Periodo")
            .assertDoesNotExist()

        // PASO 8: Verificar que las estadísticas siguen mostrándose
        composeTestRule.onNodeWithText("Kilómetros Totales")
            .assertIsDisplayed()

        cerrarSesion()
    }

    /**
     * Test 4: Cambiar de vehículo (si hay múltiples vehículos)
     * Criterio verificado:
     * - Se pueden ver estadísticas de diferentes vehículos
     */
    @Test
    fun cambiarVehiculoSiHayMultiples() {
        iniciarSesion()

        // Navegar a Estadísticas
        composeTestRule.onNodeWithContentDescription("Estadísticas")
            .performClick()

        // Esperar carga
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Estadísticas")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Intentar cambiar de vehículo si existe el dropdown
        try {
            composeTestRule.onNodeWithContentDescription("Selector de vehículo")
                .performClick()

            Thread.sleep(500)

            // Seleccionar el segundo vehículo si existe
            composeTestRule.onAllNodesWithTag("vehiculoItem")
                .onLast()
                .performClick()

            // Verificar que las estadísticas siguen mostrándose
            composeTestRule.onNodeWithText("Kilómetros Totales")
                .assertIsDisplayed()
        } catch (e: AssertionError) {
            // Si no hay múltiples vehículos, verificar que al menos se muestra uno
            composeTestRule.onNodeWithText("Estadísticas")
                .assertIsDisplayed()
        }

        cerrarSesion()
    }


    // Función auxiliar para iniciar sesión
    private fun iniciarSesion() {
        composeTestRule.onNodeWithText("Continuar")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.onAllNodesWithText("Email")[0]
            .performTextInput(userEmail)

        composeTestRule.onAllNodesWithText("Contraseña")[0]
            .performTextInput(userPassword)

        composeTestRule.onNodeWithText("Iniciar Sesión")
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Home")
                .fetchSemanticsNodes().isNotEmpty() ||
                    composeTestRule.onAllNodesWithText("Inicio")
                        .fetchSemanticsNodes().isNotEmpty()
        }

        Thread.sleep(2000)
    }

    // Función auxiliar para cerrar sesión
    private fun cerrarSesion() {
        Thread.sleep(500)

        composeTestRule.onNodeWithContentDescription("Volver")
            .performClick()

        Thread.sleep(500)

        // Verificar que llegamos a Home
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            // Ajusta este selector según el contenido de tu pantalla Home
            composeTestRule.onAllNodesWithText("Home")
                .fetchSemanticsNodes().isNotEmpty() ||
                    composeTestRule.onAllNodesWithText("Inicio")
                        .fetchSemanticsNodes().isNotEmpty()
        }
        Thread.sleep(500)

        composeTestRule.onNodeWithContentDescription("Perfil")
            .assertIsDisplayed()
            .performClick()

        Thread.sleep(500)

        composeTestRule.onNodeWithText("Cerrar Sesión")
            .assertIsDisplayed()
            .performClick()

        Thread.sleep(500)
    }

    // Función auxiliar para verificar que un texto contiene una cadena
    private fun hasTextThat(contains: String): SemanticsMatcher {
        return SemanticsMatcher("contains '$contains'") { node ->
            try {
                val textList = node.config[androidx.compose.ui.semantics.SemanticsProperties.Text]
                textList.any { it.text.contains(contains, ignoreCase = true) }
            } catch (e: IllegalStateException) {
                false
            }
        }
    }
}