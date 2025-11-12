package eina.unizar.frontend.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eina.unizar.frontend.network.RetrofitClient
import eina.unizar.frontend.models.EstadisticasData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EstadisticasViewModel : ViewModel() {
    private val _estadisticas = MutableStateFlow<EstadisticasData?>(null)
    val estadisticas: StateFlow<EstadisticasData?> = _estadisticas

    fun fetchEstadisticas(vehiculoId: String, mes: Int, ano: Int, token: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.getEstadisticas(
                    vehiculoId = vehiculoId,
                    mes = mes,
                    ano = ano,
                    token = "Bearer $token"
                )
                if (response.isSuccessful) {
                    _estadisticas.value = response.body()
                } else {
                    Log.e("EstadisticasViewModel", "Error: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("EstadisticasViewModel", "Exception: ${e.message}")
            }
        }
    }
}