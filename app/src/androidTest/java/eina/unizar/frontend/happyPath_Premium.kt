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

@RunWith(AndroidJUnit4::class)
class PremiumAcceptanceTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val userEmail = "maria.garcia@email.com"
    private val userPassword = "password123"


    @Before
    fun setup() {
        composeTestRule.waitForIdle()
    }

    @Test
    fun happyPath_crearRepostaje() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

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

        // PASO 3: Navegar a la pantalla de repostajes
        composeTestRule.onNodeWithContentDescription("Repostajes")
            .assertIsDisplayed()
            .performClick()

        // PASO 4: Pulsar en "Añadir repostaje" (1 accion)
        composeTestRule.onNodeWithContentDescription("Añadir repostaje")
            .assertIsDisplayed()
            .performClick()

        // PASO 5: Volver atrás (2 acciones)
        composeTestRule.onNodeWithContentDescription("Volver")
            .performClick()

        // PASO 6: Pulsar en "Añadir repostaje" (3 acciones)
        composeTestRule.onNodeWithContentDescription("Añadir repostaje")
            .assertIsDisplayed()
            .performClick()

        // PASO 7: Volver atrás (4 acciones)
        composeTestRule.onNodeWithContentDescription("Volver")
            .performClick()

        // Ya aparecerá el anuncio de premium tras varios intentos
        // PASO 8: Pulsar en "Quiero ser premium"
        composeTestRule.onNodeWithText("Quiero ser Premium")
            .assertIsDisplayed()
            .performClick()

        // PASO 9: Esperar a que cargue la pantalla Premium
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodesWithText("Continuar con el pago")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // PASO 10: Hacer scroll hasta el botón y pulsarlo
        composeTestRule.onNodeWithText("Continuar con el pago")
            .performScrollTo()
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        // Paso 11: Completar los datos de pago
        composeTestRule.onNodeWithText("Número de tarjeta")
            .performTextInput("1234123412341234")

        composeTestRule.onNodeWithText("MM/AA")
            .performTextInput("12/34")

        composeTestRule.onNodeWithText("CVV")
            .performTextInput("123")

        // Paso 12: Pulsar en "Pagar"
        composeTestRule.onNodeWithText("Pagar")
            .assertIsDisplayed()
            .performClick()

        // PASO 13: Esperar a que se procese el pago
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodesWithText("¡Ya eres Premium!")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // PASO 14: Comprobar que ya somos premium
        composeTestRule.onNodeWithText("¡Ya eres Premium!")
            .performScrollTo()
            .assertIsDisplayed()
    }


}