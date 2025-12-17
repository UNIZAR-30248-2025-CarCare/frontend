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
import eina.unizar.frontend.models.NuevoRepostajeData
import eina.unizar.frontend.models.Vehiculo
import eina.unizar.frontend.viewmodels.RepostajesViewModel
import org.json.JSONObject

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearRepostajeScreen(
    onBackClick: () -> Unit,
    onCrearRepostaje: (NuevoRepostajeData) -> Unit,
    efectiveUserId: String,
    efectiveToken: String,
    vehiculos: List<Vehiculo>? = null
) {
    val context = LocalContext.current
    val repostajeViewModel = remember { RepostajesViewModel() }
    // ViewModel y vehículos
    val homeViewModel = remember { HomeViewModel() }
    val vehiculosDTO by homeViewModel.vehiculos.collectAsState()
    val vehiculosList = vehiculos ?: vehiculosDTO.map { it.toVehiculo() }

    var errorMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        homeViewModel.fetchVehiculos(efectiveUserId, efectiveToken)
    }

    // Estado para el vehículo seleccionado y referencia al mapa
    var selectedIndex by remember { mutableIntStateOf(0) }
    var vehiculoSeleccionado = vehiculosList.getOrNull(selectedIndex)

    var litros by remember { mutableStateOf("") }
    var precioPorLitro by remember { mutableStateOf("") }
    var precioTotal by remember { mutableStateOf("") }
    var precioTotalEditadoManualmente by remember { mutableStateOf(false) }

    var expandedVehiculo by remember { mutableStateOf(false) }

    // Estados para fecha/hora inicio y fin
    var fechaHora by remember { mutableStateOf<LocalDateTime?>(null) }
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
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFFEF4444),
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
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Text(
                    text = "Crear Repostaje",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
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
                text = "Detalles del repostaje",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Selector de vehículo
            Text(
                text = "Vehículo",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        vehiculoSeleccionado?.let { vehiculo ->
                            Log.d("CrearRepostajeScreen", "Vehículo seleccionado: $vehiculo")
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
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Expandir",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                ExposedDropdownMenu(
                    expanded = expandedVehiculo,
                    onDismissRequest = { expandedVehiculo = false },
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    vehiculosList.forEach { vehiculo ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "${vehiculo.nombre} - ${vehiculo.matricula}",
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
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
                value = litros,
                onValueChange = {
                    litros = it
                    if (!precioTotalEditadoManualmente) {
                        val litrosDouble = it.toDoubleOrNull()
                        val precioPorLitroDouble = precioPorLitro.toDoubleOrNull()
                        if (litrosDouble != null && precioPorLitroDouble != null) {
                            precioTotal = String.format(Locale.US, "%.2f", litrosDouble * precioPorLitroDouble)
                        } else {
                            precioTotal = ""
                        }
                    }
                },
                label = {
                    Text(
                        "Litros repostados",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = precioPorLitro,
                onValueChange = {
                    precioPorLitro = it
                    if (!precioTotalEditadoManualmente) {
                        val litrosDouble = litros.toDoubleOrNull()
                        val precioPorLitroDouble = it.toDoubleOrNull()
                        if (litrosDouble != null && precioPorLitroDouble != null) {
                            precioTotal = String.format(Locale.US, "%.2f", litrosDouble * precioPorLitroDouble)
                        } else {
                            precioTotal = ""
                        }
                    }
                },
                label = {
                    Text(
                        "Precio por litro",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = precioTotal,
                onValueChange = {
                    precioTotal = it
                    precioTotalEditadoManualmente = true
                },
                label = {
                    Text(
                        "Precio total",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDateTimePicker { fechaHora = it } }
            ) {
                OutlinedTextField(
                    value = fechaHora?.format(formatter) ?: "",
                    onValueChange = {},
                    label = {
                        Text(
                            "Fecha y hora del repostaje",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    enabled = false,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    val error = validarCamposRepostaje(
                        vehiculoSeleccionado,
                        litros,
                        precioPorLitro,
                        precioTotal,
                        fechaHora,
                    )
                    if (error != null) {
                        errorMsg = error
                        return@Button
                    }
                    vehiculoSeleccionado?.let { vehiculo ->
                        repostajeViewModel.crearRepostaje(
                            repostaje = NuevoRepostajeData(
                                usuarioId = efectiveUserId,
                                vehiculoId = vehiculo.id,
                                litros = litros.toDouble(),
                                precioPorLitro = precioPorLitro.toDouble(),
                                precioTotal = precioTotal.toDouble(),
                                fecha = fechaHora?.format(DateTimeFormatter.ISO_DATE_TIME) ?: "",
                            ),
                            token = efectiveToken
                        ){ resultMsg ->
                            if (resultMsg == null) {
                                errorMsg = null
                                Toast.makeText(context, "Repostaje creado correctamente", Toast.LENGTH_SHORT).show()
                                onBackClick()
                            } else {
                                errorMsg = try {
                                    JSONObject(resultMsg).optString("error", "Error desconocido")
                                } catch (e: Exception) {
                                    "Error desconocido"
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                // enabled = litros.isNotBlank() &&  fechaHora != null && precioPorLitro.isNotBlank() && precioTotal.isNotBlank()
            ) {
                Text(
                    text = "Crear Repostaje",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
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

@RequiresApi(Build.VERSION_CODES.O)
fun validarCamposRepostaje(
    vehiculoSeleccionado: Vehiculo?,
    litros: String,
    precioPorLitro: String,
    precioTotal: String,
    fechaHora: LocalDateTime?,
): String? {
    if (vehiculoSeleccionado == null) return "El vehículo no existe"
    if (litros.isBlank()) return "Los litros repostados no pueden estar vacíos"
    val litrosDouble = litros.toDoubleOrNull()
    if (litrosDouble == null || litrosDouble <= 0) return "Los litros repostados deben ser un número mayor que 0"
    if (precioPorLitro.isBlank()) return "El precio por litro no puede estar vacío"
    val precioPorLitroDouble = precioPorLitro.toDoubleOrNull()
    if (precioPorLitroDouble == null || precioPorLitroDouble <= 0) return "El precio por litro debe ser un número mayor que 0"
    if (precioTotal.isBlank()) return "El precio total no puede estar vacío"
    val precioTotalDouble = precioTotal.toDoubleOrNull()
    if (precioTotalDouble == null || precioTotalDouble <= 0) return "El precio total debe ser un número mayor que 0"
    if (fechaHora == null) return "La fecha y hora del repostaje no puede estar vacía"
    return null
}