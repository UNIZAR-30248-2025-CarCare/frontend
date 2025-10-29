package eina.unizar.frontend

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import eina.unizar.frontend.models.Repostaje
import eina.unizar.frontend.models.toVehiculo
import eina.unizar.frontend.viewmodels.HomeViewModel
import eina.unizar.frontend.models.Viaje
import eina.unizar.frontend.viewmodels.RepostajesViewModel
import eina.unizar.frontend.viewmodels.ViajesViewModel
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepostajesScreen(
    onBackClick: () -> Unit,
    onAddRepostajeClick: () -> Unit,
    navController: NavHostController,
    efectiveUserId: String,
    efectiveToken: String
) {
    val homeViewModel = remember { HomeViewModel() }
    val repostajesViewModel = remember { RepostajesViewModel() }
    val vehiculosDTO by homeViewModel.vehiculos.collectAsState()
    val vehiculos = vehiculosDTO.map { it.toVehiculo() }
    var selectedIndex by remember { mutableIntStateOf(0) }
    val vehiculoSeleccionado = vehiculos.getOrNull(selectedIndex)
    var vehiculoMenuExpanded by remember { mutableStateOf(false) }

    val currentRoute = navController.currentBackStackEntry?.destination?.route

    LaunchedEffect(Unit) {
        homeViewModel.fetchVehiculos(efectiveUserId, efectiveToken)
    }
    LaunchedEffect(vehiculoSeleccionado?.id) {
        vehiculoSeleccionado?.let {
            repostajesViewModel.fetchRepostajes(efectiveToken, it.id)
        }
    }

    LaunchedEffect(vehiculoSeleccionado?.id) {
        vehiculoSeleccionado?.let {
            repostajesViewModel.fetchRepostajes(efectiveToken, it.id)
            repostajesViewModel.fetchProximoRepostaje(efectiveToken, it.id)
        }
    }

    // Datos de resumen y lista de repostajes
    val resumen by repostajesViewModel.resumen.collectAsState()
    val proximo by repostajesViewModel.proximo.collectAsState()

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
                        text = "Repostajes",
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
                                        selectedIndex = index
                                        vehiculoMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }

                item {
                    resumen?.let {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, RoundedCornerShape(12.dp))
                                .shadow(2.dp, RoundedCornerShape(12.dp))
                                .padding(16.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Resumen",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Color(0xFF1F2937)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Total gastado:",
                                        fontSize = 15.sp,
                                        color = Color(0xFF6B7280)
                                    )
                                    Text(
                                        text = "Total litros",
                                        fontSize = 15.sp,
                                        color = Color(0xFF6B7280)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${it.totalPrecio} €",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFEF4444)
                                    )
                                    Text(
                                        text = "${it.totalLitros} L",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF3B82F6)
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                }

                item {
                    // Caja azul próximo repostaje
                    proximo?.let {
                        val nombreUsuario = it.proximoUsuario?.nombre ?: "Desconocido"
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF3B82F6), RoundedCornerShape(12.dp))
                                .padding(16.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Próximo en repostar: $nombreUsuario",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Importe: ${it.importeEstimado} €",
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }

                items(resumen?.repostajes ?: emptyList()) { repostaje ->
                    RepostajeCard(repostaje)
                }

            }

            // Botón flotante añadir repostaje
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 20.dp, bottom = 10.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                FloatingActionButton(
                    onClick = onAddRepostajeClick,
                    containerColor = Color(0xFFEF4444),
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Añadir repostaje",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

// Genera un color único a partir del nombre del usuario
fun colorPorUsuario(nombre: String): Color {
    val hash = abs(nombre.hashCode())
    val r = 100 + (hash % 156)
    val g = 100 + ((hash / 100) % 156)
    val b = 100 + ((hash / 10000) % 156)
    return Color(r, g, b)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RepostajeCard(repostaje: Repostaje) {
    val colorUsuario = colorPorUsuario(repostaje.usuarioNombre)
    val fechaFormateada = try {
        OffsetDateTime.parse(repostaje.fecha)
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy · HH:mm"))
    } catch (e: Exception) {
        repostaje.fecha
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono de perfil
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(colorUsuario, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Usuario",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            // Info principal
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = repostaje.usuarioNombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color(0xFF1F2937)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = fechaFormateada,
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280)
                )
                Text(
                    text = "${repostaje.litros} L",
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280)
                )
            }
            // Precio a la derecha
            Text(
                text = "${repostaje.precioTotal} €",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFFEF4444)
            )
        }
    }
}


