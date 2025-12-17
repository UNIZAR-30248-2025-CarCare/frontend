package eina.unizar.frontend

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eina.unizar.frontend.models.ReservaDTO
import eina.unizar.frontend.models.RevisionDTO
import eina.unizar.frontend.models.IncidenciaDetalle
import eina.unizar.frontend.models.SearchResult
import eina.unizar.frontend.models.TipoResultado
import eina.unizar.frontend.models.Viaje
import eina.unizar.frontend.models.toVehiculo
import eina.unizar.frontend.viewmodels.BusquedaViewModel
import eina.unizar.frontend.viewmodels.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusquedaScreen(
    efectiveUserId: String,
    efectiveToken: String,
    onBackClick: () -> Unit
) {
    val viewModel = remember { BusquedaViewModel() }

    val homeViewModel = remember { HomeViewModel() }
    val vehiculosDTO by homeViewModel.vehiculos.collectAsState()
    val vehiculos = vehiculosDTO.map { it.toVehiculo() }

    // ViewModel y vehículos
    LaunchedEffect(Unit) {
        homeViewModel.fetchVehiculos(efectiveUserId, efectiveToken)
    }

    // Estado para el vehículo seleccionado y referencia al mapa
    var selectedIndex by remember { mutableIntStateOf(0) }
    val vehiculoSeleccionado = vehiculos.getOrNull(selectedIndex)
    var vehiculoMenuExpanded by remember { mutableStateOf(false) }

    val resultados by viewModel.resultados.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val filtroActivo by viewModel.filtroActivo.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var resultadoSeleccionado by remember { mutableStateOf<SearchResult?>(null) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    // Debounce para la búsqueda
    LaunchedEffect(searchQuery, vehiculoSeleccionado?.id) {
        delay(300) // Espera 300ms después de que el usuario deje de escribir
        vehiculoSeleccionado?.let { vehiculo ->
            if (searchQuery.isNotEmpty()) {
                viewModel.buscar(vehiculo.id.toString(), searchQuery, efectiveToken)
            }
        }
    }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding(),
                color = Color(0xFFEF4444),
            ) {
                Column {
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
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Text(
                            text = "Búsqueda Global",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.weight(1f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.width(48.dp))
                    }


                    // Selector de vehículo
                    vehiculoSeleccionado?.let { vehiculo ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .height(50.dp)
                                .shadow(2.dp, RoundedCornerShape(25.dp))
                                .clickable{vehiculoMenuExpanded = true},
                            shape = RoundedCornerShape(25.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Cambiar vehículo",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        DropdownMenu(
                            expanded = vehiculoMenuExpanded,
                            onDismissRequest = { vehiculoMenuExpanded = false },
                            containerColor = MaterialTheme.colorScheme.surface
                        ) {
                            vehiculos.forEachIndexed { index, v ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            "${v.nombre} - ${v.matricula}",
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    onClick = {
                                        selectedIndex = index
                                        vehiculoMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Barra de búsqueda
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        placeholder = {
                            Text(
                                "Buscar...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Buscar",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = {
                                    searchQuery = ""
                                    viewModel.limpiar()
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Limpiar",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(25.dp),
                        singleLine = true
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Filtros
            if (searchQuery.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = filtroActivo == null,
                            onClick = { viewModel.setFiltro(null) },
                            label = {
                                Text(
                                    "Todos",
                                    color = if (filtroActivo == null) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        )
                    }

                    items(TipoResultado.entries) { tipo ->
                        FilterChip(
                            selected = filtroActivo == tipo,
                            onClick = { viewModel.setFiltro(tipo) },
                            label = {
                                Text(
                                    tipo.displayName,
                                    color = if (filtroActivo == tipo) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = tipo.color.copy(alpha = 0.2f),
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    }
                }
            }

            // Resultados
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                searchQuery.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Busca viajes, repostajes, incidencias...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                resultados.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se encontraron resultados",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(resultados) { resultado ->
                            ResultadoCard(
                                resultado = resultado,
                                onClick = {
                                    resultadoSeleccionado = resultado
                                    scope.launch { sheetState.show() }
                                }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
    resultadoSeleccionado?.let { resultado ->
        ModalBottomSheet(
            onDismissRequest = { resultadoSeleccionado = null },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            when (resultado) {
                is SearchResult.ViajeResult -> {
                    DetalleViajeModal(viaje = resultado.viaje)
                }
                is SearchResult.ReservaResult -> {
                    DetalleReservaModal(reserva = resultado.reserva)
                }
                is SearchResult.RevisionResult -> {
                    DetalleRevisionModal(revision = resultado.revision)
                }
                is SearchResult.IncidenciaResult -> {
                    DetalleIncidenciaModal(incidencia = resultado.incidencia)
                }
            }
        }
    }
}

@Composable
fun ResultadoCard(
    resultado: SearchResult,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                    .background(
                        resultado.tipo.color.copy(alpha = 0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (resultado.tipo) {
                        TipoResultado.VIAJE -> Icons.Default.Place
                        //TipoResultado.REPOSTAJE -> Icons.Default.Settings
                        TipoResultado.INCIDENCIA -> Icons.Default.Warning
                        TipoResultado.RESERVA -> Icons.Filled.DateRange
                        TipoResultado.REVISION -> Icons.Default.Build
                    },
                    contentDescription = resultado.tipo.displayName,
                    tint = resultado.tipo.color,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = resultado.tipo.displayName,
                        fontSize = 12.sp,
                        color = resultado.tipo.color,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = resultado.titulo,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = resultado.subtitulo,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                resultado.fecha?.let {
                    Text(
                        text = it.replace("T", " ").substring(0, 16),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Ver detalles",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DetalleViajeModal(viaje: Viaje) {
    Column(modifier = Modifier.padding(24.dp)) {
        Text(
            text = viaje.nombre,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(16.dp))

        DetalleItem(label = "Descripción", valor = viaje.descripcion)
        DetalleItem(label = "Usuario", valor = viaje.usuario ?: "No especificado")
        DetalleItem(
            label = "Fecha inicio",
            valor = viaje.fechaHoraInicio.replace("T", " ").substring(0, 16)
        )
        DetalleItem(
            label = "Fecha fin",
            valor = viaje.fechaHoraFin.replace("T", " ").substring(0, 16)
        )
        DetalleItem(label = "Km realizados", valor = "${viaje.kmRealizados} km")
        DetalleItem(label = "Consumo", valor = "${viaje.consumoCombustible} L")

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun DetalleReservaModal(reserva: ReservaDTO) {
    Column(modifier = Modifier.padding(24.dp)) {
        Text(
            text = "Reserva",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(16.dp))

        DetalleItem(label = "Usuario", valor = reserva.Usuario.nombre)
        DetalleItem(label = "Email", valor = reserva.Usuario.email)
        DetalleItem(label = "Vehículo", valor = "${reserva.Vehiculo.nombre} - ${reserva.Vehiculo.matricula}")
        DetalleItem(
            label = "Fecha inicio",
            valor = "${reserva.fechaInicio} ${reserva.horaInicio}"
        )
        DetalleItem(
            label = "Fecha fin",
            valor = "${reserva.fechaFin} ${reserva.horaFin}"
        )
        DetalleItem(label = "Motivo", valor = reserva.motivo)
        reserva.descripcion?.let {
            DetalleItem(label = "Descripción", valor = it)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun DetalleRevisionModal(revision: RevisionDTO) {
    Column(modifier = Modifier.padding(24.dp)) {
        Text(
            text = "Revisión - ${revision.tipo}",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(16.dp))

        DetalleItem(label = "Tipo", valor = revision.tipo)
        DetalleItem(label = "Observaciones", valor = revision.observaciones)
        revision.usuario?.let {
            DetalleItem(label = "Registrado por", valor = it)
        }
        DetalleItem(
            label = "Fecha",
            valor = revision.fecha.replace("T", " ").substring(0, 16)
        )
        DetalleItem(label = "Kilometraje", valor = "${revision.kilometraje} km")
        revision.taller?.let {
            DetalleItem(label = "Taller", valor = it)
        }
        revision.proximaRevision?.let {
            DetalleItem(label = "Próxima revisión", valor = it)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun DetalleItem(label: String, valor: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = valor,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun DetalleIncidenciaModal(incidencia: IncidenciaDetalle) {
    Column(modifier = Modifier.padding(24.dp)) {
        // Título con tipo y estado
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = incidencia.titulo,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            // Badge de estado
            Box(
                modifier = Modifier
                    .background(
                        when (incidencia.estado) {
                            "Pendiente" -> Color(0xFFFEF3C7)
                            "En progreso" -> Color(0xFFDBEAFE)
                            "Resuelta" -> Color(0xFFD1FAE5)
                            "Cancelada" -> Color(0xFFFEE2E2)
                            else -> Color(0xFFF3F4F6)
                        },
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = incidencia.estado,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = when (incidencia.estado) {
                        "Pendiente" -> Color(0xFF92400E)
                        "En progreso" -> Color(0xFF1E40AF)
                        "Resuelta" -> Color(0xFF065F46)
                        "Cancelada" -> Color(0xFF991B1B)
                        else -> Color(0xFF374151)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Badge de prioridad
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(
                        when (incidencia.prioridad) {
                            "Alta" -> Color(0xFFDC2626)
                            "Media" -> Color(0xFFF59E0B)
                            "Baja" -> Color(0xFF10B981)
                            else -> Color(0xFF6B7280)
                        }.copy(alpha = 0.1f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Prioridad ${incidencia.prioridad}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = when (incidencia.prioridad) {
                        "Alta" -> Color(0xFFDC2626)
                        "Media" -> Color(0xFFF59E0B)
                        "Baja" -> Color(0xFF10B981)
                        else -> Color(0xFF6B7280)
                    }
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = incidencia.tipo,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Detalles principales
        DetalleItem(label = "Descripción", valor = incidencia.descripcion)

        incidencia.nombreVehiculo?.let { nombre ->
            DetalleItem(
                label = "Vehículo",
                valor = incidencia.matriculaVehiculo?.let { "$nombre - $it" } ?: nombre
            )
        }

        incidencia.nombreUsuario?.let {
            DetalleItem(label = "Reportado por", valor = it)
        }

        DetalleItem(
            label = "Fecha creación",
            valor = incidencia.fechaCreacion.replace("T", " ").substring(0, 16)
        )

        incidencia.fechaActualizacion?.let {
            DetalleItem(
                label = "Última actualización",
                valor = it.replace("T", " ").substring(0, 16)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}