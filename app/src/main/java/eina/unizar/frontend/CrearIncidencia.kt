package eina.unizar.frontend

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eina.unizar.frontend.models.Vehiculo
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import eina.unizar.frontend.models.CrearIncidenciaRequest
import eina.unizar.frontend.models.VehiculoDTO
import eina.unizar.frontend.viewmodels.HomeViewModel
import eina.unizar.frontend.viewmodels.IncidenciaViewModel


data class NuevaIncidenciaData(
    val vehiculoId: String,
    val tipo: TipoIncidencia,
    val prioridad: PrioridadIncidencia,
    val titulo: String,
    val descripcion: String,
    val compartirConGrupo: Boolean
)


/**
 * Pantalla para crear y reportar nuevas incidencias relacionadas con un vehículo.
 *
 * Permite al usuario seleccionar:
 * - Vehículo afectado
 * - Tipo de incidencia (avería, daño, otro)
 * - Nivel de prioridad
 * - Título y descripción
 * - Opción para compartir con el grupo
 *
 * Muestra menús desplegables (`DropdownMenu`) para elegir vehículo, tipo y prioridad.
 * Los datos se agrupan en el objeto `NuevaIncidenciaData`, enviado al callback
 * `onReportarIncidencia()` al confirmar.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaIncidenciaScreen(
    userId: String,
    token: String,
    onBackClick: () -> Unit,
    onIncidenciaCreada: () -> Unit
) {
    val viewModel: IncidenciaViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel()

    val vehiculos by homeViewModel.vehiculos.collectAsState()

    var vehiculoSeleccionado by remember { mutableStateOf<VehiculoDTO?>(null) }
    var tipoSeleccionado by remember { mutableStateOf("AVERIA") }
    var prioridadSeleccionada by remember { mutableStateOf("MEDIA") }
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var compartirConGrupo by remember { mutableStateOf(true) }
    var expandedVehiculo by remember { mutableStateOf(false) }
    var expandedTipo by remember { mutableStateOf(false) }
    var expandedPrioridad by remember { mutableStateOf(false) }

    val tiposIncidencia = listOf("AVERIA", "ACCIDENTE", "MANTENIMIENTO", "OTRO")
    val prioridades = listOf("ALTA", "MEDIA", "BAJA")

    // Cargar vehículos al iniciar
    LaunchedEffect(Unit) {
        if (vehiculos.isEmpty()) {
            homeViewModel.fetchVehiculos(userId, token)
        }
    }

    // Establecer primer vehículo como seleccionado
    LaunchedEffect(vehiculos) {
        if (vehiculoSeleccionado == null && vehiculos.isNotEmpty()) {
            vehiculoSeleccionado = vehiculos.first()
        }
    }

    // Observar estados del ViewModel
    val errorMessage = viewModel.errorMessage
    val creacionExitosa = viewModel.creacionExitosa
    val isLoading = viewModel.isLoading

    // Manejar éxito de creación
    LaunchedEffect(creacionExitosa) {
        if (creacionExitosa) {
            onIncidenciaCreada()
        }
    }

    // Snackbar para errores
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues)
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
                        text = "Reportar Incidencia",
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
                    text = "Detalles",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Selector de vehículo
                if (vehiculos.isEmpty()) {
                    Text(
                        text = "Cargando vehículos...",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280),
                        modifier = Modifier.padding(vertical = 20.dp)
                    )
                } else {
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
                                    val tipoVehiculo = TipoVehiculo.valueOf(vehiculo.tipo.uppercase())

                                    Box(
                                        modifier = Modifier
                                            .size(30.dp)
                                            .background(
                                                tipoVehiculo.color.copy(alpha = 0.1f),
                                                androidx.compose.foundation.shape.CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(id = tipoVehiculo.iconRes),
                                            contentDescription = tipoVehiculo.name,
                                            tint = tipoVehiculo.color,
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

                    // Tipo y Prioridad
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Tipo de incidencia
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Tipo de incidencia",
                                fontSize = 13.sp,
                                color = Color(0xFF6B7280),
                                modifier = Modifier.padding(bottom = 5.dp)
                            )

                            ExposedDropdownMenuBox(
                                expanded = expandedTipo,
                                onExpandedChange = { expandedTipo = it }
                            ) {
                                OutlinedTextField(
                                    value = tipoSeleccionado,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTipo)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFFEF4444),
                                        unfocusedBorderColor = Color(0xFFE5E7EB)
                                    ),
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Build,
                                            contentDescription = null,
                                            tint = Color(0xFFEF4444)
                                        )
                                    }
                                )

                                ExposedDropdownMenu(
                                    expanded = expandedTipo,
                                    onDismissRequest = { expandedTipo = false }
                                ) {
                                    tiposIncidencia.forEach { tipo ->
                                        DropdownMenuItem(
                                            text = { Text(tipo) },
                                            onClick = {
                                                tipoSeleccionado = tipo
                                                expandedTipo = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Prioridad
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Prioridad",
                                fontSize = 13.sp,
                                color = Color(0xFF6B7280),
                                modifier = Modifier.padding(bottom = 5.dp)
                            )

                            ExposedDropdownMenuBox(
                                expanded = expandedPrioridad,
                                onExpandedChange = { expandedPrioridad = it }
                            ) {
                                OutlinedTextField(
                                    value = prioridadSeleccionada,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPrioridad)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFFEF4444),
                                        unfocusedBorderColor = Color(0xFFE5E7EB)
                                    ),
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = when (prioridadSeleccionada) {
                                                "ALTA" -> Color(0xFFEF4444)
                                                "MEDIA" -> Color(0xFFF59E0B)
                                                else -> Color(0xFF10B981)
                                            }
                                        )
                                    }
                                )

                                ExposedDropdownMenu(
                                    expanded = expandedPrioridad,
                                    onDismissRequest = { expandedPrioridad = false }
                                ) {
                                    prioridades.forEach { prioridad ->
                                        DropdownMenuItem(
                                            text = { Text(prioridad) },
                                            onClick = {
                                                prioridadSeleccionada = prioridad
                                                expandedPrioridad = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    // Título
                    Text(
                        text = "Título",
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280),
                        modifier = Modifier.padding(bottom = 5.dp)
                    )
                    OutlinedTextField(
                        value = titulo,
                        onValueChange = { titulo = it },
                        placeholder = { Text("Ej: Ruido extraño en el motor") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFEF4444),
                            unfocusedBorderColor = Color(0xFFE5E7EB)
                        )
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    // Descripción
                    Text(
                        text = "Descripción",
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280),
                        modifier = Modifier.padding(bottom = 5.dp)
                    )
                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        placeholder = {
                            Text("Describe qué ha ocurrido,\ncuándo lo detectaste...")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFEF4444),
                            unfocusedBorderColor = Color(0xFFE5E7EB)
                        ),
                        maxLines = 5
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    // Añadir fotos (placeholder)
                    Text(
                        text = "Fotos (opcional)",
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280),
                        modifier = Modifier.padding(bottom = 5.dp)
                    )
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .border(
                                width = 2.dp,
                                color = Color(0xFFE5E7EB),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { /* TODO: Abrir selector de fotos */ },
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Create,
                                contentDescription = "Añadir fotos",
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Toca para añadir fotos",
                                fontSize = 13.sp,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Compartir con grupo
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Switch(
                                checked = compartirConGrupo,
                                onCheckedChange = { compartirConGrupo = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF10B981)
                                )
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Compartir con todos los usuarios",
                                fontSize = 14.sp,
                                color = Color(0xFF1F2937)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    // Botón Reportar
                    Button(
                        onClick = {
                            vehiculoSeleccionado?.let { vehiculo ->
                                val request = CrearIncidenciaRequest(
                                    vehiculoId = vehiculo.id.toString(),
                                    tipo = tipoSeleccionado,
                                    prioridad = prioridadSeleccionada,
                                    titulo = titulo,
                                    descripcion = descripcion,
                                    fotos = emptyList(),
                                    compartirConGrupo = compartirConGrupo
                                )
                                viewModel.crearIncidencia(token, request)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF4444)
                        ),
                        enabled = titulo.isNotBlank() &&
                                descripcion.isNotBlank() &&
                                vehiculoSeleccionado != null &&
                                !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Reportar Incidencia",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}