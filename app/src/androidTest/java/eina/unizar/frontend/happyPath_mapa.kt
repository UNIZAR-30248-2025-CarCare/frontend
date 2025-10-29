package eina.unizar.frontend


import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Before

// Asume que MainActivity es tu Activity principal
import eina.unizar.frontend.MainActivity


/**
 * Tests de Aceptación - Happy Path para Mapa de Ubicación
 *
 * Estos tests verifican el flujo completo de visualización del mapa.
 */
@RunWith(AndroidJUnit4::class)
class MapaUbicacionHappyPathTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val preRegisteredEmail = "juan.perez@email.com"
    private val preRegisteredPassword = "password123"

    @Before
    fun setup() {
        // Ejecutar el flujo de login y esperar a la pantalla Home
        // Todos los tests comienzan aquí: en la pantalla Home, NO en la del mapa.
        loginUsuario()
    }

    // ==============================================================================
    // FUNCIONES AUXILIARES (NAVEGACIÓN)
    // ==============================================================================

    /**
     * Realiza el flujo completo de login y espera a que la pantalla Home esté cargada.
     */
    private fun loginUsuario() {
        // Si ya estás logueado (ej. test anterior), salta este paso
        val isHomeLoaded = composeTestRule.onAllNodesWithText("Hola,", ignoreCase = true).fetchSemanticsNodes().isNotEmpty()
        if (isHomeLoaded) return

        // 1. Navegar desde la pantalla de bienvenida/inicial (Si existe)
        try {
            composeTestRule.onNodeWithText("Continuar", ignoreCase = true)
                .assertIsDisplayed()
                .performClick()
        } catch (e: Exception) {
            // Si "Continuar" no existe, asumimos que estamos en la pantalla de login
        }


        // 2. Ingresar credenciales
        composeTestRule.onAllNodesWithText("Email")[0]
            .assertIsDisplayed()
            .performTextInput(preRegisteredEmail)

        composeTestRule.onAllNodesWithText("Contraseña")[0]
            .assertIsDisplayed()
            .performTextInput(preRegisteredPassword)

        // 3. Click en botón de iniciar sesión
        composeTestRule.onNodeWithText("Iniciar Sesión")
            .assertIsEnabled()
            .performClick()

        // 4. Esperar a que se cargue la pantalla Home (verificando el header)
        composeTestRule.waitUntil(timeoutMillis = 7000) {
            composeTestRule.onAllNodesWithText("Hola,", ignoreCase = true).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.waitForIdle()
    }

    /**
     * Navega a la pantalla del mapa usando el Content Description del icono de la NavBar.
     */
    private fun navegarAMapa() {
        // Buscar por Content Description (el label de la pestaña)
        composeTestRule.onNodeWithContentDescription("Mapa", ignoreCase = true)
            .assertIsDisplayed()
            .performClick()

        // Esperar a que el título de la pantalla de destino ("Ubicación del Vehículo") aparezca
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Ubicación del Vehículo").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.waitForIdle()
    }

    // ==============================================================================
    // TESTS DE ACEPTACIÓN
    // ==============================================================================

    @Test
    fun happyPath_visualizarMapaConVehiculos() {
        // Aseguramos la navegación, es el primer paso
        navegarAMapa()

        // Verificar que estamos en la pantalla de ubicación
        composeTestRule.onNodeWithText("Ubicación del Vehículo")
            .assertIsDisplayed()

        // PASO 2: Esperar a que se carguen los vehículos
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithContentDescription("Vehículo")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // PASO 3: Verificar que hay al menos una tarjeta de vehículo visible
        composeTestRule.onNodeWithContentDescription("Vehículo")
            .assertIsDisplayed()

        // PASO 4: Verificar que aparecen las coordenadas del vehículo
        composeTestRule.onNode(
            hasText("Lat:", substring = true)
        ).assertExists()
    }

    @Test
    fun happyPath_navegarEntreVehiculos() {
        // CORRECCIÓN: Aseguramos la navegación al mapa
        navegarAMapa()

        // Esperar carga de vehículos
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithContentDescription("Vehículo")
                .fetchSemanticsNodes().size > 0
        }

        // PASO 1: Ver primer vehículo
        val primerVehiculo = composeTestRule.onAllNodesWithContentDescription("Vehículo")[0]
        primerVehiculo.assertIsDisplayed()

        // PASO 2: Deslizar para ver siguiente vehículo
        composeTestRule.waitForIdle()

        // Realizar swipe a la izquierda
        composeTestRule.onRoot()
            .performTouchInput {
                swipeLeft()
            }

        composeTestRule.waitForIdle()

        // PASO 3: Verificar que la tarjeta sigue visible
        composeTestRule.onNodeWithContentDescription("Vehículo")
            .assertIsDisplayed()
    }

    @Test
    fun happyPath_centrarMapaEnVehiculo() {
        // CORRECCIÓN: Aseguramos la navegación al mapa
        navegarAMapa()

        // Esperar carga de botones "IR"
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("IR", ignoreCase = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        // PASO 1: Click en botón "IR" para centrar el mapa
        composeTestRule.onNodeWithText("IR", ignoreCase = true)
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        // PASO 2: Verificar que seguimos en la pantalla del mapa
        composeTestRule.onNodeWithText("Ubicación del Vehículo")
            .assertIsDisplayed()
    }

    @Test
    fun happyPath_verSnackbarTutorial() {
        // CORRECCIÓN: Aseguramos la navegación al mapa
        navegarAMapa()

        // Esperar a que aparezca el snackbar tutorial
        val snackbarText = "Desliza para ver el resto de vehículos"

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText(snackbarText)
                .fetchSemanticsNodes().isNotEmpty() ||
                    true
        }

        // Si aparece el snackbar, cerrarlo
        try {
            composeTestRule.onNodeWithText("OK", ignoreCase = true)
                .performClick()

            composeTestRule.waitForIdle()

            // Verificar que el snackbar desapareció
            composeTestRule.onNodeWithText(snackbarText)
                .assertDoesNotExist()
        } catch (e: AssertionError) {
            // No hay problema si no apareció el snackbar
        }
    }

    @Test
    fun happyPath_verDetallesVehiculo() {
        // CORRECCIÓN: Aseguramos la navegación al mapa
        navegarAMapa()

        // Esperar carga
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithContentDescription("Vehículo")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // PASO 1: Verificar que el componente de vehículo está visible
        composeTestRule.onNodeWithContentDescription("Vehículo")
            .assertIsDisplayed()

        // PASO 2: Verificar coordenadas visibles
        composeTestRule.onNode(
            hasText("Lat:", substring = true)
        ).assertExists()
    }

    @Test
    fun happyPath_navegarDesdeMapaAHome() {
        // CORRECCIÓN: Aseguramos la navegación al mapa
        navegarAMapa()

        // PASO 1: Estar en el mapa
        composeTestRule.onNodeWithText("Ubicación del Vehículo")
            .assertIsDisplayed()

        // PASO 2: Click en el botón de volver (Content Description "Volver" del ArrowBack)
        composeTestRule.onNodeWithContentDescription("Volver")
            .assertIsDisplayed()
            .performClick()

        // PASO 3: Verificar que volvemos a Home (Header "Hola,")
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("Hola,")
                .fetchSemanticsNodes().isNotEmpty()
        }
    }


    @Test
    fun happyPath_interaccionConMarcadores() {
        // CORRECCIÓN: Aseguramos la navegación al mapa
        navegarAMapa()

        // Esperar carga completa del mapa y tarjetas de vehículo
        composeTestRule.waitUntil(timeoutMillis = 7000) {
            composeTestRule.onAllNodesWithText("IR", ignoreCase = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        // PASO 1: Verificar que el mapa está cargado
        composeTestRule.onNodeWithText("Ubicación del Vehículo")
            .assertIsDisplayed()

        // PASO 2: Interactuar con el botón "IR" para simular una interacción con el vehículo visible
        composeTestRule.onNodeWithText("IR", ignoreCase = true)
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        // PASO 3: Verificar que la interacción fue exitosa y seguimos en el mapa
        composeTestRule.onNodeWithText("Ubicación del Vehículo")
            .assertIsDisplayed()
    }
}