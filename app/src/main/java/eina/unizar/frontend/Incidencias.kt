package eina.unizar.frontend

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.icu.text.SimpleDateFormat
import android.os.Environment
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
import java.time.LocalDate
import androidx.compose.material.icons.filled.ShoppingCart
import android.graphics.Paint
import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
//import com.google.ai.edge.litert.Environment
import eina.unizar.frontend.models.IncidenciaDetalle
import eina.unizar.frontend.models.Vehiculo
import eina.unizar.frontend.models.VehiculoDTO
import eina.unizar.frontend.viewmodels.HomeViewModel
import eina.unizar.frontend.viewmodels.IncidenciaViewModel


import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream


import java.util.Date           // Para Date
import java.util.Locale         // Para Locale

import java.util.*


// Clases solicitadas
data class Usuario(
    val id: String,
    val nombre: String,
    val iniciales: String,
    val email: String
)

//! HAY QUE PONER ICONOS PERSONALIZADOS
enum class TipoVehiculo(val iconRes: Int, val color: Color) {
    CAMION(R.drawable.ic_camion, Color(0xFF3B82F6)),
    FURGONETA(R.drawable.ic_furgoneta, Color(0xFFF59E0B)),
    COCHE(R.drawable.ic_coche, Color(0xFF10B981)),
    MOTO(R.drawable.ic_moto, Color(0xFFEF4444)),
    OTRO(R.drawable.ic_otro, Color(0xFF6B7280))
}

data class Incidencia(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val tipo: TipoIncidencia,
    val prioridad: PrioridadIncidencia,
    val reportadoPor: Usuario,
    val fecha: LocalDate,
    val vehiculo: Vehiculo,
    val estado: EstadoIncidencia
)

enum class TipoIncidencia(val nombre: String) {
    AVERIA("Avería"),
    ACCIDENTE("Accidente"),
    MANTENIMIENTO("Mantenimiento"),
    OTRO("Otro")
}

enum class PrioridadIncidencia(val color: Color, val nombre: String) {
    ALTA(Color(0xFFEF4444), "ALTA PRIORIDAD"),
    MEDIA(Color(0xFFF59E0B), "MEDIA"),
    BAJA(Color(0xFF10B981), "BAJA")
}

enum class EstadoIncidencia {
    ACTIVA,
    RESUELTA
}

/**
 * Pantalla de gestión y visualización de incidencias de los vehículos.
 *
 * - Muestra dos pestañas: “Activas” y “Resueltas”.
 * - Cada incidencia contiene título, descripción, tipo, prioridad y estado.
 * - Permite seleccionar vehículo y navegar al detalle de la incidencia.
 *
 * Elementos destacados:
 * - `Scaffold` con barra superior y navegación inferior.
 * - Uso de `TabRow` o control similar para cambiar entre incidencias activas/resueltas.
 * - Botón flotante (`onAddIncidenciaClick`) para crear nuevas incidencias.
 *
 * Callbacks:
 * - `onBackClick()` → Regresa a la vista anterior.
 * - `onVehiculoClick()` → Abre selector de vehículo.
 * - `onIncidenciaClick(id)` → Navega al detalle de la incidencia.
 * - `onAddIncidenciaClick()` → Crea una nueva incidencia.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidenciasScreen(
    userId: String,
    token: String,
    onBackClick: () -> Unit,
    onAddIncidenciaClick: () -> Unit,
    navController: NavHostController
) {
    val viewModel: IncidenciaViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val vehiculos by homeViewModel.vehiculos.collectAsState()
    val incidencias = viewModel.incidencias
    val isLoading = viewModel.isLoading

    var vehiculoSeleccionado by remember { mutableStateOf<VehiculoDTO?>(null) }
    var tabSeleccionada by remember { mutableIntStateOf(0) }
    var expandedVehiculo by remember { mutableStateOf(false) }

    // Estados para modo selección
    var modoSeleccion by remember { mutableStateOf(false) }
    var incidenciasSeleccionadas by remember { mutableStateOf<Set<Int>>(emptySet()) }

    val currentRoute = navController.currentBackStackEntry?.destination?.route

    // Cargar vehículos e incidencias
    LaunchedEffect(Unit) {
        if (vehiculos.isEmpty()) {
            homeViewModel.fetchVehiculos(userId, token)
        }
        viewModel.obtenerIncidenciasUsuario(token)
    }

    // Establecer primer vehículo como seleccionado
    LaunchedEffect(vehiculos) {
        if (vehiculoSeleccionado == null && vehiculos.isNotEmpty()) {
            vehiculoSeleccionado = vehiculos.first()
        }
    }

    // Filtrar incidencias
    val incidenciasFiltradas = incidencias.filter { incidencia ->
        vehiculoSeleccionado?.let { vehiculo ->
            incidencia.vehiculoId.toString() == vehiculo.id
        } ?: true
    }

    val incidenciasActivas = incidenciasFiltradas.filter {
        it.estado !in listOf("RESUELTA", "CANCELADA")
    }
    val incidenciasResueltas = incidenciasFiltradas.filter {
        it.estado in listOf("RESUELTA", "CANCELADA")
    }

    val incidenciasAMostrar = if (tabSeleccionada == 0) incidenciasActivas else incidenciasResueltas

    Scaffold(
        bottomBar = {
            if (modoSeleccion) {
                // Barra de selección
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFEF4444),
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(65.dp)
                            .padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "${incidenciasSeleccionadas.size} seleccionadas",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            TextButton(
                                onClick = {
                                    incidenciasSeleccionadas = if (incidenciasSeleccionadas.size == incidenciasAMostrar.size) {
                                        emptySet()
                                    } else {
                                        incidenciasAMostrar.map { it.id }.toSet()
                                    }
                                },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = if (incidenciasSeleccionadas.size == incidenciasAMostrar.size)
                                        "Deseleccionar todas"
                                    else
                                        "Seleccionar todas",
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }

                        Button(
                            onClick = {
                                if (incidenciasSeleccionadas.isNotEmpty()) {
                                    scope.launch {
                                        val incidenciasParaExportar = incidencias.filter {
                                            it.id in incidenciasSeleccionadas
                                        }
                                        generarPDF(context, incidenciasParaExportar, vehiculoSeleccionado)
                                        modoSeleccion = false
                                        incidenciasSeleccionadas = emptySet()
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFFEF4444)
                            ),
                            shape = RoundedCornerShape(20.dp),
                            enabled = incidenciasSeleccionadas.isNotEmpty()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Exportar",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Exportar PDF",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
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
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
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
                        IconButton(onClick = {
                            if (modoSeleccion) {
                                modoSeleccion = false
                                incidenciasSeleccionadas = emptySet()
                            } else {
                                onBackClick()
                            }
                        }) {
                            Icon(
                                imageVector = if (modoSeleccion) Icons.Default.Close else Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = if (modoSeleccion) "Cancelar" else "Volver",
                                tint = Color.White
                            )
                        }
                        Text(
                            text = if (modoSeleccion) "Seleccionar incidencias" else "Incidencias",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.weight(1f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        if (!modoSeleccion) {
                            IconButton(onClick = {
                                modoSeleccion = true
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Exportar",
                                    tint = Color.White
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.width(48.dp))
                        }
                    }
                }

                if (isLoading && incidencias.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFFEF4444))
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 20.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(15.dp))

                            // Selector de vehículo
                            if (vehiculos.isNotEmpty() && vehiculoSeleccionado != null) {
                                ExposedDropdownMenuBox(
                                    expanded = expandedVehiculo,
                                    onExpandedChange = { expandedVehiculo = it }
                                ) {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp)
                                            .shadow(2.dp, RoundedCornerShape(25.dp))
                                            .menuAnchor()
                                            .clickable { expandedVehiculo = true },
                                        shape = RoundedCornerShape(25.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(horizontal = 16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            vehiculoSeleccionado?.let { vehiculo ->
                                                val tipoVehiculo = TipoVehiculo.valueOf(vehiculo.tipo.uppercase())

                                                Box(
                                                    modifier = Modifier
                                                        .size(30.dp)
                                                        .background(
                                                            tipoVehiculo.color.copy(alpha = 0.1f),
                                                            CircleShape
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        painter = painterResource(id = tipoVehiculo.iconRes),
                                                        contentDescription = tipoVehiculo.name,
                                                        tint = tipoVehiculo.color,
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
                                            }
                                            Icon(
                                                imageVector = Icons.Default.ArrowDropDown,
                                                contentDescription = "Cambiar vehículo",
                                                tint = Color(0xFF6B7280)
                                            )
                                        }
                                    }

                                    ExposedDropdownMenu(
                                        expanded = expandedVehiculo,
                                        onDismissRequest = { expandedVehiculo = false }
                                    ) {
                                        vehiculos.forEach { vehiculo ->
                                            DropdownMenuItem(
                                                text = { Text("${vehiculo.nombre} - ${vehiculo.matricula}") },
                                                onClick = {
                                                    vehiculoSeleccionado = vehiculo
                                                    expandedVehiculo = false
                                                    modoSeleccion = false
                                                    incidenciasSeleccionadas = emptySet()
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(15.dp))

                            // Tabs
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                TabButton(
                                    text = "Activas (${incidenciasActivas.size})",
                                    selected = tabSeleccionada == 0,
                                    onClick = {
                                        tabSeleccionada = 0
                                        incidenciasSeleccionadas = emptySet()
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                                TabButton(
                                    text = "Resueltas",
                                    selected = tabSeleccionada == 1,
                                    onClick = {
                                        tabSeleccionada = 1
                                        incidenciasSeleccionadas = emptySet()
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))
                        }

                        // Lista de incidencias
                        if (incidenciasAMostrar.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (tabSeleccionada == 0)
                                            "No hay incidencias activas"
                                        else
                                            "No hay incidencias resueltas",
                                        fontSize = 14.sp,
                                        color = Color(0xFF9CA3AF)
                                    )
                                }
                            }
                        } else {
                            items(incidenciasAMostrar) { incidencia ->
                                IncidenciaCardItemSeleccionable(
                                    incidencia = incidencia,
                                    modoSeleccion = modoSeleccion,
                                    seleccionada = incidencia.id in incidenciasSeleccionadas,
                                    onClick = {
                                        if (modoSeleccion) {
                                            incidenciasSeleccionadas = if (incidencia.id in incidenciasSeleccionadas) {
                                                incidenciasSeleccionadas - incidencia.id
                                            } else {
                                                incidenciasSeleccionadas + incidencia.id
                                            }
                                        } else {
                                            navController.navigate("detalle_incidencia/${incidencia.id}")
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.height(15.dp))
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(20.dp))

                            // Estadísticas
                            Text(
                                text = "Resumen",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F2937)
                            )

                            Spacer(modifier = Modifier.height(15.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                EstadisticaCard(
                                    numero = incidenciasActivas.size.toString(),
                                    texto = "Incidencias\nactivas",
                                    color = Color(0xFFEF4444),
                                    modifier = Modifier.weight(1f)
                                )
                                EstadisticaCard(
                                    numero = incidenciasResueltas.size.toString(),
                                    texto = "Incidencias\nresueltas",
                                    color = Color(0xFF10B981),
                                    modifier = Modifier.weight(1f)
                                )
                                EstadisticaCard(
                                    numero = incidenciasFiltradas.size.toString(),
                                    texto = "Total\nhistórico",
                                    color = Color(0xFF3B82F6),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(120.dp))
                        }
                    }
                }
            }

            // Botón flotante (solo visible cuando NO está en modo selección)
            if (!modoSeleccion) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(end = 20.dp, bottom = 20.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    FloatingActionButton(
                        onClick = onAddIncidenciaClick,
                        containerColor = Color(0xFFEF4444),
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Añadir incidencia",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun IncidenciaCardItemSeleccionable(
    incidencia: IncidenciaDetalle,
    modoSeleccion: Boolean,
    seleccionada: Boolean,
    onClick: () -> Unit
) {
    val prioridadColor = when (incidencia.prioridad.uppercase()) {
        "ALTA" -> Color(0xFFEF4444)
        "MEDIA" -> Color(0xFFF59E0B)
        else -> Color(0xFF10B981)
    }

    val prioridadTexto = when (incidencia.prioridad.uppercase()) {
        "ALTA" -> "ALTA PRIORIDAD"
        "MEDIA" -> "MEDIA PRIORIDAD"
        else -> "BAJA PRIORIDAD"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (seleccionada) Color(0xFFEF4444).copy(alpha = 0.08f) else Color.White
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Barra de color según prioridad
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .height(85.dp)
                    .background(prioridadColor)
            )

            // Contenido principal de la tarjeta
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Checkbox en modo selección
                if (modoSeleccion) {
                    Checkbox(
                        checked = seleccionada,
                        onCheckedChange = { onClick() },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFFEF4444),
                            uncheckedColor = Color(0xFF9CA3AF)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                // Icono
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            prioridadColor.copy(alpha = 0.1f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (incidencia.prioridad.uppercase()) {
                            "ALTA" -> Icons.Default.KeyboardArrowUp
                            "MEDIA" -> Icons.Default.KeyboardArrowDown
                            else -> Icons.Default.Info
                        },
                        contentDescription = null,
                        tint = prioridadColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Información principal
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = incidencia.titulo,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Text(
                        text = incidencia.fechaCreacion.formatToDateOnly(),
                        fontSize = 12.sp,
                        color = Color(0xFF9CA3AF)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Badge de prioridad
                Surface(
                    shape = RoundedCornerShape(11.dp),
                    color = prioridadColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = prioridadTexto,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = prioridadColor,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

// Función para generar el PDF
fun generarPDF(
    context: Context,
    incidencias: List<IncidenciaDetalle>,
    vehiculo: VehiculoDTO?
) {
    try {
        val pdfDocument = PdfDocument()
        val pageWidth = 595 // A4 width in points
        val pageHeight = 842 // A4 height in points

        var pageNumber = 1
        var yPosition = 80f
        val lineHeight = 20f
        val margin = 40f

        // Crear primera página
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        // Paint para diferentes estilos
        val titlePaint = Paint().apply {
            textSize = 24f
            //color = Color.rgb(239, 68, 68)
            color = android.graphics.Color.RED
            isFakeBoldText = true
        }

        val headerPaint = Paint().apply {
            textSize = 18f
            color = android.graphics.Color.BLACK
            isFakeBoldText = true
        }

        val normalPaint = Paint().apply {
            textSize = 12f
            color = android.graphics.Color.BLACK
        }

        val smallPaint = Paint().apply {
            textSize = 10f
            color = android.graphics.Color.GRAY
        }

        // PORTADA
        canvas.drawText("Reporte de Incidencias", margin, yPosition, titlePaint)
        yPosition += 40f

        val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        canvas.drawText("Fecha de exportación: $fechaActual", margin, yPosition, normalPaint)
        yPosition += 30f

        if (vehiculo != null) {
            canvas.drawText("Vehículo: ${vehiculo.nombre} - ${vehiculo.matricula}", margin, yPosition, normalPaint)
            yPosition += lineHeight
        }

        canvas.drawText("Total de incidencias: ${incidencias.size}", margin, yPosition, normalPaint)
        yPosition += 50f

        // ÍNDICE
        canvas.drawText("Índice de Incidencias", margin, yPosition, headerPaint)
        yPosition += 30f

        incidencias.forEachIndexed { index, incidencia ->
            if (yPosition > pageHeight - 100) {
                pdfDocument.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                yPosition = 80f
            }

            canvas.drawText("${index + 1}. ${incidencia.titulo}", margin + 20, yPosition, normalPaint)
            yPosition += lineHeight
        }

        // Nueva página para detalles
        pdfDocument.finishPage(page)
        pageNumber++

        // DETALLES DE CADA INCIDENCIA
        incidencias.forEachIndexed { index, incidencia ->
            pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            page = pdfDocument.startPage(pageInfo)
            canvas = page.canvas
            yPosition = 80f

            // Título de la incidencia
            canvas.drawText("Incidencia #${index + 1}", margin, yPosition, headerPaint)
            yPosition += 30f

            // Cuadro de estado con color
            val estadoColor = when (incidencia.estado.uppercase()) {
                "RESUELTA" -> android.graphics.Color.rgb(16, 185, 129)
                "CANCELADA" -> android.graphics.Color.rgb(107, 114, 128)
                "EN PROGRESO" -> android.graphics.Color.rgb(245, 158, 11)
                else -> android.graphics.Color.rgb(239, 68, 68)
            }

            val estadoPaint = Paint().apply {
                color = estadoColor
                style = Paint.Style.FILL
            }

            canvas.drawRoundRect(
                margin, yPosition - 15f, margin + 150f, yPosition + 5f,
                10f, 10f, estadoPaint
            )

            val estadoTextPaint = Paint().apply {
                textSize = 12f
                color = android.graphics.Color.WHITE
                isFakeBoldText = true
            }
            canvas.drawText(incidencia.estado, margin + 10, yPosition, estadoTextPaint)
            yPosition += 40f

            // Información principal
            canvas.drawText("Título:", margin, yPosition, headerPaint)
            yPosition += 20f
            canvas.drawText(incidencia.titulo, margin + 20, yPosition, normalPaint)
            yPosition += 30f

            canvas.drawText("Descripción:", margin, yPosition, headerPaint)
            yPosition += 20f

            // Dividir descripción en líneas
            val descripcionPalabras = incidencia.descripcion.split(" ")
            var lineaActual = ""
            val maxWidth = pageWidth - (2 * margin) - 20

            descripcionPalabras.forEach { palabra ->
                val testLine = if (lineaActual.isEmpty()) palabra else "$lineaActual $palabra"
                if (normalPaint.measureText(testLine) > maxWidth) {
                    canvas.drawText(lineaActual, margin + 20, yPosition, normalPaint)
                    yPosition += lineHeight
                    lineaActual = palabra
                } else {
                    lineaActual = testLine
                }
            }
            if (lineaActual.isNotEmpty()) {
                canvas.drawText(lineaActual, margin + 20, yPosition, normalPaint)
                yPosition += lineHeight
            }

            yPosition += 20f

            // Tipo y Prioridad
            canvas.drawText("Tipo: ${incidencia.tipo}", margin, yPosition, normalPaint)
            yPosition += lineHeight

            val prioridadColor = when (incidencia.prioridad.uppercase()) {
                "ALTA" -> android.graphics.Color.rgb(239, 68, 68)
                "MEDIA" -> android.graphics.Color.rgb(245, 158, 11)
                else -> android.graphics.Color.rgb(16, 185, 129)
            }

            val prioridadPaint = Paint().apply {
                textSize = 12f
                color = prioridadColor
                isFakeBoldText = true
            }
            canvas.drawText("Prioridad: ", margin, yPosition, normalPaint)
            canvas.drawText(incidencia.prioridad, margin + 80, yPosition, prioridadPaint)
            yPosition += lineHeight

            // Fecha
            canvas.drawText("Fecha de creación: ${incidencia.fechaCreacion.formatToDateOnly()}", margin, yPosition, normalPaint)
            yPosition += 40f

            // Línea separadora
            canvas.drawLine(margin, yPosition, pageWidth - margin, yPosition, smallPaint)

            // Pie de página
            canvas.drawText("Página $pageNumber",
                (pageWidth / 2 - 30).toFloat(), pageHeight - 30f, smallPaint)

            pdfDocument.finishPage(page)
            pageNumber++
        }

        // PÁGINA DE RESUMEN
        pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        page = pdfDocument.startPage(pageInfo)
        canvas = page.canvas
        yPosition = 80f

        canvas.drawText("Resumen Estadístico", margin, yPosition, headerPaint)
        yPosition += 40f

        val totalActivas = incidencias.count { it.estado !in listOf("RESUELTA", "CANCELADA") }
        val totalResueltas = incidencias.count { it.estado in listOf("RESUELTA", "CANCELADA") }
        val totalAlta = incidencias.count { it.prioridad.uppercase() == "ALTA" }
        val totalMedia = incidencias.count { it.prioridad.uppercase() == "MEDIA" }
        val totalBaja = incidencias.count { it.prioridad.uppercase() == "BAJA" }

        canvas.drawText("Total de incidencias: ${incidencias.size}", margin, yPosition, normalPaint)
        yPosition += 25f
        canvas.drawText("Incidencias activas: $totalActivas", margin, yPosition, normalPaint)
        yPosition += 25f
        canvas.drawText("Incidencias resueltas: $totalResueltas", margin, yPosition, normalPaint)
        yPosition += 40f

        canvas.drawText("Por prioridad:", margin, yPosition, normalPaint)
        yPosition += 25f
        canvas.drawText("  Alta: $totalAlta", margin + 20, yPosition, normalPaint)
        yPosition += 20f
        canvas.drawText("  Media: $totalMedia", margin + 20, yPosition, normalPaint)
        yPosition += 20f
        canvas.drawText("  Baja: $totalBaja", margin + 20, yPosition, normalPaint)

        canvas.drawText("Página $pageNumber",
            (pageWidth / 2 - 30).toFloat(), pageHeight - 30f, smallPaint)

        pdfDocument.finishPage(page)

        // Guardar el PDF
        val fileName = "Incidencias_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.pdf"
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, fileName)

        FileOutputStream(file).use { outputStream ->
            pdfDocument.writeTo(outputStream)
        }

        pdfDocument.close()

        Toast.makeText(
            context,
            "PDF guardado en Descargas: $fileName",
            Toast.LENGTH_LONG
        ).show()

    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(
            context,
            "Error al generar PDF: ${e.message}",
            Toast.LENGTH_LONG
        ).show()
    }
}

@Composable
fun IncidenciaCardItem(
    incidencia: IncidenciaDetalle,
    onClick: () -> Unit
) {
    val prioridadColor = when (incidencia.prioridad.uppercase()) {
        "ALTA" -> Color(0xFFEF4444)
        "MEDIA" -> Color(0xFFF59E0B)
        else -> Color(0xFF10B981)
    }

    val prioridadTexto = when (incidencia.prioridad.uppercase()) {
        "ALTA" -> "ALTA PRIORIDAD"
        "MEDIA" -> "MEDIA PRIORIDAD"
        else -> "BAJA PRIORIDAD"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Barra de color según prioridad: REDUCIMOS LA ALTURA
            Box(
                modifier = Modifier
                    .width(5.dp)
                    // Altura reducida de 110.dp a 85.dp para estrechar la tarjeta
                    .height(85.dp)
                    .background(prioridadColor)
            )

            // Contenido principal de la tarjeta
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    // Reducimos el padding vertical para estrechar la tarjeta
                    .padding(vertical = 12.dp, horizontal = 16.dp)
            ) {
                // PRIMERA FILA: Icono, Título/Vehículo, y Badge de Prioridad
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Icono
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                prioridadColor.copy(alpha = 0.1f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (incidencia.prioridad.uppercase()) {
                                "ALTA" -> Icons.Default.KeyboardArrowUp
                                "MEDIA" -> Icons.Default.KeyboardArrowDown
                                else -> Icons.Default.Info
                            },
                            contentDescription = null,
                            tint = prioridadColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Información principal (Título y Vehículo/ID)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = incidencia.titulo,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937),
                            maxLines = 1, // Añadido para evitar que el título ocupe mucho espacio
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                        Text(
                            text = incidencia.fechaCreacion.formatToDateOnly(),
                            fontSize = 12.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp)) // Espacio entre info y badge

                    // Badge de prioridad (MOVIDO AQUÍ A LA DERECHA)
                    Surface(
                        shape = RoundedCornerShape(11.dp),
                        color = prioridadColor.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = prioridadTexto,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = prioridadColor,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun TabButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(45.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Color(0xFFEF4444) else Color.White,
            contentColor = if (selected) Color.White else Color(0xFF6B7280)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (selected) 0.dp else 0.dp
        )
    ) {
        Text(
            text = text,
            fontSize = 15.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun EstadisticaCard(
    numero: String,
    texto: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(110.dp)
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
            Text(
                text = numero,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = texto,
                fontSize = 12.sp,
                color = Color(0xFF6B7280),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 14.sp
            )
        }
    }
}