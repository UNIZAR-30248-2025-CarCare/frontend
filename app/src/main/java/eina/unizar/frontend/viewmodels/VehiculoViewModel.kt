package eina.unizar.frontend.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eina.unizar.frontend.models.RegistrarVehiculoRequest
import eina.unizar.frontend.network.RetrofitClient
import kotlinx.coroutines.launch

/**
 * ViewModel para gestionar operaciones relacionadas con vehículos.
 * 
 * Actualmente maneja el registro de nuevos vehículos en el sistema,
 * proporcionando feedback sobre el éxito o errores en el proceso.
 */
class VehiculoViewModel : ViewModel() {

    /**
     * Mensaje de error en caso de fallo al registrar un vehículo.
     * 
     * Es null si no hay error. La UI puede observar este valor
     * para mostrar mensajes de error al usuario.
     */
    var errorMessage by mutableStateOf<String?>(null)

    /**
     * Indica si el registro del vehículo fue exitoso.
     * 
     * La UI puede observar este valor para navegar a otra pantalla
     * o mostrar un mensaje de confirmación tras el registro exitoso.
     */
    var registroExitoso by mutableStateOf(false)

    /**
     * Registra un nuevo vehículo en el sistema.
     * 
     * Realiza una petición suspendida al backend para crear un nuevo vehículo.
     * Actualiza registroExitoso a true si tiene éxito, o establece errorMessage
     * con los detalles del error si falla.
     * 
     * @param token Token JWT de autenticación (sin el prefijo "Bearer")
     * @param request Objeto con todos los datos del vehículo a registrar
     */
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