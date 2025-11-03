package eina.unizar.frontend

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.junit.Before
import java.text.SimpleDateFormat
import java.util.*

/**
 * Tests de Aceptación - Happy Path
 *
 * IMPORTANTE: Este test se debe ejecutar con la BD recien creada
 *
 * Estos tests verifican el flujo completo para vincular un usuario a un vehiculo
 * 1. Usuario 1 inicia sesión con las credenciales creadas
 * 2. Usuario 1 llega exitosamente a la pantalla Home
 * 3. Usuario 1 vincula un vehículo con otro usuario
 * 4. Usuario 2 inicia sesión con las credenciales creadas
 * 5. Usuario 2 llega exitosamente a la pantalla Home y acepta la invitación
 * 6. Usuario 2 verifica que el vehículo compartido aparece en su lista de vehículos
 */
@RunWith(AndroidJUnit4::class)
class VincularUsuariosAcceptanceTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val usuario1Email = "juan.perez@email.com"
    private val usuario1Password = "password123"
    private val usuario2Email = "maria.garcia@email.com"
    private val usuario2Password = "password123"

    private val nombreCoche = "Mi Seat León"

    @Before
    fun setup() {
        // Asegurarse de que estamos en la pantalla inicial
        composeTestRule.waitForIdle()
    }

    @Test
    fun happyPath_registroInicioSesionYHome() {
        // PASO 1: Navegar a la pantalla de iniciar sesión
        composeTestRule.onNodeWithText("Continuar")
            .assertIsDisplayed()
            .performClick()

        // Verificar que estamos de vuelta en la pantalla de inicio de sesión
        composeTestRule.onNodeWithText("Bienvenido a")
            .assertIsDisplayed()

        // PASO 2: Iniciar sesión con las credenciales del usuario 1
        // Ingresar email
        composeTestRule.onAllNodesWithText("Email")[0]
            .performTextInput(usuario1Email)

        // Ingresar contraseña
        composeTestRule.onAllNodesWithText("Contraseña")[0]
            .performTextInput(usuario1Password)

        // Click en botón de iniciar sesión
        composeTestRule.onNodeWithText("Iniciar Sesión")
            .performClick()

        // PASO 3: Verificar que llegamos a Home
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            // Ajusta este selector según el contenido de tu pantalla Home
            composeTestRule.onAllNodesWithText("Home")
                .fetchSemanticsNodes().isNotEmpty() ||
                    composeTestRule.onAllNodesWithText("Inicio")
                        .fetchSemanticsNodes().isNotEmpty()
        }

        Thread.sleep(2000)

        // PASO 4: Acceder a un vehículo
        composeTestRule.onNodeWithText(nombreCoche)
            .assertIsDisplayed()
            .performClick()

        Thread.sleep(2000)

        composeTestRule.onNodeWithTag("addUserButton").performScrollTo().performClick()

        Thread.sleep(2000)

        // PASO 5: Ingresar email del usuario a vincular
        composeTestRule.onNodeWithText("Email del invitado")
            .performTextInput(usuario2Email)

        Thread.sleep(2000)

        composeTestRule.onNodeWithText("Enviar")
            .performClick()

        Thread.sleep(2000)

        composeTestRule.onNodeWithText("Invitación generada exitosamente")
            .assertIsDisplayed()

        Thread.sleep(2000)

        composeTestRule.onNodeWithText("Cancelar")
            .performClick()

        Thread.sleep(2000)

        composeTestRule.onNodeWithContentDescription("Volver")
            .performClick()

        Thread.sleep(2000)

        // PASO 6: Verificar que llegamos a Home
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            // Ajusta este selector según el contenido de tu pantalla Home
            composeTestRule.onAllNodesWithText("Home")
                .fetchSemanticsNodes().isNotEmpty() ||
                    composeTestRule.onAllNodesWithText("Inicio")
                        .fetchSemanticsNodes().isNotEmpty()
        }

        Thread.sleep(2000)

        // PASO 7: Cerrar sesión del Usuario 1
        composeTestRule.onNodeWithContentDescription("Perfil")
            .assertIsDisplayed()
            .performClick()

        Thread.sleep(2000)

        composeTestRule.onNodeWithText("Cerrar Sesión")
            .assertIsDisplayed()
            .performClick()

        Thread.sleep(2000)

        // PASO 8: Navegar a la pantalla de iniciar sesión
        composeTestRule.onNodeWithText("Continuar")
            .assertIsDisplayed()
            .performClick()

        Thread.sleep(2000)

        // Verificar que estamos de vuelta en la pantalla de inicio de sesión
        composeTestRule.onNodeWithText("Bienvenido a")
            .assertIsDisplayed()

        Thread.sleep(2000)

        // PASO 2: Iniciar sesión con las credenciales del usuario 2
        // Ingresar email
        composeTestRule.onAllNodesWithText("Email")[0]
            .performTextInput(usuario2Email)

        Thread.sleep(2000)

        // Ingresar contraseña
        composeTestRule.onAllNodesWithText("Contraseña")[0]
            .performTextInput(usuario2Password)

        Thread.sleep(2000)

        // Click en botón de iniciar sesión
        composeTestRule.onNodeWithText("Iniciar Sesión")
            .performClick()

        Thread.sleep(2000)

        // PASO 9: Verificar que llegamos a Home
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            // Ajusta este selector según el contenido de tu pantalla Home
            composeTestRule.onAllNodesWithText("Home")
                .fetchSemanticsNodes().isNotEmpty() ||
                    composeTestRule.onAllNodesWithText("Inicio")
                        .fetchSemanticsNodes().isNotEmpty()
        }

        Thread.sleep(2000)

        // PASO 10: Aceptar la invitación
        composeTestRule.onNodeWithContentDescription("Perfil")
            .assertIsDisplayed()
            .performClick()

        Thread.sleep(2000)

        composeTestRule.onNodeWithText("Ver invitaciones")
            .assertIsDisplayed()
            .performClick()

        Thread.sleep(2000)

        composeTestRule.onNodeWithText(nombreCoche)
            .assertIsDisplayed()

        Thread.sleep(2000)

        composeTestRule.onNodeWithContentDescription("Aceptar")
            .assertIsDisplayed()
            .performClick()

        Thread.sleep(2000)

        composeTestRule.onNodeWithContentDescription("Volver")
            .performClick()

        Thread.sleep(2000)

        // PASO 11: Verificar que llegamos a Home
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            // Ajusta este selector según el contenido de tu pantalla Home
            composeTestRule.onAllNodesWithText("Home")
                .fetchSemanticsNodes().isNotEmpty() ||
                    composeTestRule.onAllNodesWithText("Inicio")
                        .fetchSemanticsNodes().isNotEmpty()
        }

        Thread.sleep(2000)

        // PASO 12: Verificar que el vehículo compartido aparece en la lista de vehículos
        composeTestRule.onNodeWithText(nombreCoche)
            .assertIsDisplayed()

    }

}