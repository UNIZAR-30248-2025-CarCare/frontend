package eina.unizar.frontend.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eina.unizar.frontend.models.RevisionDTO
import eina.unizar.frontend.models.RevisionesListResponse
import eina.unizar.frontend.models.RevisionRequest
import eina.unizar.frontend.models.RevisionResponse
import eina.unizar.frontend.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume

class RevisionViewModel : ViewModel() {
    private val _revisiones = MutableStateFlow<List<RevisionDTO>>(emptyList())
    val revisiones: StateFlow<List<RevisionDTO>> = _revisiones

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchRevisiones(token: String, vehiculoId: Int, tipo: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            RetrofitClient.instance.obtenerRevisiones(vehiculoId, tipo, "Bearer $token")
                .enqueue(object : Callback<RevisionesListResponse> {
                    override fun onResponse(
                        call: Call<RevisionesListResponse>,
                        response: Response<RevisionesListResponse>
                    ) {
                        _isLoading.value = false
                        if (response.isSuccessful) {
                            val revisionesResponse = response.body()
                            _revisiones.value = revisionesResponse?.revisiones ?: emptyList()
                            Log.d("RevisionViewModel", "Revisiones cargadas: ${_revisiones.value.size}")
                        } else {
                            val errorMsg = "Error al cargar revisiones: ${response.code()}"
                            _error.value = errorMsg
                            Log.e("RevisionViewModel", errorMsg)
                            Log.e("RevisionViewModel", "Body: ${response.errorBody()?.string()}")
                        }
                    }

                    override fun onFailure(call: Call<RevisionesListResponse>, t: Throwable) {
                        _isLoading.value = false
                        val errorMsg = "Error de conexión: ${t.message}"
                        _error.value = errorMsg
                        Log.e("RevisionViewModel", errorMsg, t)
                    }
                })
        }
    }

    // ✅ NUEVO: Crear revisión Y refrescar automáticamente
    suspend fun crearYRefrescarRevision(token: String, revision: RevisionRequest, vehiculoId: Int): Boolean {
        return suspendCancellableCoroutine { continuation ->
            RetrofitClient.instance.registrarRevision("Bearer $token", revision)
                .enqueue(object : Callback<RevisionResponse> {
                    override fun onResponse(call: Call<RevisionResponse>, response: Response<RevisionResponse>) {
                        if (response.isSuccessful) {
                            Log.d("RevisionViewModel", "✅ Revisión creada exitosamente: ${response.body()}")

                            // Lanzar refresh después de un delay
                            viewModelScope.launch {
                                delay(500)
                                fetchRevisiones(token, vehiculoId)
                            }

                            continuation.resume(true)
                        } else {
                            val errorBody = response.errorBody()?.string()
                            _error.value = "Error al crear revisión: ${response.code()}"
                            Log.e("RevisionViewModel", "❌ Error ${response.code()}: $errorBody")
                            continuation.resume(false)
                        }
                    }

                    override fun onFailure(call: Call<RevisionResponse>, t: Throwable) {
                        _error.value = "Error al crear revisión: ${t.message}"
                        Log.e("RevisionViewModel", "❌ Excepción al crear revisión", t)
                        continuation.resume(false)
                    }
                })
        }
    }

    // ✅ Filtrar revisiones por tipo localmente
    fun filtrarPorTipo(tipo: String?) {
        val todasLasRevisiones = _revisiones.value
        if (tipo.isNullOrEmpty() || tipo == "Todos") {
            // Si no hay filtro o es "Todos", mantener todas las revisiones
            return
        }

        val revisionesFiltradas = todasLasRevisiones.filter {
            it.tipo.equals(tipo, ignoreCase = true)
        }
        _revisiones.value = revisionesFiltradas
    }

    // ✅ Obtener la revisión más reciente
    fun obtenerUltimaRevision(): RevisionDTO? {
        return _revisiones.value.maxByOrNull { it.fecha }
    }

    // ✅ Obtener próximas revisiones programadas
    fun obtenerProximasRevisiones(): List<RevisionDTO> {
        return _revisiones.value
            .filter { !it.proximaRevision.isNullOrEmpty() }
            .sortedBy { it.proximaRevision }
    }

    // ✅ Limpiar error
    fun clearError() {
        _error.value = null
    }
}