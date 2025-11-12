package eina.unizar.frontend

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import eina.unizar.frontend.models.Vehiculo
import eina.unizar.frontend.models.Ubicacion
import eina.unizar.frontend.EstadoVehiculo
import eina.unizar.frontend.TipoVehiculo

/*
 * Tests de la pantalla CrearRepostajeScreen
 * No se comprueba el formato de las fechas, ya que
 * la interfaz de usuario no permite introducir datos inválidos en esos campos.
 */
@RunWith(AndroidJUnit4::class)
class CrearRespostajeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()


    val vehiculoTest = Vehiculo(
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
     * Test 1: Creación exitosa de repostaje con datos válidos
     */
    @Test
    fun testCrearRepostajeExitoso_DatosValidos() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        composeTestRule.setContent {
            CrearRepostajeScreen(
                onCrearRepostaje = {},
                onBackClick = {},
                efectiveToken = "",
                efectiveUserId = 0.toString(),
                vehiculos = listOf(vehiculoTest)
            )
        }

        composeTestRule.onNodeWithText("Litros repostados").performTextInput("45.0")
        composeTestRule.onNodeWithText("Precio por litro").performTextInput("1.5")
        //composeTestRule.onNodeWithText("Precio total").performTextInput("67.5")

        // PASO 7: Seleccionar fecha y hora
        composeTestRule.onNodeWithText("Fecha y hora del repostaje")
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

        // Pulsar en "Crear Repostaje"
        composeTestRule.onNode(
            hasText("Crear Repostaje") and hasClickAction()
        ).performScrollTo().performClick()

        composeTestRule.waitUntil(timeoutMillis = 1000) {
            try {
                composeTestRule.onNodeWithText("El vehículo no existe").assertDoesNotExist()
                composeTestRule.onNodeWithText("Los litros repostados no pueden estar vacíos").assertDoesNotExist()
                composeTestRule.onNodeWithText("Los litros repostados deben ser un número mayor que 0").assertDoesNotExist()
                composeTestRule.onNodeWithText("El precio por litro no puede estar vacío").assertDoesNotExist()
                composeTestRule.onNodeWithText("El precio por litro debe ser un número mayor que 0").assertDoesNotExist()
                composeTestRule.onNodeWithText("El precio total no puede estar vacío").assertDoesNotExist()
                composeTestRule.onNodeWithText("El precio total debe ser un número mayor que 0").assertDoesNotExist()
                composeTestRule.onNodeWithText("La fecha y hora del repostaje no puede estar vacía").assertDoesNotExist()
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }

    /**
     * Test 2: Error cuando litros repostados no es un número válido
     */
    @Test
    fun testCrearRepostajeFallido_LitrosNumeroNoValido() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        composeTestRule.setContent {
            CrearRepostajeScreen(
                onCrearRepostaje = {},
                onBackClick = {},
                efectiveToken = "",
                efectiveUserId = 0.toString(),
                vehiculos = listOf(vehiculoTest)
            )
        }

        composeTestRule.onNodeWithText("Litros repostados").performTextInput("abc")
        composeTestRule.onNodeWithText("Precio por litro").performTextInput("1.5")
        composeTestRule.onNodeWithText("Precio total").performTextInput("75.0")

        // PASO 7: Seleccionar fecha y hora
        composeTestRule.onNodeWithText("Fecha y hora del repostaje")
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

        // Pulsar en "Crear Repostaje"
        composeTestRule.onNode(
            hasText("Crear Repostaje") and hasClickAction()
        ).performScrollTo().performClick()

        composeTestRule.waitUntil(timeoutMillis = 1000) {
            try {
                composeTestRule.onNodeWithText("Los litros repostados deben ser un número mayor que 0")
                    .performScrollTo()
                    .assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }

    /**
     * Test 3: Error cuando precio por litro no es un número válido
     */
    @Test
    fun testCrearRepostajeFallido_PrecioPorLitroNoValido() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        composeTestRule.setContent {
            CrearRepostajeScreen(
                onCrearRepostaje = {},
                onBackClick = {},
                efectiveToken = "",
                efectiveUserId = 0.toString(),
                vehiculos = listOf(vehiculoTest)
            )
        }

        composeTestRule.onNodeWithText("Litros repostados").performTextInput("50.0")
        composeTestRule.onNodeWithText("Precio por litro").performTextInput("abc")
        composeTestRule.onNodeWithText("Precio total").performTextInput("75.0")

        // PASO 7: Seleccionar fecha y hora
        composeTestRule.onNodeWithText("Fecha y hora del repostaje")
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

        // Pulsar en "Crear Repostaje"
        composeTestRule.onNode(
            hasText("Crear Repostaje") and hasClickAction()
        ).performScrollTo().performClick()

        composeTestRule.waitUntil(timeoutMillis = 1000) {
            try {
                composeTestRule.onNodeWithText("El precio por litro debe ser un número mayor que 0")
                    .performScrollTo()
                    .assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }

    /**
     * Test 4: Error cuando precio total no es un número válido
     */
    @Test
    fun testCrearRepostajeFallido_PrecioTotalNoValido() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        composeTestRule.setContent {
            CrearRepostajeScreen(
                onCrearRepostaje = {},
                onBackClick = {},
                efectiveToken = "",
                efectiveUserId = 0.toString(),
                vehiculos = listOf(vehiculoTest)
            )
        }

        composeTestRule.onNodeWithText("Litros repostados").performTextInput("50.0")
        composeTestRule.onNodeWithText("Precio por litro").performTextInput("1.5")
        composeTestRule.onNodeWithText("Precio total").performTextInput("abc")

        // PASO 7: Seleccionar fecha y hora
        composeTestRule.onNodeWithText("Fecha y hora del repostaje")
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

        // Pulsar en "Crear Repostaje"
        composeTestRule.onNode(
            hasText("Crear Repostaje") and hasClickAction()
        ).performScrollTo().performClick()

        composeTestRule.waitUntil(timeoutMillis = 1000) {
            try {
                composeTestRule.onNodeWithText("El precio total debe ser un número mayor que 0")
                    .performScrollTo()
                    .assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }
}