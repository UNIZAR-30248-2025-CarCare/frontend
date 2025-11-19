package eina.unizar.frontend

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import eina.unizar.frontend.models.NuevaReservaData
import eina.unizar.frontend.viewmodels.ReservaViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarReservaScreen(
    navController: NavHostController,
    vehiculoId: String,
    reservaId: String,
    token: String
) {
    val reservaViewModel: ReservaViewModel = viewModel()
    val reservasDTO by reservaViewModel.reservas.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Obtener la reserva específica
    val reservaActual = reservasDTO.find { it.id.toString() == reservaId }

    // Estados inicializados con los datos de la reserva
    var fechaInicio by remember {
        mutableStateOf(
            if (reservaActual != null) {
                LocalDate.parse(reservaActual.fechaInicio.split("T")[0])
            } else {
                LocalDate.now()
            }
        )
    }
    var fechaFin by remember {
        mutableStateOf(
            if (reservaActual != null) {
                LocalDate.parse(reservaActual.fechaFin.split("T")[0])
            } else {
                LocalDate.now()
            }
        )
    }
    var horaInicio by remember {
        mutableStateOf(
            reservaActual?.horaInicio?.substring(0, 5) ?: "09:00"
        )
    }
    var horaFin by remember {
        mutableStateOf(
            reservaActual?.horaFin?.substring(0, 5) ?: "18:00"
        )
    }
    var tipoReserva by remember {
        mutableStateOf(
            when (reservaActual?.motivo?.uppercase()) {
                "TRABAJO" -> TipoReserva.TRABAJO
                "PERSONAL" -> TipoReserva.PERSONAL
                else -> TipoReserva.TRABAJO
            }
        )
    }
    var notas by remember {
        mutableStateOf(reservaActual?.descripcion ?: "")
    }

    var mostrarDatePickerInicio by remember { mutableStateOf(false) }
    var mostrarDatePickerFin by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Cargar reservas al inicio si no están cargadas
    LaunchedEffect(token) {
        if (reservasDTO.isEmpty()) {
            reservaViewModel.fetchReservas(token)
        }
    }

    // DatePicker para fecha inicio
    LaunchedEffect(mostrarDatePickerInicio) {
        if (mostrarDatePickerInicio) {
            val dialog = android.app.DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    fechaInicio = LocalDate.of(year, month + 1, dayOfMonth)
                    mostrarDatePickerInicio = false
                },
                fechaInicio.year,
                fechaInicio.monthValue - 1,
                fechaInicio.dayOfMonth
            )
            dialog.show()
        }
    }

    // DatePicker para fecha fin
    LaunchedEffect(mostrarDatePickerFin) {
        if (mostrarDatePickerFin) {
            val dialog = android.app.DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    fechaFin = LocalDate.of(year, month + 1, dayOfMonth)
                    mostrarDatePickerFin = false
                },
                fechaFin.year,
                fechaFin.monthValue - 1,
                fechaFin.dayOfMonth
            )
            dialog.show()
        }
    }

    Scaffold(
        topBar = {
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
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Text(
                        text = "Editar Reserva",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(48.dp))
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (reservaActual == null && !reservasDTO.isEmpty()) {
            // Error: reserva no encontrada
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Error",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Reserva no encontrada",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            "Volver",
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 30.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Error message
                errorMessage?.let { error ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Error",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = error,
                                fontSize = 14.sp,
                                color = Color(0xFFEF4444)
                            )
                        }
                    }
                }

                // Información del vehículo
                reservaActual?.let { reserva ->
                    val tipoVehiculo = when (reserva.Vehiculo.tipo.uppercase()) {
                        "COCHE" -> TipoVehiculo.COCHE
                        "MOTO" -> TipoVehiculo.MOTO
                        "FURGONETA" -> TipoVehiculo.FURGONETA
                        "CAMION" -> TipoVehiculo.CAMION
                        else -> TipoVehiculo.COCHE
                    }

                    val colorVehiculo = getColorForVehiculo(reserva.Vehiculo.id)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(colorVehiculo.copy(alpha = 0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = tipoVehiculo.iconRes),
                                    contentDescription = "Vehículo",
                                    tint = colorVehiculo,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = reserva.Vehiculo.nombre,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = reserva.Vehiculo.matricula,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "Fechas de la reserva",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Fecha de inicio
                Text(
                    text = "Fecha de inicio",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
                OutlinedTextField(
                    value = fechaInicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { mostrarDatePickerInicio = true },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    leadingIcon = {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "Fecha",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    enabled = false
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Fecha de fin
                Text(
                    text = "Fecha de fin",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
                OutlinedTextField(
                    value = fechaFin.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { mostrarDatePickerFin = true },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    leadingIcon = {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "Fecha",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    enabled = false
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Horario",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Hora inicio
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Hora inicio",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 5.dp)
                        )
                        OutlinedTextField(
                            value = horaInicio,
                            onValueChange = { horaInicio = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
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
                                    Icons.Default.Call,
                                    contentDescription = "Hora",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                    }

                    // Hora fin
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Hora fin",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 5.dp)
                        )
                        OutlinedTextField(
                            value = horaFin,
                            onValueChange = { horaFin = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
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
                                    Icons.Default.Call,
                                    contentDescription = "Hora",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Tipo de reserva",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TipoReservaCard(
                        tipo = TipoReserva.TRABAJO,
                        selected = tipoReserva == TipoReserva.TRABAJO,
                        onClick = { tipoReserva = TipoReserva.TRABAJO },
                        modifier = Modifier.weight(1f)
                    )
                    TipoReservaCard(
                        tipo = TipoReserva.PERSONAL,
                        selected = tipoReserva == TipoReserva.PERSONAL,
                        onClick = { tipoReserva = TipoReserva.PERSONAL },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Notas (opcional)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = notas,
                    onValueChange = { notas = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    placeholder = {
                        Text(
                            "Añade notas sobre la reserva...",
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
                    ),
                    shape = RoundedCornerShape(8.dp),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        scope.launch {
                            errorMessage = null

                            // Validaciones
                            if (fechaFin.isBefore(fechaInicio)) {
                                errorMessage = "La fecha de fin no puede ser anterior a la fecha de inicio"
                                return@launch
                            }

                            val inicio = LocalTime.parse(horaInicio)
                            val fin = LocalTime.parse(horaFin)
                            if (fin.isBefore(inicio) || fin == inicio) {
                                errorMessage = "La hora de fin debe ser posterior a la hora de inicio"
                                return@launch
                            }

                            isLoading = true

                            val reservaData = NuevaReservaData(
                                vehiculoId = vehiculoId,
                                fechaInicio = fechaInicio,
                                fechaFinal = fechaFin,
                                horaInicio = inicio,
                                horaFin = fin,
                                tipo = tipoReserva,
                                notas = notas
                            )

                            Log.d("EditarReserva", "Editando reserva - vehiculoId: $vehiculoId, reservaId: $reservaId")

                            // Primero eliminar la reserva antigua
                            val eliminado = reservaViewModel.eliminarReserva(token, reservaId)

                            if (eliminado) {
                                // Luego crear la nueva (que es la editada)
                                val exito = reservaViewModel.crearYRefrescarReserva(token, reservaData)

                                isLoading = false

                                if (exito) {
                                    Log.d("EditarReserva", "Reserva editada exitosamente")
                                    navController.popBackStack()
                                } else {
                                    errorMessage = "Error al actualizar la reserva"
                                    Log.e("EditarReserva", "Error al crear nueva reserva")
                                }
                            } else {
                                isLoading = false
                                errorMessage = "Error al eliminar la reserva anterior"
                                Log.e("EditarReserva", "Error al eliminar reserva antigua")
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
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "Guardar cambios",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}