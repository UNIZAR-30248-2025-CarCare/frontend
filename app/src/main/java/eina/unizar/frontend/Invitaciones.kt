package eina.unizar.frontend

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import eina.unizar.frontend.viewmodels.InvitacionViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@Composable
fun InvitacionesScreen(
    usuarioId: String,
    token: String,
    navController: NavHostController,
    currentRoute: String,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val invitacionViewModel = remember { InvitacionViewModel() }
    LaunchedEffect(Unit) {
        invitacionViewModel.fetchInvitaciones(usuarioId, token)
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
                        text = "Invitaciones",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.weight(1f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(48.dp))
                }
            }
            if (invitacionViewModel.loading) {
                CircularProgressIndicator()
            } else {
                LazyColumn {
                    items(invitacionViewModel.invitaciones) { invitacion ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_coche),
                                    contentDescription = "Vehículo",
                                    tint = Color(0xFF3B82F6),
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(invitacion.Vehiculo.nombre, fontWeight = FontWeight.Bold)
                                    Text(invitacion.Vehiculo.matricula, color = Color.Gray)
                                }
                                IconButton(
                                    onClick = {
                                        invitacionViewModel.aceptarInvitacion(
                                            invitacion.codigo,
                                            token
                                        ) { mensaje ->
                                            Toast.makeText(context, mensaje ?: "Error", Toast.LENGTH_SHORT).show()
                                            invitacionViewModel.fetchInvitaciones(usuarioId, token)
                                        }
                                    },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF22C55E))
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_tick),
                                        contentDescription = "Aceptar",
                                        tint = Color.White
                                    )
                                }
                                Spacer(Modifier.width(16.dp))
                                IconButton(
                                    onClick = {
                                        invitacionViewModel.rechazarInvitacion(
                                            invitacion.id,
                                            usuarioId.toInt(),
                                            token
                                        ) { mensaje ->
                                            Toast.makeText(context, mensaje ?: "Error", Toast.LENGTH_SHORT).show()
                                            invitacionViewModel.fetchInvitaciones(usuarioId, token)
                                        }
                                    },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEF4444))
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_cross),
                                        contentDescription = "Rechazar",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}