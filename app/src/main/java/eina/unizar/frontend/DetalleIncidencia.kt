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
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleIncidenciaScreen(
    incidenciaId: Int,
    token: String,
    onBackClick: () -> Unit
) {
    val viewModel: IncidenciaViewModel = viewModel()
    val context = LocalContext.current

    val incidenciaDetalle = viewModel.incidenciaDetalle
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage
    val edicionExitosa = viewModel.edicionExitosa
    val mensajeEliminacion = viewModel.mensajeEliminacion

    var modoEdicion by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEstadoDialog by remember { mutableStateOf(false) }
    var imagenAmpliadaIndex by remember { mutableStateOf<Int?>(null) }

    var tituloEdit by remember { mutableStateOf("") }
    var descripcionEdit by remember { mutableStateOf("") }
    var tipoEdit by remember { mutableStateOf("AVERIA") }
    var prioridadEdit by remember { mutableStateOf("MEDIA") }
    var estadoEdit by remember { mutableStateOf("PENDIENTE") }
    var fotosEdit by remember { mutableStateOf<List<String>>(emptyList()) }
    var expandedTipo by remember { mutableStateOf(false) }
    var expandedPrioridad by remember { mutableStateOf(false) }
    var expandedEstado by remember { mutableStateOf(false) }

    val tiposIncidencia = listOf("AVERIA", "ACCIDENTE", "MANTENIMIENTO", "OTRO")
    val prioridades = listOf("ALTA", "MEDIA", "BAJA")
    val estados = listOf("PENDIENTE", "EN PROGRESO", "RESUELTA", "CANCELADA")

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val nuevasFotos = mutableListOf<String>()

            uris.forEach { uri ->
                try {
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        val resizedBitmap = resizeImage(bitmap, 800)
                        val base64String = bitmapToBase64(resizedBitmap)
                        nuevasFotos.add("data:image/jpeg;base64,$base64String")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            fotosEdit = fotosEdit + nuevasFotos
        }
    }

    LaunchedEffect(incidenciaId) {
        viewModel.obtenerIncidencia(token, incidenciaId)
    }

    LaunchedEffect(incidenciaDetalle) {
        incidenciaDetalle?.let {
            tituloEdit = it.titulo
            descripcionEdit = it.descripcion
            tipoEdit = it.tipo
            prioridadEdit = it.prioridad
            estadoEdit = it.estado
            fotosEdit = it.fotos ?: emptyList()
        }
    }

    LaunchedEffect(edicionExitosa) {
        if (edicionExitosa) {
            modoEdicion = false
            viewModel.obtenerIncidencia(token, incidenciaId)
            viewModel.resetStates()
        }
    }

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

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar Incidencia", color = MaterialTheme.colorScheme.onSurface) },
            text = { Text("¿Estás seguro de que quieres eliminar esta incidencia? Esta acción no se puede deshacer.", color = MaterialTheme.colorScheme.onSurface) },
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
                    Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    if (showEstadoDialog) {
        AlertDialog(
            onDismissRequest = { showEstadoDialog = false },
            title = { Text("Cambiar Estado", color = MaterialTheme.colorScheme.onSurface) },
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
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.primary
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(estado.replace("_", " "), color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showEstadoDialog = false }) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    imagenAmpliadaIndex?.let { index ->
        val fotos = if (modoEdicion) fotosEdit else (incidenciaDetalle?.fotos ?: emptyList())
        if (index < fotos.size) {
            Dialog(onDismissRequest = { imagenAmpliadaIndex = null }) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { imagenAmpliadaIndex = null },
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .fillMaxHeight(0.8f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            val base64String = fotos[index].removePrefix("data:image/jpeg;base64,")
                            val bitmap = base64ToBitmap(base64String)

                            if (bitmap != null) {
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = "Imagen ampliada",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            }

                            IconButton(
                                onClick = { imagenAmpliadaIndex = null },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Cerrar",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary
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
                            text = if (modoEdicion) "Editar Incidencia" else "Detalle Incidencia",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.weight(1f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        if (!modoEdicion && incidenciaDetalle != null) {
                            IconButton(onClick = { showDeleteDialog = true }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar",
                                    tint = MaterialTheme.colorScheme.onPrimary
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
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else if (incidenciaDetalle != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 30.dp)
                    ) {
                        Spacer(modifier = Modifier.height(20.dp))

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
                                    Text("Cambiar Estado", color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        if (modoEdicion) {
                            Text(
                                text = "Título",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 5.dp)
                            )
                            OutlinedTextField(
                                value = tituloEdit,
                                onValueChange = { tituloEdit = it },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface
                                )
                            )

                            Spacer(modifier = Modifier.height(15.dp))

                            Text(
                                text = "Descripción",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface
                                ),
                                maxLines = 5
                            )

                            Spacer(modifier = Modifier.height(30.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        modoEdicion = false
                                        incidenciaDetalle?.let {
                                            tituloEdit = it.titulo
                                            descripcionEdit = it.descripcion
                                            tipoEdit = it.tipo
                                            prioridadEdit = it.prioridad
                                            estadoEdit = it.estado
                                            fotosEdit = it.fotos ?: emptyList()
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(55.dp),
                                    shape = RoundedCornerShape(28.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.primary
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
                                            fotos = fotosEdit,
                                            compartirConGrupo = true
                                        )
                                        viewModel.actualizarIncidencia(token, incidenciaId, request)
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(55.dp),
                                    shape = RoundedCornerShape(28.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    ),
                                    enabled = tituloEdit.isNotBlank() &&
                                            descripcionEdit.isNotBlank() &&
                                            !isLoading
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Text("Guardar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                        } else {
                            Text(
                                text = "Información",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            Spacer(modifier = Modifier.height(15.dp))

                            InfoField(
                                label = "Título",
                                value = incidenciaDetalle.titulo
                            )

                            Spacer(modifier = Modifier.height(15.dp))

                            InfoField(
                                label = "Descripción",
                                value = incidenciaDetalle.descripcion,
                                minHeight = 100.dp
                            )

                            Spacer(modifier = Modifier.height(15.dp))

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

                            InfoField(
                                label = "Fecha de creación",
                                value = incidenciaDetalle.fechaCreacion.formatToDateOnly()
                            )

                            Spacer(modifier = Modifier.height(30.dp))

                            Button(
                                onClick = { modoEdicion = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(55.dp),
                                shape = RoundedCornerShape(28.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
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
                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    minHeight: Dp = 55.dp
) {
    Column {
        Text(
            text = label,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 5.dp)
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = minHeight),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                    fontWeight = if (valueColor != MaterialTheme.colorScheme.onSurface) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}

fun base64ToBitmap(base64String: String): Bitmap? {
    return try {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun String.formatToDateOnly(): String {
    return try {
        val instant = Instant.parse(this)
        val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()
        localDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
    } catch (e: Exception) {
        this
    }
}