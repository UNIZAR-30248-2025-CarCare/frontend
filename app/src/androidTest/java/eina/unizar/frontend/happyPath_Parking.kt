package eina.unizar.frontend

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import eina.unizar.frontend.models.Parking
import eina.unizar.frontend.models.UbicacionParking
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
@RunWith(AndroidJUnit4::class)
class HappyPathParkingTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val parkingPrueba = Parking(
        id = 1,
        nombre = "Parking Test",
        ubicacion = UbicacionParking(lat = 40.4168, lng = -3.7038),
        notas = "Parking de prueba",
        usuarioId = 1,
        createdAt = "2025-12-01T10:00:00Z",
        updatedAt = "2025-12-01T10:00:00Z"
    )

    /**
     * Happy Path 1: Ver lista vacía y navegar a crear parking
     */
    @Test
    fun testHappyPath_VerListaVaciaYNavegarACrear() {
        var navegoACrear = false

        composeTestRule.setContent {
            ParkingsScreen(
                userId = "1",
                token = "test_token",
                onBackClick = {},
                onAddParkingClick = { navegoACrear = true },
                onEditParkingClick = {}
            )
        }

        composeTestRule.waitForIdle()

        // Verificar pantalla de parkings vacía
        composeTestRule.onNodeWithText("Mis Parkings")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("No hay parkings registrados")
            .assertIsDisplayed()

        // Navegar a crear parking
        composeTestRule.onNodeWithContentDescription("Añadir parking")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        assertTrue("Debería navegar a crear parking", navegoACrear)
    }

    /**
     * Happy Path 2: Crear parking con datos válidos
     */
    @Test
    fun testHappyPath_CrearParkingConDatosValidos() {
        composeTestRule.setContent {
            CrearParkingScreen(
                onBackClick = {},
                efectiveToken = "test_token",
                efectiveUserId = "1"
            )
        }

        // Verificar pantalla de creación (buscar en toda la jerarquía)
        composeTestRule.onAllNodesWithText("Crear Parking")[0]
            .assertIsDisplayed()

        // Rellenar nombre
        composeTestRule.onAllNodes(hasSetTextAction())[0]
            .assertIsDisplayed()
            .performTextInput("Parking Centro")

        composeTestRule.waitForIdle()

        // Rellenar notas
        composeTestRule.onAllNodes(hasSetTextAction())[1]
            .assertIsDisplayed()
            .performTextInput("Parking principal del centro")

        composeTestRule.waitForIdle()

        // Hacer clic en el botón (usando selector más específico)
        composeTestRule.onNode(
            hasText("Crear Parking") and hasClickAction()
        ).performScrollTo().performClick()

        composeTestRule.waitForIdle()

        // Verificar que no hay errores de validación
        composeTestRule.waitUntil(timeoutMillis = 2000) {
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
     * Happy Path 3: Ver parking en la lista y abrir opciones
     */
    @Test
    fun testHappyPath_VerParkingYAbrirOpciones() {
        var editarPresionado = false

        composeTestRule.setContent {
            ParkingCard(
                parking = parkingPrueba,
                onDeleteClick = {},
                onEditClick = { editarPresionado = true }
            )
        }

        // Verificar información del parking
        composeTestRule.onNodeWithText(parkingPrueba.nombre)
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("${parkingPrueba.ubicacion.lat}, ${parkingPrueba.ubicacion.lng}")
            .assertIsDisplayed()

        // Abrir menú de opciones
        composeTestRule.onNodeWithContentDescription("Opciones")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        // Verificar opciones disponibles y hacer clic en editar
        composeTestRule.onNodeWithText("Editar")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        assertTrue("Debería navegar a editar", editarPresionado)
    }

    /**
     * Happy Path 4: Expandir y ver detalles del parking
     */
    @Test
    fun testHappyPath_ExpandirYVerDetalles() {
        composeTestRule.setContent {
            ParkingCard(
                parking = parkingPrueba,
                onDeleteClick = {},
                onEditClick = {}
            )
        }

        // Verificar que notas no están visibles inicialmente
        composeTestRule.onNodeWithText("Notas:")
            .assertDoesNotExist()

        // Expandir card
        composeTestRule.onNodeWithContentDescription("Expandir")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        // Verificar notas visibles
        composeTestRule.onNodeWithText("Notas:")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(parkingPrueba.notas!!)
            .assertIsDisplayed()

        // Contraer card
        composeTestRule.onNodeWithContentDescription("Contraer")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        // Verificar notas ocultas nuevamente
        composeTestRule.onNodeWithText("Notas:")
            .assertDoesNotExist()
    }

    /**
     * Happy Path 5: Editar parking existente
     */
    @Test
    fun testHappyPath_EditarParkingExistente() {
        composeTestRule.setContent {
            EditarParkingScreen(
                parking = parkingPrueba,
                onBackClick = {},
                efectiveToken = "test_token",
                onEditSuccess = {}
            )
        }

        // Verificar pantalla de edición
        composeTestRule.onAllNodesWithText("Editar Parking")[0]
            .assertIsDisplayed()

        // Verificar que los datos están precargados
        composeTestRule.onNodeWithText(parkingPrueba.nombre)
            .assertIsDisplayed()

        // Modificar nombre
        composeTestRule.onAllNodes(hasSetTextAction())[0]
            .performTextClearance()

        composeTestRule.waitForIdle()

        composeTestRule.onAllNodes(hasSetTextAction())[0]
            .performTextInput("Parking Centro Actualizado")

        composeTestRule.waitForIdle()

        // Hacer clic en el botón guardar
        composeTestRule.onNode(
            hasText("Guardar Cambios") and hasClickAction()
        ).performClick()

        composeTestRule.waitForIdle()

        // Verificar que no hay errores
        composeTestRule.waitUntil(timeoutMillis = 2000) {
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
     * Happy Path 6: Crear parking solo con nombre (sin notas)
     */
    @Test
    fun testHappyPath_CrearParkingSoloNombre() {
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

        // Verificar que se creó correctamente
        composeTestRule.waitUntil(timeoutMillis = 2000) {
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
     * Happy Path 7: Volver desde pantalla de crear
     */
    @Test
    fun testHappyPath_VolverDesdeCrear() {
        var volverPresionado = false

        composeTestRule.setContent {
            CrearParkingScreen(
                onBackClick = { volverPresionado = true },
                efectiveToken = "test_token",
                efectiveUserId = "1"
            )
        }

        // Verificar botón volver
        composeTestRule.onNodeWithContentDescription("Volver")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        assertTrue("Debería ejecutar callback de volver", volverPresionado)
    }

    /**
     * Happy Path 8: Volver desde pantalla de editar
     */
    @Test
    fun testHappyPath_VolverDesdeEditar() {
        var volverPresionado = false

        composeTestRule.setContent {
            EditarParkingScreen(
                parking = parkingPrueba,
                onBackClick = { volverPresionado = true },
                efectiveToken = "test_token",
                onEditSuccess = {}
            )
        }

        // Verificar botón volver
        composeTestRule.onNodeWithContentDescription("Volver")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        assertTrue("Debería ejecutar callback de volver", volverPresionado)
    }

    /**
     * Happy Path 9: Verificar icono de ubicación en crear parking
     */
    @Test
    fun testHappyPath_IconoUbicacionPresente() {
        composeTestRule.setContent {
            CrearParkingScreen(
                onBackClick = {},
                efectiveToken = "test_token",
                efectiveUserId = "1"
            )
        }

        // Verificar que el icono de ubicación está presente
        composeTestRule.onNodeWithContentDescription("Seleccionar ubicación")
            .assertExists()
    }

    /**
     * Happy Path 10: Crear parking con todos los campos completos
     */
    @Test
    fun testHappyPath_CrearParkingCompleto() {
        composeTestRule.setContent {
            CrearParkingScreen(
                onBackClick = {},
                efectiveToken = "test_token",
                efectiveUserId = "1"
            )
        }

        // Rellenar todos los campos
        composeTestRule.onAllNodes(hasSetTextAction())[0]
            .performTextInput("Parking Completo")

        composeTestRule.waitForIdle()

        composeTestRule.onAllNodes(hasSetTextAction())[1]
            .performTextInput("Parking con todas las opciones configuradas")

        composeTestRule.waitForIdle()

        // Verificar todos los campos están completos
        composeTestRule.onNodeWithText("Parking Completo")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Parking con todas las opciones configuradas")
            .assertIsDisplayed()

        // Verificar botón habilitado usando selector específico
        composeTestRule.onNode(
            hasText("Crear Parking") and hasClickAction()
        ).assertIsEnabled()
    }
}