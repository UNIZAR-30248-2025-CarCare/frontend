package eina.unizar.frontend.viewmodels

// ViewModel para manejar la lógica de la API
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eina.unizar.frontend.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.await

class HomeViewModel : ViewModel() {
    var userName by mutableStateOf("Cargando...")
        private set

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
}