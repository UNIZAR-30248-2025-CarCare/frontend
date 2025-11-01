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
class CrearViajeAcceptanceTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val userEmail = "juan.perez@email.com"
    private val userPassword = "password123"

    private val testNombreViaje = "Viaje de Juan a Madrid"
    private val testDescripcion = "Viaje de fin de semana a Madrid"
    private val testKmRealizados = "600.0"
    private val testConsumoCombustible = "6.5"

    @Before
    fun setup() {
        composeTestRule.waitForIdle()
    }

    @Test
    fun happyPath_crearViaje() {
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

        // PASO 3: Navegar a la pantalla de viajes
        composeTestRule.onNodeWithContentDescription("Viajes")
            .assertIsDisplayed()
            .performClick()

        // PASO 4: Pulsar en "Añadir viaje"
        composeTestRule.onNodeWithContentDescription("Añadir viaje")
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
        composeTestRule.onNodeWithText("Nombre del viaje")
            .performTextInput(testNombreViaje)
        composeTestRule.onNodeWithText("Descripción")
            .performTextInput(testDescripcion)
        composeTestRule.onNodeWithText("Km realizados")
            .performTextInput(testKmRealizados)
        composeTestRule.onNodeWithText("Consumo combustible (L)")
            .performTextInput(testConsumoCombustible)


        // PASO 7: Seleccionar fecha y hora inicio
        composeTestRule.onNodeWithText("Fecha y hora inicio")
            .assertIsDisplayed()
            .performClick()



        // Esperar a que aparezca el diálogo nativo
        device.wait(Until.hasObject(By.text("ACEPTAR")), 1000)

        device.findObject(By.text("1")).click()

        // Interactuar con el diálogo nativo
        device.findObject(By.text("ACEPTAR")).click()

        // Ahora el TimePicker
        device.wait(Until.hasObject(By.text("ACEPTAR")), 1000)
        device.findObject(By.text("ACEPTAR")).click()

        // PASO 8: Seleccionar fecha y hora fin
        composeTestRule.onNodeWithText("Fecha y hora fin")
            .assertIsDisplayed()
            .performClick()

        // Esperar a que aparezca el diálogo nativo
        device.wait(Until.hasObject(By.text("ACEPTAR")), 1000)

        // Fecha posterior a la de inicio
        device.findObject(By.text("15")).click()

        // Interactuar con el diálogo nativo
        device.findObject(By.text("ACEPTAR")).click()

        // Ahora el TimePicker
        device.wait(Until.hasObject(By.text("ACEPTAR")), 3000)
        device.findObject(By.text("ACEPTAR")).click()

        // PASO 9: Abrir el selector de ubicación
        /*
        composeTestRule.onNodeWithText("Ubicación destino")
            .assertIsDisplayed()
            .performTouchInput {
                click(center)
            }

        // Esperar a que aparezca el diálogo del mapa
        Thread.sleep(500)

        // Esperar a que se muestre el mapa (ajusta el timeout y el texto según tu UI)
        device.wait(Until.hasObject(By.text("Seleccionar ubicación")), 3000)

        // Simular un click en el mapa (ajusta las coordenadas según tu UI)
        device.click(500, 800) // Coordenadas aproximadas en pantalla

        // Confirmar la selección
        device.findObject(By.text("Seleccionar ubicación")).click()
         */

        // PASO 10: Pulsar en "Crear Viaje"
        composeTestRule.onNode(
            hasText("Crear Viaje") and hasClickAction()
        ).performScrollTo().performClick()

        // PASO 9: Verificar que el repostaje aparece en la lista (puedes buscar por el precio total, por ejemplo)
        composeTestRule.onNodeWithText("$testNombreViaje")
            .assertIsDisplayed()
    }
}