package eina.unizar.frontend.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eina.unizar.frontend.models.*
import eina.unizar.frontend.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SuscripcionViewModel : ViewModel() {

    private val _estadoSuscripcion = MutableStateFlow<EstadoSuscripcionResponse?>(null)
    val estadoSuscripcion: StateFlow<EstadoSuscripcionResponse?> = _estadoSuscripcion

    private val _verificacionAnuncio = MutableStateFlow<VerificarAnuncioResponse?>(null)
    val verificacionAnuncio: StateFlow<VerificarAnuncioResponse?> = _verificacionAnuncio

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun obtenerEstadoSuscripcion(token: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val response = RetrofitClient.instance.obtenerEstadoSuscripcion("Bearer $token")

                if (response.isSuccessful) {
                    _estadoSuscripcion.value = response.body()
                    Log.d("SuscripcionViewModel", "Estado de suscripción obtenido: ${response.body()}")
                } else {
                    _error.value = "Error al obtener estado de suscripción"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error desconocido"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun verificarAnuncio(token: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.verificarAnuncio("Bearer $token")

                if (response.isSuccessful) {
                    _verificacionAnuncio.value = response.body()
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun procesarPago(
        token: String,
        tipoSuscripcion: String,
        datosTarjeta: DatosTarjeta,
        onSuccess: (PagoResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val request = ProcesarPagoRequest(
                    tipoSuscripcion = tipoSuscripcion,
                    datosTarjeta = datosTarjeta
                )

                val response = RetrofitClient.instance.procesarPago("Bearer $token", request)

                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) }
                    // Actualizar estado después del pago exitoso
                    obtenerEstadoSuscripcion(token)
                } else {
                    onError("Error al procesar el pago")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Error desconocido")
            } finally {
                _isLoading.value = false
            }
        }
    }
}