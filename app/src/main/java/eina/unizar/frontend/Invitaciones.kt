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
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
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
                        text = "Invitaciones",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.weight(1f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(48.dp))
                }
            }
            if (invitacionViewModel.loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(invitacionViewModel.invitaciones) { invitacion ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                                    Text(
                                        text = invitacion.Vehiculo.nombre,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = invitacion.Vehiculo.matricula,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
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