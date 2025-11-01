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
 * Tests de la pantalla CrearViajeScreen
 * No se comprueba el formato de las fechas ni de la ubicación, ya que
 * la interfaz de usuario no permite introducir datos inválidos en esos campos.
 */
@RunWith(AndroidJUnit4::class)
class CrearViajeScreenTest {

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
     * Test 1: Creación exitosa de viaje con datos válidos
     */
    @Test
    fun testCrearViajeExitoso_DatosValidos() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        composeTestRule.setContent {
            CrearViajeScreen(
                onCrearViaje = {},
                onBackClick = {},
                efectiveToken = "",
                efectiveUserId = 0.toString(),
                vehiculos = listOf(vehiculoTest)
            )
        }

        composeTestRule.onNodeWithText("Nombre del viaje").performTextInput("Viaje a Madrid")
        composeTestRule.onNodeWithText("Descripción").performTextInput("Fin de semana en Madrid")
        composeTestRule.onNodeWithText("Km realizados").performTextInput("600.0")
        composeTestRule.onNodeWithText("Consumo combustible (L)").performTextInput("6.6")
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

        // Pulsar en "Crear Viaje"
        composeTestRule.onNode(
            hasText("Crear Viaje") and hasClickAction()
        ).performScrollTo().performClick()

        composeTestRule.waitUntil(timeoutMillis = 1000) {
            try {
                composeTestRule.onNodeWithText("El usuario no existe").assertDoesNotExist()
                composeTestRule.onNodeWithText("El vehículo no existe").assertDoesNotExist()
                composeTestRule.onNodeWithText("El nombre debe ser un string no vacío").assertDoesNotExist()
                composeTestRule.onNodeWithText("El usuario no existe").assertDoesNotExist()
                composeTestRule.onNodeWithText("La descripción debe ser un string no vacío").assertDoesNotExist()
                composeTestRule.onNodeWithText("Las fechas deben tener un formato válido").assertDoesNotExist()
                composeTestRule.onNodeWithText("La fecha de inicio no puede ser mayor que la de fin").assertDoesNotExist()
                composeTestRule.onNodeWithText("Los km realizados deben ser un número mayor que 0").assertDoesNotExist()
                composeTestRule.onNodeWithText("El consumo de combustible debe ser un número mayor que 0").assertDoesNotExist()
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }

    /**
     * Test 2: Error cuando falta el nombre del viaje
     */
    @Test
    fun testCrearViajeFallido_NombreVacio() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        composeTestRule.setContent {
            CrearViajeScreen(
                onCrearViaje = {},
                onBackClick = {},
                efectiveToken = "",
                efectiveUserId = 0.toString(),
                vehiculos = listOf(vehiculoTest)
            )
        }

        composeTestRule.onNodeWithText("Descripción").performTextInput("Fin de semana en Madrid")
        composeTestRule.onNodeWithText("Km realizados").performTextInput("600.0")
        composeTestRule.onNodeWithText("Consumo combustible (L)").performTextInput("6.6")
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

        // Pulsar en "Crear Viaje"
        composeTestRule.onNode(
            hasText("Crear Viaje") and hasClickAction()
        ).performScrollTo().performClick()

        composeTestRule.waitUntil(timeoutMillis = 6000) {
            try {
                composeTestRule.onNodeWithText("El nombre debe ser un string no vacío")
                    .performScrollTo()
                    .assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }

    /**
     * Test 3: Error cuando falta la descripción del viaje
     */
    @Test
    fun testCrearViajeFallido_DescripcionVacio() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        composeTestRule.setContent {
            CrearViajeScreen(
                onCrearViaje = {},
                onBackClick = {},
                efectiveToken = "",
                efectiveUserId = 0.toString(),
                vehiculos = listOf(vehiculoTest)
            )
        }

        composeTestRule.onNodeWithText("Nombre del viaje").performTextInput("Fin de semana en Madrid")
        composeTestRule.onNodeWithText("Km realizados").performTextInput("600.0")
        composeTestRule.onNodeWithText("Consumo combustible (L)").performTextInput("6.6")
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

        // Pulsar en "Crear Viaje"
        composeTestRule.onNode(
            hasText("Crear Viaje") and hasClickAction()
        ).performScrollTo().performClick()

        composeTestRule.waitUntil(timeoutMillis = 6000) {
            try {
                composeTestRule.onNodeWithText("La descripción debe ser un string no vacío")
                    .performScrollTo()
                    .assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }

    /**
     * Test 4: Error cuando el consumo no es numérico
     */
    @Test
    fun testCrearViajeFallido_ConsumoNoNumerico() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        composeTestRule.setContent {
            CrearViajeScreen(
                onCrearViaje = {},
                onBackClick = {},
                efectiveToken = "",
                efectiveUserId = 0.toString(),
                vehiculos = listOf(vehiculoTest)
            )
        }

        composeTestRule.onNodeWithText("Nombre del viaje").performTextInput("Fin de semana en Madrid")
        composeTestRule.onNodeWithText("Descripción").performTextInput("Viaje a Madrid")
        composeTestRule.onNodeWithText("Km realizados").performTextInput("600.0")
        composeTestRule.onNodeWithText("Consumo combustible (L)").performTextInput("abc")
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

        // Pulsar en "Crear Viaje"
        composeTestRule.onNode(
            hasText("Crear Viaje") and hasClickAction()
        ).performScrollTo().performClick()

        composeTestRule.waitUntil(timeoutMillis = 6000) {
            try {
                composeTestRule.onNodeWithText("El consumo de combustible debe ser un número mayor que 0")
                    .performScrollTo()
                    .assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }

    /**
     * Test 5: Error cuando los km no son numéricos
     */
    @Test
    fun testCrearViajeFallido_kmNoNumericos() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        composeTestRule.setContent {
            CrearViajeScreen(
                onCrearViaje = {},
                onBackClick = {},
                efectiveToken = "",
                efectiveUserId = 0.toString(),
                vehiculos = listOf(vehiculoTest)
            )
        }

        composeTestRule.onNodeWithText("Nombre del viaje").performTextInput("Fin de semana en Madrid")
        composeTestRule.onNodeWithText("Descripción").performTextInput("Viaje a Madrid")
        composeTestRule.onNodeWithText("Km realizados").performTextInput("abc")
        composeTestRule.onNodeWithText("Consumo combustible (L)").performTextInput("6.6")
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

        // Pulsar en "Crear Viaje"
        composeTestRule.onNode(
            hasText("Crear Viaje") and hasClickAction()
        ).performScrollTo().performClick()

        composeTestRule.waitUntil(timeoutMillis = 6000) {
            try {
                composeTestRule.onNodeWithText("Los km realizados deben ser un número mayor que 0")
                    .performScrollTo()
                    .assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }

    /**
     * Test 6: Error cuando la fecha de fin es anterior a la de inicio
     */
    @Test
    fun testCrearViajeFallido_FechaFinAntesDeInicio() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        composeTestRule.setContent {
            CrearViajeScreen(
                onCrearViaje = {},
                onBackClick = {},
                efectiveToken = "",
                efectiveUserId = 0.toString(),
                vehiculos = listOf(vehiculoTest)
            )
        }

        composeTestRule.onNodeWithText("Nombre del viaje").performTextInput("Fin de semana en Madrid")
        composeTestRule.onNodeWithText("Descripción").performTextInput("Viaje a Madrid")
        composeTestRule.onNodeWithText("Km realizados").performTextInput("500.0")
        composeTestRule.onNodeWithText("Consumo combustible (L)").performTextInput("6.6")
        // PASO 7: Seleccionar fecha y hora inicio
        composeTestRule.onNodeWithText("Fecha y hora inicio")
            .assertIsDisplayed()
            .performClick()

        // Esperar a que aparezca el diálogo nativo
        device.wait(Until.hasObject(By.text("ACEPTAR")), 1000)

        device.findObject(By.text("15")).click()

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
        device.findObject(By.text("1")).click()

        // Interactuar con el diálogo nativo
        device.findObject(By.text("ACEPTAR")).click()

        // Ahora el TimePicker
        device.wait(Until.hasObject(By.text("ACEPTAR")), 3000)
        device.findObject(By.text("ACEPTAR")).click()

        // Pulsar en "Crear Viaje"
        composeTestRule.onNode(
            hasText("Crear Viaje") and hasClickAction()
        ).performScrollTo().performClick()

        composeTestRule.waitUntil(timeoutMillis = 6000) {
            try {
                composeTestRule.onNodeWithText("La fecha de inicio no puede ser mayor que la de fin")
                    .performScrollTo()
                    .assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }
}