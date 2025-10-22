@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.carcare.ui

import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView


/**
 * Pantalla que muestra la ubicación actual de un vehículo en un mapa interactivo.
 *
 * - Usa MapLibre con MapTiler como proveedor de mapas.
 * - Posiciona el mapa en las coordenadas del vehículo.
 * - Gestiona el ciclo de vida del mapa con el `LifecycleOwner`.
 *
 * Elementos principales:
 * - `TopAppBar` con botón de retroceso.
 * - `BottomNavigationBar` para moverse entre secciones (Inicio, Mapa, Reservas).
 * - `FloatingActionButton` para centrar el mapa.
 * - `Card` inferior con información del vehículo (modelo, matrícula, acción “IR”).
 *
 * Callbacks:
 * - `onBackClick()` → Vuelve a la pantalla anterior.
 * - `onInicioClick()`, `onMapaClick()`, `onReservasClick()` → Navegación inferior.
 */
@Composable
fun UbicacionVehiculoScreen(
    onBackClick: () -> Unit = {},
    onInicioClick: () -> Unit = {},
    onMapaClick: () -> Unit = {},
    onReservasClick: () -> Unit = {},
    navController: NavHostController
) {
    val vehiclePosition = LatLng(41.6833, -0.8880)
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    val currentRoute = navController.currentBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ubicación del Vehículo", color = Color.White, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFE53935))
            )
        },
        bottomBar = {
            eina.unizar.frontend.BottomNavigationBar(
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
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { factoryContext ->
                    // Initialize MapView normally
                    MapLibre.getInstance(factoryContext, "vzU3m7mUFKYAvOtFHKIq", WellKnownTileServer.MapTiler)
                    val mapView = MapView(factoryContext).apply {
                        onCreate(Bundle())
                        getMapAsync { map ->
                            map.setStyle("https://api.maptiler.com/maps/streets/style.json?key=vzU3m7mUFKYAvOtFHKIq") { _ ->
                                map.cameraPosition = CameraPosition.Builder()
                                    .target(vehiclePosition)
                                    .zoom(14.0)
                                    .build()
                            }
                        }

                    }
                    mapView
                },
                update = { mapView ->
                    lifecycleOwner.lifecycle.addObserver(
                        androidx.lifecycle.LifecycleEventObserver { _, event ->
                            when (event) {
                                androidx.lifecycle.Lifecycle.Event.ON_START -> mapView.onStart()
                                androidx.lifecycle.Lifecycle.Event.ON_RESUME -> mapView.onResume()
                                androidx.lifecycle.Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                                androidx.lifecycle.Lifecycle.Event.ON_STOP -> mapView.onStop()
                                androidx.lifecycle.Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                                else -> {}
                            }
                        }
                    )
                }
            )


            FloatingActionButton(
                onClick = { /* center map in future versions */ },
                containerColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 120.dp)
            ) {
                Icon(Icons.Default.Place, contentDescription = "Centrar mapa", tint = Color(0xFFE53935))
            }

            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "Vehículo",
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Seat Ibiza", color = Color.Black, fontSize = 18.sp)
                        Text("1234 ABC", color = Color.Gray, fontSize = 14.sp)
                    }
                    Button(
                        onClick = { /* open external maps app */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("IR", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    onInicioClick: () -> Unit,
    onMapaClick: () -> Unit,
    onReservasClick: () -> Unit
) {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            selected = false,
            onClick = onInicioClick,
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") }
        )
        NavigationBarItem(
            selected = true,
            onClick = onMapaClick,
            icon = { Icon(Icons.Default.Place, contentDescription = "Mapa") },
            label = { Text("Mapa") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = Color(0xFFE53935),
                indicatorColor = Color(0xFFE53935)
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = onReservasClick,
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Reservas") },
            label = { Text("Reservas") }
        )
    }
}