package eina.unizar.frontend.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eina.unizar.frontend.models.NuevoRepostajeData
import eina.unizar.frontend.models.ProximoRepostajeResponse
import eina.unizar.frontend.models.ResumenRepostajesResponse
import eina.unizar.frontend.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para gestionar los repostajes de vehículos.
 *
 * Responsabilidades:
 * - Obtener el resumen de repostajes de un vehículo.
 * - Calcular el próximo repostaje necesario.
 * - Crear nuevos repostajes.
 */
class RepostajesViewModel : ViewModel() {
    private val _resumen = MutableStateFlow<ResumenRepostajesResponse?>(null)
    val resumen: StateFlow<ResumenRepostajesResponse?> = _resumen

    /**
     * Obtiene el resumen de repostajes para un vehículo específico.
     *
     * @param token Token de autenticación JWT.
     * @param vehiculoId ID del vehículo cuyos repostajes se desean obtener.
     */
    fun fetchRepostajes(token: String, vehiculoId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.obtenerRepostajesVehiculo(token, vehiculoId)
                if (response.isSuccessful) {
                    _resumen.value = response.body()
                } else {
                    _resumen.value = null
                }
            } catch (e: Exception) {
                _resumen.value = null
            }
        }
    }

    private val _proximo = MutableStateFlow<ProximoRepostajeResponse?>(null)
    val proximo: StateFlow<ProximoRepostajeResponse?> = _proximo

    /**
     * Calcula el próximo repostaje necesario para un vehículo específico.
     *
     * @param token Token de autenticación JWT.
     * @param vehiculoId ID del vehículo para el cual se calcula el próximo repostaje.
     */
    fun fetchProximoRepostaje(token: String, vehiculoId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.calcularProximoRepostaje(token, vehiculoId)
                if (response.isSuccessful) {
                    _proximo.value = response.body()
                } else {
                    _proximo.value = null
                }
            } catch (e: Exception) {
                _proximo.value = null
            }
        }
    }

    /**
     * Crea un nuevo repostaje para un vehículo.
     *
     * @param token Token de autenticación JWT.
     * @param repostaje Datos del nuevo repostaje a crear.
     * @param onResult Callback que recibe un String con el error en caso de fallo, o null si fue exitoso.
     */
    fun crearRepostaje(
        token: String,
        repostaje: NuevoRepostajeData,
        onResult: (String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.crearRepostaje("Bearer $token", repostaje)
                if (response.isSuccessful && response.code() == 201) {
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