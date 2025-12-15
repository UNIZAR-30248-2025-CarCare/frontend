package eina.unizar.frontend

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CrearParkingScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Test 1: Verificar que todos los campos están presentes
     */
    @Test
    fun testPantalla_TodosLosCamposPresentes() {
        composeTestRule.setContent {
            CrearParkingScreen(
                onBackClick = {},
                efectiveToken = "test_token",
                efectiveUserId = "1"
            )
        }

        // Verificar título (primer nodo con este texto)
        composeTestRule.onAllNodesWithText("Crear Parking")[0]
            .assertIsDisplayed()

        // Verificar campos
        composeTestRule.onNodeWithText("Detalles del parking").assertIsDisplayed()
        composeTestRule.onNodeWithText("Nombre del parking").assertExists()
        composeTestRule.onNodeWithText("Notas (opcional)").assertExists()

        // Verificar botón (usar selector específico)
        composeTestRule.onNode(
            hasText("Crear Parking") and hasClickAction()
        ).assertExists()
    }

    /**
     * Test 2: Verificar navegación hacia atrás
     */
    @Test
    fun testNavegacion_BotonVolver() {
        var volverPresionado = false

        composeTestRule.setContent {
            CrearParkingScreen(
                onBackClick = { volverPresionado = true },
                efectiveToken = "test_token",
                efectiveUserId = "1"
            )
        }

        // Hacer clic en volver
        composeTestRule.onNodeWithContentDescription("Volver")
            .performClick()

        assertTrue("Debería ejecutar callback de volver", volverPresionado)
    }

    /**
     * Test 3: Verificar campo de nombre funcional
     */
    @Test
    fun testCampoNombre_EdicionFuncional() {
        composeTestRule.setContent {
            CrearParkingScreen(
                onBackClick = {},
                efectiveToken = "test_token",
                efectiveUserId = "1"
            )
        }

        // Escribir en nombre
        composeTestRule.onAllNodes(hasSetTextAction())[0]
            .performTextInput("Parking Centro")

        composeTestRule.waitForIdle()

        // Verificar cambio
        composeTestRule.onNodeWithText("Parking Centro")
            .assertExists()
    }

    /**
     * Test 4: Verificar campo de notas opcional funcional
     */
    @Test
    fun testCampoNotas_EdicionFuncional() {
        composeTestRule.setContent {
            CrearParkingScreen(
                onBackClick = {},
                efectiveToken = "test_token",
                efectiveUserId = "1"
            )
        }

        // Escribir en notas
        composeTestRule.onAllNodes(hasSetTextAction())[1]
            .performTextInput("Parking principal")

        composeTestRule.waitForIdle()

        // Verificar cambio
        composeTestRule.onNodeWithText("Parking principal")
            .assertExists()
    }

    /**
     * Test 5: Verificar validación de nombre vacío
     */
    @Test
    fun testValidacion_NombreVacio() {
        composeTestRule.setContent {
            CrearParkingScreen(
                onBackClick = {},
                efectiveToken = "test_token",
                efectiveUserId = "1"
            )
        }

        // Intentar crear sin nombre usando selector específico
        composeTestRule.onNode(
            hasText("Crear Parking") and hasClickAction()
        ).performClick()

        composeTestRule.waitForIdle()

        // Debería mostrar error
        composeTestRule.onNodeWithText("El nombre debe ser un texto no vacío")
            .assertExists()
    }

    /**
     * Test 6: Verificar creación exitosa con datos válidos
     */
    @Test
    fun testCrearParking_DatosValidos() {
        composeTestRule.setContent {
            CrearParkingScreen(
                onBackClick = {},
                efectiveToken = "test_token",
                efectiveUserId = "1"
            )
        }

        // Rellenar campos
        composeTestRule.onAllNodes(hasSetTextAction())[0]
            .performTextInput("Parking Test")

        composeTestRule.onAllNodes(hasSetTextAction())[1]
            .performTextInput("Notas de prueba")

        composeTestRule.waitForIdle()

        // Crear parking usando selector específico
        composeTestRule.onNode(
            hasText("Crear Parking") and hasClickAction()
        ).performClick()

        composeTestRule.waitForIdle()

        // Verificar que no hay errores
        composeTestRule.waitUntil(timeoutMillis = 1000) {
            try {
                composeTestRule.onNodeWithText("El nombre debe ser un texto no vacío")
                    .assertDoesNotExist()
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }

    /**
     * Test 7: Verificar creación sin notas opcionales
     */
    @Test
    fun testCrearParking_SinNotasOpcionales() {
        composeTestRule.setContent {
            CrearParkingScreen(
                onBackClick = {},
                efectiveToken = "test_token",
                efectiveUserId = "1"
            )
        }

        // Solo rellenar nombre
        composeTestRule.onAllNodes(hasSetTextAction())[0]
            .performTextInput("Parking Mínimo")

        composeTestRule.waitForIdle()

        // Crear parking usando selector específico
        composeTestRule.onNode(
            hasText("Crear Parking") and hasClickAction()
        ).performClick()

        composeTestRule.waitForIdle()

        // No debería haber errores
        composeTestRule.waitUntil(timeoutMillis = 1000) {
            try {
                composeTestRule.onNodeWithText("El nombre debe ser un texto no vacío")
                    .assertDoesNotExist()
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }

    /**
     * Test 8: Verificar header correcto
     */
    @Test
    fun testHeader_ElementosCorrectos() {
        composeTestRule.setContent {
            CrearParkingScreen(
                onBackClick = {},
                efectiveToken = "test_token",
                efectiveUserId = "1"
            )
        }

        // Verificar título (primer nodo)
        composeTestRule.onAllNodesWithText("Crear Parking")[0]
            .assertIsDisplayed()

        // Verificar botón de volver
        composeTestRule.onNodeWithContentDescription("Volver")
            .assertIsDisplayed()
    }

    /**
     * Test 9: Verificar icono de ubicación presente
     */
    @Test
    fun testUbicacion_IconoPresente() {
        composeTestRule.setContent {
            CrearParkingScreen(
                onBackClick = {},
                efectiveToken = "test_token",
                efectiveUserId = "1"
            )
        }

        // Verificar que existe el icono de ubicación
        composeTestRule.onNodeWithContentDescription("Seleccionar ubicación")
            .assertExists()
    }

    /**
     * Test 10: Verificar botón crear inicialmente habilitado
     */
    @Test
    fun testBotonCrear_EstadoInicial() {
        composeTestRule.setContent {
            CrearParkingScreen(
                onBackClick = {},
                efectiveToken = "test_token",
                efectiveUserId = "1"
            )
        }

        // El botón debería estar habilitado (usar selector específico)
        composeTestRule.onNode(
            hasText("Crear Parking") and hasClickAction()
        ).assertIsEnabled()
    }

    /**
     * Test 11: Verificar que el campo de nombre acepta texto largo
     */
    @Test
    fun testCampoNombre_TextoLargo() {
        composeTestRule.setContent {
            CrearParkingScreen(
                onBackClick = {},
                efectiveToken = "test_token",
                efectiveUserId = "1"
            )
        }

        val textoLargo = "Parking de prueba con un nombre muy largo para verificar el comportamiento"
        
        composeTestRule.onAllNodes(hasSetTextAction())[0]
            .performTextInput(textoLargo)

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(textoLargo)
            .assertExists()
    }

    /**
     * Test 12: Verificar que el campo de notas acepta múltiples líneas
     */
    @Test
    fun testCampoNotas_MultilineasFuncional() {
        composeTestRule.setContent {
            CrearParkingScreen(
                onBackClick = {},
                efectiveToken = "test_token",
                efectiveUserId = "1"
            )
        }

        val notasMultilinea = "Línea 1\nLínea 2\nLínea 3"
        
        composeTestRule.onAllNodes(hasSetTextAction())[1]
            .performTextInput(notasMultilinea)

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(notasMultilinea)
            .assertExists()
    }

    /**
     * Test 13: Verificar que se pueden borrar los campos
     */
    @Test
    fun testCampos_BorrarTexto() {
        composeTestRule.setContent {
            CrearParkingScreen(
                onBackClick = {},
                efectiveToken = "test_token",
                efectiveUserId = "1"
            )
        }

        // Escribir en nombre
        composeTestRule.onAllNodes(hasSetTextAction())[0]
            .performTextInput("Parking Temporal")

        composeTestRule.waitForIdle()

        // Borrar texto
        composeTestRule.onAllNodes(hasSetTextAction())[0]
            .performTextClearance()

        composeTestRule.waitForIdle()

        // Verificar que el texto fue borrado (intentar crear debería fallar)
        composeTestRule.onNode(
            hasText("Crear Parking") and hasClickAction()
        ).performClick()

        composeTestRule.waitForIdle()

        // Debería mostrar error de validación
        composeTestRule.onNodeWithText("El nombre debe ser un texto no vacío")
            .assertExists()
    }

    /**
     * Test 14: Verificar comportamiento con espacios en blanco
     */
    @Test
    fun testValidacion_SoloEspaciosEnBlanco() {
        composeTestRule.setContent {
            CrearParkingScreen(
                onBackClick = {},
                efectiveToken = "test_token",
                efectiveUserId = "1"
            )
        }

        // Intentar con solo espacios
        composeTestRule.onAllNodes(hasSetTextAction())[0]
            .performTextInput("   ")

        composeTestRule.waitForIdle()

        // Crear parking
        composeTestRule.onNode(
            hasText("Crear Parking") and hasClickAction()
        ).performClick()

        composeTestRule.waitForIdle()

        // Debería mostrar error si valida trim
        composeTestRule.waitUntil(timeoutMillis = 1000) {
            try {
                composeTestRule.onNodeWithText("El nombre debe ser un texto no vacío")
                    .assertExists()
                true
            } catch (e: AssertionError) {
                false
            }
        }
    }

    /**
     * Test 15: Verificar que se muestran ambos campos de texto
     */
    @Test
    fun testCampos_AmbosVisibles() {
        composeTestRule.setContent {
            CrearParkingScreen(
                onBackClick = {},
                efectiveToken = "test_token",
                efectiveUserId = "1"
            )
        }

        // Verificar que hay exactamente 2 campos de texto
        composeTestRule.onAllNodes(hasSetTextAction())
            .assertCountEquals(2)
    }

    /**
     * Test 16: Verificar interacción completa: escribir, borrar y reescribir
     */
    @Test
    fun testCampos_InteraccionCompleta() {
        composeTestRule.setContent {
            CrearParkingScreen(
                onBackClick = {},
                efectiveToken = "test_token",
                efectiveUserId = "1"
            )
        }

        // Escribir nombre inicial
        composeTestRule.onAllNodes(hasSetTextAction())[0]
            .performTextInput("Parking Inicial")

        composeTestRule.waitForIdle()

        // Borrar
        composeTestRule.onAllNodes(hasSetTextAction())[0]
            .performTextClearance()

        composeTestRule.waitForIdle()

        // Escribir nombre final
        composeTestRule.onAllNodes(hasSetTextAction())[0]
            .performTextInput("Parking Final")

        composeTestRule.waitForIdle()

        // Verificar que se muestra el nombre final
        composeTestRule.onNodeWithText("Parking Final")
            .assertExists()

        // Verificar que el nombre inicial no existe
        composeTestRule.onNodeWithText("Parking Inicial")
            .assertDoesNotExist()
    }

    /**
     * Test 17: Verificar que el título no es clickable
     */
    @Test
    fun testTitulo_NoEsClickable() {
        composeTestRule.setContent {
            CrearParkingScreen(
                onBackClick = {},
                efectiveToken = "test_token",
                efectiveUserId = "1"
            )
        }

        // El título (primer nodo) no debería tener acción de click
        composeTestRule.onAllNodesWithText("Crear Parking")[0]
            .assert(hasNoClickAction())
    }

    /**
     * Test 18: Verificar que el botón tiene la acción correcta
     */
    @Test
    fun testBoton_TieneAccionClick() {
        composeTestRule.setContent {
            CrearParkingScreen(
                onBackClick = {},
                efectiveToken = "test_token",
                efectiveUserId = "1"
            )
        }

        // Verificar que el botón tiene acción de click
        composeTestRule.onNode(
            hasText("Crear Parking") and hasClickAction()
        ).assertExists()
    }

    /**
     * Test 19: Verificar creación con nombre que contiene caracteres especiales
     */
    @Test
    fun testCrearParking_CaracteresEspeciales() {
        composeTestRule.setContent {
            CrearParkingScreen(
                onBackClick = {},
                efectiveToken = "test_token",
                efectiveUserId = "1"
            )
        }

        // Nombre con caracteres especiales
        composeTestRule.onAllNodes(hasSetTextAction())[0]
            .performTextInput("Parking #1 - Centro (Main)")

        composeTestRule.waitForIdle()

        // Verificar que se acepta
        composeTestRule.onNodeWithText("Parking #1 - Centro (Main)")
            .assertExists()
    }

    /**
     * Test 20: Verificar que todos los elementos necesarios están en pantalla
     */
    @Test
    fun testPantalla_ElementosCompletos() {
        composeTestRule.setContent {
            CrearParkingScreen(
                onBackClick = {},
                efectiveToken = "test_token",
                efectiveUserId = "1"
            )
        }

        // Verificar header
        composeTestRule.onNodeWithContentDescription("Volver")
            .assertIsDisplayed()
        
        composeTestRule.onAllNodesWithText("Crear Parking")[0]
            .assertIsDisplayed()

        // Verificar sección de detalles
        composeTestRule.onNodeWithText("Detalles del parking")
            .assertIsDisplayed()

        // Verificar campos
        composeTestRule.onNodeWithText("Nombre del parking")
            .assertExists()
        
        composeTestRule.onNodeWithText("Notas (opcional)")
            .assertExists()

        // Verificar ubicación
        composeTestRule.onNodeWithContentDescription("Seleccionar ubicación")
            .assertExists()

        // Verificar botón
        composeTestRule.onNode(
            hasText("Crear Parking") and hasClickAction()
        ).assertExists()
    }
}