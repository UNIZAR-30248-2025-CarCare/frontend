package eina.unizar.frontend.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eina.unizar.frontend.models.RegistrarVehiculoRequest
import eina.unizar.frontend.network.RetrofitClient
import kotlinx.coroutines.launch

class VehiculoViewModel : ViewModel() {
    var errorMessage by mutableStateOf<String?>(null)
    var registroExitoso by mutableStateOf(false)

    fun registrarVehiculo(token: String, request: RegistrarVehiculoRequest) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.registrarVehiculo("Bearer $token", request)
                if (response.isSuccessful) {
                    registroExitoso = true
                } else {
                    val errorBody = response.errorBody()?.string()
                    errorMessage = errorBody ?: "Error: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error de red: ${e.message}"
            }
        }
    }
}