package eina.unizar.frontend

import android.os.Bundle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView

@Composable
fun SelectorUbicacionMapLibreDialog(
    onDismiss: () -> Unit,
    onUbicacionSeleccionada: (lat: Double, lng: Double) -> Unit
) {
    var selectedLatLng by remember { mutableStateOf<LatLng?>(null) }
    var mapLibreMap by remember { mutableStateOf<MapLibreMap?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column(Modifier.padding(16.dp)) {
                AndroidView(
                    modifier = Modifier
                        .height(350.dp)
                        .fillMaxWidth(),
                    factory = { context ->
                        MapLibre.getInstance(context, "vzU3m7mUFKYAvOtFHKIq", WellKnownTileServer.MapTiler)
                        MapView(context).apply {
                            onCreate(Bundle())
                            getMapAsync { map ->
                                map.setStyle("https://api.maptiler.com/maps/streets/style.json?key=vzU3m7mUFKYAvOtFHKIq")
                                mapLibreMap = map
                                map.addOnMapClickListener { point: LatLng ->
                                    selectedLatLng = point
                                    map.clear()
                                    map.addMarker(
                                        org.maplibre.android.annotations.MarkerOptions()
                                            .position(point)
                                            .title("Destino")
                                    )
                                    true
                                }
                            }
                        }
                    }
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        selectedLatLng?.let { onUbicacionSeleccionada(it.latitude, it.longitude) }
                        onDismiss()
                    },
                    enabled = selectedLatLng != null,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Seleccionar ubicación")
                }
            }
        }
    }
}