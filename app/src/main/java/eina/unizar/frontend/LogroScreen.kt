package eina.unizar.frontend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.compose.ui.platform.LocalContext
import eina.unizar.frontend.services.LogrosSyncService
import eina.unizar.frontend.models.LogroDTO
import eina.unizar.frontend.viewmodels.FiltroLogro
import eina.unizar.frontend.viewmodels.LogroViewModel

/**
 * Pantalla principal de Logros.
 *
 * Muestra:
 * - Estadísticas generales del usuario (total, desbloqueados, puntos)
 * - Lista de logros con progreso visual
 * - Filtros (Todos, Desbloqueados, Pendientes)
 * - Sincronización automática en segundo plano
 *
 * @param navController Controlador de navegación
 * @param userId ID del usuario actual
 * @param token Token de autenticación
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogrosScreen(
    navController: NavHostController,
    userId: String,
    token: String
) {
    val logroViewModel: LogroViewModel = viewModel()
    val context = LocalContext.current

    val logros by logroViewModel.logros.collectAsState()
    val estadisticas by logroViewModel.estadisticas.collectAsState()
    val isLoading by logroViewModel.isLoading.collectAsState()
    val error by logroViewModel.error.collectAsState()
    val filtroActual by logroViewModel.filtroActual.collectAsState()
    val nuevosLogros by logroViewModel.nuevosLogros.collectAsState()

    val logrosFiltrados by remember {
        derivedStateOf {
            when (filtroActual) {
                FiltroLogro.TODOS -> logros
                FiltroLogro.DESBLOQUEADOS -> logros.filter { it.desbloqueado }
                FiltroLogro.PENDIENTES -> logros.filter { !it.desbloqueado }
            }
        }
    }

    // Cargar logros al iniciar
    LaunchedEffect(Unit) {
        logroViewModel.cargarLogros(userId.toInt(), token)
        
        //Sincronización automática en segundo plano
        LogrosSyncService.iniciarSincronizacion(
            context = context,
            usuarioId = userId.toInt(),
            token = token,
            onNuevosLogros = { nuevos ->
                // Actualizar ViewModel cuando hay nuevos logros
                logroViewModel.actualizarNuevosLogros(nuevos)
                // Recargar lista de logros
                logroViewModel.cargarLogros(userId.toInt(), token)
            }
        )
    }
    
    DisposableEffect(Unit) {
        onDispose {
            LogrosSyncService.detenerSincronizacion()
        }
    }

    // Mostrar alerta de nuevos logros
    if (nuevosLogros.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { logroViewModel.limpiarNuevosLogros() },
            icon = { Text("🎉", fontSize = 40.sp) },
            title = { Text("¡Nuevos Logros Desbloqueados!") },
            text = {
                Column {
                    nuevosLogros.forEach { logro ->
                        Text("${logro.icono} ${logro.nombre} (+${logro.puntos} pts)")
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { logroViewModel.limpiarNuevosLogros() }) {
                    Text("¡Genial!")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Logros", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFEF4444),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            if (isLoading && logros.isEmpty()) {
                // Loading inicial
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Tarjeta de estadísticas
                    item {
                        estadisticas?.let { stats ->
                            EstadisticasCard(stats)
                        }
                    }

                    // Filtros
                    item {
                        FiltrosRow(
                            filtroActual = filtroActual,
                            onFiltroChange = { logroViewModel.cambiarFiltro(it) }
                        )
                    }

                    // Lista de logros
                    items(logrosFiltrados) { logro ->
                        LogroCard(logro = logro)
                    }

                    // Mensaje si no hay logros
                    if (logrosFiltrados.isEmpty()) {
                        item {
                            Text(
                                text = "No hay logros ${filtroActual.name.lowercase()}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                textAlign = TextAlign.Center,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            // Mensaje de error
            error?.let { errorMsg ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = { logroViewModel.limpiarError() }) {
                            Text("OK")
                        }
                    }
                ) {
                    Text(errorMsg)
                }
            }
        }
    }
}

/**
 * Card con las estadísticas generales del usuario.
 */
@Composable
fun EstadisticasCard(stats: eina.unizar.frontend.models.EstadisticasLogros) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEF4444)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Tu Progreso",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                EstadisticaItem("🏆", stats.desbloqueados.toString(), "Logrados")
                EstadisticaItem("⭐", stats.puntosTotales.toString(), "Puntos")
                EstadisticaItem("📊", "${stats.porcentajeCompletado}%", "Completado")
            }
        }
    }
}

@Composable
fun EstadisticaItem(icono: String, valor: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icono, fontSize = 32.sp)
        Text(
            text = valor,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

/**
 * Fila de filtros (Todos, Desbloqueados, Pendientes).
 */
@Composable
fun FiltrosRow(
    filtroActual: FiltroLogro,
    onFiltroChange: (FiltroLogro) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FiltroLogro.entries.forEach { filtro ->
            FilterChip(
                selected = filtroActual == filtro,
                onClick = { onFiltroChange(filtro) },
                label = {
                    Text(
                        when (filtro) {
                            FiltroLogro.TODOS -> "Todos"
                            FiltroLogro.DESBLOQUEADOS -> "Logrados"
                            FiltroLogro.PENDIENTES -> "Pendientes"
                        }
                    )
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Card individual de un logro.
 */
@Composable
fun LogroCard(logro: LogroDTO) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (logro.desbloqueado) 1f else 0.6f)
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (logro.desbloqueado) Color.White else Color(0xFFE0E0E0)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        color = if (logro.desbloqueado) Color(0xFFFFD700) else Color.Gray,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = logro.icono,
                    fontSize = 32.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Información
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = logro.nombre,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = logro.descripcion,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Barra de progreso
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${logro.progreso} / ${logro.criterio}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "${logro.porcentaje}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (logro.desbloqueado) Color(0xFF4CAF50) else Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { logro.porcentaje / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = if (logro.desbloqueado) Color(0xFF4CAF50) else Color.Gray,
                        trackColor = Color.LightGray,
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Puntos
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "+${logro.puntos}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (logro.desbloqueado) Color(0xFF6A5ACD) else Color.Gray
                )
                Text(
                    text = "pts",
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }
        }
    }
}