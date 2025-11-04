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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import eina.unizar.frontend.models.RevisionDTO
import eina.unizar.frontend.models.TiposRevision
import eina.unizar.frontend.models.toVehiculo
import eina.unizar.frontend.viewmodels.HomeViewModel
import eina.unizar.frontend.viewmodels.RevisionViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RevisionesScreen(
    onBackClick: () -> Unit,
    onAddRevisionClick: () -> Unit,
    navController: NavHostController,
    efectiveUserId: String,
    efectiveToken: String
) {
    // ViewModels - Igual que en Repostajes
    val homeViewModel = remember { HomeViewModel() }
    val revisionViewModel = remember { RevisionViewModel() }

    // Estados reactivos del ViewModel
    val vehiculosDTO by homeViewModel.vehiculos.collectAsState()
    val revisiones by revisionViewModel.revisiones.collectAsState()
    val isLoading by revisionViewModel.isLoading.collectAsState()
    val error by revisionViewModel.error.collectAsState()

    val vehiculos = vehiculosDTO.map { it.toVehiculo() }
    var selectedIndex by remember { mutableIntStateOf(0) }
    val vehiculoSeleccionado = vehiculos.getOrNull(selectedIndex)
    var vehiculoMenuExpanded by remember { mutableStateOf(false) }

    // Estados específicos para filtros de revisión
    var tipoFiltroSeleccionado by remember { mutableStateOf("Todos") }
    var tipoMenuExpanded by remember { mutableStateOf(false) }

    val currentRoute = navController.currentBackStackEntry?.destination?.route

    // Efectos reactivos
    LaunchedEffect(Unit) {
        homeViewModel.fetchVehiculos(efectiveUserId, efectiveToken)
    }

    LaunchedEffect(vehiculoSeleccionado?.id, tipoFiltroSeleccionado) {
        vehiculoSeleccionado?.let {
            revisionViewModel.fetchRevisiones(
                efectiveToken,
                it.id,
                if (tipoFiltroSeleccionado == "Todos") null else tipoFiltroSeleccionado
            )
        }
    }

    // Manejo de errores
    error?.let { errorMsg ->
        LaunchedEffect(errorMsg) {
            Log.e("RevisionesScreen", "Error: $errorMsg")
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
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "Revisiones",
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

                    // Selector de Vehículo
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

                // Filtro por Tipo de Revisión
                item {
                    if (vehiculoSeleccionado != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, RoundedCornerShape(12.dp))
                                .shadow(2.dp, RoundedCornerShape(12.dp))
                                .padding(16.dp)
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        Icons.Default.Settings,
                                        contentDescription = null,
                                        tint = Color(0xFF1F2937),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Filtrar por Tipo",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1F2937)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { tipoMenuExpanded = true },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = tipoFiltroSeleccionado,
                                            fontSize = 14.sp,
                                            color = Color(0xFF1F2937)
                                        )
                                        Icon(
                                            Icons.Default.ArrowDropDown,
                                            contentDescription = "Seleccionar tipo",
                                            tint = Color(0xFF6B7280)
                                        )
                                    }
                                }

                                DropdownMenu(
                                    expanded = tipoMenuExpanded,
                                    onDismissRequest = { tipoMenuExpanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Todos") },
                                        onClick = {
                                            tipoFiltroSeleccionado = "Todos"
                                            tipoMenuExpanded = false
                                        }
                                    )
                                    TiposRevision.TODOS.forEach { tipo ->
                                        DropdownMenuItem(
                                            text = { Text(tipo) },
                                            onClick = {
                                                tipoFiltroSeleccionado = tipo
                                                tipoMenuExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Lista de Revisiones
                if (vehiculoSeleccionado != null) {
                    when {
                        isLoading -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = Color(0xFFEF4444))
                                }
                            }
                        }
                        revisiones.isEmpty() -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White, RoundedCornerShape(12.dp))
                                        .shadow(2.dp, RoundedCornerShape(12.dp))
                                        .padding(16.dp)
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            Icons.Default.Build,
                                            contentDescription = null,
                                            tint = Color(0xFF9CA3AF),
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "No hay revisiones para este vehículo",
                                            color = Color(0xFF9CA3AF),
                                            fontSize = 16.sp
                                        )
                                        if (tipoFiltroSeleccionado != "Todos") {
                                            Text(
                                                text = "con el filtro '${tipoFiltroSeleccionado}'",
                                                color = Color(0xFF9CA3AF),
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        else -> {
                            items(revisiones) { revision ->
                                RevisionItem(revision = revision)
                            }
                        }
                    }
                } else {
                    // Mensaje cuando no hay vehículo seleccionado
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, RoundedCornerShape(12.dp))
                                .shadow(2.dp, RoundedCornerShape(12.dp))
                                .padding(16.dp)
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.Build,
                                    contentDescription = null,
                                    tint = Color(0xFF9CA3AF),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Selecciona un vehículo para ver sus revisiones",
                                    color = Color(0xFF9CA3AF),
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }

            // Botón flotante añadir revisión
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 20.dp, bottom = 10.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                FloatingActionButton(
                    onClick = onAddRevisionClick,
                    containerColor = Color(0xFFEF4444),
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Añadir revisión",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RevisionItem(revision: RevisionDTO) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = revision.tipo,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                    Text(
                        text = revision.fecha,
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280)
                    )
                    if (revision.kilometraje > 0) {
                        Text(
                            text = "${revision.kilometraje} km",
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    if (!revision.taller.isNullOrEmpty()) {
                        Text(
                            text = revision.taller,
                            fontSize = 12.sp,
                            color = Color(0xFF10B981),
                            fontWeight = FontWeight.Medium
                        )
                    }
                    if (!revision.proximaRevision.isNullOrEmpty()) {
                        Text(
                            text = "Próxima: ${revision.proximaRevision}",
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            }

            if (revision.observaciones.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = revision.observaciones,
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color(0xFFF5F5F5),
                            RoundedCornerShape(6.dp)
                        )
                        .padding(8.dp)
                )
            }
        }
    }
}