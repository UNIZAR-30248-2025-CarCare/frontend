package eina.unizar.frontend.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eina.unizar.frontend.models.NuevoParkingData
import eina.unizar.frontend.models.Parking
import eina.unizar.frontend.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para gestionar los parkings del usuario.
 *
 * Responsabilidades:
 * - Obtener la lista de parkings de un usuario.
 * - Crear nuevos parkings.
 * - Eliminar parkings existentes.
 */
class ParkingsViewModel : ViewModel() {
    private val _parkings = MutableStateFlow<List<Parking>>(emptyList())
    val parkings: StateFlow<List<Parking>> = _parkings

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    /**
     * Obtiene la lista de parkings para un usuario específico.
     *
     * @param usuarioId ID del usuario cuyos parkings se desean obtener.
     * @param token Token de autenticación JWT.
     */
    fun fetchParkings(usuarioId: String, token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.instance.obtenerParkingsUsuario(
                    token = "Bearer $token",
                    usuarioId = usuarioId
                )
                if (response.isSuccessful) {
                    _parkings.value = response.body()?.parkings ?: emptyList()
                    Log.d("ParkingsViewModel", "Parkings obtenidos: ${_parkings.value.size}")
                } else {
                    Log.e("ParkingsViewModel", "Error al obtener parkings: ${response.code()}")
                    _parkings.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("ParkingsViewModel", "Excepción al obtener parkings", e)
                _parkings.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Crea un nuevo parking.
     *
     * @param parking Datos del nuevo parking a crear.
     * @param token Token de autenticación JWT.
     * @param onResult Callback que recibe un String con el mensaje de error en caso de fallo,
     *                 o null si la operación fue exitosa.
     */
    fun crearParking(
        parking: NuevoParkingData,
        token: String,
        onResult: (String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.crearParking(
                    token = "Bearer $token",
                    parking = parking
                )
                if (response.isSuccessful) {
                    Log.d("ParkingsViewModel", "Parking creado exitosamente")
                    onResult(null)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("ParkingsViewModel", "Error al crear parking: $errorBody")
                    onResult(errorBody)
                }
            } catch (e: Exception) {
                Log.e("ParkingsViewModel", "Excepción al crear parking", e)
                onResult(e.message)
            }
        }
    }

    /**
     * Elimina un parking existente.
     *
     * @param parkingId ID del parking a eliminar.
     * @param token Token de autenticación JWT.
     * @param onResult Callback que recibe true si la operación fue exitosa, false en caso contrario.
     */
    fun eliminarParking(parkingId: Int, token: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.eliminarParking(
                    token = "Bearer $token",
                    parkingId = parkingId
                )
                if (response.isSuccessful) {
                    Log.d("ParkingsViewModel", "Parking eliminado exitosamente")
                    onResult(true)
                } else {
                    Log.e("ParkingsViewModel", "Error al eliminar parking: ${response.code()}")
                    onResult(false)
                }
            } catch (e: Exception) {
                Log.e("ParkingsViewModel", "Excepción al eliminar parking", e)
                onResult(false)
            }
        }
    }

    /**
     * Actualiza un parking existente.
     *
     * @param parkingId ID del parking a actualizar.
     * @param parking Datos actualizados del parking.
     * @param token Token de autenticación JWT.
     * @param onResult Callback que recibe un String con el mensaje de error en caso de fallo,
     *                 o null si la operación fue exitosa.
     */
    fun actualizarParking(
        parkingId: Int,
        parking: NuevoParkingData,
        token: String,
        onResult: (String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.actualizarParking(
                    token = "Bearer $token",
                    parkingId = parkingId,
                    parking = parking
                )
                if (response.isSuccessful) {
                    Log.d("ParkingsViewModel", "Parking actualizado exitosamente")
                    onResult(null)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("ParkingsViewModel", "Error al actualizar parking: $errorBody")
                    onResult(errorBody)
                }
            } catch (e: Exception) {
                Log.e("ParkingsViewModel", "Excepción al actualizar parking", e)
                onResult(e.message)
            }
        }
    }
}