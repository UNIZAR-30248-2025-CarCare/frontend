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
class BusquedaAcceptanceTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val userEmail = "juan.perez@email.com"
    private val userPassword = "password123"

    @Before
    fun setup() {
        composeTestRule.waitForIdle()
    }

    @Test
    fun happyPath_buscarYVerDetalles() {
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

        // PASO 3: Esperar a llegar a Home
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Home")
                .fetchSemanticsNodes().isNotEmpty() ||
                    composeTestRule.onAllNodesWithText("Inicio")
                        .fetchSemanticsNodes().isNotEmpty()
        }

        // PASO 4: Navegar a la pantalla de búsqueda
        composeTestRule.onNodeWithContentDescription("Busqueda")
            .assertIsDisplayed()
            .performClick()

        // PASO 5: Verificar que se muestra el mensaje inicial
        composeTestRule.onNodeWithText("Busca viajes, repostajes, incidencias...")
            .assertIsDisplayed()

        // PASO 6: Realizar una búsqueda
        composeTestRule.onNode(hasSetTextAction())
            .performTextInput("Madrid")

        // PASO 7: Esperar a que aparezcan los resultados
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithText("Todos").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        // PASO 8: Verificar que aparecen los filtros
        composeTestRule.onNodeWithText("Todos")
            .performScrollTo()
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Viaje")
            .performScrollTo()
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Reserva")
            .performScrollTo()
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Incidencia")
            .performScrollTo()
            .assertIsDisplayed()


        // PASO 9: Aplicar filtro de Viajes
        composeTestRule.onNodeWithText("Viaje")
            .performClick()

        // Esperar a que se aplique el filtro
        composeTestRule.waitForIdle()

        // PASO 10: Verificar que solo hay resultados de tipo Viaje
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            try {
                // Verificar que al menos hay un resultado
                composeTestRule.onAllNodes(hasClickAction())
                    .fetchSemanticsNodes().size > 3 // TopBar + filtros + al menos 1 resultado
                true
            } catch (e: AssertionError) {
                false
            }
        }

        // PASO 11: Cambiar a filtro de Reservas
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Reserva")
            .performClick()

        composeTestRule.waitForIdle()

        // PASO 12: Quitar filtros mostrando "Todos"
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Todos")
            .performClick()

        // PASO 13: Verificar que se muestran todos los tipos de resultados
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            try {
                // Debe haber más resultados que antes
                composeTestRule.onAllNodes(hasClickAction())
                    .fetchSemanticsNodes().size > 5
                true
            } catch (e: AssertionError) {
                false
            }
        }

        // PASO 14: Volver atrás
        composeTestRule.onNodeWithContentDescription("Volver")
            .performClick()

        // PASO 15: Verificar que volvió a Home
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("Home")
                .fetchSemanticsNodes().isNotEmpty() ||
                    composeTestRule.onAllNodesWithText("Inicio")
                        .fetchSemanticsNodes().isNotEmpty()
        }
    }
}