package eina.unizar.frontend.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eina.unizar.frontend.models.*
import eina.unizar.frontend.network.RetrofitClient
import kotlinx.coroutines.launch

/**
 * ViewModel para gestionar operaciones relacionadas con incidencias.
 *
 * Maneja la creación, edición, eliminación y consulta de incidencias,
 * proporcionando feedback sobre el éxito o errores en cada operación.
 */
class IncidenciaViewModel : ViewModel() {

    // ========== ESTADOS DE UI ==========

    /**
     * Mensaje de error en caso de fallo.
     * La UI puede observar este valor para mostrar mensajes de error al usuario.
     */
    var errorMessage by mutableStateOf<String?>(null)
        private set

    /**
     * Indica si la creación de incidencia fue exitosa.
     */
    var creacionExitosa by mutableStateOf(false)
        private set

    /**
     * Indica si la edición de incidencia fue exitosa.
     */
    var edicionExitosa by mutableStateOf(false)
        private set

    /**
     * Mensaje de confirmación tras eliminar una incidencia.
     */
    var mensajeEliminacion by mutableStateOf<String?>(null)
        private set

    /**
     * Lista de incidencias cargadas.
     */
    var incidencias by mutableStateOf<List<IncidenciaDetalle>>(emptyList())
        private set

    /**
     * Incidencia individual cargada.
     */
    var incidenciaDetalle by mutableStateOf<IncidenciaDetalle?>(null)
        private set

    /**
     * Indica si se está cargando información.
     */
    var isLoading by mutableStateOf(false)
        private set

    // ========== FUNCIONES PÚBLICAS ==========

    /**
     * Reinicia los estados de éxito/error.
     * Útil para limpiar mensajes antes de una nueva operación.
     */
    fun resetStates() {
        errorMessage = null
        creacionExitosa = false
        edicionExitosa = false
        mensajeEliminacion = null
    }

    /**
     * Crear una nueva incidencia.
     *
     * @param token Token JWT de autenticación (sin el prefijo "Bearer")
     * @param request Objeto con los datos de la incidencia a crear
     */
    fun crearIncidencia(token: String, request: CrearIncidenciaRequest) {
        viewModelScope.launch {
            resetStates()
            isLoading = true

            try {
                val response = RetrofitClient.instance.crearIncidencia("Bearer $token", request)
                if (response.isSuccessful) {
                    creacionExitosa = true
                    errorMessage = null
                } else {
                    val errorBody = response.errorBody()?.string()
                    errorMessage = errorBody ?: "Error: ${response.code()}"
                    creacionExitosa = false
                }
            } catch (e: Exception) {
                errorMessage = "Error de red: ${e.message}"
                creacionExitosa = false
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Obtener todas las incidencias de un vehículo específico.
     *
     * @param token Token JWT de autenticación
     * @param vehiculoId ID del vehículo
     */
    fun obtenerIncidenciasVehiculo(token: String, vehiculoId: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val response = RetrofitClient.instance.obtenerIncidenciasVehiculo(
                    "Bearer $token",
                    vehiculoId
                )
                if (response.isSuccessful) {
                    incidencias = response.body()?.incidencias ?: emptyList()
                    errorMessage = null
                } else {
                    val errorBody = response.errorBody()?.string()
                    errorMessage = errorBody ?: "Error: ${response.code()}"
                    incidencias = emptyList()
                }
            } catch (e: Exception) {
                errorMessage = "Error de red: ${e.message}"
                incidencias = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Obtener todas las incidencias de los vehículos del usuario.
     *
     * @param token Token JWT de autenticación
     */
    fun obtenerIncidenciasUsuario(token: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val response = RetrofitClient.instance.obtenerIncidenciasUsuario("Bearer $token")
                if (response.isSuccessful) {
                    incidencias = response.body()?.incidencias ?: emptyList()
                    errorMessage = null
                } else {
                    val errorBody = response.errorBody()?.string()
                    errorMessage = errorBody ?: "Error: ${response.code()}"
                    incidencias = emptyList()
                }
            } catch (e: Exception) {
                errorMessage = "Error de red: ${e.message}"
                incidencias = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Obtener el detalle de una incidencia específica.
     *
     * @param token Token JWT de autenticación
     * @param incidenciaId ID de la incidencia
     */
    fun obtenerIncidencia(token: String, incidenciaId: Int) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val response = RetrofitClient.instance.obtenerIncidencia("Bearer $token", incidenciaId)
                if (response.isSuccessful) {
                    incidenciaDetalle = response.body()?.incidencia
                    errorMessage = null
                } else {
                    val errorBody = response.errorBody()?.string()
                    errorMessage = errorBody ?: "Error: ${response.code()}"
                    incidenciaDetalle = null
                }
            } catch (e: Exception) {
                errorMessage = "Error de red: ${e.message}"
                incidenciaDetalle = null
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Actualizar el estado de una incidencia.
     *
     * @param token Token JWT de autenticación
     * @param incidenciaId ID de la incidencia
     * @param nuevoEstado Nuevo estado ("Pendiente", "En progreso", "Resuelta", "Cancelada")
     */
    fun actualizarEstadoIncidencia(token: String, incidenciaId: Int, nuevoEstado: String) {
        viewModelScope.launch {
            resetStates()
            isLoading = true

            try {
                val request = ActualizarEstadoRequest(estado = nuevoEstado)
                val response = RetrofitClient.instance.actualizarEstadoIncidencia(
                    "Bearer $token",
                    incidenciaId,
                    request
                )
                if (response.isSuccessful) {
                    edicionExitosa = true
                    errorMessage = null
                } else {
                    val errorBody = response.errorBody()?.string()
                    errorMessage = errorBody ?: "Error: ${response.code()}"
                    edicionExitosa = false
                }
            } catch (e: Exception) {
                errorMessage = "Error de red: ${e.message}"
                edicionExitosa = false
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Actualizar una incidencia completa.
     *
     * @param token Token JWT de autenticación
     * @param incidenciaId ID de la incidencia
     * @param request Objeto con los nuevos datos de la incidencia
     */
    fun actualizarIncidencia(token: String, incidenciaId: Int, request: CrearIncidenciaRequest) {
        viewModelScope.launch {
            resetStates()
            isLoading = true

            try {
                val response = RetrofitClient.instance.actualizarIncidencia(
                    "Bearer $token",
                    incidenciaId,
                    request
                )
                if (response.isSuccessful) {
                    edicionExitosa = true
                    errorMessage = null
                } else {
                    val errorBody = response.errorBody()?.string()
                    errorMessage = errorBody ?: "Error: ${response.code()}"
                    edicionExitosa = false
                }
            } catch (e: Exception) {
                errorMessage = "Error de red: ${e.message}"
                edicionExitosa = false
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Eliminar una incidencia.
     *
     * @param token Token JWT de autenticación
     * @param incidenciaId ID de la incidencia a eliminar
     */
    fun eliminarIncidencia(token: String, incidenciaId: Int) {
        viewModelScope.launch {
            resetStates()
            isLoading = true

            try {
                val response = RetrofitClient.instance.eliminarIncidencia("Bearer $token", incidenciaId)
                if (response.isSuccessful) {
                    mensajeEliminacion = response.body()?.message ?: "Incidencia eliminada"
                    errorMessage = null
                } else {
                    val errorBody = response.errorBody()?.string()
                    errorMessage = errorBody ?: "Error al eliminar"
                    mensajeEliminacion = null
                }
            } catch (e: Exception) {
                errorMessage = "Error de red: ${e.message}"
                mensajeEliminacion = null
            } finally {
                isLoading = false
            }
        }
    }
}