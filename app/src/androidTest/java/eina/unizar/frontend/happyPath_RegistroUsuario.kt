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
 * Estos tests verifican el flujo completo desde el registro hasta llegar a Home:
 * 1. Usuario se registra con datos válidos
 * 2. Usuario inicia sesión con las credenciales creadas
 * 3. Usuario llega exitosamente a la pantalla Home
 */
@RunWith(AndroidJUnit4::class)
class RegistroLoginAcceptanceTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val testEmail = "testuser${System.currentTimeMillis()}@carcare.com"
    private val testPassword = "Test12345"
    private val testNombre = "Usuario Test"
    private val testFechaNacimiento = "01/01/1995"

    @Before
    fun setup() {
        // Asegurarse de que estamos en la pantalla inicial
        composeTestRule.waitForIdle()
    }

    @Test
    fun happyPath_registroInicioSesionYHome() {
        // PASO 1: Navegar a la pantalla de registro
        composeTestRule.onNodeWithText("Continuar")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.onNodeWithText("Registrarse")
            .assertIsDisplayed()
            .performClick()

        // Verificar que estamos en la pantalla de registro
        composeTestRule.onNodeWithText("Registrarse")
            .assertIsDisplayed()

        // PASO 2: Completar el formulario de registro
        // Ingresar nombre
        composeTestRule.onNodeWithText("Nombre")
            .performTextInput(testNombre)

        // Ingresar email
        composeTestRule.onNodeWithText("Email")
            .performTextInput(testEmail)

        // Ingresar contraseña
        composeTestRule.onNodeWithText("Contraseña")
            .performTextInput(testPassword)

        // Ingresar fecha de nacimiento
        composeTestRule.onNodeWithText("Fecha de nacimiento")
            .performTextInput(testFechaNacimiento)

        // Aceptar términos y condiciones
        composeTestRule.onNodeWithText("Acepto los términos y condiciones")
            .assertIsDisplayed()
            .performClick()

        // PASO 3: Enviar formulario de registro
        composeTestRule.onNodeWithText("Registrarse")
            .assertIsEnabled()
            .performClick()

        // Esperar respuesta del servidor
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Bienvenido a")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Verificar que estamos de vuelta en la pantalla de inicio de sesión
        composeTestRule.onNodeWithText("Bienvenido a")
            .assertIsDisplayed()

        // PASO 4: Iniciar sesión con las credenciales recién creadas
        // Ingresar email
        composeTestRule.onAllNodesWithText("Email")[0]
            .performTextInput(testEmail)

        // Ingresar contraseña
        composeTestRule.onAllNodesWithText("Contraseña")[0]
            .performTextInput(testPassword)

        // Click en botón de iniciar sesión
        composeTestRule.onNodeWithText("Iniciar Sesión")
            .performClick()

        // PASO 5: Verificar que llegamos a Home
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            // Ajusta este selector según el contenido de tu pantalla Home
            composeTestRule.onAllNodesWithText("Home")
                .fetchSemanticsNodes().isNotEmpty() ||
                    composeTestRule.onAllNodesWithText("Inicio")
                        .fetchSemanticsNodes().isNotEmpty()
        }

        // Verificar que ya no estamos en la pantalla de login
        composeTestRule.onAllNodesWithText("Bienvenido a")
            .assertCountEquals(0)
    }

}