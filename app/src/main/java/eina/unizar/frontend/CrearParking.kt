package eina.unizar.frontend

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eina.unizar.frontend.models.NuevoParkingData
import eina.unizar.frontend.models.UbicacionParking
import eina.unizar.frontend.viewmodels.ParkingsViewModel
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearParkingScreen(
    onBackClick: () -> Unit,
    efectiveUserId: String,
    efectiveToken: String
) {
    val context = LocalContext.current
    val parkingViewModel = remember { ParkingsViewModel() }

    var nombre by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }
    var lat by remember { mutableStateOf("40.4168") }
    var lng by remember { mutableStateOf("-3.7038") }
    var ubicacionTexto by remember { mutableStateOf("(40.4168, -3.7038)") }
    var mostrarSelectorMapa by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .statusBarsPadding()
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFFEF4444)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Crear Parking",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.width(48.dp))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 30.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Detalles del parking",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del parking") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = notas,
                onValueChange = { notas = it },
                label = { Text("Notas (opcional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(8.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("UbicacionParkingBox")
            ) {
                OutlinedTextField(
                    value = ubicacionTexto,
                    onValueChange = {},
                    label = { Text("Ubicación del parking") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .pointerInput(Unit) {
                            detectTapGestures {
                                mostrarSelectorMapa = true
                            }
                        },
                    readOnly = true,
                    enabled = false,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = Color(0xFF9CA3AF)
                    ),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Seleccionar ubicación"
                        )
                    }
                )
            }

            if (mostrarSelectorMapa) {
                SelectorUbicacionMapLibreDialog(
                    onDismiss = { mostrarSelectorMapa = false },
                    onUbicacionSeleccionada = { latitud, longitud ->
                        lat = latitud.toString()
                        lng = longitud.toString()
                        ubicacionTexto = "($latitud, $longitud)"
                        mostrarSelectorMapa = false
                    }
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    val error = validarCamposParking(nombre)
                    if (error != null) {
                        errorMsg = error
                        return@Button
                    }

                    parkingViewModel.crearParking(
                        parking = NuevoParkingData(
                            nombre = nombre,
                            ubicacion = UbicacionParking(
                                lat = lat.toDoubleOrNull() ?: 0.0,
                                lng = lng.toDoubleOrNull() ?: 0.0
                            ),
                            notas = notas.ifBlank { null }
                        ),
                        token = efectiveToken
                    ) { resultMsg ->
                        if (resultMsg == null) {
                            errorMsg = null
                            Toast.makeText(context, "Parking creado correctamente", Toast.LENGTH_SHORT).show()
                            onBackClick()
                        } else {
                            errorMsg = try {
                                JSONObject(resultMsg).optString("error", "Error desconocido")
                            } catch (e: Exception) {
                                "Error desconocido"
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEF4444)
                )
            ) {
                Text(
                    text = "Crear Parking",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (errorMsg != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMsg ?: "",
                    color = Color.Red,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

fun validarCamposParking(nombre: String): String? {
    if (nombre.isBlank()) return "El nombre debe ser un texto no vacío"
    return null
}