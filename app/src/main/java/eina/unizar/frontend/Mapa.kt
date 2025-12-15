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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import eina.unizar.frontend.R
import eina.unizar.frontend.models.toVehiculo
import eina.unizar.frontend.viewmodels.HomeViewModel
import eina.unizar.frontend.viewmodels.ParkingsViewModel
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import eina.unizar.frontend.IconPreferences
import eina.unizar.frontend.ImageUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Combinamos vehículos y parkings en una lista de items
sealed class MapItem {
    data class VehiculoItem(val vehiculo: eina.unizar.frontend.models.Vehiculo) : MapItem()
    data class ParkingItem(val parking: eina.unizar.frontend.models.Parking) : MapItem()
}

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

    // ViewModels para vehículos y parkings
    val vehiculosViewModel = remember { HomeViewModel() }
    val parkingsViewModel = remember { ParkingsViewModel() }
    
    val vehiculosDTO by vehiculosViewModel.vehiculos.collectAsState()
    val vehiculos = vehiculosDTO.map { it.toVehiculo() }
    
    val parkings by parkingsViewModel.parkings.collectAsState()

    vehiculos.forEach { vehiculo ->
        Log.d("Mapa", "Vehículo ${vehiculo.id} - tipo: ${vehiculo.tipo} - icono_url: ${vehiculo.icono_url}")
    }

    LaunchedEffect(Unit) {
        if (efectiveUserId != null && efectiveToken != null) {
            vehiculosViewModel.fetchVehiculos(efectiveUserId, efectiveToken)
            parkingsViewModel.fetchParkings(efectiveUserId, efectiveToken)
        }
    }

    Log.d("Mapa", "Vehículos obtenidos: ${vehiculos.size}, Parkings obtenidos: ${parkings.size}")

    
    val mapItems = remember(vehiculos, parkings) {
        vehiculos.map { MapItem.VehiculoItem(it) } + parkings.map { MapItem.ParkingItem(it) }
    }

    var selectedIndex by remember { mutableIntStateOf(0) }
    val selectedItem = mapItems.getOrNull(selectedIndex)
    var mapLibreMap by remember { mutableStateOf<MapLibreMap?>(null) }
    var showTutorialSnackbar by remember { mutableStateOf(true) }

    LaunchedEffect(showTutorialSnackbar) {
        if (showTutorialSnackbar && mapItems.size > 1) {
            kotlinx.coroutines.delay(5000)
            showTutorialSnackbar = false
        }
    }

    // Función para centrar el mapa en el item seleccionado
    fun centerMapOnItem() {
        selectedItem?.let { item ->
            val latLng = when (item) {
                is MapItem.VehiculoItem -> item.vehiculo.ubicacion_actual?.let { 
                    LatLng(it.latitud, it.longitud) 
                }
                is MapItem.ParkingItem -> LatLng(item.parking.ubicacion.lat, item.parking.ubicacion.lng)
            }
            
            latLng?.let { position ->
                mapLibreMap?.animateCamera(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.Builder()
                            .target(position)
                            .zoom(16.0)
                            .build()
                    ),
                    1000
                )
            }
        }
    }

    LaunchedEffect(selectedIndex) {
        if (mapItems.isNotEmpty()) {
            centerMapOnItem()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mapa de Vehículos y Parkings", color = Color.White, fontSize = 18.sp) },
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
                    MapLibre.getInstance(factoryContext, "vzU3m7mUFKYAvOtFHKIq", WellKnownTileServer.MapTiler)
                    val mapView = MapView(factoryContext).apply {
                        onCreate(Bundle())
                        getMapAsync { map ->
                            mapLibreMap = map
                            map.setStyle("https://api.maptiler.com/maps/streets/style.json?key=vzU3m7mUFKYAvOtFHKIq") {
                                val iconFactory = org.maplibre.android.annotations.IconFactory.getInstance(context)

                                // Centrar en el primer item al iniciar
                                if (mapItems.isNotEmpty()) {
                                    val firstPosition = when (val item = mapItems[0]) {
                                        is MapItem.VehiculoItem -> item.vehiculo.ubicacion_actual?.let {
                                            LatLng(it.latitud, it.longitud)
                                        }
                                        is MapItem.ParkingItem -> LatLng(item.parking.ubicacion.lat, item.parking.ubicacion.lng)
                                    }
                                    
                                    firstPosition?.let {
                                        map.cameraPosition = CameraPosition.Builder()
                                            .target(it)
                                            .zoom(14.0)
                                            .build()
                                    }
                                }
                            }
                        }
                    }
                    mapView
                },
                update = { mapView ->
                    mapView.getMapAsync { map ->
                        if (mapItems.isNotEmpty()) {
                            Log.d("Mapa", "Actualizando marcadores. Items: ${mapItems.size}")

                            map.clear()

                            try {
                                val iconFactory = org.maplibre.android.annotations.IconFactory.getInstance(mapView.context)
                                val iconPreferences = IconPreferences(mapView.context)

                                val customSize = 100 // tamaño para icono personalizado
                                val defaultSize = 50 // tamaño para icono por defecto
                                val parkingSize = 50 // tamaño para icono de parking

                                // Añadir marcadores para vehículos
                                vehiculos.forEach { vehiculo ->
                                    vehiculo.ubicacion_actual?.let { ubicacion ->
                                        if (!vehiculo.icono_url.isNullOrBlank()) {
                                            Log.d("Mapa", "Intentando descargar icono personalizado para vehículo ${vehiculo.id}: ${vehiculo.icono_url}")
                                            val urlCompleta = if (vehiculo.icono_url.startsWith("/")) {
                                                "http://10.0.2.2:3000${vehiculo.icono_url}"
                                            } else {
                                                vehiculo.icono_url
                                            }
                                            CoroutineScope(Dispatchers.IO).launch {
                                                try {
                                                    val bmp = android.graphics.BitmapFactory.decodeStream(java.net.URL(urlCompleta).openStream())
                                                    withContext(Dispatchers.Main) {
                                                        val circularBitmap = ImageUtils.createCircularIcon(bmp, customSize)
                                                        val icon = iconFactory.fromBitmap(circularBitmap)
                                                        map.addMarker(
                                                            org.maplibre.android.annotations.MarkerOptions()
                                                                .position(LatLng(ubicacion.latitud, ubicacion.longitud))
                                                                .title(vehiculo.nombre)
                                                                .snippet("${vehiculo.matricula} - ${vehiculo.tipo}")
                                                                .icon(icon)
                                                        )
                                                    }
                                                } catch (e: Exception) {
                                                    Log.d("Mapa", "Error al descargar icono personalizado: ${e.message}")
                                                    withContext(Dispatchers.Main) {
                                                        val defaultIconResId = iconPreferences.getDefaultIconForTipo(vehiculo.tipo)
                                                        val markerIcon = iconFactory.fromResource(defaultIconResId)
                                                        val icon = iconFactory.fromBitmap(
                                                            android.graphics.Bitmap.createScaledBitmap(
                                                                markerIcon.bitmap,
                                                                defaultSize,
                                                                defaultSize,
                                                                false
                                                            )
                                                        )
                                                        map.addMarker(
                                                            org.maplibre.android.annotations.MarkerOptions()
                                                                .position(LatLng(ubicacion.latitud, ubicacion.longitud))
                                                                .title(vehiculo.nombre)
                                                                .snippet("${vehiculo.matricula} - ${vehiculo.tipo}")
                                                                .icon(icon)
                                                        )
                                                    }
                                                }
                                            }
                                        } else {
                                            Log.d("Mapa", "Usando icono por defecto para vehículo ${vehiculo.id} (tipo: ${vehiculo.tipo})")
                                            val defaultIconResId = iconPreferences.getDefaultIconForTipo(vehiculo.tipo)
                                            val markerIcon = iconFactory.fromResource(defaultIconResId)
                                            val icon = iconFactory.fromBitmap(
                                                android.graphics.Bitmap.createScaledBitmap(
                                                    markerIcon.bitmap,
                                                    defaultSize,
                                                    defaultSize,
                                                    false
                                                )
                                            )
                                            map.addMarker(
                                                org.maplibre.android.annotations.MarkerOptions()
                                                    .position(LatLng(ubicacion.latitud, ubicacion.longitud))
                                                    .title(vehiculo.nombre)
                                                    .snippet("${vehiculo.matricula} - ${vehiculo.tipo}")
                                                    .icon(icon)
                                            )
                                        }
                                    }
                                }

                                // Añadir marcadores para parkings
                                parkings.forEach { parking ->
                                    val parkingIcon = iconFactory.fromResource(R.drawable.ic_parking)
                                    val scaledIcon = iconFactory.fromBitmap(
                                        android.graphics.Bitmap.createScaledBitmap(
                                            parkingIcon.bitmap,
                                            parkingSize,
                                            parkingSize,
                                            false
                                        )
                                    )

                                    map.addMarker(
                                        org.maplibre.android.annotations.MarkerOptions()
                                            .position(LatLng(parking.ubicacion.lat, parking.ubicacion.lng))
                                            .title(parking.nombre)
                                            .snippet(parking.notas ?: "Parking")
                                            .icon(scaledIcon)
                                    )
                                }

                                // Centrar en el item seleccionado
                                selectedItem?.let { item ->
                                    val position = when (item) {
                                        is MapItem.VehiculoItem -> item.vehiculo.ubicacion_actual?.let {
                                            LatLng(it.latitud, it.longitud)
                                        }
                                        is MapItem.ParkingItem -> LatLng(item.parking.ubicacion.lat, item.parking.ubicacion.lng)
                                    }
                                    
                                    position?.let {
                                        map.animateCamera(
                                            CameraUpdateFactory.newCameraPosition(
                                                CameraPosition.Builder()
                                                    .target(it)
                                                    .zoom(15.0)
                                                    .build()
                                            ),
                                            500
                                        )
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("Mapa", "Error al añadir marcadores: ${e.message}", e)
                            }
                        }
                    }
                }
            )

            // Pager con las tarjetas
            val pagerState = rememberPagerState(pageCount = { mapItems.size })

            LaunchedEffect(pagerState.currentPage) {
                selectedIndex = pagerState.currentPage
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                contentPadding = PaddingValues(horizontal = 48.dp),
                pageSpacing = 80.dp,
                key = { it }
            ) { page ->
                val item = mapItems[page]
                Card(
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        when (item) {
                            is MapItem.VehiculoItem -> {
                                val vehiculo = item.vehiculo
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = "Vehículo",
                                    tint = Color(0xFFE53935),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        vehiculo.nombre,
                                        color = Color.Black,
                                        fontSize = 18.sp,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        vehiculo.matricula,
                                        color = Color.Gray,
                                        fontSize = 14.sp,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    vehiculo.ubicacion_actual?.let { ubicacion ->
                                        Text(
                                            "Lat: ${String.format("%.4f", ubicacion.latitud)}, Lon: ${String.format("%.4f", ubicacion.longitud)}",
                                            color = Color.Gray,
                                            fontSize = 12.sp,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                            is MapItem.ParkingItem -> {
                                val parking = item.parking
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_parking),
                                    contentDescription = "Parking",
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        parking.nombre,
                                        color = Color.Black,
                                        fontSize = 18.sp,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        parking.notas ?: "Sin notas",
                                        color = Color.Gray,
                                        fontSize = 14.sp,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 1
                                    )
                                    Text(
                                        "Lat: ${String.format("%.4f", parking.ubicacion.lat)}, Lon: ${String.format("%.4f", parking.ubicacion.lng)}",
                                        color = Color.Gray,
                                        fontSize = 12.sp,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                        Button(
                            onClick = { centerMapOnItem() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text("IR", color = Color.White)
                        }
                    }
                }
            }

            // Snackbar tutorial
            if (showTutorialSnackbar && mapItems.size > 1) {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp),
                    containerColor = Color(0xFF2D2D2D),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    action = {
                        TextButton(onClick = { showTutorialSnackbar = false }) {
                            Text("OK", color = Color(0xFFE53935))
                        }
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.carcare_logo),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "Desliza para ver vehículos y parkings",
                            style = MaterialTheme.typography.bodyMedium
                        )
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