package eina.unizar.frontend

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import eina.unizar.frontend.models.Ubicacion
import eina.unizar.frontend.models.Vehiculo
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class EditarVehiculoScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()
    val vehiculo = Vehiculo(
        id = "1",
        estado = EstadoVehiculo.DISPONIBLE, // Usa el enum correspondiente
        nombre = "Coche 1",
        matricula = "1234 BCD",
        modelo = "Corolla",
        fabricante = "Toyota",
        antiguedad = 5,
        tipo_combustible = "Diésel",
        litros_combustible = 45.5f,
        consumo_medio = 5.2f,
        ubicacion_actual = Ubicacion(40.4168, -3.7038), // Ejemplo: Madrid
        tipo = TipoVehiculo.COCHE, // Usa el enum correspondiente
        usuariosVinculados = emptyList()
    )

    /**
     * Test 1: Edición exitosa con datos válidos
     */
    @Test
    fun testRegistroExitoso_DatosValidos() {
        composeTestRule.setContent {
            EditVehiculoScreen(
                onAddClick = {},
                onBackClick = {},
                token = "",
                userId = 0.toString(),
                vehiculo = vehiculo
            )
        }

        // Tipo de vehículo
        composeTestRule.onNodeWithText("Coche").performClick()
        // Nombre del vehículo
        composeTestRule.onNodeWithText("Coche 1").performTextReplacement("Coche 1 EDITADO")
        // Fabricante
        composeTestRule.onNodeWithText("Toyota").performTextReplacement("Toyota EDITADO")
        // Modelo
        composeTestRule.onNodeWithText("Corolla").performTextReplacement("Corolla EDITADO")
        //Matrícula
        composeTestRule.onNodeWithText("1234 BCD").performTextReplacement("1234 BCF")
        // Año ?
        composeTestRule.onNodeWithText("2020").performTextReplacement("2021")
        // Tipo de combustible
        composeTestRule.onNodeWithText("Diésel").performClick()
        composeTestRule.onNodeWithText("Gasolina").performClick()
        // Capacidad depósito
        composeTestRule.onNodeWithText("45.5").performTextReplacement("45.6")
        // Consumo medio
        composeTestRule.onNodeWithText("5.2").performTextReplacement("5.3")

        composeTestRule.onNode(
            hasText("Editar Vehículo") and hasClickAction()
        ).performScrollTo().performClick()

        composeTestRule.waitUntil(timeoutMillis = 1000) {
            try {
                composeTestRule.onNodeWithText("Matrícula inválida").assertDoesNotExist()
                composeTestRule.onNodeWithText("Año inválido").assertDoesNotExist()
                composeTestRule.onNodeWithText("Formato numérico incorrecto").assertDoesNotExist()
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }

    /**
     * Test 2: Matrícula inválida
     */
    @Test
    fun testRegistroFallido_MatriculaInvalida() {
        composeTestRule.setContent {
            EditVehiculoScreen(
                onAddClick = {},
                onBackClick = {},
                token = "",
                userId = 0.toString(),
                vehiculo = vehiculo
            )
        }

        //Matrícula
        composeTestRule.onNodeWithText("1234 BCD").performTextReplacement("12 AB123")

        composeTestRule.onNode(
            hasText("Editar Vehículo") and hasClickAction()
        ).performScrollTo().performClick()

        composeTestRule.waitUntil(timeoutMillis = 2000) {
            try {
                composeTestRule.onNodeWithText("Matrícula inválida").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }

    /**
     * Test 3: Año fuera de rango
     */
    @Test
    fun testRegistroFallido_AnioInvalido() {
        composeTestRule.setContent {
            EditVehiculoScreen(
                onAddClick = {},
                onBackClick = {},
                token = "",
                userId = 0.toString(),
                vehiculo = vehiculo
            )
        }

        // Año
        composeTestRule.onNodeWithText("2020").performTextReplacement("1899")


        composeTestRule.onNode(
            hasText("Editar Vehículo") and hasClickAction()
        ).performScrollTo().performClick()

        composeTestRule.waitUntil(timeoutMillis = 2000) {
            try {
                composeTestRule.onNodeWithText("Año inválido").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }

    /**
     * Test 4: Capacidad depósito formato incorrecto
     */
    @Test
    fun testRegistroFallido_CapacidadFormatoIncorrecto() {
        composeTestRule.setContent {
            EditVehiculoScreen(
                onAddClick = {},
                onBackClick = {},
                token = "",
                userId = 0.toString(),
                vehiculo = vehiculo
            )
        }

        // Capacidad depósito
        composeTestRule.onNodeWithText("45.5").performTextReplacement("45")

        composeTestRule.onNode(
            hasText("Editar Vehículo") and hasClickAction()
        ).performScrollTo().performClick()

        composeTestRule.waitUntil(timeoutMillis = 2000) {
            try {
                composeTestRule.onNodeWithText("Formato numérico incorrecto").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }

    /**
     * Test 5: Consumo medio formato incorrecto
     */
    @Test
    fun testRegistroFallido_ConsumoFormatoIncorrecto() {
        composeTestRule.setContent {
            EditVehiculoScreen(
                onAddClick = {},
                onBackClick = {},
                token = "",
                userId = 0.toString(),
                vehiculo = vehiculo
            )
        }

        composeTestRule.onNodeWithText("5.2").performTextReplacement("5")

        composeTestRule.onNode(
            hasText("Editar Vehículo") and hasClickAction()
        ).performScrollTo().performClick()

        composeTestRule.waitUntil(timeoutMillis = 2000) {
            try {
                composeTestRule.onNodeWithText("Formato numérico incorrecto").assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }

    /*
     * No se han hecho tests para comprobar el tipo de vehículo o el tipo de combustible
     * porque esos campos son desplegables con opciones fijas, por lo que no hay
     * margen de error en la entrada del usuario.
     */
}