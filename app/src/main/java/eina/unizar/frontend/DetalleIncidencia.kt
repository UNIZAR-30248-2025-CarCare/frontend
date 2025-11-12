package eina.unizar.frontend

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import eina.unizar.frontend.models.ActualizarEstadoRequest
import eina.unizar.frontend.models.CrearIncidenciaRequest
import eina.unizar.frontend.viewmodels.IncidenciaViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleIncidenciaScreen(
    incidenciaId: Int,
    token: String,
    onBackClick: () -> Unit
) {
    val viewModel: IncidenciaViewModel = viewModel()

    val incidenciaDetalle = viewModel.incidenciaDetalle
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage
    val edicionExitosa = viewModel.edicionExitosa
    val mensajeEliminacion = viewModel.mensajeEliminacion

    var modoEdicion by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEstadoDialog by remember { mutableStateOf(false) }

    // Estados para edición
    var tituloEdit by remember { mutableStateOf("") }
    var descripcionEdit by remember { mutableStateOf("") }
    var tipoEdit by remember { mutableStateOf("AVERIA") }
    var prioridadEdit by remember { mutableStateOf("MEDIA") }
    var estadoEdit by remember { mutableStateOf("PENDIENTE") }
    var expandedTipo by remember { mutableStateOf(false) }
    var expandedPrioridad by remember { mutableStateOf(false) }
    var expandedEstado by remember { mutableStateOf(false) }

    val tiposIncidencia = listOf("AVERIA", "ACCIDENTE", "MANTENIMIENTO", "OTRO")
    val prioridades = listOf("ALTA", "MEDIA", "BAJA")
    val estados = listOf("PENDIENTE", "EN PROGRESO", "RESUELTA", "CANCELADA")

    // Cargar incidencia al iniciar
    LaunchedEffect(incidenciaId) {
        viewModel.obtenerIncidencia(token, incidenciaId)
    }

    // Actualizar campos de edición cuando se carga la incidencia
    LaunchedEffect(incidenciaDetalle) {
        incidenciaDetalle?.let {
            tituloEdit = it.titulo
            descripcionEdit = it.descripcion
            tipoEdit = it.tipo
            prioridadEdit = it.prioridad
            estadoEdit = it.estado
        }
    }

    // Manejar éxito de edición
    LaunchedEffect(edicionExitosa) {
        if (edicionExitosa) {
            modoEdicion = false
            viewModel.obtenerIncidencia(token, incidenciaId)
            viewModel.resetStates()
        }
    }

    // Manejar eliminación exitosa
    LaunchedEffect(mensajeEliminacion) {
        if (mensajeEliminacion != null) {
            onBackClick()
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
        }
    }

    // Diálogo de confirmación de eliminación
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar Incidencia") },
            text = { Text("¿Estás seguro de que quieres eliminar esta incidencia? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.eliminarIncidencia(token, incidenciaId)
                        showDeleteDialog = false
                    }
                ) {
                    Text("Eliminar", color = Color(0xFFEF4444))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo para cambiar estado rápido
    if (showEstadoDialog) {
        AlertDialog(
            onDismissRequest = { showEstadoDialog = false },
            title = { Text("Cambiar Estado") },
            text = {
                Column {
                    estados.forEach { estado ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.actualizarEstadoIncidencia(token, incidenciaId, estado)
                                    showEstadoDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = estadoEdit == estado,
                                onClick = {
                                    viewModel.actualizarEstadoIncidencia(token, incidenciaId, estado)
                                    showEstadoDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(estado.replace("_", " "))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showEstadoDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
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
                            text = if (modoEdicion) "Editar Incidencia" else "Detalle Incidencia",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.weight(1f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        if (!modoEdicion && incidenciaDetalle != null) {
                            IconButton(onClick = { showDeleteDialog = true }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar",
                                    tint = Color.White
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.width(48.dp))
                        }
                    }
                }

                if (isLoading && incidenciaDetalle == null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFFEF4444))
                    }
                } else if (incidenciaDetalle != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 30.dp)
                    ) {
                        Spacer(modifier = Modifier.height(20.dp))

                        // Badge de estado
                        val estadoColor = when (incidenciaDetalle.estado.uppercase()) {
                            "RESUELTA" -> Color(0xFF10B981)
                            "CANCELADA" -> Color(0xFF6B7280)
                            "EN PROGRESO" -> Color(0xFFF59E0B)
                            else -> Color(0xFFEF4444)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = estadoColor.copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = incidenciaDetalle.estado.replace("_", " "),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = estadoColor,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                                )
                            }

                            if (!modoEdicion) {
                                TextButton(onClick = { showEstadoDialog = true }) {
                                    Text("Cambiar Estado", color = Color(0xFFEF4444))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        if (modoEdicion) {
                            // MODO EDICIÓN

                            // Título
                            Text(
                                text = "Título",
                                fontSize = 13.sp,
                                color = Color(0xFF6B7280),
                                modifier = Modifier.padding(bottom = 5.dp)
                            )
                            OutlinedTextField(
                                value = tituloEdit,
                                onValueChange = { tituloEdit = it },
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
                                value = descripcionEdit,
                                onValueChange = { descripcionEdit = it },
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

                            // Tipo y Prioridad
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Tipo
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Tipo",
                                        fontSize = 13.sp,
                                        color = Color(0xFF6B7280),
                                        modifier = Modifier.padding(bottom = 5.dp)
                                    )

                                    ExposedDropdownMenuBox(
                                        expanded = expandedTipo,
                                        onExpandedChange = { expandedTipo = it }
                                    ) {
                                        OutlinedTextField(
                                            value = tipoEdit,
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
                                            )
                                        )

                                        ExposedDropdownMenu(
                                            expanded = expandedTipo,
                                            onDismissRequest = { expandedTipo = false }
                                        ) {
                                            tiposIncidencia.forEach { tipo ->
                                                DropdownMenuItem(
                                                    text = { Text(tipo) },
                                                    onClick = {
                                                        tipoEdit = tipo
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
                                            value = prioridadEdit,
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
                                            )
                                        )

                                        ExposedDropdownMenu(
                                            expanded = expandedPrioridad,
                                            onDismissRequest = { expandedPrioridad = false }
                                        ) {
                                            prioridades.forEach { prioridad ->
                                                DropdownMenuItem(
                                                    text = { Text(prioridad) },
                                                    onClick = {
                                                        prioridadEdit = prioridad
                                                        expandedPrioridad = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(15.dp))

                            // Estado
                            Text(
                                text = "Estado",
                                fontSize = 13.sp,
                                color = Color(0xFF6B7280),
                                modifier = Modifier.padding(bottom = 5.dp)
                            )

                            ExposedDropdownMenuBox(
                                expanded = expandedEstado,
                                onExpandedChange = { expandedEstado = it }
                            ) {
                                OutlinedTextField(
                                    value = estadoEdit.replace("_", " "),
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEstado)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFFEF4444),
                                        unfocusedBorderColor = Color(0xFFE5E7EB)
                                    )
                                )

                                ExposedDropdownMenu(
                                    expanded = expandedEstado,
                                    onDismissRequest = { expandedEstado = false }
                                ) {
                                    estados.forEach { estado ->
                                        DropdownMenuItem(
                                            text = { Text(estado.replace("_", " ")) },
                                            onClick = {
                                                estadoEdit = estado
                                                expandedEstado = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Fotos (placeholder)
                            Text(
                                text = "Fotos (pendiente de implementar)",
                                fontSize = 13.sp,
                                color = Color(0xFF6B7280),
                                modifier = Modifier.padding(bottom = 5.dp)
                            )
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .border(
                                        width = 2.dp,
                                        color = Color(0xFFE5E7EB),
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB))
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Gestión de fotos próximamente",
                                        fontSize = 12.sp,
                                        color = Color(0xFF9CA3AF)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(30.dp))

                            // Botones de acción
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        modoEdicion = false
                                        // Restaurar valores originales
                                        incidenciaDetalle?.let {
                                            tituloEdit = it.titulo
                                            descripcionEdit = it.descripcion
                                            tipoEdit = it.tipo
                                            prioridadEdit = it.prioridad
                                            estadoEdit = it.estado
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(55.dp),
                                    shape = RoundedCornerShape(28.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color(0xFFEF4444)
                                    ),
                                    border = ButtonDefaults.outlinedButtonBorder.copy(
                                        width = 2.dp,
                                        brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFEF4444))
                                    )
                                ) {
                                    Text("Cancelar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        val request = CrearIncidenciaRequest(
                                            vehiculoId = incidenciaDetalle.vehiculoId.toString(),
                                            tipo = tipoEdit,
                                            prioridad = prioridadEdit,
                                            titulo = tituloEdit,
                                            descripcion = descripcionEdit,
                                            fotos = emptyList(),
                                            compartirConGrupo = true
                                        )
                                        viewModel.actualizarIncidencia(token, incidenciaId, request)
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(55.dp),
                                    shape = RoundedCornerShape(28.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFEF4444)
                                    ),
                                    enabled = tituloEdit.isNotBlank() &&
                                            descripcionEdit.isNotBlank() &&
                                            !isLoading
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = Color.White,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Text("Guardar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                        } else {
                            // MODO VISUALIZACIÓN

                            Text(
                                text = "Información",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F2937)
                            )

                            Spacer(modifier = Modifier.height(15.dp))

                            // Título
                            InfoField(
                                label = "Título",
                                value = incidenciaDetalle.titulo
                            )

                            Spacer(modifier = Modifier.height(15.dp))

                            // Descripción
                            InfoField(
                                label = "Descripción",
                                value = incidenciaDetalle.descripcion,
                                minHeight = 100.dp
                            )

                            Spacer(modifier = Modifier.height(15.dp))

                            // Tipo y Prioridad
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    InfoField(
                                        label = "Tipo",
                                        value = incidenciaDetalle.tipo
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    InfoField(
                                        label = "Prioridad",
                                        value = incidenciaDetalle.prioridad,
                                        valueColor = when (incidenciaDetalle.prioridad.uppercase()) {
                                            "ALTA" -> Color(0xFFEF4444)
                                            "MEDIA" -> Color(0xFFF59E0B)
                                            else -> Color(0xFF10B981)
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(15.dp))

                            // Fecha
                            InfoField(
                                label = "Fecha de creación",
                                value = incidenciaDetalle.fechaCreacion.formatToDateOnly()
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Fotos (placeholder)
                            Text(
                                text = "Fotos",
                                fontSize = 13.sp,
                                color = Color(0xFF6B7280),
                                modifier = Modifier.padding(bottom = 5.dp)
                            )
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB))
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No hay fotos disponibles",
                                        fontSize = 12.sp,
                                        color = Color(0xFF9CA3AF)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(30.dp))

                            // Botón editar
                            Button(
                                onClick = { modoEdicion = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(55.dp),
                                shape = RoundedCornerShape(28.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFEF4444)
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Editar",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Editar Incidencia",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(30.dp))
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se pudo cargar la incidencia",
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InfoField(
    label: String,
    value: String,
    valueColor: Color = Color(0xFF1F2937),
    minHeight: Dp = 55.dp
) {
    Column {
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color(0xFF6B7280),
            modifier = Modifier.padding(bottom = 5.dp)
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = minHeight),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = value,
                    fontSize = 15.sp,
                    color = valueColor,
                    fontWeight = if (valueColor != Color(0xFF1F2937)) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}




/**
 * Convierte una cadena de fecha/hora en formato ISO 8601 (ej: 2025-11-12T12:11:57.000Z)
 * a un formato de solo fecha (ej: 2025-11-12).
 */
fun String.formatToDateOnly(): String {
    return try {
        // 1. Parsear la cadena ISO (ej: 2025-11-12T12:11:57.000Z)
        val instant = Instant.parse(this)

        // 2. Convertir a una fecha local usando la zona horaria del sistema y formatearla
        val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()

        // 3. Devolver la fecha en formato ISO (AAAA-MM-DD)
        localDate.format(DateTimeFormatter.ISO_LOCAL_DATE)

    } catch (e: Exception) {
        // En caso de error (cadena no válida), devuelve la cadena original
        this
    }
}