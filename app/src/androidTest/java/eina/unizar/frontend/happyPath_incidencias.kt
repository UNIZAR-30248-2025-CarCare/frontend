package eina.unizar.frontend

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Before

@RunWith(AndroidJUnit4::class)
class NuevaIncidenciaScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val preRegisteredEmail = "juan.perez@email.com"
    private val preRegisteredPassword = "password123"

    // ==============================================================================
    // FUNCIONES AUXILIARES DE NAVEGACIÓN CORREGIDAS
    // ==============================================================================

    /**
     * Intenta navegar a la pantalla de Home, realizando el login si es necesario.
     */
    private fun ensureLoggedInAndAtHome() {
        // 1. Verificar si ya estamos en la pantalla Home (Post-login)
        val isHomeLoaded = composeTestRule.onAllNodesWithText("Hola,", ignoreCase = true).fetchSemanticsNodes().isNotEmpty()

        if (isHomeLoaded) {
            // Ya estamos logueados y en Home.
            return
        }

        // --- FLUJO DE LOGIN COMPLETO SI NO ESTAMOS EN HOME ---

        // 2. Intentar click en "Continuar" (Pantalla de bienvenida)
        try {
            composeTestRule.onNodeWithText("Continuar", ignoreCase = true)
                .assertIsDisplayed()
                .performClick()
        } catch (e: Exception) {
            // Si "Continuar" no existe, asumimos que el test ya pasó el inicio,
            // o que Compose está lento. Continuamos.
        }

        // 3. Ingresar credenciales
        composeTestRule.onAllNodesWithText("Email")[0]
            .assertIsDisplayed()
            .performTextInput(preRegisteredEmail)

        composeTestRule.onAllNodesWithText("Contraseña")[0]
            .assertIsDisplayed()
            .performTextInput(preRegisteredPassword)

        // 4. Click en botón de iniciar sesión
        composeTestRule.onNodeWithText("Iniciar Sesión")
            .assertIsEnabled()
            .performClick()

        // 5. Esperar a que se cargue la pantalla Home
        composeTestRule.waitUntil(timeoutMillis = 7000) {
            composeTestRule.onAllNodesWithText("Hola,", ignoreCase = true).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.waitForIdle()
    }

    /**
     * Navega desde Home a la pantalla de Nueva Incidencia.
     */
    private fun navigateToNuevaIncidenciaScreen() {
        ensureLoggedInAndAtHome()

        // --- 1. NAVEGACIÓN A LISTA DE INCIDENCIAS (NavBar) ---

        // 2. HACER CLIC en "Incidencias" en la BottomNavigationBar (USANDO CONTENT DESCRIPTION)
        composeTestRule.onNodeWithContentDescription("Incidencias", ignoreCase = true)
            .assertIsDisplayed()
            .performClick()

        // 3. Esperar a que se cargue la pantalla de Lista de Incidencias.
        val fabContentDescription = "Añadir incidencia"
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithContentDescription(fabContentDescription).fetchSemanticsNodes().isNotEmpty()
        }

        // --- 4. NAVEGACIÓN FINAL: Clic en el FAB (+) ---

        // 5. HACER CLIC en el FAB
        composeTestRule.onNodeWithContentDescription(fabContentDescription)
            .assertIsDisplayed()
            .performClick()

        // 6. Esperar a que se cargue la pantalla de Nueva Incidencia
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Reportar Incidencia").fetchSemanticsNodes().size >= 1
        }
        composeTestRule.waitForIdle()
    }


    // ==============================================================================
    // CONFIGURACIÓN (AHORA MÁS ROBUSTA)
    // ==============================================================================
    @Before
    fun setupLoginAndNavigateToNuevaIncidenciaScreen() {
        // En cada test, navegamos a la pantalla de incidencia desde cero (Home)
        navigateToNuevaIncidenciaScreen()
    }

    // ==============================================================================
    // TESTS DE LA PANTALLA "Reportar Incidencia"
    // ==============================================================================

    @Test
    fun testPantalla_TodosLosCamposPresentes() {
        // 1. Verificación del Título de la pantalla (Seleccionamos el primer nodo encontrado)
        composeTestRule.onAllNodesWithText("Reportar Incidencia")[0].assertIsDisplayed()

        // El resto de aserciones sin cambios
        composeTestRule.onNodeWithText("Detalles").assertIsDisplayed()

        // Verificación de todos los campos de entrada/selección
        composeTestRule.onNodeWithText("Vehículo").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tipo de incidencia").assertIsDisplayed()
        composeTestRule.onNodeWithText("Prioridad").assertIsDisplayed()

        // Verificación de campos de texto por label
        composeTestRule.onNodeWithText("Título").assertIsDisplayed()
        composeTestRule.onNodeWithText("Descripción").assertIsDisplayed()

        // Verificación de Fotos y botón
        composeTestRule.onNodeWithText("Fotos (opcional)").assertIsDisplayed()

        // 2. Verificación del Botón de acción (Usando el selector compuesto para evitar ambigüedad)
        composeTestRule.onNode(
            hasText("Reportar Incidencia") and hasClickAction()
        ).assertIsDisplayed() // Ya lo tienes bien en tu código original, pero es clave usarlo aquí.
    }

    @Test
    fun testTipoIncidencia_TodosLosTiposDisponibles() {
        composeTestRule.waitForIdle()

        // 1. Buscar el dropdown de "Tipo de incidencia" y hacer clic
        // Como es un ExposedDropdownMenuBox, el texto actual será "Avería" (valor por defecto)
        composeTestRule.onAllNodesWithText("Avería")[0]
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        // 2. Verificar que aparecen todas las opciones
        //composeTestRule.onNodeWithText("Avería").assertIsDisplayed()
        composeTestRule.onNodeWithText("Accidente").assertIsDisplayed()
        composeTestRule.onNodeWithText("Mantenimiento").assertIsDisplayed()
        composeTestRule.onNodeWithText("Otro").assertIsDisplayed()

        // 3. Seleccionar una opción
        composeTestRule.onNodeWithText("Mantenimiento").performClick()
        composeTestRule.waitForIdle()

        // 4. Verificar que se seleccionó correctamente
        composeTestRule.onNodeWithText("Mantenimiento").assertIsDisplayed()
    }

    @Test
    fun testPrioridad_TodosLosTiposDisponibles() {
        composeTestRule.waitForIdle()

        // 1. Buscar el dropdown de "Prioridad" y hacer clic (El valor por defecto es "Media")
        composeTestRule.onAllNodesWithText("Media")[0]
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        // 2. Verificar que aparecen todas las opciones
        composeTestRule.onNodeWithText("Alta").assertIsDisplayed()

        // CORRECCIÓN: Usar [1] para referirse a la opción de la lista.
        composeTestRule.onAllNodesWithText("Media")[1].assertIsDisplayed()

        composeTestRule.onNodeWithText("Baja").assertIsDisplayed()

        // 3. Seleccionar una opción
        composeTestRule.onNodeWithText("Alta").performClick()
        composeTestRule.waitForIdle()

        // 4. Verificar que se seleccionó correctamente
        composeTestRule.onNodeWithText("Alta").assertIsDisplayed()
    }

    @Test
    fun testReportarIncidencia_CamposObligatorios() {
        composeTestRule.waitForIdle()

        // 1. Verificar que el botón está deshabilitado al inicio
        composeTestRule.onNode(
            hasText("Reportar Incidencia") and hasClickAction()
        ).assertIsNotEnabled()

        // 2. Rellenar el campo Título
        // Buscar el TextField con el placeholder
        composeTestRule.onAllNodes(
            hasText("Ej: Ruido extraño en el motor")
        )[0].performTextInput("Título de prueba")

        composeTestRule.waitForIdle()

        // 3. Rellenar el campo Descripción
        composeTestRule.onAllNodes(
            hasText("Describe qué ha ocurrido,\ncuándo lo detectaste...")
        )[0].performTextInput("Descripción de prueba")

        composeTestRule.waitForIdle()

        // 4. Verificar que el botón ahora está habilitado
        composeTestRule.onNode(
            hasText("Reportar Incidencia") and hasClickAction()
        ).assertIsEnabled()
    }
}