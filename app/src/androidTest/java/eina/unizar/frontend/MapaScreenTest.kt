package eina.unizar.frontend

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import eina.unizar.frontend.models.Vehiculo
import eina.unizar.frontend.models.Ubicacion
import androidx.navigation.testing.TestNavHostController
import androidx.compose.ui.platform.LocalContext
import com.example.carcare.ui.UbicacionVehiculoScreen
import org.junit.Assert.*


@RunWith(AndroidJUnit4::class)
class UbicacionVehiculoScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Test 1: Verificar que la pantalla se carga correctamente
     */
    @Test
    fun testPantalla_CargaCorrectamente() {
        composeTestRule.setContent {
            val navController = TestNavHostController(LocalContext.current)
             UbicacionVehiculoScreen(
                navController = navController,
                efectiveUserId = "u1",
                efectiveToken = "t1"
            )
        }

        // Verificar que el título está presente
        composeTestRule.onNodeWithText("Ubicación del Vehículo")
            .assertIsDisplayed()

        // Verificar que el botón de volver está presente
        composeTestRule.onNodeWithContentDescription("Volver")
            .assertIsDisplayed()
    }

    /**
     * Test 2: Verificar navegación hacia atrás
     */
    @Test
    fun testNavegacion_BotonVolver() {
        var volverPresionado = false

        composeTestRule.setContent {
            val navController = TestNavHostController(LocalContext.current)
            UbicacionVehiculoScreen(
                onBackClick = { volverPresionado = true },
                navController = navController,
                efectiveUserId = "u1",
                efectiveToken = "t1"
            )
        }

        // Hacer clic en volver
        composeTestRule.onNodeWithContentDescription("Volver")
            .performClick()

        assertTrue("Debería ejecutar callback de volver", volverPresionado)
    }

    /**
     * Test 3: Verificar que el mapa se muestra sin crash
     */
    @Test
    fun testMapa_SeMuestraSinCrash() {
        composeTestRule.setContent {
            val navController = TestNavHostController(LocalContext.current)
            UbicacionVehiculoScreen(
                navController = navController,
                efectiveUserId = "u1",
                efectiveToken = "t1"
            )
        }

        // Esperar a que cargue
        composeTestRule.waitForIdle()

        // Si llegamos aquí sin crash, el test pasa
        composeTestRule.onNodeWithText("Ubicación del Vehículo")
            .assertIsDisplayed()
    }

    /**
     * Test 4: Verificar que el BottomNavigationBar está presente
     */
    @Test
    fun testBottomBar_EstáPresente() {
        composeTestRule.setContent {
            val navController = TestNavHostController(LocalContext.current)
            UbicacionVehiculoScreen(
                navController = navController,
                efectiveUserId = "u1",
                efectiveToken = "t1"
            )
        }

        composeTestRule.waitForIdle()

        // Verificar que los items del bottom bar están presentes
        composeTestRule.onNodeWithText("Mapa").assertExists()
    }

    /**
     * Test 5: Verificar que se espera la carga de vehículos sin crash
     */
    @Test
    fun testCarga_EsperaVehiculos() {
        composeTestRule.setContent {
            val navController = TestNavHostController(LocalContext.current)
            UbicacionVehiculoScreen(
                navController = navController,
                efectiveUserId = "u1",
                efectiveToken = "t1"
            )
        }

        // Esperar un tiempo razonable para la carga
        composeTestRule.waitForIdle()

        // Verificar que la pantalla sigue visible
        composeTestRule.onNodeWithText("Ubicación del Vehículo")
            .assertIsDisplayed()
    }

    /**
     * Test 6: Verificar header con título y botón de volver
     */
    @Test
    fun testHeader_ElementosCorrectos() {
        composeTestRule.setContent {
            val navController = TestNavHostController(LocalContext.current)
            UbicacionVehiculoScreen(
                navController = navController,
                efectiveUserId = "u1",
                efectiveToken = "t1"
            )
        }

        // Verificar título
        composeTestRule.onNodeWithText("Ubicación del Vehículo")
            .assertIsDisplayed()

        // Verificar botón de volver
        composeTestRule.onNodeWithContentDescription("Volver")
            .assertIsDisplayed()
    }

    /**
     * Test 7: Verificar que no crashea sin token
     */
    @Test
    fun testSinToken_NoCrashea() {
        composeTestRule.setContent {
            val navController = TestNavHostController(LocalContext.current)
            UbicacionVehiculoScreen(
                navController = navController,
                efectiveUserId = null,
                efectiveToken = null
            )
        }

        composeTestRule.waitForIdle()

        // Verificar que la pantalla se muestra
        composeTestRule.onNodeWithText("Ubicación del Vehículo")
            .assertIsDisplayed()
    }

    /**
     * Test 8: Verificar que no crashea sin userId
     */
    @Test
    fun testSinUserId_NoCrashea() {
        composeTestRule.setContent {
            val navController = TestNavHostController(LocalContext.current)
            UbicacionVehiculoScreen(
                navController = navController,
                efectiveUserId = null,
                efectiveToken = "t1"
            )
        }

        composeTestRule.waitForIdle()

        // Verificar que la pantalla se muestra
        composeTestRule.onNodeWithText("Ubicación del Vehículo")
            .assertIsDisplayed()
    }
}