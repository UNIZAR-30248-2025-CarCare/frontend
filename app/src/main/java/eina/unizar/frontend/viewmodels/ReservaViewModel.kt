package eina.unizar.frontend.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eina.unizar.frontend.models.ReservaDTO
import eina.unizar.frontend.models.ReservasListResponse
import eina.unizar.frontend.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
}