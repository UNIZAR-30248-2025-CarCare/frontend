package eina.unizar.frontend

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class RegistroVehiculoScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Test 1: Registro exitoso con datos válidos
     */
    @Test
    fun testRegistroExitoso_DatosValidos() {
        composeTestRule.setContent {
            AddVehiculoScreen(
                onAddClick = {},
                onBackClick = {},
                token = "",
                userId = 0.toString()
            )
        }

        // Tipo de vehículo
        composeTestRule.onNodeWithText("Coche").performClick()
        // Nombre del vehículo
        composeTestRule.onNodeWithText("Ej: Mi coche, Furgoneta de trabajo...").performTextInput("Coche 1")
        // Fabricante
        composeTestRule.onNodeWithText("Ej: Seat, Toyota, Ford...").performTextInput("Toyota")
        // Modelo
        composeTestRule.onNodeWithText("Ej: Ibiza, Corolla...").performTextInput("Corolla")
        //Matrícula
        composeTestRule.onNodeWithText("1234 ABC").performTextInput("1234 BCD")
        // Año
        composeTestRule.onNodeWithText("2020").performTextInput("2020")
        // Tipo de combustible
        composeTestRule.onNodeWithText("Gasolina").performClick()
        composeTestRule.onNodeWithText("Diésel").performClick()
        // Capacidad depósito
        composeTestRule.onNodeWithText("45.0").performTextInput("45.5")
        // Consumo medio
        composeTestRule.onNodeWithText("5.5").performTextInput("5.2")

        composeTestRule.onNode(
            hasText("Añadir Vehículo") and hasClickAction()
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
            AddVehiculoScreen(
                onAddClick = {},
                onBackClick = {},
                token = "",
                userId = 0.toString()
            )
        }

        // Tipo de vehículo
        composeTestRule.onNodeWithText("Coche").performClick()
        // Nombre del vehículo
        composeTestRule.onNodeWithText("Ej: Mi coche, Furgoneta de trabajo...").performTextInput("Coche 1")
        // Fabricante
        composeTestRule.onNodeWithText("Ej: Seat, Toyota, Ford...").performTextInput("Toyota")
        // Modelo
        composeTestRule.onNodeWithText("Ej: Ibiza, Corolla...").performTextInput("Corolla")
        //Matrícula
        composeTestRule.onNodeWithText("1234 ABC").performTextInput("12 AB123")
        // Año
        composeTestRule.onNodeWithText("2020").performTextInput("2020")
        // Tipo de combustible
        composeTestRule.onNodeWithText("Gasolina").performClick()
        composeTestRule.onNodeWithText("Diésel").performClick()
        // Capacidad depósito
        composeTestRule.onNodeWithText("45.0").performTextInput("45.5")
        // Consumo medio
        composeTestRule.onNodeWithText("5.5").performTextInput("5.2")

        composeTestRule.onNode(
            hasText("Añadir Vehículo") and hasClickAction()
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
            AddVehiculoScreen(
                onAddClick = {},
                onBackClick = {},
                token = "",
                userId = 0.toString()
            )
        }

        // Tipo de vehículo
        composeTestRule.onNodeWithText("Coche").performClick()

        // Nombre del vehículo
        composeTestRule.onNodeWithText("Ej: Mi coche, Furgoneta de trabajo...").performTextInput("Coche 1")
        // Fabricante
        composeTestRule.onNodeWithText("Ej: Seat, Toyota, Ford...").performTextInput("Toyota")
        // Modelo
        composeTestRule.onNodeWithText("Ej: Ibiza, Corolla...").performTextInput("Corolla")
        //Matrícula
        composeTestRule.onNodeWithText("1234 ABC").performTextInput("1234 BCD")
        // Año
        composeTestRule.onNodeWithText("2020").performTextInput("1899")
        // Tipo de combustible
        composeTestRule.onNodeWithText("Gasolina").performClick()
        composeTestRule.onNodeWithText("Diésel").performClick()
        // Capacidad depósito
        composeTestRule.onNodeWithText("45.0").performTextInput("45.5")
        // Consumo medio
        composeTestRule.onNodeWithText("5.5").performTextInput("5.2")

        composeTestRule.onNode(
            hasText("Añadir Vehículo") and hasClickAction()
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
            AddVehiculoScreen(
                onAddClick = {},
                onBackClick = {},
                token = "",
                userId = 0.toString()
            )
        }

        // Tipo de vehículo
        composeTestRule.onNodeWithText("Coche").performClick()
        // Nombre del vehículo
        composeTestRule.onNodeWithText("Ej: Mi coche, Furgoneta de trabajo...").performTextInput("Coche 1")
        // Fabricante
        composeTestRule.onNodeWithText("Ej: Seat, Toyota, Ford...").performTextInput("Toyota")
        // Modelo
        composeTestRule.onNodeWithText("Ej: Ibiza, Corolla...").performTextInput("Corolla")
        //Matrícula
        composeTestRule.onNodeWithText("1234 ABC").performTextInput("1234 BCD")
        // Año
        composeTestRule.onNodeWithText("2020").performTextInput("2020")
        // Tipo de combustible
        composeTestRule.onNodeWithText("Gasolina").performClick()
        composeTestRule.onNodeWithText("Diésel").performClick()
        // Capacidad depósito
        composeTestRule.onNodeWithText("45.0").performTextInput("45")
        // Consumo medio
        composeTestRule.onNodeWithText("5.5").performTextInput("5.2")

        composeTestRule.onNode(
            hasText("Añadir Vehículo") and hasClickAction()
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
            AddVehiculoScreen(
                onAddClick = {},
                onBackClick = {},
                token = "",
                userId = 0.toString()
            )
        }

        // Tipo de vehículo
        composeTestRule.onNodeWithText("Coche").performClick()
        // Nombre del vehículo
        composeTestRule.onNodeWithText("Ej: Mi coche, Furgoneta de trabajo...").performTextInput("Coche 1")
        // Fabricante
        composeTestRule.onNodeWithText("Ej: Seat, Toyota, Ford...").performTextInput("Toyota")
        // Modelo
        composeTestRule.onNodeWithText("Ej: Ibiza, Corolla...").performTextInput("Corolla")
        //Matrícula
        composeTestRule.onNodeWithText("1234 ABC").performTextInput("1234 BCD")
        // Año
        composeTestRule.onNodeWithText("2020").performTextInput("2020")
        // Tipo de combustible
        composeTestRule.onNodeWithText("Gasolina").performClick()
        composeTestRule.onNodeWithText("Diésel").performClick()
        // Capacidad depósito
        composeTestRule.onNodeWithText("45.0").performTextInput("45.5")
        // Consumo medio
        composeTestRule.onNodeWithText("5.5").performTextInput("5")

        composeTestRule.onNode(
            hasText("Añadir Vehículo") and hasClickAction()
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
}