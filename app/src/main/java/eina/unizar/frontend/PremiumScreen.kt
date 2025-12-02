package eina.unizar.frontend

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eina.unizar.frontend.models.DatosTarjeta
import eina.unizar.frontend.viewmodels.SuscripcionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumScreen(
    token: String,
    onBackClick: () -> Unit
) {
    val viewModel = remember { SuscripcionViewModel() }
    val estadoSuscripcion by viewModel.estadoSuscripcion.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var selectedPlan by remember { mutableStateOf("mensual") }
    var showPaymentForm by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.obtenerEstadoSuscripcion(token)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Hazlo Premium",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFEF4444)
                )
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFEF4444))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF5F5F5))
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Premium
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFFFD700),
                                    Color(0xFFFFA500)
                                )
                            )
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Premium",
                            tint = Color.White,
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Desbloquea todas las funciones",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Sin anuncios • Funciones exclusivas",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Beneficios
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = "Beneficios Premium",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    BeneficioItem(
                        icon = Icons.Default.Star, //Cambiar por Block
                        titulo = "Sin anuncios",
                        descripcion = "Disfruta de la app sin interrupciones"
                    )

                    BeneficioItem(
                        icon = Icons.Default.Star,
                        titulo = "Badge Premium",
                        descripcion = "Muestra tu corona dorada en el perfil"
                    )

                    BeneficioItem(
                        icon = Icons.Default.Star, // Cambiar por Speed
                        titulo = "Experiencia mejorada",
                        descripcion = "Acceso prioritario a nuevas funciones"
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Planes
                    Text(
                        text = "Elige tu plan",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    estadoSuscripcion?.let { estado ->
                        PlanCard(
                            tipo = "Mensual",
                            precio = estado.precios.mensual,
                            descripcion = "Facturación mensual",
                            selected = selectedPlan == "mensual",
                            onClick = { selectedPlan = "mensual" }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        PlanCard(
                            tipo = "Anual",
                            precio = estado.precios.anual,
                            descripcion = "Ahorra ${String.format("%.2f", (estado.precios.mensual * 12) - estado.precios.anual)}€ al año",
                            selected = selectedPlan == "anual",
                            popular = true,
                            onClick = { selectedPlan = "anual" }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón de suscripción
                    if (estadoSuscripcion?.esPremium == false) {
                        Button(
                            onClick = { showPaymentForm = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFD700)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Continuar con el pago",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F2937)
                            )
                        }
                    } else {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(2.dp, RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFD1FAE5)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF065F46)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        "¡Ya eres Premium!",
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF065F46)
                                    )
                                    Text(
                                        "Plan ${estadoSuscripcion?.tipoSuscripcion}",
                                        fontSize = 14.sp,
                                        color = Color(0xFF047857)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }

        // Modal de pago
        if (showPaymentForm) {
            PagoModal(
                token = token,
                tipoSuscripcion = selectedPlan,
                precio = if (selectedPlan == "mensual")
                    estadoSuscripcion?.precios?.mensual ?: 0.0
                else
                    estadoSuscripcion?.precios?.anual ?: 0.0,
                viewModel = viewModel,
                onDismiss = { showPaymentForm = false },
                onSuccess = {
                    showPaymentForm = false
                    // Aquí podrías mostrar un mensaje de éxito
                }
            )
        }
    }
}

@Composable
fun BeneficioItem(
    icon: ImageVector,
    titulo: String,
    descripcion: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFFFFD700).copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = titulo,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
            Text(
                text = descripcion,
                fontSize = 14.sp,
                color = Color(0xFF6B7280)
            )
        }
    }
}

@Composable
fun PlanCard(
    tipo: String,
    precio: Double,
    descripcion: String,
    selected: Boolean,
    popular: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(if (selected) 4.dp else 2.dp, RoundedCornerShape(16.dp))
            .border(
                width = if (selected) 2.dp else 0.dp,
                color = if (selected) Color(0xFFFFD700) else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Box {
            if (popular) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(
                            Color(0xFFFFD700),
                            RoundedCornerShape(bottomStart = 12.dp, topEnd = 16.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        "POPULAR",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = tipo,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                    Text(
                        text = descripcion,
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${String.format("%.2f", precio)}€",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700)
                    )
                    Text(
                        text = if (tipo == "Mensual") "/mes" else "/año",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PagoModal(
    token: String,
    tipoSuscripcion: String,
    precio: Double,
    viewModel: SuscripcionViewModel,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    var numeroTarjeta by remember { mutableStateOf("") }
    var fechaExpiracion by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Datos de pago",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    "${String.format("%.2f", precio)}€",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD700)
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = numeroTarjeta,
                    onValueChange = {
                        if (it.length <= 16 && it.all { char -> char.isDigit() }) {
                            numeroTarjeta = it
                        }
                    },
                    label = { Text("Número de tarjeta") },
                    placeholder = { Text("1234 5678 9012 3456") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = fechaExpiracion,
                        onValueChange = {
                            if (it.length <= 5) {
                                val cleaned = it.filter { char -> char.isDigit() }
                                fechaExpiracion = when {
                                    cleaned.length >= 2 -> "${cleaned.take(2)}/${cleaned.drop(2).take(2)}"
                                    else -> cleaned
                                }
                            }
                        },
                        label = { Text("MM/AA") },
                        placeholder = { Text("12/25") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = cvv,
                        onValueChange = {
                            if (it.length <= 3 && it.all { char -> char.isDigit() }) {
                                cvv = it
                            }
                        },
                        label = { Text("CVV") },
                        placeholder = { Text("123") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                if (showError) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage,
                        color = Color(0xFFEF4444),
                        fontSize = 14.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (numeroTarjeta.length == 16 &&
                        fechaExpiracion.length == 5 &&
                        cvv.length == 3) {

                        val datosTarjeta = DatosTarjeta(
                            numero = numeroTarjeta,
                            cvv = cvv,
                            fechaExpiracion = fechaExpiracion
                        )

                        viewModel.procesarPago(
                            token = token,
                            tipoSuscripcion = tipoSuscripcion,
                            datosTarjeta = datosTarjeta,
                            onSuccess = { onSuccess() },
                            onError = { error ->
                                showError = true
                                errorMessage = error
                            }
                        )
                    } else {
                        showError = true
                        errorMessage = "Por favor completa todos los campos correctamente"
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFD700)
                )
            ) {
                Text("Pagar", color = Color(0xFF1F2937), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color(0xFF6B7280))
            }
        }
    )
}