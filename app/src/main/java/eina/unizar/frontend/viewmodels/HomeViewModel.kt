package eina.unizar.frontend.viewmodels

// ViewModel para manejar la lógica de la API
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eina.unizar.frontend.models.VehiculoDTO
import eina.unizar.frontend.models.VehiculoResponse
import eina.unizar.frontend.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.await
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * ViewModel para la pantalla principal (Home) de la aplicación.
 * 
 * Gestiona la obtención y almacenamiento de:
 * - Nombre del usuario autenticado
 * - Lista de vehículos disponibles para el usuario
 * 
 * Utiliza coroutines para operaciones asíncronas y StateFlow/State
 * para exponer datos reactivos a la UI.
 */
class HomeViewModel : ViewModel() {

    /**
     * Nombre del usuario actual.
     * 
     * Utiliza mutableStateOf para que los cambios sean observables
     * por la UI de Compose. Inicializado con texto de carga.
     */
    var userName by mutableStateOf("Cargando...")
        private set

    /**
     * Flow privado mutable para la lista de vehículos.
     */
    private val _vehiculos = MutableStateFlow<List<VehiculoDTO>>(emptyList())

    /**
     * StateFlow público con la lista de vehículos del usuario.
     * La UI puede colectar este flow para actualizar la lista de vehículos.
     */
    val vehiculos: StateFlow<List<VehiculoDTO>> = _vehiculos

    /**
     * URL de la foto de perfil del usuario.
     * Puede ser null si no tiene foto o si aún no se ha cargado.
     */
    private val _fotoPerfilUrl = MutableStateFlow<String?>(null)
    val fotoPerfilUrl: StateFlow<String?> = _fotoPerfilUrl.asStateFlow()

    /**
     * Obtiene el nombre del usuario desde el backend.
     * 
     * Realiza una petición asíncrona para recuperar el nombre del usuario
     * utilizando su ID y token de autenticación. Actualiza userName con
     * el resultado o con un mensaje de error si falla.
     * 
     * @param userId Identificador del usuario
     * @param token Token JWT de autenticación (sin el prefijo "Bearer")
     */
    fun fetchUserName(userId: String, token: String) {
        viewModelScope.launch {
            try {
                // Añade logs para depuración
                Log.d("HomeViewModel", "Obteniendo nombre de usuario para ID: $userId")

                // Usar await() en lugar de execute()
                val response = RetrofitClient.instance.obtenerNombreUsuario(userId, "Bearer $token")
                    .await()

                userName = response.nombre ?: "Usuario no encontrado"
                Log.d("HomeViewModel", "Nombre obtenido: $userName")
            } catch (e: HttpException) {
                Log.e("HomeViewModel", "Error HTTP: ${e.code()} ${e.message()}")
                userName = "Error de red: ${e.code()}"
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error al obtener nombre: ${e.message}", e)
                userName = "Error: ${e.message?.take(30) ?: "desconocido"}"
            }
        }
    }

    /**
     * Obtiene la URL de la foto de perfil del usuario desde el backend
     * y actualiza el StateFlow observable.
     *
     * @param token Token JWT de autenticación (sin el prefijo "Bearer")
     */
    fun fetchUserPhoto(token: String) {
        viewModelScope.launch {
            try {
                Log.d("HomeViewModel", "Obteniendo foto de perfil")

                // Llama a la función suspend de Retrofit.
                // Esto devolverá directamente un Response<FotoPerfilResponse>.
                val response = RetrofitClient.instance.obtenerFotoPerfil("Bearer $token")

                if (response.isSuccessful) {
                    // Si la respuesta HTTP fue 2xx (ej. 200 OK):
                    val fotoResponse = response.body()

                    // Actualiza el StateFlow con la URL/Base64.
                    _fotoPerfilUrl.value = fotoResponse?.foto_perfil

                    Log.d("HomeViewModel", "Foto de perfil obtenida: ${_fotoPerfilUrl.value}")

                } else {
                    // Si la respuesta HTTP no es exitosa (ej. 404, 500)
                    Log.e("HomeViewModel", "Error HTTP al obtener foto: ${response.code()}")
                    _fotoPerfilUrl.value = null // Limpia la foto en caso de error
                }

            } catch (e: HttpException) {
                Log.e("HomeViewModel", "Error HTTP (Excepción) al obtener foto: ${e.code()} ${e.message()}")
                _fotoPerfilUrl.value = null
            } catch (e: Exception) {
                // Este catch maneja errores de red o errores de deserialización (si quedan)
                Log.e("HomeViewModel", "Error al obtener foto: ${e.message}", e)
                _fotoPerfilUrl.value = null
            }
        }
    }

    /**
     * Obtiene la lista de vehículos asociados al usuario.
     * 
     * Realiza una petición al backend para recuperar todos los vehículos
     * vinculados al usuario. Actualiza el StateFlow de vehículos con los
     * resultados o registra un error si falla la petición.
     * 
     * Utiliza callbacks de Retrofit en lugar de coroutines para manejar
     * la respuesta de forma asíncrona.
     * 
     * @param userId Identificador del usuario
     * @param token Token JWT de autenticación (sin el prefijo "Bearer")
     */
    fun fetchVehiculos(userId: String, token: String) {
        Log.d("HomeViewModel", "Iniciando fetchVehiculos para userId: $userId")
        RetrofitClient.instance.obtenerVehiculos(userId, "Bearer $token")
            .enqueue(object : Callback<VehiculoResponse> {
                override fun onResponse(
                    call: Call<VehiculoResponse>,
                    response: Response<VehiculoResponse>
                ) {
                    Log.d("HomeViewModel", "Respuesta recibida: ${response.code()}")
                    if (response.isSuccessful) {
                        val vehiculos = response.body()?.vehiculos ?: emptyList()
                        Log.d("HomeViewModel", "Vehículos obtenidos: ${vehiculos.size}")

                        // >>> LOG CRUCIAL AÑADIDO AQUI <<<
                        vehiculos.forEach { vehiculo ->
                            Log.i("HOME_DATA",
                                "ID: ${vehiculo.id} | " +
                                        "Estado: ${vehiculo.estado} | " +
                                        "Usuario Activo ID: ${vehiculo.usuarioActivoId} | " +
                                        "Tipo de Usuario Activo: ${vehiculo.usuarioActivoId?.javaClass?.simpleName ?: "null"}"
                            )
                        }
                        _vehiculos.value = vehiculos
                    } else {
                        Log.e("HomeViewModel", "Error en la respuesta: ${response.errorBody()?.string()}")
                    }
                }
                override fun onFailure(call: Call<VehiculoResponse>, t: Throwable) {
                    Log.e("HomeViewModel", "Error en fetchVehiculos: ${t.message}", t)
                }
            })
    }
}