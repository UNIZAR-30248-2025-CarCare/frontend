@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.carcare.ui

import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import eina.unizar.frontend.R
import eina.unizar.frontend.models.toVehiculo
import eina.unizar.frontend.viewmodels.HomeViewModel
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView

@Composable
fun UbicacionVehiculoScreen(
    onBackClick: () -> Unit = {},
    onInicioClick: () -> Unit = {},
    onMapaClick: () -> Unit = {},
    onReservasClick: () -> Unit = {},
    navController: NavHostController,
    efectiveUserId: String?,
    efectiveToken: String?
) {
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    val currentRoute = navController.currentBackStackEntry?.destination?.route

    // 1. Obtén los vehículos (ejemplo usando ViewModel)
    val viewModel = remember { HomeViewModel() }
    val vehiculosDTO by viewModel.vehiculos.collectAsState()
    val vehiculos = vehiculosDTO.map { it.toVehiculo() }

    LaunchedEffect(Unit) {
        if (efectiveUserId != null) {
            if (efectiveToken != null) {
                viewModel.fetchVehiculos(efectiveUserId, efectiveToken)
            }
        } // Asegúrate de tener este método en tu ViewModel
    }

    Log.d("Mapa", "Vehículos obtenidos: ${vehiculos.size}")

    // 2. Estado para el vehículo seleccionado
    var selectedIndex by remember { mutableStateOf(0) }
    val selectedVehiculo = vehiculos.getOrNull(selectedIndex)

    // 3. Muestra los marcadores en el mapa y centra en el seleccionado
    // (en el factory y update del MapView, recorre vehiculos y añade marcadores)

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
                            map.setStyle("https://api.maptiler.com/maps/streets/style.json?key=vzU3m7mUFKYAvOtFHKIq") {
                                vehiculos.forEach { vehiculo ->
                                    vehiculo.ubicacion_actual?.let { ubicacion ->
                                        map.addMarker(
                                            org.maplibre.android.annotations.MarkerOptions()
                                                .position(LatLng(ubicacion.latitud, ubicacion.longitud))
                                                .title(vehiculo.nombre)
                                                .icon(org.maplibre.android.annotations.IconFactory.getInstance(context)
                                                    .fromResource(R.drawable.ic_marker))
                                        )
                                    }
                                }
                            }
                        }
                    }
                    mapView
                },
                update = { mapView ->
                    selectedVehiculo?.let {
                        mapView.getMapAsync { map ->
                            val pos = it.ubicacion_actual?.let { it1 -> LatLng(it1.latitud, it1.longitud) }
                            map.cameraPosition = CameraPosition.Builder()
                                .target(pos)
                                .zoom(14.0)
                                .build()
                        }
                    }
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

            val pagerState = rememberPagerState(pageCount = { vehiculos.size })
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                contentPadding = PaddingValues(horizontal = 32.dp),
                key = { it }
            ) { page ->
                val vehiculo = vehiculos[page]
                Card(
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
                            Text(vehiculo.nombre, color = Color.Black, fontSize = 18.sp)
                            Text(vehiculo.matricula, color = Color.Gray, fontSize = 14.sp)
                        }
                        Button(
                            onClick = { /* abrir en maps */ },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text("IR", color = Color.White)
                        }
                    }
                }
                // Al cambiar de página, centra el mapa en la ubicación del vehículo
                LaunchedEffect(page) {
                    // Centra el mapa en vehiculo.ubicacion_actual
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