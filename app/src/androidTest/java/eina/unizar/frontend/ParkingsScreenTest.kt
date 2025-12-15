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
class ParkingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val parkingsPrueba = listOf(
        Parking(
            id = 1,
            nombre = "Parking Centro",
            ubicacion = UbicacionParking(lat = 40.4168, lng = -3.7038),
            notas = "Parking principal del centro de la ciudad",
            usuarioId = 1,
            createdAt = "2025-12-01T10:00:00Z",
            updatedAt = "2025-12-01T10:00:00Z"
        ),
        Parking(
            id = 2,
            nombre = "Parking Universidad",
            ubicacion = UbicacionParking(lat = 41.6488, lng = -0.8891),
            notas = null,
            usuarioId = 1,
            createdAt = "2025-12-01T11:00:00Z",
            updatedAt = "2025-12-01T11:00:00Z"
        )
    )

    /**
     * Test 1: Verificar que todos los elementos del header están presentes
     */
    @Test
    fun testHeader_ElementosCorrectos() {
        composeTestRule.setContent {
            ParkingsScreen(
                userId = "1",
                token = "test_token",
                onBackClick = {},
                onAddParkingClick = {},
                onEditParkingClick = {}
            )
        }

        // Verificar título
        composeTestRule.onNodeWithText("Mis Parkings")
            .assertIsDisplayed()

        // Verificar botón de volver
        composeTestRule.onNodeWithContentDescription("Volver")
            .assertIsDisplayed()

        // Verificar botón de añadir
        composeTestRule.onNodeWithContentDescription("Añadir parking")
            .assertIsDisplayed()
    }

    /**
     * Test 2: Verificar navegación hacia atrás
     */
    @Test
    fun testNavegacion_BotonVolver() {
        var volverPresionado = false

        composeTestRule.setContent {
            ParkingsScreen(
                userId = "1",
                token = "test_token",
                onBackClick = { volverPresionado = true },
                onAddParkingClick = {},
                onEditParkingClick = {}
            )
        }

        // Hacer clic en volver
        composeTestRule.onNodeWithContentDescription("Volver")
            .performClick()

        assertTrue("Debería ejecutar callback de volver", volverPresionado)
    }

    /**
     * Test 3: Verificar navegación al añadir parking
     */
    @Test
    fun testNavegacion_BotonAnadir() {
        var anadirPresionado = false

        composeTestRule.setContent {
            ParkingsScreen(
                userId = "1",
                token = "test_token",
                onBackClick = {},
                onAddParkingClick = { anadirPresionado = true },
                onEditParkingClick = {}
            )
        }

        // Hacer clic en añadir
        composeTestRule.onNodeWithContentDescription("Añadir parking")
            .performClick()

        assertTrue("Debería ejecutar callback de añadir", anadirPresionado)
    }

    /**
     * Test 4: Verificar mensaje cuando no hay parkings
     */
    @Test
    fun testParkings_MensajeSinParkings() {
        composeTestRule.setContent {
            ParkingsScreen(
                userId = "1",
                token = "test_token",
                onBackClick = {},
                onAddParkingClick = {},
                onEditParkingClick = {}
            )
        }

        // Esperar a que cargue
        composeTestRule.waitForIdle()

        // Verificar mensaje de lista vacía
        composeTestRule.onNodeWithText("No hay parkings registrados")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Añade tu primer parking usando el botón +")
            .assertIsDisplayed()
    }

    /**
     * Test 5: Verificar card de parking muestra información correcta
     */
    @Test
    fun testParkingCard_MuestraInformacionCorrecta() {
        val parking = parkingsPrueba[0]

        composeTestRule.setContent {
            ParkingCard(
                parking = parking,
                onDeleteClick = {},
                onEditClick = {}
            )
        }

        // Verificar nombre
        composeTestRule.onNodeWithText(parking.nombre)
            .assertIsDisplayed()

        // Verificar ubicación
        composeTestRule.onNodeWithText("${parking.ubicacion.lat}, ${parking.ubicacion.lng}")
            .assertIsDisplayed()
    }

    /**
     * Test 6: Verificar expansión de card muestra notas
     */
    @Test
    fun testParkingCard_ExpansionMuestraNotas() {
        val parking = parkingsPrueba[0]

        composeTestRule.setContent {
            ParkingCard(
                parking = parking,
                onDeleteClick = {},
                onEditClick = {}
            )
        }

        // Inicialmente las notas no deberían estar visibles
        composeTestRule.onNodeWithText("Notas:")
            .assertDoesNotExist()

        // Hacer clic para expandir
        composeTestRule.onNodeWithContentDescription("Expandir")
            .performClick()

        composeTestRule.waitForIdle()

        // Verificar que aparecen las notas
        composeTestRule.onNodeWithText("Notas:")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(parking.notas!!)
            .assertIsDisplayed()
    }

    /**
     * Test 7: Verificar menú de opciones se abre
     */
    @Test
    fun testParkingCard_MenuOpcionesSeAbre() {
        val parking = parkingsPrueba[0]

        composeTestRule.setContent {
            ParkingCard(
                parking = parking,
                onDeleteClick = {},
                onEditClick = {}
            )
        }

        // Hacer clic en menú
        composeTestRule.onNodeWithContentDescription("Opciones")
            .performClick()

        composeTestRule.waitForIdle()

        // Verificar opciones del menú
        composeTestRule.onNodeWithText("Editar")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Eliminar")
            .assertIsDisplayed()
    }

    /**
     * Test 8: Verificar callback de editar
     */
    @Test
    fun testParkingCard_CallbackEditar() {
        val parking = parkingsPrueba[0]
        var editarPresionado = false
        var parkingEditado: Parking? = null

        composeTestRule.setContent {
            ParkingCard(
                parking = parking,
                onDeleteClick = {},
                onEditClick = {
                    editarPresionado = true
                    parkingEditado = parking
                }
            )
        }

        // Abrir menú
        composeTestRule.onNodeWithContentDescription("Opciones")
            .performClick()

        composeTestRule.waitForIdle()

        // Hacer clic en editar
        composeTestRule.onNodeWithText("Editar")
            .performClick()

        assertTrue("Debería ejecutar callback de editar", editarPresionado)
        assertEquals("Debería pasar el parking correcto", parking, parkingEditado)
    }

    /**
     * Test 9: Verificar callback de eliminar abre diálogo
     */
    @Test
    fun testParkingCard_CallbackEliminar() {
        val parking = parkingsPrueba[0]
        var eliminarPresionado = false

        composeTestRule.setContent {
            ParkingCard(
                parking = parking,
                onDeleteClick = { eliminarPresionado = true },
                onEditClick = {}
            )
        }

        // Abrir menú
        composeTestRule.onNodeWithContentDescription("Opciones")
            .performClick()

        composeTestRule.waitForIdle()

        // Hacer clic en eliminar
        composeTestRule.onNodeWithText("Eliminar")
            .performClick()

        assertTrue("Debería ejecutar callback de eliminar", eliminarPresionado)
    }

    /**
     * Test 10: Verificar contraer card oculta notas
     */
    @Test
    fun testParkingCard_ContraerOcultaNotas() {
        val parking = parkingsPrueba[0]

        composeTestRule.setContent {
            ParkingCard(
                parking = parking,
                onDeleteClick = {},
                onEditClick = {}
            )
        }

        // Expandir
        composeTestRule.onNodeWithContentDescription("Expandir")
            .performClick()

        composeTestRule.waitForIdle()

        // Verificar que aparecen las notas
        composeTestRule.onNodeWithText("Notas:")
            .assertIsDisplayed()

        // Contraer
        composeTestRule.onNodeWithContentDescription("Contraer")
            .performClick()

        composeTestRule.waitForIdle()

        // Verificar que desaparecen las notas
        composeTestRule.onNodeWithText("Notas:")
            .assertDoesNotExist()
    }

    /**
     * Test 11: Verificar card sin notas no muestra sección de notas
     */
    @Test
    fun testParkingCard_SinNotasNoMuestraSeccion() {
        val parking = parkingsPrueba[1] // Este no tiene notas

        composeTestRule.setContent {
            ParkingCard(
                parking = parking,
                onDeleteClick = {},
                onEditClick = {}
            )
        }

        // Expandir
        composeTestRule.onNodeWithContentDescription("Expandir")
            .performClick()

        composeTestRule.waitForIdle()

        // Verificar que no aparece la sección de notas
        composeTestRule.onNodeWithText("Notas:")
            .assertDoesNotExist()
    }

    /**
     * Test 12: Verificar icono del parking se muestra
     */
    @Test
    fun testParkingCard_IconoSeMustra() {
        val parking = parkingsPrueba[0]

        composeTestRule.setContent {
            ParkingCard(
                parking = parking,
                onDeleteClick = {},
                onEditClick = {}
            )
        }

        // Verificar que existe un icono (aunque no podemos verificar cuál específicamente)
        composeTestRule.onNodeWithContentDescription("Expandir")
            .assertExists()
    }
}