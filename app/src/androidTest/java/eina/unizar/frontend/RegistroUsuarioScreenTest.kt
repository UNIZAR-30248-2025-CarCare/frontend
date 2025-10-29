package eina.unizar.frontend

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class RegistroUsuarioScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Test 1: Registro exitoso con datos válidos
     * Verifica que el usuario puede registrarse correctamente cuando todos los datos son válidos
     */
    @Test
    fun testRegistroExitoso_DatosValidos() {
        composeTestRule.setContent {
            RegistroUsuarioScreen()
        }

        // Rellenar todos los campos con datos válidos
        composeTestRule.onNodeWithText("Nombre").performTextInput("Juan Pérez")
        composeTestRule.onNodeWithText("Email").performTextInput("juan.perez@example.com")
        composeTestRule.onNodeWithText("Contraseña").performTextInput("password123")
        composeTestRule.onNodeWithText("Fecha de nacimiento").performTextInput("15/03/1995")

        // Aceptar términos y condiciones
        composeTestRule.onNode(hasClickAction() and hasText("Acepto los términos y condiciones"))
            .performClick()

        // Hacer clic en registrarse
        composeTestRule.onNodeWithText("Registrarse").performClick()

        // Verificar que no aparece ningún mensaje de error
        composeTestRule.onNodeWithText("Todos los campos son obligatorios").assertDoesNotExist()
        composeTestRule.onNodeWithText("Formato de email incorrecto").assertDoesNotExist()
        composeTestRule.onNodeWithText("La contraseña debe tener al menos 5 caracteres").assertDoesNotExist()
        composeTestRule.onNodeWithText("Formato de fecha incorrecto (DD/MM/AAAA)").assertDoesNotExist()
        composeTestRule.onNodeWithText("Debes ser mayor de 16 años").assertDoesNotExist()
    }

    /**
     * Test 2: Error cuando falta el nombre
     * Verifica que se muestra un error si el campo nombre está vacío
     */
    @Test
    fun testRegistroFallido_NombreVacio() {
        composeTestRule.setContent {
            RegistroUsuarioScreen()
        }

        // Rellenar campos excepto el nombre
        composeTestRule.onNodeWithText("Email").performTextInput("juan.perez@example.com")
        composeTestRule.onNodeWithText("Contraseña").performTextInput("password123")
        composeTestRule.onNodeWithText("Fecha de nacimiento").performTextInput("15/03/1995")

        // Aceptar términos y condiciones
        composeTestRule.onNode(hasClickAction() and hasText("Acepto los términos y condiciones"))
            .performClick()

        // Intentar registrarse
        composeTestRule.onNodeWithText("Registrarse").performClick()

        // Verificar que aparece el mensaje de error
        composeTestRule.onNodeWithText("Todos los campos son obligatorios")
            .assertExists()
    }

    /**
     * Test 3: Error cuando falta el email
     * Verifica que se muestra un error si el campo email está vacío
     */
    @Test
    fun testRegistroFallido_EmailVacio() {
        composeTestRule.setContent {
            RegistroUsuarioScreen()
        }

        // Rellenar campos excepto el email
        composeTestRule.onNodeWithText("Nombre").performTextInput("Juan Pérez")
        composeTestRule.onNodeWithText("Contraseña").performTextInput("password123")
        composeTestRule.onNodeWithText("Fecha de nacimiento").performTextInput("15/03/1995")

        // Aceptar términos y condiciones
        composeTestRule.onNode(hasClickAction() and hasText("Acepto los términos y condiciones"))
            .performClick()

        // Intentar registrarse
        composeTestRule.onNodeWithText("Registrarse").performClick()

        // Verificar que aparece el mensaje de error
        composeTestRule.onNodeWithText("Todos los campos son obligatorios")
            .assertExists()
    }

    /**
     * Test 4: Error cuando falta la contraseña
     * Verifica que se muestra un error si el campo contraseña está vacío
     */
    @Test
    fun testRegistroFallido_ContraseñaVacia() {
        composeTestRule.setContent {
            RegistroUsuarioScreen()
        }

        // Rellenar campos excepto la contraseña
        composeTestRule.onNodeWithText("Nombre").performTextInput("Juan Pérez")
        composeTestRule.onNodeWithText("Email").performTextInput("juan.perez@example.com")
        composeTestRule.onNodeWithText("Fecha de nacimiento").performTextInput("15/03/1995")

        // Aceptar términos y condiciones
        composeTestRule.onNode(hasClickAction() and hasText("Acepto los términos y condiciones"))
            .performClick()

        // Intentar registrarse
        composeTestRule.onNodeWithText("Registrarse").performClick()

        // Verificar que aparece el mensaje de error
        composeTestRule.onNodeWithText("Todos los campos son obligatorios")
            .assertExists()
    }

    /**
     * Test 5: Error cuando falta la fecha de nacimiento
     * Verifica que se muestra un error si el campo fecha está vacío
     */
    @Test
    fun testRegistroFallido_FechaNacimientoVacia() {
        composeTestRule.setContent {
            RegistroUsuarioScreen()
        }

        // Rellenar campos excepto la fecha de nacimiento
        composeTestRule.onNodeWithText("Nombre").performTextInput("Juan Pérez")
        composeTestRule.onNodeWithText("Email").performTextInput("juan.perez@example.com")
        composeTestRule.onNodeWithText("Contraseña").performTextInput("password123")

        // Aceptar términos y condiciones
        composeTestRule.onNode(hasClickAction() and hasText("Acepto los términos y condiciones"))
            .performClick()

        // Intentar registrarse
        composeTestRule.onNodeWithText("Registrarse").performClick()

        // Verificar que aparece el mensaje de error
        composeTestRule.onNodeWithText("Todos los campos son obligatorios")
            .assertExists()
    }

    /**
     * Test 6: Error con formato de email inválido
     * Verifica que se muestra un error si el email no tiene un formato válido
     */
    @Test
    fun testRegistroFallido_EmailInvalido() {
        composeTestRule.setContent {
            RegistroUsuarioScreen()
        }

        // Rellenar campos con email inválido
        composeTestRule.onNodeWithText("Nombre").performTextInput("Juan Pérez")
        composeTestRule.onNodeWithText("Email").performTextInput("emailinvalido")
        composeTestRule.onNodeWithText("Contraseña").performTextInput("password123")
        composeTestRule.onNodeWithText("Fecha de nacimiento").performTextInput("15/03/1995")

        // Aceptar términos y condiciones
        composeTestRule.onNode(hasClickAction() and hasText("Acepto los términos y condiciones"))
            .performClick()

        // Intentar registrarse
        composeTestRule.onNodeWithText("Registrarse").performClick()

        // Verificar que aparece el mensaje de error
        composeTestRule.onNodeWithText("Formato de email incorrecto")
            .assertExists()
    }

    /**
     * Test 7: Error con contraseña demasiado corta
     * Verifica que se muestra un error si la contraseña tiene menos de 5 caracteres
     */
    @Test
    fun testRegistroFallido_ContraseñaCorta() {
        composeTestRule.setContent {
            RegistroUsuarioScreen()
        }

        // Rellenar campos con contraseña corta
        composeTestRule.onNodeWithText("Nombre").performTextInput("Juan Pérez")
        composeTestRule.onNodeWithText("Email").performTextInput("juan.perez@example.com")
        composeTestRule.onNodeWithText("Contraseña").performTextInput("1234")
        composeTestRule.onNodeWithText("Fecha de nacimiento").performTextInput("15/03/1995")

        // Aceptar términos y condiciones
        composeTestRule.onNode(hasClickAction() and hasText("Acepto los términos y condiciones"))
            .performClick()

        // Intentar registrarse
        composeTestRule.onNodeWithText("Registrarse").performClick()

        // Verificar que aparece el mensaje de error
        composeTestRule.onNodeWithText("La contraseña debe tener al menos 5 caracteres")
            .assertExists()
    }

    /**
     * Test 8: Error con formato de fecha inválido
     * Verifica que se muestra un error si la fecha no tiene el formato DD/MM/AAAA
     * No se puede ejecutar ya que la UI no deja introducir una fecha en formato inválido
    @Test
    fun testRegistroFallido_FormatoFechaInvalido() {
        composeTestRule.setContent {
            RegistroUsuarioScreen()
        }

        // Rellenar campos con fecha inválida
        composeTestRule.onNodeWithText("Nombre").performTextInput("Juan Pérez")
        composeTestRule.onNodeWithText("Email").performTextInput("juan.perez@example.com")
        composeTestRule.onNodeWithText("Contraseña").performTextInput("password123")
        composeTestRule.onNodeWithText("Fecha de nacimiento").performTextInput("1995/03/15")

        // Aceptar términos y condiciones
        composeTestRule.onNode(hasClickAction() and hasText("Acepto los términos y condiciones"))
            .performClick()

        // Intentar registrarse
        composeTestRule.onNodeWithText("Registrarse").performClick()

        // Verificar que aparece el mensaje de error
        composeTestRule.onNodeWithText("Formato de fecha incorrecto (DD/MM/AAAA)")
            .assertExists()
    }
     */

    /**
     * Test 9: Error cuando el usuario es menor de 16 años
     * Verifica que se muestra un error si el usuario tiene menos de 16 años
     */
    @Test
    fun testRegistroFallido_MenorDeEdad() {
        composeTestRule.setContent {
            RegistroUsuarioScreen()
        }

        // Calcular una fecha que resulte en menos de 16 años
        val fechaMenorDeEdad = "01/01/2015"

        // Rellenar campos con fecha de menor de edad
        composeTestRule.onNodeWithText("Nombre").performTextInput("Juan Pérez")
        composeTestRule.onNodeWithText("Email").performTextInput("juan.perez@example.com")
        composeTestRule.onNodeWithText("Contraseña").performTextInput("password123")
        composeTestRule.onNodeWithText("Fecha de nacimiento").performTextInput(fechaMenorDeEdad)

        // Aceptar términos y condiciones
        composeTestRule.onNode(hasClickAction() and hasText("Acepto los términos y condiciones"))
            .performClick()

        // Intentar registrarse
        composeTestRule.onNodeWithText("Registrarse").performClick()

        // Verificar que aparece el mensaje de error
        composeTestRule.onNodeWithText("Debes ser mayor de 16 años")
            .assertExists()
    }

    /**
     * Test 10: Error cuando no se aceptan los términos y condiciones
     * Verifica que se muestra un error si no se aceptan los términos
     */
    @Test
    fun testRegistroFallido_TerminosNoAceptados() {
        composeTestRule.setContent {
            RegistroUsuarioScreen()
        }

        // Rellenar todos los campos pero NO aceptar términos
        composeTestRule.onNodeWithText("Nombre").performTextInput("Juan Pérez")
        composeTestRule.onNodeWithText("Email").performTextInput("juan.perez@example.com")
        composeTestRule.onNodeWithText("Contraseña").performTextInput("password123")
        composeTestRule.onNodeWithText("Fecha de nacimiento").performTextInput("15/03/1995")

        // NO marcar el checkbox de términos

        // Verificar que el botón está deshabilitado
        composeTestRule.onNodeWithText("Registrarse")
            .assertIsNotEnabled()
    }

    /**
     * Test 11: Verificar navegación hacia atrás
     * Verifica que el botón de volver funciona correctamente
     */
    @Test
    fun testNavegacion_BotonVolver() {
        var volverPresionado = false

        composeTestRule.setContent {
            RegistroUsuarioScreen(
                onBackClick = { volverPresionado = true }
            )
        }

        // Hacer clic en el botón de volver
        composeTestRule.onNodeWithContentDescription("Volver").performClick()

        assertTrue("Debería navegar hacia atrás", volverPresionado)
    }

    /**
     * Test 12: Verificar navegación a login
     * Verifica que el enlace "Iniciar Sesión" funciona correctamente
     */
    @Test
    fun testNavegacion_IrALogin() {
        var loginPresionado = false

        composeTestRule.setContent {
            RegistroUsuarioScreen(
                onLoginClick = { loginPresionado = true }
            )
        }

        // Hacer clic en el enlace de iniciar sesión
        composeTestRule.onNodeWithText("Iniciar Sesión").performClick()

        assertTrue("Debería navegar a login", loginPresionado)
    }
}

