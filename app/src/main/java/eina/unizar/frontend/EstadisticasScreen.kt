package eina.unizar.frontend

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import eina.unizar.frontend.models.toVehiculo
import eina.unizar.frontend.viewmodels.HomeViewModel
import eina.unizar.frontend.viewmodels.EstadisticasViewModel
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

data class EstadisticaItem(
    val titulo: String,
    val valor: String,
    val icono: ImageVector,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstadisticasScreen(
    navController: NavHostController,
    efectiveUserId: String,
    efectiveToken: String
) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    // ViewModels
    val homeViewModel = remember { HomeViewModel() }
    val estadisticasViewModel = remember { EstadisticasViewModel() }

    val vehiculosDTO by homeViewModel.vehiculos.collectAsState()
    val vehiculos = vehiculosDTO.map { it.toVehiculo() }

    val estadisticas by estadisticasViewModel.estadisticas.collectAsState()

    // Estados para filtros
    var selectedVehiculoIndex by remember { mutableIntStateOf(0) }
    var vehiculoMenuExpanded by remember { mutableStateOf(false) }

    var mesSeleccionado by remember { mutableIntStateOf(LocalDate.now().monthValue) }
    var anoSeleccionado by remember { mutableIntStateOf(LocalDate.now().year) }
    var showDatePicker by remember { mutableStateOf(false) }

    val vehiculoSeleccionado = vehiculos.getOrNull(selectedVehiculoIndex)

    // Cargar vehículos al inicio
    LaunchedEffect(Unit) {
        homeViewModel.fetchVehiculos(efectiveUserId, efectiveToken)
    }

    // Cargar estadísticas cuando cambia el vehículo o los filtros
    LaunchedEffect(vehiculoSeleccionado, mesSeleccionado, anoSeleccionado) {
        vehiculoSeleccionado?.let {
            estadisticasViewModel.fetchEstadisticas(
                vehiculoId = it.id,
                mes = mesSeleccionado,
                ano = anoSeleccionado,
                token = efectiveToken
            )
        }
    }

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
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "Estadísticas",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.weight(1f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(48.dp))
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(15.dp))

                    // Selector de vehículo
                    if (vehiculos.isNotEmpty()) {
                        vehiculoSeleccionado?.let { vehiculo ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .shadow(2.dp, RoundedCornerShape(25.dp))
                                    .clickable { vehiculoMenuExpanded = true },
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
                            DropdownMenu(
                                expanded = vehiculoMenuExpanded,
                                onDismissRequest = { vehiculoMenuExpanded = false }
                            ) {
                                vehiculos.forEachIndexed { index, v ->
                                    DropdownMenuItem(
                                        text = { Text("${v.nombre} - ${v.matricula}") },
                                        onClick = {
                                            selectedVehiculoIndex = index
                                            vehiculoMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    // Selector de periodo (Mes/Año)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .shadow(2.dp, RoundedCornerShape(25.dp))
                            .clickable { showDatePicker = true },
                        shape = RoundedCornerShape(25.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Periodo",
                                tint = Color(0xFF6B7280)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "${LocalDate.of(anoSeleccionado, mesSeleccionado, 1).month.getDisplayName(TextStyle.FULL, Locale("es"))} $anoSeleccionado",
                                fontSize = 15.sp,
                                color = Color(0xFF1F2937),
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Cambiar periodo",
                                tint = Color(0xFF6B7280)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Resumen del Periodo",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )

                    Spacer(modifier = Modifier.height(15.dp))
                }

                // Tarjetas de estadísticas
                val estadisticasItems = listOf(
                    EstadisticaItem(
                        titulo = "Kilómetros Totales",
                        valor = "${estadisticas?.kmTotales ?: 0} km",
                        icono = Icons.Default.Place,
                        color = Color(0xFF3B82F6)
                    ),
                    EstadisticaItem(
                        titulo = "Horas de Trayecto",
                        valor = "${estadisticas?.horasTotales ?: 0.0} h",
                        icono = Icons.Default.DateRange,
                        color = Color(0xFF8B5CF6)
                    ),
                    EstadisticaItem(
                        titulo = "Consumo Promedio",
                        valor = "${estadisticas?.consumoPromedio ?: 0.0} L/100km",
                        icono = Icons.Default.Info,
                        color = Color(0xFFEF4444)
                    ),
                    EstadisticaItem(
                        titulo = "Gasto Total",
                        valor = "${estadisticas?.gastoTotal ?: 0.0} €",
                        icono = Icons.Default.Build,
                        color = Color(0xFFEAB308)
                    ),
                    EstadisticaItem(
                        titulo = "Litros Repostados",
                        valor = "${estadisticas?.litrosTotales ?: 0.0} L",
                        icono = Icons.Default.Info,
                        color = Color(0xFF10B981)
                    )
                )

                items(estadisticasItems) { item ->
                    EstadisticaCard(item)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

    // Diálogo selector de mes y año
    if (showDatePicker) {
        DatePickerDialog(
            mesActual = mesSeleccionado,
            anoActual = anoSeleccionado,
            onDismiss = { showDatePicker = false },
            onConfirm = { mes, ano ->
                mesSeleccionado = mes
                anoSeleccionado = ano
                showDatePicker = false
            }
        )
    }
}

@Composable
fun EstadisticaCard(item: EstadisticaItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(item.color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icono,
                    contentDescription = item.titulo,
                    tint = item.color,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.titulo,
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280)
                )
                Text(
                    text = item.valor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
            }
        }
    }
}

@Composable
fun DatePickerDialog(
    mesActual: Int,
    anoActual: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    var mesTemp by remember { mutableIntStateOf(mesActual) }
    var anoTemp by remember { mutableIntStateOf(anoActual) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar Periodo") },
        text = {
            Column {
                Text("Mes", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { if (mesTemp > 1) mesTemp-- }) {
                        Icon(Icons.Default.KeyboardArrowLeft, "Anterior")
                    }
                    Text(
                        LocalDate.of(anoTemp, mesTemp, 1).month.getDisplayName(TextStyle.FULL, Locale("es")),
                        fontSize = 16.sp
                    )
                    IconButton(onClick = { if (mesTemp < 12) mesTemp++ }) {
                        Icon(Icons.Default.KeyboardArrowRight, "Siguiente")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Año", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { anoTemp-- }) {
                        Icon(Icons.Default.KeyboardArrowLeft, "Anterior")
                    }
                    Text(anoTemp.toString(), fontSize = 16.sp)
                    IconButton(onClick = { anoTemp++ }) {
                        Icon(Icons.Default.KeyboardArrowRight, "Siguiente")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(mesTemp, anoTemp) }) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}