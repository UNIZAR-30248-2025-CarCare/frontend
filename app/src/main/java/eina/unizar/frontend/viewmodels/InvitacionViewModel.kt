package eina.unizar.frontend.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eina.unizar.frontend.models.InvitacionBody
import eina.unizar.frontend.models.InvitacionRecibida
import eina.unizar.frontend.network.RetrofitClient
import kotlinx.coroutines.launch

/**
 * ViewModel para gestionar las invitaciones de vehículos.
 *
 * Responsabilidades:
 * - Obtener la lista de invitaciones recibidas por un usuario.
 * - Enviar nuevas invitaciones a usuarios por email.
 * - Aceptar o rechazar invitaciones recibidas.
 */
class InvitacionViewModel : ViewModel() {
    var invitaciones by mutableStateOf<List<InvitacionRecibida>>(emptyList())
        private set
    var loading by mutableStateOf(false)
        private set
    var mensaje by mutableStateOf<String?>(null)

    /**
     * Obtiene las invitaciones recibidas por un usuario.
     *
     * @param usuarioId ID del usuario cuyas invitaciones se desean obtener.
     * @param token Token de autenticación JWT.
     */
    fun fetchInvitaciones(usuarioId: String, token: String) {
        viewModelScope.launch {
            loading = true
            try {
                val response = RetrofitClient.instance.getInvitacionesRecibidas(usuarioId, "Bearer $token")
                invitaciones = response.body()?.invitaciones ?: emptyList()
            } catch (_: Exception) {
                invitaciones = emptyList()
            }
            loading = false
        }
    }

    /**
     * Envía una invitación a un usuario para unirse a un vehículo.
     *
     * @param vehiculoId ID del vehículo al que se invita.
     * @param usuarioId ID del usuario que envía la invitación.
     * @param email Email del usuario al que se invita.
     * @param token Token de autenticación JWT.
     */
    fun enviarInvitacion(
        vehiculoId: String,
        usuarioId:String,
        email: String,
        token: String
    ) {
        viewModelScope.launch {
            loading = true
            mensaje = null
            try {
                val body = InvitacionBody(usuarioId, email)
                val response = RetrofitClient.instance.generarInvitacion(vehiculoId, "Bearer $token", body)
                if (response.isSuccessful) {
                    mensaje = response.body()?.message
                } else {
                    val errorBody = response.errorBody()?.string()
                    mensaje = errorBody ?: "Error: ${response.code()}"
                }
            } catch (e: Exception) {
                mensaje = "Error de red: ${e.message}"
            }
            loading = false
        }
    }

    /**
     * Acepta una invitación utilizando un código de invitación.
     *
     * @param codigo Código de la invitación a aceptar.
     * @param token Token de autenticación JWT.
     * @param onResult Callback que recibe el mensaje de resultado o error.
     */
    fun aceptarInvitacion(codigo: String, token: String, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                val body = mapOf("codigo" to codigo)
                val response = RetrofitClient.instance.aceptarInvitacion("Bearer $token", body)
                if (!response.isSuccessful) {
                    val errorBody = response.errorBody()?.string()
                    onResult(errorBody ?: "Error: ${response.code()}")
                } else {
                    onResult(response.body()?.message)
                }
            } catch (e: Exception) {
                onResult("Error: ${e.message}")
            }
        }
    }

    /**
     * Rechaza una invitación recibida.
     *
     * @param invitacionId ID de la invitación a rechazar.
     * @param usuarioId ID del usuario que rechaza la invitación.
     * @param token Token de autenticación JWT.
     * @param onResult Callback que recibe el mensaje de resultado o error.
     */
    fun rechazarInvitacion(invitacionId: Int, usuarioId: Int, token: String, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                val body = mapOf("invitacionId" to invitacionId, "usuarioId" to usuarioId)
                val response = RetrofitClient.instance.rechazarInvitacion("Bearer $token", body)
                if (response.isSuccessful) {
                    onResult(response.body()?.message)
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