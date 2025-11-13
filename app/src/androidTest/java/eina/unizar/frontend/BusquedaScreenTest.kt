package eina.unizar.frontend

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until

@RunWith(AndroidJUnit4::class)
class BusquedaScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Test 1: La pantalla muestra el mensaje inicial cuando no hay búsqueda
     */
    @Test
    fun testPantallaInicial_MuestraMensajeBienvenida() {
        composeTestRule.setContent {
            BusquedaScreen(
                efectiveUserId = "1",
                efectiveToken = "test_token",
                onBackClick = {}
            )
        }

        composeTestRule.onNodeWithText("Busca viajes, repostajes, incidencias...")
            .assertExists()
    }

    /**
     * Test 2: El campo de búsqueda acepta texto
     */
    @Test
    fun testCampoBusqueda_AceptaTexto() {
        composeTestRule.setContent {
            BusquedaScreen(
                efectiveUserId = "1",
                efectiveToken = "test_token",
                onBackClick = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("Buscar")
            .performClick()

        composeTestRule.onNode(
            hasSetTextAction() and hasContentDescription("Buscar")
        ).performTextInput("Madrid")

        composeTestRule.onNodeWithText("Madrid")
            .assertExists()
    }

    /**
     * Test 3: El botón de limpiar borra la búsqueda
     */
    @Test
    fun testBotonLimpiar_BorraBusqueda() {
        composeTestRule.setContent {
            BusquedaScreen(
                efectiveUserId = "1",
                efectiveToken = "test_token",
                onBackClick = {}
            )
        }

        // Escribir en el campo de búsqueda
        composeTestRule.onNode(
            hasSetTextAction()
        ).performTextInput("Madrid")

        // Clic en botón limpiar
        composeTestRule.onNodeWithContentDescription("Limpiar")
            .performClick()

        // Verificar que se limpió
        composeTestRule.onNodeWithText("Madrid")
            .assertDoesNotExist()
    }

    /**
     * Test 4: Los filtros se muestran cuando hay búsqueda
     */
    @Test
    fun testFiltros_AparecenConBusqueda() {
        composeTestRule.setContent {
            BusquedaScreen(
                efectiveUserId = "1",
                efectiveToken = "test_token",
                onBackClick = {}
            )
        }

        // Sin búsqueda, no hay filtros
        composeTestRule.onNodeWithText("Todos")
            .assertDoesNotExist()

        // Escribir búsqueda
        composeTestRule.onNode(
            hasSetTextAction()
        ).performTextInput("test")

        // Esperar a que aparezcan los filtros
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            try {
                composeTestRule.onNodeWithText("Todos").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }


    /**
     * Test 5: El botón volver funciona
     */
    @Test
    fun testBotonVolver_Funciona() {
        var backClicked = false

        composeTestRule.setContent {
            BusquedaScreen(
                efectiveUserId = "1",
                efectiveToken = "test_token",
                onBackClick = { backClicked = true }
            )
        }

        composeTestRule.onNodeWithContentDescription("Volver")
            .performClick()

        assert(backClicked)
    }
}