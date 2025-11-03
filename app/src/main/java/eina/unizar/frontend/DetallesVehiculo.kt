package eina.unizar.frontend

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import eina.unizar.frontend.models.VehiculoDetalle
import eina.unizar.frontend.viewmodels.InvitacionViewModel
import eina.unizar.frontend.viewmodels.VehiculoViewModel


/**
 * Muestra el detalle completo de un vehículo.
 *
 * Incluye información como:
 * - Estado actual
 * - Datos técnicos
 * - Usuarios vinculados
 *
 * Ofrece acciones:
 * - Volver atrás (`onBackClick`)
 * - Ver en mapa (`onVerMapaClick`)
 * - Añadir usuario (`onAddUsuarioClick`)
 *
 * La UI usa `Surface` y `Row` para estructurar encabezado y contenido.
 */

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DetalleVehiculoScreen(
    vehiculo: VehiculoDetalle,
    onBackClick: () -> Unit,
    onVerMapaClick: () -> Unit,
    onAddUsuarioClick: () -> Unit,
    efectiveUserId: String?,
    efectiveToken: String?,
    navController: NavHostController
) {
    var showDialog by remember { mutableStateOf(false) }
    val invitacionViewModel = remember { InvitacionViewModel() }
    var email by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var showUserMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val viewModel: VehiculoViewModel = viewModel()
    var usuarioSeleccionado by remember { mutableStateOf<String?>(null) }
    Column(
        modifier = Modifier
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
                    text = vehiculo.nombre,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                 IconButton(onClick ={ expanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Más",
                        tint = Color.White
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Editar vehículo") },
                            onClick = {
                                expanded = false
                                navController.navigate("editar_vehiculo/${vehiculo.id}/$efectiveUserId/$efectiveToken")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Eliminar vehículo") },
                            onClick = {
                                expanded = false
                                viewModel.eliminarVehiculo(efectiveToken ?: "", vehiculo.id.toString())
                            }
                        )

                        // Observa el resultado y muestra el Toast
                        LaunchedEffect(viewModel.mensajeEliminacion, viewModel.errorEliminacion) {
                            viewModel.mensajeEliminacion?.let {
                                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                                navController.popBackStack() // Vuelve atrás tras eliminar
                                viewModel.mensajeEliminacion = null
                            }
                            viewModel.errorEliminacion?.let {
                                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                                viewModel.errorEliminacion = null
                            }
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Hero Section con icono
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
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
                            modifier = Modifier.size(70.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            // Badge de estado
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = vehiculo.estado.color
                ) {
                    Text(
                        text = "● ${vehiculo.estado.texto}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Información del Vehículo
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Información del Vehículo",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Grid de información
                    Row(modifier = Modifier.fillMaxWidth()) {
                        InfoItem(
                            label = "Matrícula",
                            value = vehiculo.matricula,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        InfoItem(
                            label = "Fabricante",
                            value = vehiculo.fabricante,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 15.dp),
                        color = Color(0xFFE5E7EB)
                    )

                    Row(modifier = Modifier.fillMaxWidth()) {
                        InfoItem(
                            label = "Modelo",
                            value = vehiculo.modelo,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        InfoItem(
                            label = "Año",
                            value = vehiculo.anio.toString(),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 15.dp),
                        color = Color(0xFFE5E7EB)
                    )

                    Row(modifier = Modifier.fillMaxWidth()) {
                        InfoItem(
                            label = "Combustible",
                            value = vehiculo.combustible,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        InfoItem(
                            label = "Depósito",
                            value = "${vehiculo.capacidadDeposito} litros",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 15.dp),
                        color = Color(0xFFE5E7EB)
                    )

                    InfoItem(
                        label = "Consumo medio",
                        value = "${vehiculo.consumoMedio} L/100km"
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Usuarios Vinculados
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Usuarios Vinculados",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                        IconButton(onClick = { showDialog = true},
                            modifier = Modifier.testTag("addUserButton")) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Añadir usuario",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    // Lista de usuarios
                    vehiculo.usuariosVinculados.forEach { nombre ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .combinedClickable(
                                    onClick = { /* nada */ },
                                    onLongClick = { usuarioSeleccionado = nombre }
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color.Cyan, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = nombre.split(" ")
                                        .filter { it.isNotEmpty() }
                                        .map { it.first().uppercaseChar() }
                                        .joinToString(""),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = nombre,
                                fontSize = 15.sp,
                                color = Color(0xFF1F2937)
                            )
                        }
                    }
                }
            }
            if (usuarioSeleccionado != null) {
                AlertDialog(
                    onDismissRequest = { usuarioSeleccionado = null },
                    title = { Text("Eliminar usuario") },
                    text = { Text("¿Eliminar a $usuarioSeleccionado del vehículo?") },
                    confirmButton = {
                        Button(onClick = {
                            usuarioSeleccionado?.let { nombre ->
                                if (efectiveToken != null) {
                                    viewModel.eliminarUsuarioVinculado(efectiveToken, vehiculo.id.toString(), nombre)
                                }
                            }
                            usuarioSeleccionado = null
                        }) { Text("Eliminar") }
                    },
                    dismissButton = {
                        TextButton(onClick = { usuarioSeleccionado = null }) { Text("Cancelar") }
                    }
                )
            }

            // Observa los mensajes de eliminación
            LaunchedEffect(viewModel.mensajeEliminacionUsuario, viewModel.errorEliminacionUsuario) {
                viewModel.mensajeEliminacionUsuario?.let {
                    Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                    viewModel.mensajeEliminacionUsuario = null
                }
                viewModel.errorEliminacionUsuario?.let {
                    Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                    viewModel.errorEliminacionUsuario = null
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botón Ver en el mapa
            Button(
                onClick = onVerMapaClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEF4444)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Mapa",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ver en el mapa",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false; email = ""; invitacionViewModel.mensaje = null },
                title = { Text("Invitar usuario") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email del invitado") }
                        )
                        val esExito = invitacionViewModel.mensaje == "Invitación generada exitosamente"
                        val colorMensaje = if (esExito) Color(0xFF22C55E) else Color(0xFFEF4444)

                        invitacionViewModel.mensaje?.let {
                            Spacer(Modifier.height(8.dp))
                            Text(it, color = colorMensaje)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (efectiveUserId != null && efectiveToken != null) {
                                invitacionViewModel.enviarInvitacion(
                                    vehiculo.id,
                                    efectiveUserId,
                                    email,
                                    efectiveToken
                                )
                            }
                        },
                        enabled = email.isNotBlank() && !invitacionViewModel.loading
                    ) {
                        Text(if (invitacionViewModel.loading) "Enviando..." else "Enviar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false; email = ""; invitacionViewModel.mensaje = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun InfoItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color(0xFF6B7280)
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1F2937)
        )
    }
}