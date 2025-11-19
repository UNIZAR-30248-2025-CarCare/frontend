package eina.unizar.frontend

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eina.unizar.frontend.models.Vehiculo
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel




import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import eina.unizar.frontend.models.CrearIncidenciaRequest
import eina.unizar.frontend.models.VehiculoDTO
import eina.unizar.frontend.viewmodels.HomeViewModel
import eina.unizar.frontend.viewmodels.IncidenciaViewModel
import java.io.ByteArrayOutputStream


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
    val context = LocalContext.current

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

    // Estado para las fotos
    var fotosBase64 by remember { mutableStateOf<List<String>>(emptyList()) }
    var fotosBitmap by remember { mutableStateOf<List<Bitmap>>(emptyList()) }

    val tiposIncidencia = listOf("AVERIA", "ACCIDENTE", "MANTENIMIENTO", "OTRO")
    val prioridades = listOf("ALTA", "MEDIA", "BAJA")

    // Launcher para seleccionar múltiples imágenes
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val nuevasFotos = mutableListOf<String>()
            val nuevosBitmaps = mutableListOf<Bitmap>()

            uris.forEach { uri ->
                try {
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        val bitmap = BitmapFactory.decodeStream(inputStream)

                        // Redimensionar la imagen para que no sea muy grande
                        val resizedBitmap = resizeImage(bitmap, 800)

                        // Convertir a Base64
                        val base64String = bitmapToBase64(resizedBitmap)

                        nuevasFotos.add("data:image/jpeg;base64,$base64String")
                        nuevosBitmaps.add(resizedBitmap)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            fotosBase64 = fotosBase64 + nuevasFotos
            fotosBitmap = fotosBitmap + nuevosBitmaps
        }
    }

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
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            // Header
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
                        text = "Reportar Incidencia",
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
                    text = "Detalles",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Selector de vehículo
                if (vehiculos.isEmpty()) {
                    Text(
                        text = "Cargando vehículos...",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 20.dp)
                    )
                } else {
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
                            vehiculos.forEach { vehiculo ->
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
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Build,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                )

                                ExposedDropdownMenu(
                                    expanded = expandedTipo,
                                    onDismissRequest = { expandedTipo = false },
                                    containerColor = MaterialTheme.colorScheme.surface
                                ) {
                                    tiposIncidencia.forEach { tipo ->
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
                        }

                        // Prioridad
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Prioridad",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                        focusedTextColor = when (prioridadSeleccionada) {
                                            "ALTA" -> Color(0xFFEF4444)
                                            "MEDIA" -> Color(0xFFF59E0B)
                                            else -> Color(0xFF10B981)
                                        },
                                        unfocusedTextColor = when (prioridadSeleccionada) {
                                            "ALTA" -> Color(0xFFEF4444)
                                            "MEDIA" -> Color(0xFFF59E0B)
                                            else -> Color(0xFF10B981)
                                        },
                                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
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
                                    onDismissRequest = { expandedPrioridad = false },
                                    containerColor = MaterialTheme.colorScheme.surface
                                ) {
                                    prioridades.forEach { prioridad ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    prioridad,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            },
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 5.dp)
                    )
                    OutlinedTextField(
                        value = titulo,
                        onValueChange = { titulo = it },
                        placeholder = {
                            Text(
                                "Ej: Ruido extraño en el motor",
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

                    // Descripción
                    Text(
                        text = "Descripción",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 5.dp)
                    )
                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        placeholder = {
                            Text(
                                "Describe qué ha ocurrido,\ncuándo lo detectaste...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        maxLines = 5
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    // Añadir fotos
                    Text(
                        text = "Fotos (opcional) - ${fotosBitmap.size} seleccionadas",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 5.dp)
                    )

                    if (fotosBitmap.isEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { imagePickerLauncher.launch("image/*") },
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddCircle,
                                    contentDescription = "Añadir fotos",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Toca para añadir fotos",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        Column {
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(fotosBitmap) { bitmap ->
                                    Box(
                                        modifier = Modifier.size(100.dp)
                                    ) {
                                        Card(
                                            modifier = Modifier.fillMaxSize(),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.surface
                                            )
                                        ) {
                                            Image(
                                                bitmap = bitmap.asImageBitmap(),
                                                contentDescription = "Foto seleccionada",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                val index = fotosBitmap.indexOf(bitmap)
                                                fotosBitmap = fotosBitmap.toMutableList().apply { removeAt(index) }
                                                fotosBase64 = fotosBase64.toMutableList().apply { removeAt(index) }
                                            },
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .size(24.dp)
                                                .background(Color.Black.copy(alpha = 0.6f), androidx.compose.foundation.shape.CircleShape)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Eliminar",
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }

                                item {
                                    Card(
                                        modifier = Modifier
                                            .size(100.dp)
                                            .clickable { imagePickerLauncher.launch("image/*") },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.background
                                        )
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = "Añadir más",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Compartir con grupo
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                    checkedTrackColor = Color(0xFF10B981)
                                )
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Compartir con todos los usuarios",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
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
                                    fotos = fotosBase64, // Enviamos las fotos en Base64
                                    compartirConGrupo = compartirConGrupo
                                )
                                viewModel.crearIncidencia(token, request)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("botonReportarIncidencia")
                            .height(55.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        enabled = titulo.isNotBlank() &&
                                descripcion.isNotBlank() &&
                                vehiculoSeleccionado != null &&
                                !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
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

// Función para redimensionar imagen
fun resizeImage(bitmap: Bitmap, maxSize: Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height

    if (width <= maxSize && height <= maxSize) {
        return bitmap
    }

    val aspectRatio = width.toFloat() / height.toFloat()
    val newWidth: Int
    val newHeight: Int

    if (width > height) {
        newWidth = maxSize
        newHeight = (maxSize / aspectRatio).toInt()
    } else {
        newHeight = maxSize
        newWidth = (maxSize * aspectRatio).toInt()
    }

    return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
}

// Función para convertir Bitmap a Base64
fun bitmapToBase64(bitmap: Bitmap): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.NO_WRAP)
}