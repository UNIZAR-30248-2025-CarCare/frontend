package eina.unizar.frontend

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.junit.Before
import java.text.SimpleDateFormat
import java.util.*

/**
 * Tests de Aceptación - Happy Path
 *
 * Estos tests verifican el flujo completo de registrar un nuevo vehículo:
 * 1. Usuario inicia sesión con datos válidos
 * 2. Usuario registra un nuevo vehículo con datos válidos
 * 3. El vehículo aparece en la lista de vehículos del usuario
 */
@RunWith(AndroidJUnit4::class)
class RegistroVehiculoAcceptanceTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val userEmail = "juan.perez@email.com"
    private val userPassword = "password123"
    private val testNombreVehiculo = "Coche de Juan" + System.currentTimeMillis().toString()
    private val testFabricante = "Toyota"
    private val testModelo = "Corolla"
    private val testMatricula = (System.currentTimeMillis() % 10000).toString() + " BBC"
    private val testAno = "2020"
    private val testCapacidadDeposito = "50.0"
    private val testConsumoMedio = "5.5"
    private val testTipoVehiculo = "Coche"
    private val testTipoCombustible = "Diésel"


    @Before
    fun setup() {
        // Asegurarse de que estamos en la pantalla inicial
        composeTestRule.waitForIdle()
    }

    @Test
    fun happyPath_loginYRegistroVehiculo() {
        // PASO 1: Navegar a la pantalla de inicio de sesión
        composeTestRule.onNodeWithText("Continuar")
            .assertIsDisplayed()
            .performClick()


        // PASO 2: Iniciar sesión con credenciales existentes
        composeTestRule.onAllNodesWithText("Email")[0]
            .performTextInput(userEmail)

        composeTestRule.onAllNodesWithText("Contraseña")[0]
            .performTextInput(userPassword)

        composeTestRule.onNodeWithText("Iniciar Sesión")
            .assertIsDisplayed()
            .performClick()

        // Esperar a llegar a Home
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Home")
                .fetchSemanticsNodes().isNotEmpty() ||
                    composeTestRule.onAllNodesWithText("Inicio")
                        .fetchSemanticsNodes().isNotEmpty()
        }

        // PASO 3: Navegar a la pantalla de registro de vehículo
        composeTestRule.onNodeWithContentDescription("Añadir vehículo")
            .assertIsDisplayed()
            .performClick()

        // PASO 4: Completar el formulario de registro de vehículo
        // Tipo de vehículo
        composeTestRule.onNodeWithText(testTipoVehiculo).performClick()

        composeTestRule.onNodeWithText("Ej: Mi coche, Furgoneta de trabajo...")
            .performTextInput(testNombreVehiculo)

        composeTestRule.onNodeWithText("Ej: Seat, Toyota, Ford...")
            .performTextInput(testFabricante)

        composeTestRule.onNodeWithText("Ej: Ibiza, Corolla...")
            .performTextInput(testModelo)

        composeTestRule.onNodeWithText("1234 BBC")
            .performTextInput(testMatricula)

        composeTestRule.onNodeWithText("2020")
            .performTextInput(testAno)

        composeTestRule.onNodeWithText("45.0")
            .performTextInput(testCapacidadDeposito)

        composeTestRule.onNodeWithText("5.5")
            .performTextInput(testConsumoMedio)

        composeTestRule.onNodeWithText("Gasolina").performClick()
        composeTestRule.onNodeWithText(testTipoCombustible).performClick()


        // PASO 5: Enviar formulario de registro de vehículo
        composeTestRule.onNode(
            hasText("Añadir Vehículo") and hasClickAction()
        ).performScrollTo().performClick()

        // PASO 6: Verificar que el vehículo se registró correctamente

        composeTestRule.onNodeWithText(testNombreVehiculo)
            .assertIsDisplayed()
    }

}