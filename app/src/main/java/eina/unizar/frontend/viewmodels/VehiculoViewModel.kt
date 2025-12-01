package eina.unizar.frontend.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eina.unizar.frontend.models.RegistrarVehiculoRequest
import eina.unizar.frontend.network.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

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
     * Indica si la edición del vehículo fue exitosa.
     *
     * La UI puede observar este valor para navegar a otra pantalla
     * o mostrar un mensaje de confirmación tras la edición exitosa.
     */
    var edicionExitosa by mutableStateOf(false)

    /**
     * Mensaje de confirmación tras eliminar un vehículo.
     *
     * La UI puede observar este valor para mostrar un mensaje de confirmación
     * tras la eliminación exitosa.
     */
    var mensajeEliminacion by mutableStateOf<String?>(null)

    /**
     * Mensaje de error en caso de fallo al eliminar un vehículo.
     *
     * La UI puede observar este valor para mostrar mensajes de error al usuario.
     */
    var errorEliminacion by mutableStateOf<String?>(null)

    /**
     * Mensaje de confirmación tras eliminar un usuario.
     *
     * La UI puede observar este valor para mostrar un mensaje de confirmación
     * tras la eliminación exitosa.
     */
    var mensajeEliminacionUsuario by mutableStateOf<String?>(null)

    /**
     * Mensaje de error en caso de fallo al eliminar un usuario.
     *
     * La UI puede observar este valor para mostrar mensajes de error al usuario.
     */
    var errorEliminacionUsuario by mutableStateOf<String?>(null)

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

    /**
     * Indica si la edición del vehículo fue exitosa.
     *
     * La UI puede observar este valor para navegar a otra pantalla
     * o mostrar un mensaje de confirmación tras la edición exitosa.
     */
    fun editarVehiculo(token: String, vehiculoId: String, request: RegistrarVehiculoRequest) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.editarVehiculo("Bearer $token", vehiculoId, request)
                if (response.isSuccessful) {
                    edicionExitosa = true
                } else {
                    val errorBody = response.errorBody()?.string()
                    errorMessage = errorBody ?: "Error: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error de red: ${e.message}"
            }
        }
    }

    /**
     * Elimina un vehículo del sistema.
     *
     * Realiza una petición suspendida al backend para eliminar un vehículo existente.
     * Actualiza mensajeEliminacion con la confirmación si tiene éxito,
     * o establece errorEliminacion con los detalles del error si falla.
     *
     * @param token Token JWT de autenticación (sin el prefijo "Bearer")
     * @param vehiculoId ID del vehículo a eliminar
     */
    fun eliminarVehiculo(token: String, vehiculoId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.eliminarVehiculo("Bearer $token", vehiculoId)
                if (response.isSuccessful) {
                    val body = response.body()
                    mensajeEliminacion = body?.message ?: "Vehículo eliminado"
                    errorEliminacion = null
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Error al eliminar"
                    errorEliminacion = errorMsg
                    mensajeEliminacion = null
                }
            } catch (e: Exception) {
                errorEliminacion = "Error de red"
                mensajeEliminacion = null
            }
        }
    }

    /**
     * Elimina un usuario vinculado a un vehículo.
     *
     * Realiza una petición suspendida al backend para eliminar un usuario vinculado.
     * Actualiza mensajeEliminacionUsuario con la confirmación si tiene éxito,
     * o establece errorEliminacionUsuario con los detalles del error si falla.
     *
     * @param token Token JWT de autenticación (sin el prefijo "Bearer")
     * @param vehiculoId ID del vehículo del cual se eliminará el usuario
     * @param usuarioId ID del usuario a eliminar
     */
    fun eliminarUsuarioVinculado(token: String, vehiculoId: String, usuarioNombre: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.eliminarUsuarioVinculado(
                    "Bearer $token",
                    vehiculoId,
                    mapOf("usuarioNombre" to usuarioNombre)
                )
                if (response.isSuccessful) {
                    mensajeEliminacionUsuario = response.body()?.message ?: "Usuario eliminado"
                    errorEliminacionUsuario = null
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Error al eliminar usuario"
                    errorEliminacionUsuario = errorMsg
                    mensajeEliminacionUsuario = null
                }
            } catch (e: Exception) {
                errorEliminacionUsuario = "Error de red"
                mensajeEliminacionUsuario = null
            }
        }
    }

    /**
     * Sube el icono personalizado del vehículo al backend.
     *
     * @param token Token JWT de autenticación (sin el prefijo "Bearer")
     * @param vehiculoId ID del vehículo
     * @param icono Imagen en formato MultipartBody.Part
     * @param onResult Callback con éxito y la URL del icono
     */
    fun subirIconoVehiculo(token: String, vehiculoId: String, icono: MultipartBody.Part, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.subirIconoVehiculo("Bearer $token", vehiculoId, icono)
                if (response.isSuccessful && response.body() != null) {
                    val iconoUrl = response.body()!!.iconoUrl
                    onResult(true, iconoUrl)
                } else {
                    onResult(false, null)
                }
            } catch (e: Exception) {
                onResult(false, null)
            }
        }
    }

    var iconoActualUrl by mutableStateOf<String?>(null)

    fun cargarIconoVehiculo(token: String, vehiculoId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.obtenerIconoVehiculo(vehiculoId, "Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    iconoActualUrl = response.body()!!.iconoUrl // O .icono_url según tu modelo
                    Log.d("IconoURL", "icono_url recibido: ${iconoActualUrl}")
                } else {
                    iconoActualUrl = null
                }
            } catch (e: Exception) {
                iconoActualUrl = null
            }
        }
    }

    fun eliminarIconoVehiculo(token: String, vehiculoId: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.eliminarIconoVehiculo("Bearer $token", vehiculoId)
                if (response.isSuccessful) {
                    iconoActualUrl = null
                    onResult(true)
                } else {
                    onResult(false)
                }
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }
}