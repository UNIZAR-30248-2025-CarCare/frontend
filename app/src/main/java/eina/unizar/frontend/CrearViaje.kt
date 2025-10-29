package eina.unizar.frontend

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eina.unizar.frontend.models.NuevoViajeData
import eina.unizar.frontend.models.Ubicacion
import eina.unizar.frontend.models.Vehiculo
import eina.unizar.frontend.models.toVehiculo
import eina.unizar.frontend.viewmodels.HomeViewModel
import java.util.Locale
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import eina.unizar.frontend.viewmodels.ViajesViewModel
import androidx.compose.foundation.gestures.detectTapGestures

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearViajeScreen(
    onBackClick: () -> Unit,
    onCrearViaje: (NuevoViajeData) -> Unit,
    efectiveUserId: String,
    efectiveToken: String
) {
    val context = LocalContext.current
    val viajeViewModel = remember { ViajesViewModel() }
    // ViewModel y vehículos
    val homeViewModel = remember { HomeViewModel() }
    val vehiculosDTO by homeViewModel.vehiculos.collectAsState()
    val vehiculos = vehiculosDTO.map { it.toVehiculo() }

    var errorMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        homeViewModel.fetchVehiculos(efectiveUserId, efectiveToken)
    }

    // Estado para el vehículo seleccionado y referencia al mapa
    var selectedIndex by remember { mutableIntStateOf(0) }
    var vehiculoSeleccionado = vehiculos.getOrNull(selectedIndex)

    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var kmRealizados by remember { mutableStateOf("") }
    var consumoCombustible by remember { mutableStateOf("") }
    var latitud by remember { mutableStateOf("40.4168") }
    var longitud by remember { mutableStateOf("-3.7038") }
    var ubicacionDestino by remember { mutableStateOf("(40.4168, -3.7038)") }
    var expandedVehiculo by remember { mutableStateOf(false) }

    var mostrarSelectorMapa by remember { mutableStateOf(false) }

    // Estados para fecha/hora inicio y fin
    var fechaHoraInicio by remember { mutableStateOf<LocalDateTime?>(null) }
    var fechaHoraFin by remember { mutableStateOf<LocalDateTime?>(null) }
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    // Función para mostrar Date y TimePicker
    fun showDateTimePicker(onDateTimeSelected: (LocalDateTime) -> Unit) {
        val now = Calendar.getInstance()
        DatePickerDialog(context, { _, year, month, dayOfMonth ->
            TimePickerDialog(context, { _, hour, minute ->
                val dateTime = LocalDateTime.of(year, month + 1, dayOfMonth, hour, minute)
                onDateTimeSelected(dateTime)
            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show()
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
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
                    text = "Crear Viaje",
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
                text = "Detalles del viaje",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Selector de vehículo
            Text(
                text = "Vehículo",
                fontSize = 13.sp,
                color = Color(0xFF6B7280),
                modifier = Modifier.padding(bottom = 5.dp)
            )

            ExposedDropdownMenuBox(
                expanded = expandedVehiculo,
                onExpandedChange = { expandedVehiculo = it }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .menuAnchor()
                        .clickable { expandedVehiculo = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        vehiculoSeleccionado?.let { vehiculo ->
                            Log.d("CrearViajeScreen", "Vehículo seleccionado: $vehiculo")
                            val (color, iconRes, name) = when (vehiculo.tipo.toString().uppercase(
                                Locale.ROOT
                            )) {
                                "COCHE" -> Triple(Color(0xFF3B82F6), R.drawable.ic_coche, "Coche")
                                "MOTO" -> Triple(Color(0xFFF59E0B), R.drawable.ic_moto, "Moto")
                                "FURGONETA" -> Triple(Color(0xFF10B981), R.drawable.ic_furgoneta, "Furgoneta")
                                "CAMION" -> Triple(Color(0xFFEF4444), R.drawable.ic_camion, "Camión")
                                else -> Triple(Color(0xFF6B7280), R.drawable.ic_otro, "Otro")
                            }

                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .background(color.copy(alpha = 0.1f), androidx.compose.foundation.shape.CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = iconRes),
                                    contentDescription = name,
                                    tint = color,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "${vehiculo.nombre} - ${vehiculo.matricula}",
                                fontSize = 15.sp,
                                color = Color(0xFF1F2937),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Expandir",
                            tint = Color(0xFF9CA3AF)
                        )
                    }
                }

                ExposedDropdownMenu(
                    expanded = expandedVehiculo,
                    onDismissRequest = { expandedVehiculo = false }
                ) {
                    vehiculos.forEach { vehiculo ->
                        DropdownMenuItem(
                            text = { Text("${vehiculo.nombre} - ${vehiculo.matricula}") },
                            onClick = {
                                vehiculoSeleccionado = vehiculo
                                expandedVehiculo = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del viaje") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDateTimePicker { fechaHoraInicio = it } }
            ) {
                OutlinedTextField(
                    value = fechaHoraInicio?.format(formatter) ?: "",
                    onValueChange = {},
                    label = { Text("Fecha y hora inicio") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    enabled = false,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDateTimePicker { fechaHoraFin = it } }
            ) {
                OutlinedTextField(
                    value = fechaHoraFin?.format(formatter) ?: "",
                    onValueChange = {},
                    label = { Text("Fecha y hora fin") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    enabled = false,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = kmRealizados,
                onValueChange = { kmRealizados = it },
                label = { Text("Km realizados") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = consumoCombustible,
                onValueChange = { consumoCombustible = it },
                label = { Text("Consumo combustible (L)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("UbicacionDestinoBox")
            ) {
                OutlinedTextField(
                    value = ubicacionDestino,
                    onValueChange = {},
                    label = { Text("Ubicación destino") },
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
                    onUbicacionSeleccionada = { lat, lng ->
                        latitud = lat.toString()
                        longitud = lng.toString()
                        ubicacionDestino = "($lat, $lng)"
                        mostrarSelectorMapa = false
                    }
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    vehiculoSeleccionado?.let { vehiculo ->
                        viajeViewModel.crearViaje(
                            viaje = NuevoViajeData(
                                usuarioId = efectiveUserId,
                                vehiculoId = vehiculo.id,
                                nombre = nombre,
                                descripcion = descripcion,
                                fechaHoraInicio = fechaHoraInicio?.format(DateTimeFormatter.ISO_DATE_TIME) ?: "",
                                fechaHoraFin = fechaHoraFin?.format(DateTimeFormatter.ISO_DATE_TIME) ?: "",
                                kmRealizados = kmRealizados.toIntOrNull() ?: 0,
                                consumoCombustible = consumoCombustible.toIntOrNull() ?: 0,
                                ubicacionFinal = Ubicacion(
                                    latitud = latitud.toDoubleOrNull() ?: 0.0,
                                    longitud = longitud.toDoubleOrNull() ?: 0.0
                                )
                            ),
                            token = efectiveToken
                        ){ resultMsg ->
                            if (resultMsg == null) {
                                errorMsg = null
                                Toast.makeText(context, "Viaje creado correctamente", Toast.LENGTH_SHORT).show()
                                onBackClick()
                            } else {
                                errorMsg = resultMsg
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
                ),
                enabled = nombre.isNotBlank() && descripcion.isNotBlank() && fechaHoraInicio != null && fechaHoraFin != null
                        && kmRealizados.isNotBlank() && consumoCombustible.isNotBlank() && latitud.isNotBlank() && longitud.isNotBlank()
            ) {
                Text(
                    text = "Crear Viaje",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (errorMsg != null) {
                Text(
                    text = errorMsg ?: "",
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}