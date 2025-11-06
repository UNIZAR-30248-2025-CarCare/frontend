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
import eina.unizar.frontend.models.toVehiculo
import eina.unizar.frontend.models.VehiculoDTO
import eina.unizar.frontend.EstadoVehiculo
import eina.unizar.frontend.TipoVehiculo

/*
 * Tests de la pantalla CrearRevisionScreen
 * No se comprueba el formato de las fechas, ya que
 * la interfaz de usuario no permite introducir datos inválidos en esos campos.
 */
@RunWith(AndroidJUnit4::class)
class CrearRevisionScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    val vehiculoTest = Vehiculo(
        id = "1",
        estado = EstadoVehiculo.DISPONIBLE,
        nombre = "Coche Test",
        matricula = "1234 BBC",
        modelo = "Passar",
        fabricante = "Volkswagen",
        antiguedad = 4,
        tipo_combustible = "Diésel",
        litros_combustible = 45.0f,
        consumo_medio = 5.5f,
        ubicacion_actual = null,
        tipo = TipoVehiculo.COCHE,
        usuariosVinculados = listOf("0")
    )

    /**
     * Test 1: Creación exitosa de revisión con datos válidos
     */
    @Test
    fun testCrearRevisionExitoso_DatosValidos() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        composeTestRule.setContent {
            CrearRevisionScreen(
                onBackClick = {},
                efectiveUserId = "1",
                efectiveToken = "token_test"
            )
        }

        // Esperar a que cargue la pantalla
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("Crear Revisión").fetchSemanticsNodes().isNotEmpty()
        }

        // Rellenar campos básicos
        composeTestRule.onNodeWithText("Kilometraje actual")
            .performTextInput("50000")

        composeTestRule.onNodeWithText("Observaciones")
            .performTextInput("Cambio de pastillas de freno delanteras. Todo en perfecto estado.")

        // Seleccionar fecha de revisión
        composeTestRule.onNodeWithText("Fecha de la revisión")
            .assertIsDisplayed()
            .performClick()

        // Esperar a que aparezca el diálogo nativo
        device.wait(Until.hasObject(By.text("OK")), 1000)

        device.findObject(By.text("1")).click()

        // Interactuar con el diálogo nativo
        device.findObject(By.text("OK")).click()

        // Pulsar en "Crear Revisión"
        composeTestRule.onNode(
            hasText("Crear Revisión") and hasClickAction()
        ).performScrollTo().performClick()

        // ✅ SOLO VERIFICAR QUE NO APARECEN ERRORES
        composeTestRule.waitUntil(timeoutMillis = 1000) {
            try {
                composeTestRule.onNodeWithText("Por favor, selecciona la fecha de la revisión").assertDoesNotExist()
                composeTestRule.onNodeWithText("El kilometraje no puede estar vacío").assertDoesNotExist()
                composeTestRule.onNodeWithText("Las observaciones no pueden estar vacías").assertDoesNotExist()
                composeTestRule.onNodeWithText("El kilometraje debe ser un número válido").assertDoesNotExist()
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }

    /**
     * Test 2: Verificar que los campos existen
     */
    @Test
    fun testCamposPresentesEnPantalla() {
        composeTestRule.setContent {
            CrearRevisionScreen(
                onBackClick = {},
                efectiveUserId = "1",
                efectiveToken = "token_test"
            )
        }

        // Esperar a que cargue la pantalla
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("Crear Revisión").fetchSemanticsNodes().isNotEmpty()
        }

        // Verificar que todos los campos existen
        composeTestRule.onNodeWithText("Kilometraje actual").assertExists()
        composeTestRule.onNodeWithText("Observaciones").assertExists()
        composeTestRule.onNodeWithText("Taller (opcional)").assertExists()
        composeTestRule.onNodeWithText("Fecha de la revisión").assertExists()
        composeTestRule.onNodeWithText("Próxima revisión (opcional)").assertExists()
        composeTestRule.onNode(hasText("Crear Revisión") and hasClickAction()).assertExists()
    }

    /**
     * Test 3: Selección de diferentes tipos de revisión
     */
    @Test
    fun testSeleccionTipoRevision() {
        composeTestRule.setContent {
            CrearRevisionScreen(
                onBackClick = {},
                efectiveUserId = "1",
                efectiveToken = "token_test"
            )
        }

        // Esperar a que cargue la pantalla
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("Aceite").fetchSemanticsNodes().isNotEmpty()
        }

        // Verificar que el tipo por defecto es "Aceite"
        composeTestRule.onNodeWithText("Aceite").assertIsDisplayed()

        // Abrir dropdown de tipos
        composeTestRule.onNodeWithText("Aceite").performClick()

        // Verificar que aparecen otros tipos y seleccionar uno
        composeTestRule.onNodeWithText("Motor").assertExists().performClick()

        // Verificar que cambió a "Motor"
        composeTestRule.onNodeWithText("Motor").assertIsDisplayed()
    }

    /**
     * Test 4: Introducir datos en campos de texto
     */
    @Test
    fun testIntroducirDatosEnCampos() {
        composeTestRule.setContent {
            CrearRevisionScreen(
                onBackClick = {},
                efectiveUserId = "1",
                efectiveToken = "token_test"
            )
        }

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("Crear Revisión").fetchSemanticsNodes().isNotEmpty()
        }

        // Introducir kilometraje
        composeTestRule.onNodeWithText("Kilometraje actual")
            .performTextInput("50000")

        // Verificar que se introdujo correctamente
        composeTestRule.onNodeWithText("50000").assertExists()

        // Introducir taller
        composeTestRule.onNodeWithText("Taller (opcional)")
            .performTextInput("Taller García")

        // Verificar que se introdujo correctamente
        composeTestRule.onNodeWithText("Taller García").assertExists()

        // Introducir observaciones
        composeTestRule.onNodeWithText("Observaciones")
            .performTextInput("Cambio de aceite completo")

        // Verificar que se introdujo correctamente
        composeTestRule.onNodeWithText("Cambio de aceite completo").assertExists()
    }

    /**
     * Test 5: Verificar campos opcionales vs obligatorios
     */
    @Test
    fun testCamposObligatoriosVsOpcionales() {
        composeTestRule.setContent {
            CrearRevisionScreen(
                onBackClick = {},
                efectiveUserId = "1",
                efectiveToken = "token_test"
            )
        }

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("Crear Revisión").fetchSemanticsNodes().isNotEmpty()
        }

        // Verificar que campos opcionales están marcados como tal
        composeTestRule.onNodeWithText("Taller (opcional)").assertExists()
        composeTestRule.onNodeWithText("Próxima revisión (opcional)").assertExists()

        // Verificar que campos obligatorios NO tienen la marca "(opcional)"
        composeTestRule.onNodeWithText("Kilometraje actual").assertExists()
        composeTestRule.onNodeWithText("Observaciones").assertExists()
        composeTestRule.onNodeWithText("Fecha de la revisión").assertExists()
    }

    /**
     * Test 6: Interacción con campos de fecha
     */
    @Test
    fun testCamposFecha() {
        composeTestRule.setContent {
            CrearRevisionScreen(
                onBackClick = {},
                efectiveUserId = "1",
                efectiveToken = "token_test"
            )
        }

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("Crear Revisión").fetchSemanticsNodes().isNotEmpty()
        }

        // Verificar que los campos de fecha están presentes y son clickeables
        composeTestRule.onNodeWithText("Fecha de la revisión")
            .assertExists()
            .performClick() // Esto abrirá el DatePicker

        composeTestRule.onNodeWithText("Próxima revisión (opcional)")
            .assertExists()
            .performClick() // Esto abrirá el DatePicker
    }
}