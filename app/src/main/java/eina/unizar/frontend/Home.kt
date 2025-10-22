package eina.unizar.frontend

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import eina.unizar.frontend.viewmodels.HomeViewModel


enum class EstadoVehiculo(val color: Color, val texto: String) {
    DISPONIBLE(Color(0xFF10B981), "Disponible"),
    EN_USO(Color(0xFFF59E0B), "En uso"),
    EN_REPARACION(Color(0xFFEF4444), "En reparación")
}

@Composable
fun HomeScreenWrapper(
    userId: String,
    token: String,
    vehiculos: List<Vehiculo>,
    onVehiculoClick: (String) -> Unit,
    onAddVehiculoClick: () -> Unit,
    onMapaClick: () -> Unit,
    onCalendarioClick: () -> Unit,
    onIncidenciasClick: () -> Unit,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    navController: NavHostController
) {
    val viewModel = remember { HomeViewModel() }

    // Llama a la API al cargar la pantalla
    LaunchedEffect(Unit) {
        viewModel.fetchUserName(userId, token)
    }

    HomeScreen(
        userName = viewModel.userName,
        vehiculos = vehiculos,
        onVehiculoClick = onVehiculoClick,
        onAddVehiculoClick = onAddVehiculoClick,
        onMapaClick = onMapaClick,
        onCalendarioClick = onCalendarioClick,
        onIncidenciasClick = onIncidenciasClick,
        selectedTab = selectedTab,
        onTabSelected = onTabSelected,
        navController = navController
    )
}

@Composable
fun HomeScreen(
    userName: String,
    vehiculos: List<Vehiculo>,
    onVehiculoClick: (String) -> Unit,
    onAddVehiculoClick: () -> Unit,
    onMapaClick: () -> Unit,
    onCalendarioClick: () -> Unit,
    onIncidenciasClick: () -> Unit,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    navController: NavHostController
) {

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
            color = Color(0xFFEF4444),
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Hola,",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Text(
                        text = userName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                // Icono perfil
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Perfil",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }

        // Contenido
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(20.dp))

                // Título Mis Vehículos
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mis Vehículos",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                    IconButton(onClick = onAddVehiculoClick) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Añadir vehículo",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Lista de vehículos
            items(vehiculos) { vehiculo ->
                VehiculoCard(
                    vehiculo = vehiculo,
                    onClick = { onVehiculoClick(vehiculo.id) }
                )
                Spacer(modifier = Modifier.height(15.dp))
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))

                // Acceso Rápido
                Text(
                    text = "Acceso Rápido",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )

                Spacer(modifier = Modifier.height(15.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    QuickAccessCard(
                        icon = Icons.Default.LocationOn,
                        title = "Mapa",
                        color = Color(0xFFEF4444),
                        onClick = onMapaClick,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    QuickAccessCard(
                        icon = Icons.Default.DateRange,
                        title = "Calendario",
                        color = Color(0xFF3B82F6),
                        onClick = onCalendarioClick,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    QuickAccessCard(
                        icon = Icons.Default.Warning,
                        title = "Incidencias",
                        color = Color(0xFF10B981),
                        onClick = onIncidenciasClick,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}
    }



@Composable
fun VehiculoCard(
    vehiculo: Vehiculo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clickable(onClick = onClick)
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono del vehículo
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        vehiculo.tipo.color.copy(alpha = 0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = vehiculo.tipo.icon,
                    contentDescription = vehiculo.tipo.name,
                    tint = vehiculo.tipo.color,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Información
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = vehiculo.nombre,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
                Text(
                    text = vehiculo.matricula,
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "● ",
                        fontSize = 13.sp,
                        color = vehiculo.estado.color
                    )
                    Text(
                        text = vehiculo.estado.texto,
                        fontSize = 13.sp,
                        color = vehiculo.estado.color
                    )
                }
            }

            // Flecha
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Ver detalles",
                tint = Color(0xFF9CA3AF),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun QuickAccessCard(
    icon: ImageVector,
    title: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(90.dp)
            .clickable(onClick = onClick)
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color(0xFF1F2937)
            )
        }
    }
}



@Composable
fun BottomNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color(0xFFEF4444), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        } else {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color(0xFF9CA3AF),
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = if (selected) Color(0xFFEF4444) else Color(0xFF9CA3AF),
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}