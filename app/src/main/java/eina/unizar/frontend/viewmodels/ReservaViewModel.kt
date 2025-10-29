package eina.unizar.frontend.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eina.unizar.frontend.models.ReservaDTO
import eina.unizar.frontend.models.ReservasListResponse
import eina.unizar.frontend.models.NuevaReservaData
import eina.unizar.frontend.models.ReservaRequest
import eina.unizar.frontend.models.ReservaResponse
import eina.unizar.frontend.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume

class ReservaViewModel : ViewModel() {
    private val _reservas = MutableStateFlow<List<ReservaDTO>>(emptyList())
    val reservas: StateFlow<List<ReservaDTO>> = _reservas

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchReservas(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            RetrofitClient.instance.obtenerReservas("Bearer $token")
                .enqueue(object : Callback<ReservasListResponse> {
                    override fun onResponse(
                        call: Call<ReservasListResponse>,
                        response: Response<ReservasListResponse>
                    ) {
                        _isLoading.value = false
                        if (response.isSuccessful) {
                            val reservasResponse = response.body()
                            _reservas.value = reservasResponse?.reservas ?: emptyList()
                            Log.d("ReservaViewModel", "Reservas cargadas: ${_reservas.value.size}")
                        } else {
                            val errorMsg = "Error al cargar reservas: ${response.code()}"
                            _error.value = errorMsg
                            Log.e("ReservaViewModel", errorMsg)
                            Log.e("ReservaViewModel", "Body: ${response.errorBody()?.string()}")
                        }
                    }

                    override fun onFailure(call: Call<ReservasListResponse>, t: Throwable) {
                        _isLoading.value = false
                        val errorMsg = "Error de conexión: ${t.message}"
                        _error.value = errorMsg
                        Log.e("ReservaViewModel", errorMsg, t)
                    }
                })
        }
    }

    // ✅ NUEVO: Crear reserva Y refrescar automáticamente
    suspend fun crearYRefrescarReserva(token: String, reserva: NuevaReservaData): Boolean {
        return suspendCancellableCoroutine { continuation ->
            // Convertir NuevaReservaData a ReservaRequest (String)
            val reservaRequest = ReservaRequest(
                vehiculoId = reserva.vehiculoId,
                fechaInicio = reserva.fechaInicio.toString(),      // LocalDate -> String
                fechaFinal = reserva.fechaFinal.toString(),        // LocalDate -> String
                horaInicio = reserva.horaInicio.toString(),        // LocalTime -> String
                horaFin = reserva.horaFin.toString(),              // LocalTime -> String
                tipo = reserva.tipo.name,                          // TipoReserva -> String
                notas = reserva.notas
            )
            
            RetrofitClient.instance.crearReserva("Bearer $token", reservaRequest)
                .enqueue(object : Callback<ReservaResponse> {
                    override fun onResponse(call: Call<ReservaResponse>, response: Response<ReservaResponse>) {
                        if (response.isSuccessful) {
                            Log.d("ReservaViewModel", "✅ Reserva creada exitosamente: ${response.body()}")
                            
                            // Lanzar refresh después de un delay
                            viewModelScope.launch {
                                delay(500)
                                fetchReservas(token)
                            }
                            
                            continuation.resume(true)
                        } else {
                            val errorBody = response.errorBody()?.string()
                            _error.value = "Error al crear reserva: ${response.code()}"
                            Log.e("ReservaViewModel", "❌ Error ${response.code()}: $errorBody")
                            continuation.resume(false)
                        }
                    }

                    override fun onFailure(call: Call<ReservaResponse>, t: Throwable) {
                        _error.value = "Error al crear reserva: ${t.message}"
                        Log.e("ReservaViewModel", "❌ Excepción al crear reserva", t)
                        continuation.resume(false)
                    }
                })
        }
    }

    // ✅ Eliminar reserva
    suspend fun eliminarReserva(token: String, reservaId: String): Boolean {
        return suspendCancellableCoroutine { continuation ->
            RetrofitClient.instance.eliminarReserva(reservaId, "Bearer $token")
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Log.d("ReservaViewModel", "✅ Reserva eliminada exitosamente")
                            
                            // Refrescar la lista
                            viewModelScope.launch {
                                delay(300)
                                fetchReservas(token)
                            }
                            
                            continuation.resume(true)
                        } else {
                            val errorBody = response.errorBody()?.string()
                            _error.value = "Error al eliminar reserva: ${response.code()}"
                            Log.e("ReservaViewModel", "❌ Error ${response.code()}: $errorBody")
                            continuation.resume(false)
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        _error.value = "Error al eliminar reserva: ${t.message}"
                        Log.e("ReservaViewModel", "❌ Excepción al eliminar", t)
                        continuation.resume(false)
                    }
                })
        }
    }

}