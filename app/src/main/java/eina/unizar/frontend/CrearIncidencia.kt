package eina.unizar.frontend

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
<<<<<<< HEAD
import androidx.compose.material.icons.automirrored.filled.ArrowBack
=======
>>>>>>> e45c30ab23000cb41f90f0969047b60c3825eec7
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class NuevaIncidenciaData(
    val vehiculoId: String,
    val tipo: TipoIncidencia,
    val prioridad: PrioridadIncidencia,
    val titulo: String,
    val descripcion: String,
    val compartirConGrupo: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaIncidenciaScreen(
    vehiculos: List<Vehiculo>,
    onBackClick: () -> Unit,
    onReportarIncidencia: (NuevaIncidenciaData) -> Unit
) {
    var vehiculoSeleccionado by remember { mutableStateOf<Vehiculo?>(vehiculos.firstOrNull()) }
    var tipoSeleccionado by remember { mutableStateOf(TipoIncidencia.AVERIA) }
    var prioridadSeleccionada by remember { mutableStateOf(PrioridadIncidencia.MEDIA) }
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var compartirConGrupo by remember { mutableStateOf(true) }
    var expandedVehiculo by remember { mutableStateOf(false) }
    var expandedTipo by remember { mutableStateOf(false) }
    var expandedPrioridad by remember { mutableStateOf(false) }

    val tiposIncidencia = TipoIncidencia.values().toList()
    val prioridades = PrioridadIncidencia.values().toList()

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
<<<<<<< HEAD
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
=======
                        imageVector = Icons.Default.ArrowBack,
>>>>>>> e45c30ab23000cb41f90f0969047b60c3825eec7
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
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .background(
                                        vehiculo.tipo.color.copy(alpha = 0.1f),
                                        androidx.compose.foundation.shape.CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = vehiculo.tipo.icon,
                                    contentDescription = vehiculo.tipo.name,
                                    tint = vehiculo.tipo.color,
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
                            value = tipoSeleccionado.nombre,
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
                                    text = { Text(tipo.nombre) },
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
                            value = when (prioridadSeleccionada) {
                                PrioridadIncidencia.ALTA -> "Alta"
                                PrioridadIncidencia.MEDIA -> "Media"
                                PrioridadIncidencia.BAJA -> "Baja"
                            },
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
                                    tint = prioridadSeleccionada.color
                                )
                            }
                        )

                        ExposedDropdownMenu(
                            expanded = expandedPrioridad,
                            onDismissRequest = { expandedPrioridad = false }
                        ) {
                            prioridades.forEach { prioridad ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            when (prioridad) {
                                                PrioridadIncidencia.ALTA -> "Alta"
                                                PrioridadIncidencia.MEDIA -> "Media"
                                                PrioridadIncidencia.BAJA -> "Baja"
                                            }
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
                    .clickable { /* Abrir selector de fotos */ },
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
                        onReportarIncidencia(
                            NuevaIncidenciaData(
                                vehiculoId = vehiculo.id,
                                tipo = tipoSeleccionado,
                                prioridad = prioridadSeleccionada,
                                titulo = titulo,
                                descripcion = descripcion,
                                compartirConGrupo = compartirConGrupo
                            )
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEF4444)
                ),
                enabled = titulo.isNotBlank() && descripcion.isNotBlank()
            ) {
                Text(
                    text = "Reportar Incidencia",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}