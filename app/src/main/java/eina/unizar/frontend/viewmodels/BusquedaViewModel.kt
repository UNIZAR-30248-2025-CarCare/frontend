// En viewmodels/BusquedaViewModel.kt
package eina.unizar.frontend.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eina.unizar.frontend.models.*
import eina.unizar.frontend.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BusquedaViewModel : ViewModel() {
    private val _resultados = MutableStateFlow<List<SearchResult>>(emptyList())
    val resultados: StateFlow<List<SearchResult>> = _resultados

    // AÑADIR: Almacenar todos los resultados sin filtrar
    private val _todosLosResultados = MutableStateFlow<List<SearchResult>>(emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _filtroActivo = MutableStateFlow<TipoResultado?>(null)
    val filtroActivo: StateFlow<TipoResultado?> = _filtroActivo

    fun buscar(vehiculoId: String, query: String, token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.instance.busquedaGlobal(
                    vehiculoId = vehiculoId,
                    query = query,
                    token = "Bearer $token"
                )

                if (response.isSuccessful) {
                    val data = response.body()
                    val resultados = mutableListOf<SearchResult>()

                    data?.viajes?.forEach { viaje ->
                        Log.d("BusquedaViewModel", "Viaje recibido: ${viaje.nombre}")
                        Log.d("BusquedaViewModel", "Usuario del viaje: ${viaje.usuario}")
                        resultados.add(SearchResult.ViajeResult(
                            id = viaje.id,
                            titulo = viaje.nombre,
                            subtitulo = "Usuario: ${viaje.usuario}",
                            fecha = viaje.fechaHoraInicio,
                            viaje = viaje
                        ))
                    }

                    data?.reservas?.forEach { reserva ->
                        resultados.add(SearchResult.ReservaResult(
                            id = reserva.id,
                            titulo = "Reserva para ${reserva.Usuario.nombre}",
                            subtitulo = "${reserva.fechaInicio} - ${reserva.fechaFin}",
                            fecha = reserva.fechaInicio,
                            reserva = reserva
                        ))
                    }

                    data?.revisiones?.forEach { revision ->
                        resultados.add(SearchResult.RevisionResult(
                            id = revision.id,
                            titulo = revision.tipo,
                            subtitulo = "${revision.kilometraje} km",
                            fecha = revision.fecha,
                            revision = revision
                        ))
                    }

                    data?.incidencias?.forEach { incidencia ->
                        resultados.add(SearchResult.IncidenciaResult(
                            id = incidencia.id.toInt(),
                            titulo = incidencia.titulo,
                            subtitulo = "Estado: ${incidencia.estado}",
                            fecha = incidencia.fechaCreacion,
                            incidencia = incidencia
                        ))
                    }

                    // CAMBIAR: Guardar todos los resultados
                    _todosLosResultados.value = resultados

                    // CAMBIAR: Aplicar filtro actual
                    aplicarFiltro()
                }
            } catch (e: Exception) {
                Log.e("BusquedaViewModel", "Error en búsqueda", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // AÑADIR: Nueva función para aplicar filtro
    private fun aplicarFiltro() {
        _resultados.value = if (_filtroActivo.value == null) {
            _todosLosResultados.value
        } else {
            _todosLosResultados.value.filter { it.tipo == _filtroActivo.value }
        }
    }

    // MODIFICAR: Aplicar filtro cuando cambia
    fun setFiltro(tipo: TipoResultado?) {
        _filtroActivo.value = tipo
        aplicarFiltro()
    }

    fun limpiar() {
        _resultados.value = emptyList()
        _todosLosResultados.value = emptyList()
        _filtroActivo.value = null
    }
}