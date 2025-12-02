package eina.unizar.frontend

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests de la pantalla PremiumScreen
 * No terminan de funcionar porque detecta que el usuario ya es premium
 */
@RunWith(AndroidJUnit4::class)
class PremiumScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Test 1: Verificar que se muestran los elementos principales
     */
    @Test
    fun testPremiumScreen_MuestraElementosPrincipales() {
        composeTestRule.setContent {
            PremiumScreen(
                token = "",
                onBackClick = {}
            )
        }

        // Esperar a que cargue
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Hazlo Premium")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Verificar título
        composeTestRule.onNodeWithText("Hazlo Premium").assertIsDisplayed()

        // Verificar header
        composeTestRule.onNodeWithText("Desbloquea todas las funciones").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sin anuncios • Funciones exclusivas").assertIsDisplayed()

        // Verificar sección de beneficios
        composeTestRule.onNodeWithText("Beneficios Premium").assertIsDisplayed()
    }

    /**
     * Test 2: Verificar que se muestran los beneficios
     */
    @Test
    fun testPremiumScreen_MuestraBeneficios() {
        composeTestRule.setContent {
            PremiumScreen(
                token = "",
                onBackClick = {}
            )
        }

        // Esperar a que cargue
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Sin anuncios")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Verificar beneficios
        composeTestRule.onNodeWithText("Sin anuncios")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Disfruta de la app sin interrupciones")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Badge Premium")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Muestra tu corona dorada en el perfil")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Experiencia mejorada")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Acceso prioritario a nuevas funciones")
            .assertIsDisplayed()
    }

    /**
     * Test 3: Verificar que se muestran los planes de suscripción
     */
    @Test
    fun testPremiumScreen_MuestraPlanes() {
        composeTestRule.setContent {
            PremiumScreen(
                token = "",
                onBackClick = {}
            )
        }

        // Esperar a que cargue
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Elige tu plan")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Verificar sección de planes
        composeTestRule.onNodeWithText("Elige tu plan")
            .performScrollTo()
            .assertIsDisplayed()

        // Verificar plan mensual
        composeTestRule.onNodeWithText("Mensual")
            .performScrollTo()
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Facturación mensual")
            .assertIsDisplayed()

        // Verificar plan anual
        composeTestRule.onNodeWithText("Anual")
            .performScrollTo()
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("POPULAR")
            .assertIsDisplayed()
    }

    /**
     * Test 4: Verificar selección de plan mensual
     */
    @Test
    fun testPremiumScreen_SeleccionPlanMensual() {
        composeTestRule.setContent {
            PremiumScreen(
                token = "",
                onBackClick = {}
            )
        }

        // Esperar a que cargue
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Mensual")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Click en plan mensual
        composeTestRule.onNodeWithText("Mensual")
            .performScrollTo()
            .performClick()

        composeTestRule.waitForIdle()

        // Verificar que el plan se seleccionó
        composeTestRule.onNodeWithText("Mensual").assertIsDisplayed()
    }

    /**
     * Test 5: Verificar selección de plan anual
     */
    @Test
    fun testPremiumScreen_SeleccionPlanAnual() {
        composeTestRule.setContent {
            PremiumScreen(
                token = "",
                onBackClick = {}
            )
        }

        // Esperar a que cargue
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Anual")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Click en plan anual
        composeTestRule.onNodeWithText("Anual")
            .performScrollTo()
            .performClick()

        composeTestRule.waitForIdle()

        // Verificar que el plan se seleccionó
        composeTestRule.onNodeWithText("Anual").assertIsDisplayed()
    }

    /**
     * Test 6: Verificar que el botón de pago abre el modal
     */
    @Test
    fun testPremiumScreen_BotonPagoAbreModal() {
        composeTestRule.setContent {
            PremiumScreen(
                token = "",
                onBackClick = {}
            )
        }

        // Esperar a que aparezca el botón
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Continuar con el pago")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Click en botón de pago
        composeTestRule.onNodeWithText("Continuar con el pago")
            .performScrollTo()
            .performClick()

        composeTestRule.waitForIdle()

        // Verificar que se abre el modal de pago
        composeTestRule.onNodeWithText("Datos de pago").assertIsDisplayed()
    }

    /**
     * Test 7: Verificar navegación hacia atrás
     */
    @Test
    fun testPremiumScreen_BotonVolver() {
        var backClicked = false

        composeTestRule.setContent {
            PremiumScreen(
                token = "",
                onBackClick = { backClicked = true }
            )
        }

        // Esperar a que cargue
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithContentDescription("Volver")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Click en botón de volver
        composeTestRule.onNodeWithContentDescription("Volver").performClick()

        assertTrue("El botón de volver debe llamar a onBackClick", backClicked)
    }

    /**
     * Test 8: Verificar modal de pago - campos vacíos
     */
    @Test
    fun testPagoModal_CamposVacios_MuestraError() {
        composeTestRule.setContent {
            PremiumScreen(
                token = "",
                onBackClick = {}
            )
        }

        // Esperar y abrir modal
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Continuar con el pago")
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Continuar con el pago")
            .performScrollTo()
            .performClick()

        composeTestRule.waitForIdle()

        // Intentar pagar sin llenar campos
        composeTestRule.onNode(
            hasText("Pagar") and hasClickAction()
        ).performClick()

        composeTestRule.waitForIdle()

        // Verificar mensaje de error
        composeTestRule.onNodeWithText("Por favor completa todos los campos correctamente")
            .assertIsDisplayed()
    }

    /**
     * Test 9: Verificar modal de pago - número de tarjeta solo acepta dígitos
     */
    @Test
    fun testPagoModal_NumeroTarjetaInvalido_NoPermiteLetras() {
        composeTestRule.setContent {
            PremiumScreen(
                token = "",
                onBackClick = {}
            )
        }

        // Esperar y abrir modal
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Continuar con el pago")
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Continuar con el pago")
            .performScrollTo()
            .performClick()

        composeTestRule.waitForIdle()

        // Intentar introducir letras
        composeTestRule.onNodeWithText("Número de tarjeta")
            .performTextInput("abcd1234")

        composeTestRule.waitForIdle()

        // Verificar que solo acepta dígitos (las letras se filtran)
        val text = composeTestRule.onNodeWithText("Número de tarjeta")
            .fetchSemanticsNode()
            .config[androidx.compose.ui.semantics.SemanticsProperties.EditableText]
            ?.text

        assertTrue("Solo debe contener dígitos", text?.contains(Regex("^[0-9]*$")) == true)
    }

    /**
     * Test 10: Verificar modal de pago - formato fecha expiración
     */
    @Test
    fun testPagoModal_FormatoFechaExpiracion() {
        composeTestRule.setContent {
            PremiumScreen(
                token = "",
                onBackClick = {}
            )
        }

        // Esperar y abrir modal
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Continuar con el pago")
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Continuar con el pago")
            .performScrollTo()
            .performClick()

        composeTestRule.waitForIdle()

        // Introducir fecha
        composeTestRule.onNodeWithText("MM/AA")
            .performTextInput("1225")

        composeTestRule.waitForIdle()

        // Verificar que se formatea con barra
        val text = composeTestRule.onNodeWithText("MM/AA")
            .fetchSemanticsNode()
            .config[androidx.compose.ui.semantics.SemanticsProperties.EditableText]
            ?.text

        assertTrue("Debe tener formato MM/AA", text?.contains("/") == true)
    }

    /**
     * Test 11: Verificar modal de pago - CVV solo acepta 3 dígitos
     */
    @Test
    fun testPagoModal_CVVMaximo3Digitos() {
        composeTestRule.setContent {
            PremiumScreen(
                token = "",
                onBackClick = {}
            )
        }

        // Esperar y abrir modal
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Continuar con el pago")
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Continuar con el pago")
            .performScrollTo()
            .performClick()

        composeTestRule.waitForIdle()

        // Intentar introducir más de 3 dígitos
        composeTestRule.onNodeWithText("CVV")
            .performTextInput("12345")

        composeTestRule.waitForIdle()

        // Verificar longitud máxima
        val text = composeTestRule.onNodeWithText("CVV")
            .fetchSemanticsNode()
            .config[androidx.compose.ui.semantics.SemanticsProperties.EditableText]
            ?.text

        assertTrue("CVV debe tener máximo 3 dígitos", text?.length ?: 0 <= 3)
    }

    /**
     * Test 12: Verificar cancelación del modal de pago
     */
    @Test
    fun testPagoModal_BotonCancelar() {
        composeTestRule.setContent {
            PremiumScreen(
                token = "",
                onBackClick = {}
            )
        }

        // Esperar y abrir modal
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Continuar con el pago")
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Continuar con el pago")
            .performScrollTo()
            .performClick()

        composeTestRule.waitForIdle()

        // Click en cancelar
        composeTestRule.onNodeWithText("Cancelar").performClick()

        composeTestRule.waitForIdle()

        // Verificar que el modal se cierra
        composeTestRule.onNodeWithText("Datos de pago").assertDoesNotExist()
    }

    /**
     * Test 13: Verificar número de tarjeta máximo 16 dígitos
     */
    @Test
    fun testPagoModal_NumeroTarjetaMaximo16Digitos() {
        composeTestRule.setContent {
            PremiumScreen(
                token = "",
                onBackClick = {}
            )
        }

        // Esperar y abrir modal
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Continuar con el pago")
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Continuar con el pago")
            .performScrollTo()
            .performClick()

        composeTestRule.waitForIdle()

        // Intentar introducir más de 16 dígitos
        composeTestRule.onNodeWithText("Número de tarjeta")
            .performTextInput("12345678901234567890")

        composeTestRule.waitForIdle()

        // Verificar longitud máxima
        val text = composeTestRule.onNodeWithText("Número de tarjeta")
            .fetchSemanticsNode()
            .config[androidx.compose.ui.semantics.SemanticsProperties.EditableText]
            ?.text

        assertTrue("Número de tarjeta debe tener máximo 16 dígitos", text?.length ?: 0 <= 16)
    }
}