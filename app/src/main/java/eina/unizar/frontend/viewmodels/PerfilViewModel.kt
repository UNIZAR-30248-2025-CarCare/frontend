package eina.unizar.frontend.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eina.unizar.frontend.network.ApiService // Importar tu ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

// 💡 Clase para inyección de dependencia
class PerfilViewModel(
    private val apiService: ApiService // Ahora inyectamos ApiService
) : ViewModel() {

    // Estados de la subida de la foto
    private val _estadoSubida = MutableStateFlow<SubidaEstado>(SubidaEstado.Inicial)
    val estadoSubida: StateFlow<SubidaEstado> = _estadoSubida

    /**
     * Sube la foto seleccionada al servidor.
     * @param context Contexto de la aplicación o actividad para acceder al contenido del URI.
     * @param uri URI de la imagen seleccionada.
     * @param token Token de autenticación del usuario (debe incluir "Bearer ").
     */
    fun subirFotoPerfil(context: Context, uri: Uri, token: String) {
        viewModelScope.launch {
            _estadoSubida.value = SubidaEstado.Cargando

            try {
                val contentResolver = context.contentResolver

                // 1. Obtener los bytes y el tipo MIME
                val inputStream = contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()

                if (bytes == null) {
                    _estadoSubida.value = SubidaEstado.Error("No se pudo leer el archivo seleccionado.")
                    return@launch
                }

                val mimeType = contentResolver.getType(uri) ?: "image/jpeg" // Default si no se puede obtener
                val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())

                // 2. Crear el MultipartBody.Part (el nombre del campo es 'foto' para Multer)
                val filePart = MultipartBody.Part.createFormData("foto", "perfil_upload.jpg", requestBody)

                // 3. Llamar al servicio API
                val response = apiService.actualizarFotoPerfil(token, filePart) // Usamos el token directamente

                if (response.isSuccessful) {
                    val urlFoto = response.body()?.foto_perfil
                    _estadoSubida.value = SubidaEstado.Exito(urlFoto ?: "")
                } else {
                    val errorBody = response.errorBody()?.string()
                    _estadoSubida.value = SubidaEstado.Error("Error al subir foto: ${response.code()}. ${errorBody ?: "Error desconocido"}")
                }

            } catch (e: Exception) {
                _estadoSubida.value = SubidaEstado.Error("Error de red/IO: ${e.message}")
            }
        }
    }
}

// 💡 Clase sellada para manejar los estados de la UI
sealed class SubidaEstado {
    data object Inicial : SubidaEstado()
    data object Cargando : SubidaEstado()
    data class Exito(val fotoUrl: String) : SubidaEstado()
    data class Error(val mensaje: String) : SubidaEstado()
}