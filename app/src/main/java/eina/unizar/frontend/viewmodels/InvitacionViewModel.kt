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

class InvitacionViewModel : ViewModel() {
    var invitaciones by mutableStateOf<List<InvitacionRecibida>>(emptyList())
        private set
    var loading by mutableStateOf(false)
        private set
    var mensaje by mutableStateOf<String?>(null)

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