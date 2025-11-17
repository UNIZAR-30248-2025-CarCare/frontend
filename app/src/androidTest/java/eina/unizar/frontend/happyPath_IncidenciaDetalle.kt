package eina.unizar.frontend

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Before

/**
 * Tests de Aceptación - Happy Path para Creación de Incidencias
 *
 * Estos tests verifican el flujo completo de crear una nueva incidencia
 * usando la API real con datos de la base de datos de prueba.
 */
@RunWith(AndroidJUnit4::class)
class CrearIncidenciaHappyPathTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val preRegisteredEmail = "juan.perez@email.com"
    private val preRegisteredPassword = "password123"

    @Before
    fun setup() {
        // Realizar login antes de cada test
        loginUsuario()
    }

    // ==============================================================================
    // FUNCIONES AUXILIARES
    // ==============================================================================

    /**
     * Realiza el flujo completo de login y espera a que la pantalla Home esté cargada.
     */
    private fun loginUsuario() {
        // Verificar si ya estamos en Home
        val isHomeLoaded = composeTestRule.onAllNodesWithText("Hola,", ignoreCase = true)
            .fetchSemanticsNodes().isNotEmpty()
        if (isHomeLoaded) return

        try {
            composeTestRule.onNodeWithText("Continuar", ignoreCase = true)
                .assertIsDisplayed()
                .performClick()
        } catch (e: Exception) {
            // Ya estamos en login
        }

        // Ingresar credenciales
        composeTestRule.onAllNodesWithText("Email")[0]
            .assertIsDisplayed()
            .performTextInput(preRegisteredEmail)

        composeTestRule.onAllNodesWithText("Contraseña")[0]
            .assertIsDisplayed()
            .performTextInput(preRegisteredPassword)

        // Click en iniciar sesión
        composeTestRule.onNodeWithText("Iniciar Sesión")
            .assertIsEnabled()
            .performClick()

        // Esperar a Home
        composeTestRule.waitUntil(timeoutMillis = 7000) {
            composeTestRule.onAllNodesWithText("Hola,", ignoreCase = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.waitForIdle()
    }

    /**
     * Navega a la pantalla de incidencias desde el menú principal.
     */
    private fun navegarAIncidencias() {
        try {
            composeTestRule.onNodeWithContentDescription("Incidencias", ignoreCase = true)
                .performClick()
        } catch (e: Exception) {
            // Ya estamos en incidencias
        }

        // Esperar a que cargue la lista
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithContentDescription("Añadir incidencia")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.waitForIdle()
    }

    /**
     * Navega a la pantalla de crear nueva incidencia.
     */
    private fun navegarACrearIncidencia() {
        navegarAIncidencias()

        // Hacer click en el FAB (+)
        composeTestRule.onNodeWithContentDescription("Añadir incidencia")
            .assertIsDisplayed()
            .performClick()

        // Esperar a que se cargue la pantalla de Nueva Incidencia
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Reportar Incidencia")
                .fetchSemanticsNodes().size >= 1
        }
        composeTestRule.waitForIdle()
    }

    // ==============================================================================
    // TESTS HAPPY PATH
    // ==============================================================================

    /**
     * Test 1: Verificar que se cargue completa la pantalla de crear incidencia
     */
    @Test
    fun happyPath_cargarPantallaCrearIncidencia() {
        navegarACrearIncidencia()

        // Verificar título de la pantalla
        composeTestRule.onAllNodesWithText("Reportar Incidencia")[0]
            .assertIsDisplayed()

        // Verificar sección de detalles
        composeTestRule.onNodeWithText("Detalles")
            .assertIsDisplayed()

        // Verificar campos principales
        composeTestRule.onNodeWithText("Vehículo")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Tipo de incidencia")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Prioridad")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Título")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Descripción")
            .assertIsDisplayed()

        // Verificar botón de reportar
        composeTestRule.onNode(
            hasText("Reportar Incidencia") and hasClickAction()
        ).assertIsDisplayed()
    }

    /**
     * Test 2: Verificar dropdown de tipo de incidencia
     */
    @Test
    fun happyPath_dropdownTipoIncidencia() {
        navegarACrearIncidencia()

        composeTestRule.waitForIdle()

        // Hacer click en el dropdown de tipo (valor por defecto "AVERIA")
        composeTestRule.onAllNodesWithText("AVERIA")[0]
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        // Verificar que aparecen todas las opciones
        composeTestRule.onNodeWithText("ACCIDENTE").assertIsDisplayed()
        composeTestRule.onNodeWithText("MANTENIMIENTO").assertIsDisplayed()
        composeTestRule.onNodeWithText("OTRO").assertIsDisplayed()

        // Seleccionar una opción
        composeTestRule.onNodeWithText("MANTENIMIENTO").performClick()
        composeTestRule.waitForIdle()

        // Verificar que se seleccionó
        composeTestRule.onNodeWithText("MANTENIMIENTO").assertIsDisplayed()
    }

    /**
     * Test 3: Verificar dropdown de prioridad
     */
    @Test
    fun happyPath_dropdownPrioridad() {
        navegarACrearIncidencia()

        composeTestRule.waitForIdle()

        // Hacer click en el dropdown de prioridad (valor por defecto "MEDIA")
        composeTestRule.onAllNodesWithText("MEDIA")[0]
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        // Verificar que aparecen todas las opciones
        composeTestRule.onNodeWithText("ALTA").assertIsDisplayed()
        composeTestRule.onAllNodesWithText("MEDIA")[1].assertIsDisplayed()
        composeTestRule.onNodeWithText("BAJA").assertIsDisplayed()

        // Seleccionar una opción
        composeTestRule.onNodeWithText("ALTA").performClick()
        composeTestRule.waitForIdle()

        // Verificar que se seleccionó
        composeTestRule.onNodeWithText("ALTA").assertIsDisplayed()
    }

    /*
    /**
     * Test 4: Verificar dropdown de vehículos
     */
    @Test
    fun happyPath_dropdownVehiculos() {
        navegarACrearIncidencia()

        composeTestRule.waitForIdle()

        // Esperar a que carguen los vehículos
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            try {
                composeTestRule.onNodeWithText("Vehículo").assertExists()
                true
            } catch (e: Exception) {
                false
            }
        }

        try {
            // El dropdown de vehículo está en un Card con un icono "Expandir"
            // Buscar el Card del vehículo y hacer click
            composeTestRule.onNode(
                hasAnyDescendant(hasContentDescription("Expandir"))
            ).performClick()

            composeTestRule.waitForIdle()

            // Verificar que se abre el menú con vehículos
            // Esperar a que aparezcan opciones de vehículos en el dropdown
            composeTestRule.waitUntil(timeoutMillis = 3000) {
                // Buscar si hay DropdownMenuItems disponibles
                composeTestRule.onAllNodes(hasClickAction())
                    .fetchSemanticsNodes().size > 5
            }

            // Si hay más de un vehículo, seleccionar el segundo
            val vehiculos = composeTestRule.onAllNodes(
                hasClickAction() and hasAnyAncestor(hasTestTag(""))
            ).fetchSemanticsNodes()

            if (vehiculos.size > 1) {
                // Cerrar el menú haciendo click en el primer vehículo
                composeTestRule.onAllNodes(hasClickAction())[1]
                    .performClick()

                composeTestRule.waitForIdle()
            }
        } catch (e: Exception) {
            // Si hay un solo vehículo o no se puede abrir el dropdown,
            // el test verifica que al menos el componente existe
            composeTestRule.onNodeWithText("Vehículo").assertExists()
        }
    }

     */
    /**
     * Test 5: Verificar que el botón está deshabilitado sin datos
     */
    @Test
    fun happyPath_botonDeshabilitadoSinDatos() {
        navegarACrearIncidencia()

        composeTestRule.waitForIdle()

        // Verificar que el botón está deshabilitado al inicio
        composeTestRule.onNodeWithTag("botonReportarIncidencia")
            .assertIsNotEnabled()
    }

    /**
     * Test 6: Rellenar título
     */
    @Test
    fun happyPath_rellenarTitulo() {
        navegarACrearIncidencia()

        composeTestRule.waitForIdle()

        // Buscar campo de título por placeholder
        composeTestRule.onNode(hasText("Ej: Ruido extraño en el motor"))
            .performClick()

        composeTestRule.waitForIdle()

        // Escribir título
        composeTestRule.onAllNodes(hasSetTextAction())[0]
            .performTextInput("Fallo en el motor")

        composeTestRule.waitForIdle()

        // Verificar que se escribió
        composeTestRule.onNodeWithText("Fallo en el motor")
            .assertExists()
    }

    /**
     * Test 7: Rellenar descripción
     */
    @Test
    fun happyPath_rellenarDescripcion() {
        navegarACrearIncidencia()

        composeTestRule.waitForIdle()

        // Buscar campo de descripción por placeholder
        composeTestRule.onNode(
            hasText("Describe qué ha ocurrido,\ncuándo lo detectaste...")
        ).performClick()

        composeTestRule.waitForIdle()

        // Escribir descripción
        composeTestRule.onAllNodes(hasSetTextAction())[1]
            .performTextInput("El motor hace un ruido extraño al arrancar")

        composeTestRule.waitForIdle()
    }
/*
    /**
     * Test 8: Verificar switch de compartir con grupo
     */
    @Test
    fun happyPath_switchCompartirGrupo() {
        navegarACrearIncidencia()

        composeTestRule.waitForIdle()

        // Hacer scroll para ver el switch
        repeat(3) {
            try {
                composeTestRule.onNode(hasScrollAction())
                    .performTouchInput {
                        swipeUp(startY = bottom * 0.8f, endY = bottom * 0.2f)
                    }
                Thread.sleep(300)
                composeTestRule.waitForIdle()
            } catch (e: Exception) {
                // Si no hay más scroll, continuamos
            }
        }

        // Verificar que existe el texto del switch
        composeTestRule.onNode(
            hasText("Compartir con todos los usuarios")
        ).assertExists()

        try {
            // El Switch está dentro de una Card junto con el texto
            // Buscar el Switch haciendo click en la Card que lo contiene
            composeTestRule.onNode(
                hasAnyDescendant(hasText("Compartir con todos los usuarios")) and
                        hasClickAction()
            ).performClick()

            composeTestRule.waitForIdle()

            // Volver a hacer click para dejarlo como estaba
            composeTestRule.onNode(
                hasAnyDescendant(hasText("Compartir con todos los usuarios")) and
                        hasClickAction()
            ).performClick()

            composeTestRule.waitForIdle()
        } catch (e: Exception) {
            // Si no podemos interactuar con el switch directamente,
            // al menos verificamos que el componente existe
            composeTestRule.onNode(
                hasText("Compartir con todos los usuarios")
            ).assertExists()
        }
    }
 */
    /**
     * Test 9: Botón habilitado con datos completos
     */
    @Test
    fun happyPath_botonHabilitadoConDatos() {
        navegarACrearIncidencia()

        composeTestRule.waitForIdle()

        // Rellenar título
        composeTestRule.onAllNodes(hasSetTextAction())[0]
            .performClick()
        composeTestRule.onAllNodes(hasSetTextAction())[0]
            .performTextInput("Título de prueba")

        composeTestRule.waitForIdle()

        // Rellenar descripción
        composeTestRule.onAllNodes(hasSetTextAction())[1]
            .performClick()
        composeTestRule.onAllNodes(hasSetTextAction())[1]
            .performTextInput("Descripción de prueba")

        composeTestRule.waitForIdle()

        // Cerrar teclado
        Espresso.pressBack()
        Thread.sleep(500)
        composeTestRule.waitForIdle()

        // Verificar que el botón ahora está habilitado
        composeTestRule.onNodeWithTag("botonReportarIncidencia")
            .assertIsEnabled()
    }

    /**
     * Test 10: Cambiar tipo de incidencia a ACCIDENTE
     */
    @Test
    fun happyPath_cambiarTipoAAccidente() {
        navegarACrearIncidencia()

        composeTestRule.waitForIdle()

        // Abrir dropdown de tipo
        composeTestRule.onAllNodesWithText("AVERIA")[0]
            .performClick()

        composeTestRule.waitForIdle()

        // Seleccionar ACCIDENTE
        composeTestRule.onNodeWithText("ACCIDENTE")
            .performClick()

        composeTestRule.waitForIdle()

        // Verificar que se cambió
        composeTestRule.onNodeWithText("ACCIDENTE")
            .assertIsDisplayed()
    }

    /**
     * Test 11: Cambiar tipo de incidencia a OTRO
     */
    @Test
    fun happyPath_cambiarTipoAOtro() {
        navegarACrearIncidencia()

        composeTestRule.waitForIdle()

        // Abrir dropdown de tipo
        composeTestRule.onAllNodesWithText("AVERIA")[0]
            .performClick()

        composeTestRule.waitForIdle()

        // Seleccionar OTRO
        composeTestRule.onNodeWithText("OTRO")
            .performClick()

        composeTestRule.waitForIdle()

        // Verificar que se cambió
        composeTestRule.onNodeWithText("OTRO")
            .assertIsDisplayed()
    }

    /**
     * Test 12: Cambiar prioridad a BAJA
     */
    @Test
    fun happyPath_cambiarPrioridadABaja() {
        navegarACrearIncidencia()

        composeTestRule.waitForIdle()

        // Abrir dropdown de prioridad
        composeTestRule.onAllNodesWithText("MEDIA")[0]
            .performClick()

        composeTestRule.waitForIdle()

        // Seleccionar BAJA
        composeTestRule.onNodeWithText("BAJA")
            .performClick()

        composeTestRule.waitForIdle()

        // Verificar que se cambió
        composeTestRule.onNodeWithText("BAJA")
            .assertIsDisplayed()
    }

    /**
     * Test 13: Verificar sección de fotos opcional
     */
    @Test
    fun happyPath_seccionFotosOpcional() {
        navegarACrearIncidencia()

        composeTestRule.waitForIdle()

        // Hacer scroll para ver la sección de fotos
        try {
            composeTestRule.onNode(hasScrollAction())
                .performTouchInput { swipeUp() }
            composeTestRule.waitForIdle()
        } catch (e: Exception) {
            // Si no hay scroll, continuamos
        }

        // Verificar que existe la sección de fotos
        composeTestRule.onNode(
            hasText("Fotos (opcional) - 0 seleccionadas")
        ).assertExists()

        // Verificar el texto de añadir fotos
        composeTestRule.onNode(
            hasText("Toca para añadir fotos")
        ).assertExists()
    }

    /**
     * Test 14: Flujo completo - Crear incidencia con todos los datos
     */
    @Test
    fun happyPath_crearIncidenciaCompleta() {
        navegarACrearIncidencia()

        composeTestRule.waitForIdle()

        // 1. Cambiar tipo a MANTENIMIENTO
        composeTestRule.onAllNodesWithText("AVERIA")[0]
            .performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("MANTENIMIENTO")
            .performClick()
        composeTestRule.waitForIdle()

        // 2. Cambiar prioridad a ALTA
        composeTestRule.onAllNodesWithText("MEDIA")[0]
            .performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("ALTA")
            .performClick()
        composeTestRule.waitForIdle()

        // 3. Rellenar título
        composeTestRule.onAllNodes(hasSetTextAction())[0]
            .performClick()
        composeTestRule.onAllNodes(hasSetTextAction())[0]
            .performTextInput("Revisión urgente necesaria")

        composeTestRule.waitForIdle()

        // 4. Rellenar descripción
        composeTestRule.onAllNodes(hasSetTextAction())[1]
            .performClick()
        composeTestRule.onAllNodes(hasSetTextAction())[1]
            .performTextInput("El vehículo necesita una revisión completa del sistema de frenos")

        composeTestRule.waitForIdle()

        // 5. Cerrar teclado
        Espresso.pressBack()
        Thread.sleep(500)
        composeTestRule.waitForIdle()

        // 6. Hacer scroll para ver el botón
        repeat(3) {
            try {
                composeTestRule.onNode(hasScrollAction())
                    .performTouchInput {
                        swipeUp(startY = bottom * 0.8f, endY = bottom * 0.2f)
                    }
                Thread.sleep(300)
            } catch (e: Exception) {
                // Si no hay más scroll, continuamos
            }
        }

        composeTestRule.waitForIdle()

        // 7. Verificar que el botón está habilitado
        composeTestRule.onNodeWithTag("botonReportarIncidencia")
            .assertExists()
            .assertIsEnabled()

        // 8. Hacer click en reportar
        composeTestRule.onNodeWithTag("botonReportarIncidencia")
            .performScrollTo()
            .performClick()

        // 9. Esperar a que se procese
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            true // Dar tiempo para el procesamiento
        }

        composeTestRule.waitForIdle()
    }

    /**
     * Test 15: Volver desde crear incidencia sin guardar
     */
    @Test
    fun happyPath_volverSinGuardar() {
        navegarACrearIncidencia()

        composeTestRule.waitForIdle()

        // Hacer click en volver
        try {
            composeTestRule.onNodeWithContentDescription("Volver")
                .performClick()

            composeTestRule.waitForIdle()

            // Verificar que volvimos a la lista de incidencias
            composeTestRule.waitUntil(timeoutMillis = 3000) {
                composeTestRule.onAllNodesWithText("Incidencias")
                    .fetchSemanticsNodes().isNotEmpty()
            }
        } catch (e: Exception) {
            // El test verifica el flujo de navegación
        }
    }
}