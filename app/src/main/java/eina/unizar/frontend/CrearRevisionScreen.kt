package eina.unizar.frontend

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import eina.unizar.frontend.models.RevisionRequest
import eina.unizar.frontend.models.TiposRevision
import eina.unizar.frontend.viewmodels.HomeViewModel
import eina.unizar.frontend.viewmodels.RevisionViewModel
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearRevisionScreen(
    onBackClick: () -> Unit,
    efectiveUserId: String,
    efectiveToken: String
) {
    val context = LocalContext.current
    val revisionViewModel = remember { RevisionViewModel() }
    val homeViewModel = remember { HomeViewModel() }

    // Estados del ViewModel
    val vehiculosDTO by homeViewModel.vehiculos.collectAsState()
    val vehiculos = vehiculosDTO.map { it.toVehiculo() }

    // Estados del formulario
    var selectedVehiculoIndex by remember { mutableIntStateOf(0) }
    val vehiculoSeleccionado = vehiculos.getOrNull(selectedVehiculoIndex)
    var expandedVehiculo by remember { mutableStateOf(false) }

    var tipoSeleccionado by remember { mutableStateOf(TiposRevision.ACEITE) }
    var expandedTipo by remember { mutableStateOf(false) }

    var kilometraje by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }
    var taller by remember { mutableStateOf("") }

    // Estados para fechas
    var fechaRevision by remember { mutableStateOf<LocalDateTime?>(null) }
    var proximaRevision by remember { mutableStateOf<LocalDateTime?>(null) }

    var errorMsg by remember { mutableStateOf<String?>(null) }
    var isCreating by remember { mutableStateOf(false) }

    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    // Cargar vehículos al inicio
    LaunchedEffect(Unit) {
        homeViewModel.fetchVehiculos(efectiveUserId, efectiveToken)
    }

    // Función para mostrar DatePicker
    fun showDatePicker(onDateSelected: (LocalDateTime) -> Unit) {
        val now = Calendar.getInstance()
        DatePickerDialog(context, { _, year, month, dayOfMonth ->
            val dateTime = LocalDateTime.of(year, month + 1, dayOfMonth, 12, 0)
            onDateSelected(dateTime)
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
                    text = "Crear Revisión",
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
                text = "Detalles de la revisión",
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
                            val (color, iconRes, name) = when (vehiculo.tipo.toString().uppercase(Locale.ROOT)) {
                                "COCHE" -> Triple(Color(0xFF3B82F6), R.drawable.ic_coche, "Coche")
                                "MOTO" -> Triple(Color(0xFFF59E0B), R.drawable.ic_moto, "Moto")
                                "FURGONETA" -> Triple(Color(0xFF10B981), R.drawable.ic_furgoneta, "Furgoneta")
                                "CAMION" -> Triple(Color(0xFFEF4444), R.drawable.ic_camion, "Camión")
                                else -> Triple(Color(0xFF6B7280), R.drawable.ic_otro, "Otro")
                            }

                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .background(color.copy(alpha = 0.1f), CircleShape),
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
                    vehiculos.forEachIndexed { index, vehiculo ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "${vehiculo.nombre} - ${vehiculo.matricula}",
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {
                                selectedVehiculoIndex = index
                                expandedVehiculo = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            // Selector de tipo de revisión
            Text(
                text = "Tipo de revisión",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 5.dp)
            )

            ExposedDropdownMenuBox(
                expanded = expandedTipo,
                onExpandedChange = { expandedTipo = it }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .menuAnchor()
                        .clickable { expandedTipo = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Build,
                            contentDescription = null,
                            tint = Color(0xFF3B82F6),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = tipoSeleccionado,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Expandir",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                ExposedDropdownMenu(
                    expanded = expandedTipo,
                    onDismissRequest = { expandedTipo = false },
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    TiposRevision.TODOS.forEach { tipo ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    tipo,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {
                                tipoSeleccionado = tipo
                                expandedTipo = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            // Campo kilometraje
            OutlinedTextField(
                value = kilometraje,
                onValueChange = { kilometraje = it },
                label = {
                    Text(
                        "Kilometraje actual",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                suffix = {
                    Text(
                        "km",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            Spacer(modifier = Modifier.height(15.dp))

            // Campo taller
            OutlinedTextField(
                value = taller,
                onValueChange = { taller = it },
                label = {
                    Text(
                        "Taller (opcional)",
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

            Spacer(modifier = Modifier.height(15.dp))

            // Fecha de revisión
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker { fechaRevision = it } }
            ) {
                OutlinedTextField(
                    value = fechaRevision?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "",
                    onValueChange = {},
                    label = {
                        Text(
                            "Fecha de la revisión",
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
                    ),
                    trailingIcon = {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "Seleccionar fecha",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            // Próxima revisión (opcional)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker { proximaRevision = it } }
            ) {
                OutlinedTextField(
                    value = proximaRevision?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "",
                    onValueChange = {},
                    label = {
                        Text(
                            "Próxima revisión (opcional)",
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
                    ),
                    trailingIcon = {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "Seleccionar fecha",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            // Campo observaciones
            OutlinedTextField(
                value = observaciones,
                onValueChange = { observaciones = it },
                label = {
                    Text(
                        "Observaciones",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(8.dp),
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Botón crear
            Button(
                onClick = {
                    vehiculoSeleccionado?.let { vehiculo ->
                        if (fechaRevision != null) {
                            isCreating = true
                            errorMsg = null

                            Log.d("CrearRevision", "Tipo seleccionado: '$tipoSeleccionado'")
                            Log.d("CrearRevision", "Longitud del tipo: ${tipoSeleccionado.length}")

                            val revisionRequest = RevisionRequest(
                                usuarioId = efectiveUserId.toInt(),
                                vehiculoId = vehiculo.id,
                                fecha = fechaRevision!!.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                                tipo = tipoSeleccionado,
                                kilometraje = kilometraje.toIntOrNull() ?: 0,
                                observaciones = observaciones,
                                proximaRevision = proximaRevision?.format(dateFormatter),
                                taller = if (taller.isBlank()) null else taller
                            )

                            // Usar callback
                            revisionViewModel.crearRevision(
                                revision = revisionRequest,
                                token = efectiveToken
                            ) { resultMsg ->
                                isCreating = false
                                if (resultMsg == null) {
                                    errorMsg = null
                                    Toast.makeText(context, "Revisión creada correctamente", Toast.LENGTH_SHORT).show()
                                    onBackClick()
                                } else {
                                    errorMsg = resultMsg
                                }
                            }
                        } else {
                            errorMsg = "Por favor, selecciona la fecha de la revisión"
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
                enabled = !isCreating &&
                        vehiculoSeleccionado != null &&
                        fechaRevision != null &&
                        kilometraje.isNotBlank() &&
                        observaciones.isNotBlank()
            ) {
                if (isCreating) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = "Crear Revisión",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            // Mostrar error si existe
            errorMsg?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}