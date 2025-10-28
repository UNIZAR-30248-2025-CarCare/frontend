package eina.unizar.frontend.viewmodels

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

class ViajesViewModel : ViewModel() {
    private val _viajes = MutableStateFlow<List<Viaje>>(emptyList())
    val viajes: StateFlow<List<Viaje>> = _viajes

    fun fetchViajes(vehiculoId: String, token: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.obtenerViajes("Bearer $token", vehiculoId)
                if (response.isSuccessful) {
                    _viajes.value = response.body()?.viajes ?: emptyList()
                } else {
                    _viajes.value = emptyList()
                }
            } catch (e: Exception) {
                _viajes.value = emptyList()
            }
        }
    }

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