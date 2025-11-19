package eina.unizar.frontend.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eina.unizar.frontend.models.EstadisticasLogros
import eina.unizar.frontend.models.LogroDTO
import eina.unizar.frontend.models.LogroDesbloqueadoDTO
import eina.unizar.frontend.models.LogrosUsuarioResponse
import eina.unizar.frontend.models.VerificarProgresoResponse
import eina.unizar.frontend.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * ViewModel para gestionar los logros del usuario.
 *
 * Responsabilidades:
 * - Cargar los logros del usuario con su progreso
 * - Verificar y actualizar el progreso automáticamente
 * - Filtrar logros por estado (todos, desbloqueados, pendientes)
 * - Gestionar el estado de carga y errores
 */
class LogroViewModel : ViewModel() {

    // Estado de los logros
    private val _logros = MutableStateFlow<List<LogroDTO>>(emptyList())
    val logros: StateFlow<List<LogroDTO>> = _logros

    // Estadísticas
    private val _estadisticas = MutableStateFlow<EstadisticasLogros?>(null)
    val estadisticas: StateFlow<EstadisticasLogros?> = _estadisticas

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Errores
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Logros recién desbloqueados
    private val _nuevosLogros = MutableStateFlow<List<LogroDesbloqueadoDTO>>(emptyList())
    val nuevosLogros: StateFlow<List<LogroDesbloqueadoDTO>> = _nuevosLogros

    // Filtro actual
    private val _filtroActual = MutableStateFlow(FiltroLogro.TODOS)
    val filtroActual: StateFlow<FiltroLogro> = _filtroActual

    /**
     * Cargar logros del usuario.
     *
     * @param usuarioId ID del usuario
     * @param token Token de autenticación
     */
    fun cargarLogros(usuarioId: Int, token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            Log.d("LogroViewModel", "Cargando logros para usuario: $usuarioId")

            RetrofitClient.instance.obtenerLogrosUsuario("Bearer $token", usuarioId)
                .enqueue(object : Callback<LogrosUsuarioResponse> {
                    override fun onResponse(
                        call: Call<LogrosUsuarioResponse>,
                        response: Response<LogrosUsuarioResponse>
                    ) {
                        _isLoading.value = false
                        if (response.isSuccessful) {
                            val body = response.body()
                            _logros.value = body?.logros ?: emptyList()
                            _estadisticas.value = body?.estadisticas
                            Log.d("LogroViewModel", "✅ Logros cargados: ${_logros.value.size}")
                            Log.d("LogroViewModel", "Estadísticas: ${_estadisticas.value}")
                        } else {
                            val errorMsg = "Error ${response.code()}: ${response.errorBody()?.string()}"
                            _error.value = errorMsg
                            Log.e("LogroViewModel", errorMsg)
                        }
                    }

                    override fun onFailure(call: Call<LogrosUsuarioResponse>, t: Throwable) {
                        _isLoading.value = false
                        val errorMsg = "Error de conexión: ${t.message}"
                        _error.value = errorMsg
                        Log.e("LogroViewModel", errorMsg, t)
                    }
                })
        }
    }

    /**
     * Verificar y actualizar el progreso de logros.
     * Detecta automáticamente logros recién desbloqueados.
     *
     * @param usuarioId ID del usuario
     * @param token Token de autenticación
     * @param onComplete Callback cuando termine (con o sin nuevos logros)
     */
    fun verificarProgreso(
        usuarioId: Int,
        token: String,
        onComplete: (nuevosLogros: List<LogroDesbloqueadoDTO>) -> Unit = {}
    ) {
        viewModelScope.launch {
            Log.d("LogroViewModel", "Verificando progreso de logros...")

            RetrofitClient.instance.verificarProgresoLogros("Bearer $token", usuarioId)
                .enqueue(object : Callback<VerificarProgresoResponse> {
                    override fun onResponse(
                        call: Call<VerificarProgresoResponse>,
                        response: Response<VerificarProgresoResponse>
                    ) {
                        if (response.isSuccessful) {
                            val body = response.body()
                            val nuevos = body?.nuevosLogros ?: emptyList()
                            _nuevosLogros.value = nuevos

                            Log.d("LogroViewModel", "✅ Progreso verificado")
                            Log.d("LogroViewModel", "Nuevos logros desbloqueados: ${nuevos.size}")

                            // Recargar logros para reflejar cambios
                            cargarLogros(usuarioId, token)

                            onComplete(nuevos)
                        } else {
                            Log.e("LogroViewModel", "Error al verificar progreso: ${response.code()}")
                            onComplete(emptyList())
                        }
                    }

                    override fun onFailure(call: Call<VerificarProgresoResponse>, t: Throwable) {
                        Log.e("LogroViewModel", "Error de conexión al verificar progreso", t)
                        onComplete(emptyList())
                    }
                })
        }
    }

    /**
     * Obtener logros filtrados según el filtro actual.
     */
    fun obtenerLogrosFiltrados(): List<LogroDTO> {
        return when (_filtroActual.value) {
            FiltroLogro.TODOS -> _logros.value
            FiltroLogro.DESBLOQUEADOS -> _logros.value.filter { it.desbloqueado }
            FiltroLogro.PENDIENTES -> _logros.value.filter { !it.desbloqueado }
        }
    }

    /**
     * Cambiar el filtro de logros.
     */
    fun cambiarFiltro(filtro: FiltroLogro) {
        _filtroActual.value = filtro
        Log.d("LogroViewModel", "Filtro cambiado a: $filtro")
    }

    /**
     * Limpiar mensajes de error.
     */
    fun limpiarError() {
        _error.value = null
    }

    /**
     * Limpiar lista de nuevos logros (después de mostrar la notificación).
     */
    fun limpiarNuevosLogros() {
        _nuevosLogros.value = emptyList()
    }

    /**
     * Actualizar lista de nuevos logros (llamado desde LogrosSyncService).
     */
    fun actualizarNuevosLogros(nuevos: List<LogroDesbloqueadoDTO>) {
        _nuevosLogros.value = nuevos
    }
}

/**
 * Enum para filtrar logros.
 */
enum class FiltroLogro {
    TODOS,
    DESBLOQUEADOS,
    PENDIENTES
}
