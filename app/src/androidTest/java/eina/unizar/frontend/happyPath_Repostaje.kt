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
class CrearRepostajeAcceptanceTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val userEmail = "juan.perez@email.com"
    private val userPassword = "password123"

    private val testLitros = "30.00"
    private val testPrecioPorLitro = "1.50"
    private val testPrecioTotal = "45.00"

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

        // PASO 4: Pulsar en "Añadir repostaje"
        composeTestRule.onNodeWithContentDescription("Añadir repostaje")
            .assertIsDisplayed()
            .performClick()

        // PASO 5: Seleccionar vehículo (asume que hay al menos uno)
        /*
        composeTestRule.onNodeWithText("Vehículo")
            .assertIsDisplayed()
            .performClick()
        composeTestRule.onAllNodes(isSelectable())[0].performClick()
         */

        // PASO 6: Rellenar formulario
        composeTestRule.onNodeWithText("Litros repostados")
            .performTextInput(testLitros)
        composeTestRule.onNodeWithText("Precio por litro")
            .performTextInput(testPrecioPorLitro)
        //composeTestRule.onNodeWithText("67.50")
            //.performTextInput(testPrecioTotal)

        // PASO 7: Seleccionar fecha y hora
        composeTestRule.onNodeWithText("Fecha y hora del repostaje")
            .assertIsDisplayed()
            .performClick()

        // Si el DatePicker/TimePicker muestra un botón "Aceptar" o "OK", simula el click:

        // Esperar a que aparezca el diálogo nativo
        device.wait(Until.hasObject(By.text("ACEPTAR")), 3000)

        // Interactuar con el diálogo nativo
        device.findObject(By.text("ACEPTAR")).click()

        // Ahora el TimePicker
        device.wait(Until.hasObject(By.text("ACEPTAR")), 3000)
        device.findObject(By.text("ACEPTAR")).click()

        // PASO 8: Pulsar en "Crear Repostaje"
        composeTestRule.onNode(
            hasText("Crear Repostaje") and hasClickAction()
        ).performScrollTo().performClick()

        // PASO 9: Verificar que el repostaje aparece en la lista (puedes buscar por el precio total, por ejemplo)
        composeTestRule.onNodeWithText("$testPrecioTotal")
            .assertIsDisplayed()
    }
}