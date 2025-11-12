package eina.unizar.frontend

import android.util.Log
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import eina.unizar.frontend.models.toVehiculo
import eina.unizar.frontend.viewmodels.HomeViewModel
import eina.unizar.frontend.models.Viaje
import eina.unizar.frontend.viewmodels.ViajesViewModel
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViajesScreen(
    onBackClick: () -> Unit,
    onViajeClick: (String) -> Unit,
    onAddViajeClick: () -> Unit,
    navController: NavHostController,
    efectiveUserId: String,
    efectiveToken: String
) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    // ViewModel y vehículos
    val homeViewModel = remember { HomeViewModel() }
    val vehiculosDTO by homeViewModel.vehiculos.collectAsState()
    val vehiculos = vehiculosDTO.map { it.toVehiculo() }

    val viajesViewModel = remember { ViajesViewModel() }
    val viajes by viajesViewModel.viajes.collectAsState()

    LaunchedEffect(Unit) {
        homeViewModel.fetchVehiculos(efectiveUserId, efectiveToken)
    }

    Log.d("Viaje", "Vehículos obtenidos: ${vehiculos.size}")

    // Estado para el vehículo seleccionado y referencia al mapa
    var selectedIndex by remember { mutableIntStateOf(0) }
    val vehiculoSeleccionado = vehiculos.getOrNull(selectedIndex)
    var vehiculoMenuExpanded by remember { mutableStateOf(false) }

    var viajeSeleccionado by remember { mutableStateOf<Viaje?>(null) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(vehiculos.size, selectedIndex) {
        vehiculos.getOrNull(selectedIndex)?.let { vehiculo ->
            Log.d("ViajesScreen", "Solicitando viajes para vehículo: ${vehiculo.id}")
            viajesViewModel.fetchViajes(vehiculo.id, efectiveToken)
        } ?: Log.w("ViajesScreen", "No hay vehículo seleccionado en índice $selectedIndex")
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
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "Viajes",
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
                                .clickable{vehiculoMenuExpanded = true},
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
                                        selectedIndex = index
                                        vehiculoMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Historial de Viajes",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )

                    Spacer(modifier = Modifier.height(15.dp))
                }

                // Lista de viajes
                if (viajes.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay viajes registrados",
                                fontSize = 14.sp,
                                color = Color(0xFF9CA3AF)
                            )
                        }
                    }
                } else {
                    items(viajes) { viaje ->
                        ViajeCard(
                            viaje = viaje,
                            onClick = {
                                viajeSeleccionado = viaje
                                scope.launch { sheetState.show() }
                            }
                        )
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(120.dp))
                }
            }

            // Banner de detalles
            if (viajeSeleccionado != null) {
                ModalBottomSheet(
                    onDismissRequest = { viajeSeleccionado = null },
                    sheetState = sheetState
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = viajeSeleccionado!!.nombre,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Descripción: ${viajeSeleccionado!!.descripcion}")
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Usuario: ${viajeSeleccionado!!.usuario}")
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Fecha inicio: ${viajeSeleccionado!!.fechaHoraInicio.replace("T", " ")}")
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Fecha fin: ${viajeSeleccionado!!.fechaHoraFin.replace("T", " ")}")
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Km realizados: ${viajeSeleccionado!!.kmRealizados}")
                        Text("Consumo combustible: ${viajeSeleccionado!!.consumoCombustible} L")
                    }
                }
            }

            // Botón flotante añadir viaje
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 20.dp, bottom = 10.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                FloatingActionButton(
                    onClick = onAddViajeClick,
                    containerColor = Color(0xFFEF4444),
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Añadir viaje",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ViajeCard(
    viaje: Viaje,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono de viaje
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        Color(0xFF8B5CF6).copy(alpha = 0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = "Viaje",
                    tint = Color(0xFF8B5CF6),
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Información del viaje
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = viaje.nombre,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
                Text(
                    text = "Realizado por ${viaje.usuario}",
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280)
                )
                Text(
                    text = viaje.fechaHoraFin.replace("T", " "),
                    fontSize = 12.sp,
                    color = Color(0xFF9CA3AF)
                )
            }

            // Flecha
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Ver detalles",
                tint = Color(0xFF9CA3AF)
            )
        }
    }
}
