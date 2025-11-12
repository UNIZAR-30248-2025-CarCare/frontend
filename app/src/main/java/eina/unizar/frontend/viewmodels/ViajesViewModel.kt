package eina.unizar.frontend.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eina.unizar.frontend.models.NuevoViajeData
import eina.unizar.frontend.models.Viaje
import eina.unizar.frontend.models.ViajesResponse
import eina.unizar.frontend.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.serialization.json.Json

/**
 * ViewModel para gestionar los viajes de vehículos.
 *
 * Responsabilidades:
 * - Obtener la lista de viajes de un vehículo.
 * - Crear nuevos viajes.
 */
class ViajesViewModel : ViewModel() {
    private val _viajes = MutableStateFlow<List<Viaje>>(emptyList())
    val viajes: StateFlow<List<Viaje>> = _viajes

    /**
     * Obtiene la lista de viajes para un vehículo específico.
     *
     * @param vehiculoId ID del vehículo cuyos viajes se desean obtener.
     * @param token Token de autenticación JWT.
     */
    fun fetchViajes(vehiculoId: String, token: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.obtenerViajes("Bearer $token", vehiculoId)
                if (response.isSuccessful) {
                     val viajesRecibidos = response.body()?.viajes ?: emptyList()
                    Log.d("ViajesViewModel", "Viajes recibidos: ${viajesRecibidos.size}")
                    Log.d("ViajesViewModel", "Contenido viajes: $viajesRecibidos")
                    _viajes.value = viajesRecibidos
                } else {
                    _viajes.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("ViajesViewModel", "Excepción al obtener viajes: ${e.message}", e)
                _viajes.value = emptyList()
            }
        }
    }

    /**
     * Crea un nuevo viaje para un vehículo.
     *
     * @param token Token de autenticación JWT.
     * @param viaje Datos del nuevo viaje a crear.
     * @param onResult Callback que recibe un String con el mensaje de error en caso de fallo,
     *                 o null si la operación fue exitosa.
     */
    fun crearViaje(
        token: String,
        viaje: NuevoViajeData,
        onResult: (String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.crearViaje("Bearer $token", viaje)
                if (response.isSuccessful && response.code() == 201) {
                    // Refresca la lista de viajes
                    //fetchViajes(vehiculoId, token)
                    onResult(null) // null indica éxito
                } else {
                    val errorBody = response.errorBody()?.string()
                    onResult(errorBody ?: "Error: ${response.code()}")
                }
            } catch (e: Exception) {
                onResult("Error: ${e.message}")
            }
        }
    }
}