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
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary
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
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Text(
                    text = vehiculo.nombre,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                IconButton(onClick ={ expanded = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Más",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Editar vehículo",
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {
                                expanded = false
                                navController.navigate("editar_vehiculo/${vehiculo.id}/$efectiveUserId/$efectiveToken")
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Eliminar vehículo",
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
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
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                        color = MaterialTheme.colorScheme.onSurface
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
                        color = MaterialTheme.colorScheme.outline
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
                        color = MaterialTheme.colorScheme.outline
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
                        color = MaterialTheme.colorScheme.outline
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
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        IconButton(onClick = { showDialog = true},
                            modifier = Modifier.testTag("addUserButton")) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Añadir usuario",
                                tint = MaterialTheme.colorScheme.primary,
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
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
            if (usuarioSeleccionado != null) {
                AlertDialog(
                    onDismissRequest = { usuarioSeleccionado = null },
                    title = {
                        Text(
                            "Eliminar usuario",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    text = {
                        Text(
                            "¿Eliminar a $usuarioSeleccionado del vehículo?",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    confirmButton = {
                        Button(onClick = {
                            usuarioSeleccionado?.let { nombre ->
                                if (efectiveToken != null) {
                                    viewModel.eliminarUsuarioVinculado(efectiveToken, vehiculo.id.toString(), nombre)
                                }
                            }
                            usuarioSeleccionado = null
                        },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(
                                "Eliminar",
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { usuarioSeleccionado = null }) {
                            Text(
                                "Cancelar",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    textContentColor = MaterialTheme.colorScheme.onSurface
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

            // 1. Lógica de habilitación y texto
            val isActivo = vehiculo.estado.texto == "Activo"

            // 💡 LOGS DE DIAGNÓSTICO
            android.util.Log.d("VEHICULO_ESTADO", "Estado del vehículo (Texto): ${vehiculo.estado.texto}")
            android.util.Log.d("VEHICULO_ESTADO", "Usuario Activo ID (Vehículo): ${vehiculo.usuarioActivoId} | Tipo: ${vehiculo.usuarioActivoId?.javaClass?.simpleName}")
            android.util.Log.d("VEHICULO_ESTADO", "Usuario Efectivo ID (App): $efectiveUserId | Tipo: ${efectiveUserId?.javaClass?.simpleName}")


            val canLiberar = isActivo && (vehiculo.usuarioActivoId == efectiveUserId)

            // 💡 LOG DE RESULTADO
            android.util.Log.d("VEHICULO_ESTADO", "Resultado de la comparación (usuarioActivoId == efectiveUserId): ${vehiculo.usuarioActivoId == efectiveUserId}")
            android.util.Log.d("VEHICULO_ESTADO", "Resultado final de canLiberar: $canLiberar")


            val canActivar = !isActivo


            val nuevoEstado = if (isActivo) "Inactivo" else "Activo"
            val isButtonEnabled = canActivar || canLiberar

            // --- BOTÓN PRINCIPAL DE ACTIVAR / LIBERAR ---
            Button(
                onClick = {
                    if (efectiveToken != null && isButtonEnabled) {
                        viewModel.actualizarEstadoVehiculo(
                            token = efectiveToken,
                            vehiculoId = vehiculo.id.toString(),
                            nuevoEstado = nuevoEstado
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                // ... estilos ...
                enabled = isButtonEnabled
            ) {
                // ... icono y texto ...
                Text(text = if (isActivo) "LIBERAR VEHÍCULO" else "ACTIVAR VEHÍCULO")
            }

            // Mensaje de bloqueo
            if (isActivo && !canLiberar) {
                Text(
                    text = "Este vehículo está activo y solo puede ser liberado por el usuario que lo activó.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- BOTÓN DE MANTENIMIENTO ---
            // Se permite si NO está activo (si está libre) O si el usuario es el propietario (puedes ajustar esta regla)
            if (canActivar) {
                Button(
                    onClick = {
                        if (efectiveToken != null) {
                            viewModel.actualizarEstadoVehiculo(
                                token = efectiveToken,
                                vehiculoId = vehiculo.id.toString(),
                                nuevoEstado = "Mantenimiento"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                ) {
                    Text(text = "MARCAR EN MANTENIMIENTO")
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Observación del resultado del cambio de estado
            LaunchedEffect(viewModel.mensajeCambioEstado, viewModel.errorCambioEstado) {
                viewModel.mensajeCambioEstado?.let {
                    Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                    viewModel.mensajeCambioEstado = null
                    // **RECARGAR DATOS**
                    // Debes llamar a la función que recarga 'vehiculo' en el ViewModel
                }
                viewModel.errorCambioEstado?.let {
                    Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                    viewModel.errorCambioEstado = null
                }
            }

            // Botón Ver en el mapa
            Button(
                onClick = onVerMapaClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Mapa",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ver en el mapa",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            Button(
                onClick = {
                    navController.navigate(
                        "personalizar_icono/${vehiculo.id}/${vehiculo.nombre}/${vehiculo.tipo.name}"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Personalizar Ícono",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSecondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Personalizar Ícono del Mapa",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false; email = ""; invitacionViewModel.mensaje = null },
                title = {
                    Text(
                        "Invitar usuario",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                text = {
                    Column {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = {
                                Text(
                                    "Email del invitado",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            )
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
                        enabled = email.isNotBlank() && !invitacionViewModel.loading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            if (invitacionViewModel.loading) "Enviando..." else "Enviar",
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false; email = ""; invitacionViewModel.mensaje = null }) {
                        Text(
                            "Cancelar",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                textContentColor = MaterialTheme.colorScheme.onSurface
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
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}