package eina.unizar.frontend

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import eina.unizar.frontend.viewmodels.ReservaViewModel
import eina.unizar.frontend.models.ReservaDTO
import eina.unizar.frontend.models.Vehiculo
import eina.unizar.frontend.BottomNavigationBar
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue
import kotlinx.coroutines.launch

fun getColorForVehiculo(vehiculoId: String): Color {
    val colors = listOf(
        Color(0xFF3B82F6), // Azul
        Color(0xFFEF4444), // Rojo
        Color(0xFF10B981), // Verde
        Color(0xFF8B5CF6), // Púrpura
        Color(0xFFF59E0B), // Naranja
        Color(0xFFEC4899), // Rosa
        Color(0xFF06B6D4), // Cian
        Color(0xFFF97316), // Naranja oscuro
        Color(0xFF14B8A6), // Teal
        Color(0xFFA855F7)  // Violeta
    )

    val index = vehiculoId.hashCode().absoluteValue % colors.size
    return colors[index]
}

data class Reserva(
    val id: String,
    val usuario: String,
    val vehiculo: String,
    val vehiculoId: String,
    val vehiculoTipo: TipoVehiculo,
    val fecha: LocalDate,
    val fechaFin: LocalDate,
    val horaInicio: String,
    val horaFin: String,
    val tipo: TipoReserva,
    val estado: EstadoReserva
)

enum class TipoReserva(val color: Color, val nombre: String) {
    TRABAJO(Color(0xFF3B82F6), "Trabajo"),
    PERSONAL(Color(0xFF8B5CF6), "Personal"),
    URGENTE(Color(0xFFEF4444), "Urgente"),
    OTRO(Color(0xFF10B981), "Otro")
}

enum class EstadoReserva {
    CONFIRMADA,
    EN_CURSO,
    COMPLETADA
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarioScreenWrapper(
    userId: String,
    token: String,
    vehiculoSeleccionado: eina.unizar.frontend.models.Vehiculo?,
    onBackClick: () -> Unit,
    onVehiculoClick: (eina.unizar.frontend.models.Vehiculo) -> Unit,
    onAddReservaClick: () -> Unit,
    navController: NavHostController
) {
    val reservaViewModel: ReservaViewModel = viewModel()
    val reservasDTO by reservaViewModel.reservas.collectAsState()
    val isLoading by reservaViewModel.isLoading.collectAsState()
    val error by reservaViewModel.error.collectAsState()

    LaunchedEffect(token) {
        reservaViewModel.fetchReservas(token)
    }

    LaunchedEffect(navController.currentBackStackEntry) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        if (currentRoute == "reservas") {
            kotlinx.coroutines.delay(300)
            reservaViewModel.fetchReservas(token)
        }
    }

    val reservasCalendario = reservasDTO.map { dto ->
        val fechaInicio = dto.fechaInicio.split("T")[0]
        val fechaFinal = dto.fechaFin.split("T")[0]

        val tipoVehiculo = when (dto.Vehiculo.tipo.uppercase()) {
            "COCHE" -> TipoVehiculo.COCHE
            "MOTO" -> TipoVehiculo.MOTO
            "FURGONETA" -> TipoVehiculo.FURGONETA
            "CAMION" -> TipoVehiculo.CAMION
            else -> TipoVehiculo.COCHE
        }

        Reserva(
            id = dto.id.toString(),
            usuario = dto.Usuario.nombre,
            vehiculo = "${dto.Vehiculo.nombre} - ${dto.Vehiculo.matricula}",
            vehiculoId = dto.Vehiculo.id,
            vehiculoTipo = tipoVehiculo,
            fecha = LocalDate.parse(fechaInicio),
            fechaFin = LocalDate.parse(fechaFinal),
            horaInicio = dto.horaInicio.substring(0, 5),
            horaFin = dto.horaFin.substring(0, 5),
            tipo = when (dto.motivo.uppercase()) {
                "TRABAJO" -> TipoReserva.TRABAJO
                "PERSONAL" -> TipoReserva.PERSONAL
                "URGENTE" -> TipoReserva.URGENTE
                else -> TipoReserva.OTRO
            },
            estado = EstadoReserva.CONFIRMADA
        )
    }

    var diaSeleccionado by remember { mutableStateOf(LocalDate.now()) }

    CalendarioScreen(
        vehiculoSeleccionado = null,
        reservasCalendario = reservasCalendario,
        reservasDTO = reservasDTO,
        isLoading = isLoading,
        error = error,
        mesActual = YearMonth.now(),
        diaSeleccionado = diaSeleccionado,
        onBackClick = onBackClick,
        onVehiculoClick = onVehiculoClick,
        onMesAnterior = { },
        onMesSiguiente = { },
        onDiaClick = { diaSeleccionado = it },
        onAddReservaClick = onAddReservaClick,
        navController = navController,
        viewModel = reservaViewModel,
        token = token
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarioScreen(
    vehiculoSeleccionado: eina.unizar.frontend.models.Vehiculo?,
    reservasCalendario: List<Reserva>,
    reservasDTO: List<ReservaDTO>,
    isLoading: Boolean,
    error: String?,
    mesActual: YearMonth,
    diaSeleccionado: LocalDate,
    onBackClick: () -> Unit,
    onVehiculoClick: (eina.unizar.frontend.models.Vehiculo) -> Unit,
    onMesAnterior: () -> Unit,
    onMesSiguiente: () -> Unit,
    onDiaClick: (LocalDate) -> Unit,
    onAddReservaClick: () -> Unit,
    navController: NavHostController,
    viewModel: ReservaViewModel,
    token: String
) {
    val reservasDelDia = reservasCalendario.filter { reserva ->
        val dias = mutableListOf<LocalDate>()
        var fecha = reserva.fecha
        while (!fecha.isAfter(reserva.fechaFin)) {
            dias.add(fecha)
            fecha = fecha.plusDays(1)
        }
        dias.contains(diaSeleccionado)
    }
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    if (route != currentRoute) {
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
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
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Text(
                        text = "Calendario",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(48.dp))
                }
            }

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Error",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = error,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 20.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(15.dp))

                            CalendarioConSelector(
                                reservas = reservasCalendario,
                                diaSeleccionado = diaSeleccionado,
                                onDiaClick = onDiaClick
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "Reservas de hoy",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        if (reservasDelDia.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No hay reservas para este día",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            items(reservasDelDia) { reserva ->
                                ReservaCard(reserva = reserva)
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "Todas mis reservas (${reservasDTO.size})",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        if (reservasDTO.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No tienes reservas",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            items(reservasDTO) { reserva ->
                                ReservaItemCard(
                                    reserva = reserva,
                                    navController = navController,
                                    viewModel = viewModel,
                                    token = token
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(100.dp))
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 20.dp, bottom = 10.dp),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        FloatingActionButton(
                            onClick = onAddReservaClick,
                            containerColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Añadir reserva",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarioMensual(
    mes: YearMonth,
    diaSeleccionado: LocalDate,
    reservas: List<Reserva>,
    onDiaClick: (LocalDate) -> Unit
) {
    val primerDiaDelMes = mes.atDay(1)
    val diaDeLaSemanaInicio = primerDiaDelMes.dayOfWeek.value - 1

    val diasEnMes = mes.lengthOfMonth()
    val totalCeldas = ((diasEnMes + diaDeLaSemanaInicio + 6) / 7) * 7

    fun obtenerRangoReserva(reserva: Reserva): List<LocalDate> {
        val dias = mutableListOf<LocalDate>()
        var fecha = reserva.fecha
        while (!fecha.isAfter(reserva.fechaFin)) {
            dias.add(fecha)
            fecha = fecha.plusDays(1)
        }
        return dias
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(10.dp)),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("L", "M", "X", "J", "V", "S", "D").forEach { dia ->
                    Text(
                        text = dia,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column {
                for (semana in 0 until totalCeldas / 7) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (dia in 0..6) {
                            val indice = semana * 7 + dia
                            val numeroDia = indice - diaDeLaSemanaInicio + 1
                            if (numeroDia in 1..diasEnMes) {
                                val fecha = mes.atDay(numeroDia)
                                val reservasDelDia = reservas.filter { reserva ->
                                    obtenerRangoReserva(reserva).contains(fecha)
                                }
                                val tieneReservas = reservasDelDia.isNotEmpty()
                                val esSeleccionado = fecha == diaSeleccionado
                                val esHoy = fecha == LocalDate.now()
                                val colorVehiculo = if (reservasDelDia.isNotEmpty()) {
                                    getColorForVehiculo(reservasDelDia.first().vehiculoId)
                                } else {
                                    Color(0xFF3B82F6)
                                }

                                val reservasMultiDia = reservas.filter { reserva ->
                                    val rango = obtenerRangoReserva(reserva)
                                    rango.size > 1 && rango.contains(fecha)
                                }
                                val esMultiDia = reservasMultiDia.isNotEmpty()

                                val multipleVehiculos = reservasDelDia.map { it.vehiculoId }.distinct().size > 1

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    when {
                                        esSeleccionado -> {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                                                    .clickable { onDiaClick(fecha) },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    if (tieneReservas) {
                                                        if (multipleVehiculos) {
                                                            Row(
                                                                horizontalArrangement = Arrangement.Center,
                                                                modifier = Modifier.width(12.dp)
                                                            ) {
                                                                reservasDelDia.map { it.vehiculoId }.distinct().take(3).forEach { vId ->
                                                                    Box(
                                                                        modifier = Modifier
                                                                            .size(3.dp)
                                                                            .background(getColorForVehiculo(vId), CircleShape)
                                                                    )
                                                                    if (reservasDelDia.map { it.vehiculoId }.distinct().indexOf(vId) <
                                                                        minOf(2, reservasDelDia.map { it.vehiculoId }.distinct().size - 1)) {
                                                                        Spacer(modifier = Modifier.width(1.dp))
                                                                    }
                                                                }
                                                            }
                                                        } else {
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(4.dp)
                                                                    .background(MaterialTheme.colorScheme.onPrimary, CircleShape)
                                                            )
                                                        }
                                                        Spacer(modifier = Modifier.height(2.dp))
                                                    }
                                                    Text(
                                                        text = numeroDia.toString(),
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onPrimary
                                                    )
                                                    if (esMultiDia) {
                                                        Spacer(modifier = Modifier.height(2.dp))
                                                        Box(
                                                            modifier = Modifier
                                                                .width(20.dp)
                                                                .height(2.dp)
                                                                .background(MaterialTheme.colorScheme.onPrimary)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        tieneReservas -> {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .background(
                                                        colorVehiculo.copy(alpha = 0.15f),
                                                        CircleShape
                                                    )
                                                    .clickable { onDiaClick(fecha) },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    if (multipleVehiculos) {
                                                        Row(
                                                            horizontalArrangement = Arrangement.Center,
                                                            modifier = Modifier.width(12.dp)
                                                        ) {
                                                            reservasDelDia.map { it.vehiculoId }.distinct().take(3).forEach { vId ->
                                                                Box(
                                                                    modifier = Modifier
                                                                        .size(3.dp)
                                                                        .background(getColorForVehiculo(vId), CircleShape)
                                                                )
                                                                if (reservasDelDia.map { it.vehiculoId }.distinct().indexOf(vId) <
                                                                    minOf(2, reservasDelDia.map { it.vehiculoId }.distinct().size - 1)) {
                                                                    Spacer(modifier = Modifier.width(1.dp))
                                                                }
                                                            }
                                                        }
                                                    } else {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(4.dp)
                                                                .background(colorVehiculo, CircleShape)
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        text = numeroDia.toString(),
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = colorVehiculo
                                                    )
                                                    if (esMultiDia) {
                                                        Spacer(modifier = Modifier.height(2.dp))
                                                        Box(
                                                            modifier = Modifier
                                                                .width(20.dp)
                                                                .height(2.dp)
                                                                .background(colorVehiculo)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        else -> {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clickable { onDiaClick(fecha) },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = numeroDia.toString(),
                                                    fontSize = 14.sp,
                                                    fontWeight = if (esHoy) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (esHoy) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarioConSelector(
    reservas: List<Reserva>,
    diaSeleccionado: LocalDate,
    onDiaClick: (LocalDate) -> Unit
) {
    var mesActual by remember { mutableStateOf(YearMonth.now()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { mesActual = mesActual.minusMonths(1) }) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Mes anterior",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "${mesActual.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${mesActual.year}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            IconButton(onClick = { mesActual = mesActual.plusMonths(1) }) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Mes siguiente",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        CalendarioMensual(
            mes = mesActual,
            diaSeleccionado = diaSeleccionado,
            reservas = reservas,
            onDiaClick = onDiaClick
        )
    }
}

@Composable
fun ReservaCard(reserva: Reserva) {
    val colorVehiculo = getColorForVehiculo(reserva.vehiculoId)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(colorVehiculo)
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(colorVehiculo.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = reserva.vehiculoTipo.iconRes),
                        contentDescription = reserva.vehiculoTipo.name,
                        tint = colorVehiculo,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = reserva.vehiculo,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${reserva.horaInicio} - ${reserva.horaFin}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = reserva.tipo.nombre,
                        fontSize = 12.sp,
                        color = reserva.tipo.color
                    )
                }

                when (reserva.estado) {
                    EstadoReserva.CONFIRMADA -> {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF10B981).copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Confirmada",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    EstadoReserva.EN_CURSO -> {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFF59E0B).copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "En curso",
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    EstadoReserva.COMPLETADA -> {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF9CA3AF).copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Completada",
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReservaItemCard(
    reserva: ReservaDTO,
    navController: NavHostController,
    viewModel: ReservaViewModel,
    token: String
) {
    val tipoVehiculo = when (reserva.Vehiculo.tipo.uppercase()) {
        "COCHE" -> TipoVehiculo.COCHE
        "MOTO" -> TipoVehiculo.MOTO
        "FURGONETA" -> TipoVehiculo.FURGONETA
        "CAMION" -> TipoVehiculo.CAMION
        else -> TipoVehiculo.COCHE
    }

    val colorVehiculo = getColorForVehiculo(reserva.Vehiculo.id)

    var expandedMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = reserva.motivo.uppercase(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (reserva.motivo.uppercase()) {
                        "TRABAJO" -> Color(0xFF3B82F6)
                        "PERSONAL" -> Color(0xFF8B5CF6)
                        else -> Color(0xFF10B981)
                    }
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "ID: ${reserva.id}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Box {
                        IconButton(
                            onClick = { expandedMenu = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Más opciones",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = expandedMenu,
                            onDismissRequest = { expandedMenu = false },
                            containerColor = MaterialTheme.colorScheme.surface
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Editar",
                                            tint = Color(0xFF3B82F6),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Editar", color = Color(0xFF3B82F6))
                                    }
                                },
                                onClick = {
                                    expandedMenu = false
                                    navController.navigate("editarReserva/${reserva.Vehiculo.id}/${reserva.id}")
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Eliminar",
                                            tint = Color(0xFFEF4444),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Eliminar", color = Color(0xFFEF4444))
                                    }
                                },
                                onClick = {
                                    expandedMenu = false
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
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

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Fecha",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${reserva.fechaInicio.split("T")[0]} → ${reserva.fechaFin.split("T")[0]}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Hora",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${reserva.horaInicio.substring(0, 5)} - ${reserva.horaFin.substring(0, 5)}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            reserva.descripcion?.let { desc ->
                if (desc.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Notas: $desc",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "Eliminar reserva",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = "¿Estás seguro de que quieres eliminar esta reserva?\n\n" +
                            "Vehículo: ${reserva.Vehiculo.nombre}\n" +
                            "Fecha: ${reserva.fechaInicio.split("T")[0]}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        coroutineScope.launch {
                            val success = viewModel.eliminarReserva(token, reserva.id.toString())
                            if (!success) {
                                android.util.Log.e("ReservaItemCard", "Error al eliminar reserva ${reserva.id}")
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF4444)
                    )
                ) {
                    Text("Eliminar")
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
}