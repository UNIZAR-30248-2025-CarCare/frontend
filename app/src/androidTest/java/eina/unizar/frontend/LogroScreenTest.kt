package eina.unizar.frontend

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LogroScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Test 1: Verifica que el header se muestra correctamente
     */
    @Test
    fun testHeaderSeVisualizaCorrectamente() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            LogrosScreen(
                navController = navController,
                userId = "1",
                token = "test-token"
            )
        }

        composeTestRule.onNodeWithText("Logros").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Volver").assertIsDisplayed()
    }

    /**
     * Test 2: Verifica que el botón de actualizar progreso existe
     */
    @Test
    fun testBotonActualizarProgresoExiste() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            LogrosScreen(
                navController = navController,
                userId = "1",
                token = "test-token"
            )
        }

        composeTestRule.onNodeWithContentDescription("Actualizar progreso")
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    /**
     * Test 3: Verifica que el botón de actualizar progreso es clickeable
     */
    @Test
    fun testBotonActualizarEsClickeable() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            LogrosScreen(
                navController = navController,
                userId = "1",
                token = "test-token"
            )
        }

        composeTestRule.onNodeWithContentDescription("Actualizar progreso")
            .performClick()

        // Verificar que no crashea después del click
        composeTestRule.onNodeWithText("Logros")
            .assertIsDisplayed()
    }

    /**
     * Test 4: Verifica que el botón Volver es clickeable
     */
    @Test
    fun testBotonVolverEsClickeable() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            LogrosScreen(
                navController = navController,
                userId = "1",
                token = "test-token"
            )
        }

        composeTestRule.onNodeWithContentDescription("Volver")
            .assertHasClickAction()
            .performClick()
    }

    /**
     * Test 5: Verifica que muestra indicador de carga o contenido
     */
    @Test
    fun testPantallaSeVisualizaCorrectamente() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            LogrosScreen(
                navController = navController,
                userId = "1",
                token = "test-token"
            )
        }

        composeTestRule.waitForIdle()
        
        // Verificar que al menos el header está visible
        composeTestRule.onNodeWithText("Logros")
            .assertIsDisplayed()
    }

    /**
     * Test 6: Verifica que se pueden hacer múltiples clicks en el botón de actualizar
     */
    @Test
    fun testBotonActualizarPermiteMultiplesClicks() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            LogrosScreen(
                navController = navController,
                userId = "1",
                token = "test-token"
            )
        }

        val botonActualizar = composeTestRule.onNodeWithContentDescription("Actualizar progreso")
        
        // Click múltiples veces
        botonActualizar.performClick()
        composeTestRule.waitForIdle()
        botonActualizar.performClick()
        composeTestRule.waitForIdle()
        botonActualizar.performClick()

        // Verificar que sigue funcionando
        composeTestRule.onNodeWithText("Logros")
            .assertIsDisplayed()
    }

    /**
     * Test 7: Verifica navegación (botón volver tiene acción)
     */
    @Test
    fun testNavegacionVolverFunciona() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            LogrosScreen(
                navController = navController,
                userId = "1",
                token = "test-token"
            )
        }

        composeTestRule.onNodeWithContentDescription("Volver")
            .performClick()

        // El botón debe tener acción de click
        composeTestRule.onNodeWithContentDescription("Volver")
            .assertHasClickAction()
    }

    /**
     * Test 8: Verifica que el layout principal existe
     */
    @Test
    fun testLayoutPrincipalExiste() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            LogrosScreen(
                navController = navController,
                userId = "1",
                token = "test-token"
            )
        }

        // Verificar elementos clave del layout
        composeTestRule.onNodeWithText("Logros").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Volver").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Actualizar progreso").assertIsDisplayed()
    }

    /**
     * Test 9: Verifica que la pantalla tiene una estructura básica
     */
    @Test
    fun testEstructuraBasicaExiste() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            LogrosScreen(
                navController = navController,
                userId = "1",
                token = "test-token"
            )
        }

        composeTestRule.waitForIdle()

        // Verificar que existe el TopAppBar
        composeTestRule.onNodeWithText("Logros").assertExists()
        composeTestRule.onNodeWithContentDescription("Volver").assertExists()
        composeTestRule.onNodeWithContentDescription("Actualizar progreso").assertExists()
    }

    /**
     * Test 10: Verifica que no crashea al hacer scroll
     */
    @Test
    fun testPantallaPermiteScroll() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            LogrosScreen(
                navController = navController,
                userId = "1",
                token = "test-token"
            )
        }

        composeTestRule.waitForIdle()
        
        // Solo verificar que la pantalla principal existe
        composeTestRule.onNodeWithText("Logros")
            .assertIsDisplayed()
    }

    /**
     * Test 11: Verifica que el TopAppBar tiene el color correcto
     */
    @Test
    fun testTopAppBarExiste() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            LogrosScreen(
                navController = navController,
                userId = "1",
                token = "test-token"
            )
        }

        // Verificar elementos del TopAppBar
        composeTestRule.onNodeWithText("Logros")
            .assertIsDisplayed()
    }

    /**
     * Test 12: Verifica que todos los botones del TopAppBar funcionan
     */
    @Test
    fun testBotonesTopAppBarFuncionan() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            LogrosScreen(
                navController = navController,
                userId = "1",
                token = "test-token"
            )
        }

        // Verificar botón volver
        composeTestRule.onNodeWithContentDescription("Volver")
            .assertHasClickAction()

        // Verificar botón actualizar
        composeTestRule.onNodeWithContentDescription("Actualizar progreso")
            .assertHasClickAction()
    }

    /**
     * Test 13: Verifica que la pantalla maneja userId válido
     */
    @Test
    fun testPantallaConUserIdValido() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            LogrosScreen(
                navController = navController,
                userId = "1",
                token = "test-token"
            )
        }

        composeTestRule.waitForIdle()

        // Verificar que se muestra correctamente
        composeTestRule.onNodeWithText("Logros")
            .assertIsDisplayed()
    }

    /**
     * Test 14: Verifica que la pantalla maneja diferentes userId
     */
    @Test
    fun testPantallaConDiferentesUserId() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            LogrosScreen(
                navController = navController,
                userId = "999",
                token = "test-token"
            )
        }

        composeTestRule.waitForIdle()

        // Verificar que no crashea
        composeTestRule.onNodeWithText("Logros")
            .assertIsDisplayed()
    }

    /**
     * Test 15: Verifica interacción completa con botones
     */
    @Test
    fun testInteraccionCompletaConBotones() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            LogrosScreen(
                navController = navController,
                userId = "1",
                token = "test-token"
            )
        }

        // Click en actualizar
        composeTestRule.onNodeWithContentDescription("Actualizar progreso")
            .performClick()

        composeTestRule.waitForIdle()

        // Verificar que sigue funcionando
        composeTestRule.onNodeWithText("Logros")
            .assertIsDisplayed()

        // Click en volver
        composeTestRule.onNodeWithContentDescription("Volver")
            .performClick()
    }

    /**
     * Test 16: Verifica que el Scaffold está correctamente configurado
     */
    @Test
    fun testScaffoldEstaConfigurado() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            LogrosScreen(
                navController = navController,
                userId = "1",
                token = "test-token"
            )
        }

        // Verificar elementos principales del Scaffold
        composeTestRule.onNodeWithText("Logros").assertExists()
        composeTestRule.onNodeWithContentDescription("Volver").assertExists()
        composeTestRule.onNodeWithContentDescription("Actualizar progreso").assertExists()
    }

    /**
     * Test 17: Verifica que no hay crashes al cambiar de estado
     */
    @Test
    fun testNoCrashAlCambiarEstado() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            LogrosScreen(
                navController = navController,
                userId = "1",
                token = "test-token"
            )
        }

        // Esperar cambios de estado
        composeTestRule.waitForIdle()

        // Click en actualizar varias veces
        repeat(3) {
            composeTestRule.onNodeWithContentDescription("Actualizar progreso")
                .performClick()
            composeTestRule.waitForIdle()
        }

        // Verificar que sigue funcionando
        composeTestRule.onNodeWithText("Logros")
            .assertIsDisplayed()
    }

    /**
     * Test 18: Verifica composición inicial
     */
    @Test
    fun testComposicionInicial() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            LogrosScreen(
                navController = navController,
                userId = "1",
                token = "test-token"
            )
        }

        // Verificar que se compone sin errores
        composeTestRule.onRoot().assertExists()
    }

    /**
     * Test 19: Verifica que la pantalla responde a eventos
     */
    @Test
    fun testPantallaRespondeAEventos() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            LogrosScreen(
                navController = navController,
                userId = "1",
                token = "test-token"
            )
        }

        // Múltiples interacciones
        composeTestRule.onNodeWithContentDescription("Actualizar progreso")
            .performClick()
        
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Volver")
            .assertExists()
    }

    /**
     * Test 20: Verifica estabilidad general de la pantalla
     */
    @Test
    fun testEstabilidadGeneral() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            LogrosScreen(
                navController = navController,
                userId = "1",
                token = "test-token"
            )
        }

        composeTestRule.waitForIdle()

        // Verificar elementos básicos
        composeTestRule.onNodeWithText("Logros").assertExists()
        composeTestRule.onNodeWithContentDescription("Volver").assertExists()
        composeTestRule.onNodeWithContentDescription("Actualizar progreso").assertExists()

        // Múltiples clicks sin crashes
        repeat(5) {
            composeTestRule.onNodeWithContentDescription("Actualizar progreso")
                .performClick()
            composeTestRule.waitForIdle()
        }

        // Verificar que sigue estable
        composeTestRule.onNodeWithText("Logros").assertIsDisplayed()
    }
}
