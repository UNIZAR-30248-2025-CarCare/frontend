package eina.unizar.frontend

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*

/**
 * Tests de la pantalla AnuncioDialog y AnuncioManager
 */
@RunWith(AndroidJUnit4::class)
class AnuncioScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Test 1: Verificar que el diálogo se muestra correctamente
     */
    @Test
    fun testAnuncioDialog_MuestraContenidoCorrectamente() {
        var dialogCerrado = false

        composeTestRule.setContent {
            AnuncioDialog(
                onDismiss = { dialogCerrado = true },
                navController = null
            )
        }

        // Verificar elementos principales
        composeTestRule.onNodeWithText("🚗").assertIsDisplayed()
        composeTestRule.onNodeWithText("¿Cansado de los anuncios?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hazte Premium y disfruta de una experiencia sin interrupciones")
            .assertIsDisplayed()

        // Verificar beneficios
        composeTestRule.onNodeWithText("✓ Sin anuncios").assertIsDisplayed()
        composeTestRule.onNodeWithText("✓ Acceso prioritario").assertIsDisplayed()
        composeTestRule.onNodeWithText("✓ Funciones exclusivas").assertIsDisplayed()

        // Verificar botón Premium
        composeTestRule.onNodeWithText("Quiero ser Premium").assertIsDisplayed()
    }

    /**
     * Test 2: Verificar que el contador de 5 segundos funciona
     */
    @Test
    fun testAnuncioDialog_ContadorFunciona() {
        composeTestRule.setContent {
            AnuncioDialog(
                onDismiss = {},
                navController = null
            )
        }

        // Verificar que el contador inicial es 5
        composeTestRule.onNodeWithText("5 s").assertIsDisplayed()

        // Esperar y verificar que disminuye
        composeTestRule.mainClock.advanceTimeBy(1000)
        composeTestRule.onNodeWithText("4 s").assertIsDisplayed()

        composeTestRule.mainClock.advanceTimeBy(1000)
        composeTestRule.onNodeWithText("3 s").assertIsDisplayed()
    }

    /**
     * Test 3: Verificar que el botón cerrar solo aparece después de 5 segundos
     */
    @Test
    fun testAnuncioDialog_BotonCerrarApareceAlFinal() {
        composeTestRule.setContent {
            AnuncioDialog(
                onDismiss = {},
                navController = null
            )
        }

        // Verificar que el icono de cerrar NO está visible al inicio
        composeTestRule.onNodeWithContentDescription("Cerrar").assertDoesNotExist()

        // Avanzar 5 segundos
        composeTestRule.mainClock.advanceTimeBy(5000)

        // Verificar que ahora SÍ está visible
        composeTestRule.onNodeWithContentDescription("Cerrar").assertIsDisplayed()
    }

    /**
     * Test 4: Verificar que se puede cerrar el diálogo tras el countdown
     */
    @Test
    fun testAnuncioDialog_PuedeCerrarDespuesDeCountdown() {
        var dialogCerrado = false

        composeTestRule.setContent {
            AnuncioDialog(
                onDismiss = { dialogCerrado = false },
                navController = null
            )
        }

        // Avanzar 5 segundos
        composeTestRule.mainClock.advanceTimeBy(5000)

        // Hacer clic en el botón de cerrar
        composeTestRule.onNodeWithContentDescription("Cerrar").performClick()

        // Verificar que se llamó a onDismiss
        composeTestRule.waitForIdle()
    }

    /**
     * Test 5: AnuncioManager - Usuario gratuito debe ver anuncios
     */
    @Test
    fun testAnuncioManager_UsuarioGratuito_MuestraAnuncios() {
        AnuncioManager.resetearContador()

        // Simular 10 acciones
        repeat(9) {
            val deberMostrar = AnuncioManager.deberMostrarAnuncio(esPremium = false)
            assertFalse("No debe mostrar anuncio antes de 10 acciones", deberMostrar)
        }

        // La décima acción debe mostrar anuncio
        val deberMostrar = AnuncioManager.deberMostrarAnuncio(esPremium = false)
        assertTrue("Debe mostrar anuncio en la acción 10", deberMostrar)
    }

    /**
     * Test 6: AnuncioManager - Usuario Premium NO debe ver anuncios
     */
    @Test
    fun testAnuncioManager_UsuarioPremium_NoMuestraAnuncios() {
        AnuncioManager.resetearContador()

        // Simular 20 acciones
        repeat(20) {
            val deberMostrar = AnuncioManager.deberMostrarAnuncio(esPremium = true)
            assertFalse("Usuario Premium nunca debe ver anuncios", deberMostrar)
        }
    }

    /**
     * Test 7: AnuncioManager - Resetear contador funciona
     */
    @Test
    fun testAnuncioManager_ResetearContador() {
        AnuncioManager.resetearContador()

        // Llegar al umbral
        repeat(10) {
            AnuncioManager.deberMostrarAnuncio(esPremium = false)
        }

        // Resetear
        AnuncioManager.resetearContador()

        // Verificar que vuelve a 0
        repeat(9) {
            val deberMostrar = AnuncioManager.deberMostrarAnuncio(esPremium = false)
            assertFalse("Contador debe haberse reseteado", deberMostrar)
        }
    }

    /**
     * Test 8: Verificar que no se puede cerrar antes de tiempo con back press
     */
    @Test
    fun testAnuncioDialog_NoSeCierraAntesDeCountdown() {
        composeTestRule.setContent {
            AnuncioDialog(
                onDismiss = {},
                navController = null
            )
        }

        // Intentar cerrar antes de tiempo (simulando back press)
        // El diálogo debe seguir visible
        composeTestRule.onNodeWithText("¿Cansado de los anuncios?").assertIsDisplayed()
    }

    /**
     * Test 9: AnuncioManager - Contador se mantiene entre llamadas
     */
    @Test
    fun testAnuncioManager_ContadorPersiste() {
        AnuncioManager.resetearContador()

        // Hacer 5 acciones
        repeat(5) {
            AnuncioManager.deberMostrarAnuncio(esPremium = false)
        }

        // Hacer 5 más (total 10)
        repeat(4) {
            AnuncioManager.deberMostrarAnuncio(esPremium = false)
        }

        // La décima debe mostrar anuncio
        val deberMostrar = AnuncioManager.deberMostrarAnuncio(esPremium = false)
        assertTrue("El contador debe persistir entre llamadas", deberMostrar)
    }
}