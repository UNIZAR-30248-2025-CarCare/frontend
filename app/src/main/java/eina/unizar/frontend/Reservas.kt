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
import eina.unizar.frontend.models.Vehiculo
import java.time.LocalDate
import java.time.YearMonth

data class Reserva(
    val id: String,
    val usuario: Usuario,
    val vehiculo: Vehiculo,
    val fecha: LocalDate,
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarioScreen(
    vehiculoSeleccionado: Vehiculo?,
    reservas: List<Reserva>,
    mesActual: YearMonth,
    diaSeleccionado: LocalDate,
    onBackClick: () -> Unit,
    onVehiculoClick: () -> Unit,
    onMesAnterior: () -> Unit,
    onMesSiguiente: () -> Unit,
    onDiaClick: (LocalDate) -> Unit,
    onAddReservaClick: () -> Unit,
    navController: NavHostController
) {
    val reservasDelDia = reservas.filter { it.fecha == diaSeleccionado }

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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
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
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Calendario",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(48.dp))
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(15.dp))

                // Selector de vehículo
                vehiculoSeleccionado?.let { vehiculo ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .shadow(2.dp, RoundedCornerShape(25.dp))
                            .clickable(onClick = onVehiculoClick),
                        shape = RoundedCornerShape(25.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .background(
                                        vehiculo.tipo.color.copy(alpha = 0.1f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = vehiculo.tipo.iconRes),
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
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Cambiar vehículo",
                                tint = Color(0xFF6B7280)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                // Navegación del mes
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onMesAnterior) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = "Mes anterior",
                                tint = Color(0xFF6B7280)
                            )
                        }
                        Text(
                            text = "${mesActual.month.name} ${mesActual.year}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                        IconButton(onClick = onMesSiguiente) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Mes siguiente",
                                tint = Color(0xFF6B7280)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                // Calendario
                CalendarioMensual(
                    mes = mesActual,
                    diaSeleccionado = diaSeleccionado,
                    reservas = reservas,
                    onDiaClick = onDiaClick
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Título de reservas del día
                Text(
                    text = "Reservas de hoy",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Lista de reservas
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
                            color = Color(0xFF9CA3AF)
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
                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        // Botón flotante añadir reserva
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 20.dp, bottom = 10.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
                onClick = onAddReservaClick,
                containerColor = Color(0xFFEF4444),
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Añadir reserva",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(10.dp)),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Días de la semana
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("L", "M", "X", "J", "V", "S", "D").forEach { dia ->
                    Text(
                        text = dia,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF6B7280),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Grid de días (simplificado - aquí irá la lógica completa)
            Column {
                repeat(5) { semana ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        repeat(7) { dia ->
                            val numeroDia = semana * 7 + dia + 1
                            if (numeroDia <= mes.lengthOfMonth()) {
                                val fecha = LocalDate.of(mes.year, mes.month, numeroDia)
                                val tieneReservas = reservas.any { it.fecha == fecha }
                                val esSeleccionado = fecha == diaSeleccionado

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (esSeleccionado) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(
                                                    Color(0xFFEF4444),
                                                    CircleShape
                                                )
                                                .clickable { onDiaClick(fecha) },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = numeroDia.toString(),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    } else {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.clickable { onDiaClick(fecha) }
                                        ) {
                                            if (tieneReservas) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(4.dp)
                                                        .background(
                                                            Color(0xFF3B82F6),
                                                            CircleShape
                                                        )
                                                )
                                            }
                                            Text(
                                                text = numeroDia.toString(),
                                                fontSize = 14.sp,
                                                color = Color(0xFF1F2937)
                                            )
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

@Composable
fun ReservaCard(reserva: Reserva) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            // Barra de color
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(reserva.tipo.color)
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar usuario
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(Color.Blue.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = reserva.usuario.nombre.first().toString(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Cyan
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Información
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = reserva.usuario.nombre,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1F2937)
                    )
                    Text(
                        text = "${reserva.horaInicio} - ${reserva.horaFin}",
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280)
                    )
                    Text(
                        text = reserva.tipo.nombre,
                        fontSize = 12.sp,
                        color = reserva.tipo.color
                    )
                }

                // Icono de estado
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