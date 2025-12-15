package eina.unizar.frontend.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eina.unizar.frontend.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class PerfilViewModel : ViewModel() {

    private val apiService = RetrofitClient.instance

    private val _estadoSubida = MutableStateFlow<SubidaEstado>(SubidaEstado.Inicial)
    val estadoSubida: StateFlow<SubidaEstado> = _estadoSubida

    /**
     * Convierte una imagen a Base64 con compresión.
     */
    private fun imageToBase64(
        context: Context,
        uri: Uri,
        maxWidth: Int = 512,
        maxHeight: Int = 512,
        quality: Int = 70
    ): String? {
        try {
            // 1. Leer la imagen
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            if (originalBitmap == null) return null

            // 2. Redimensionar
            val width = originalBitmap.width
            val height = originalBitmap.height
            val ratio = width.toFloat() / height.toFloat()

            val newWidth: Int
            val newHeight: Int

            if (width > height) {
                newWidth = if (width > maxWidth) maxWidth else width
                newHeight = (newWidth / ratio).toInt()
            } else {
                newHeight = if (height > maxHeight) maxHeight else height
                newWidth = (newHeight * ratio).toInt()
            }

            val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)

            // 3. Comprimir a JPEG
            val outputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            val compressedBytes = outputStream.toByteArray()

            // 4. Convertir a Base64
            val base64String = Base64.encodeToString(compressedBytes, Base64.NO_WRAP)
            val dataUrl = "data:image/jpeg;base64,$base64String"

            // 5. Liberar memoria
            originalBitmap.recycle()
            resizedBitmap.recycle()
            outputStream.close()

            android.util.Log.d("PerfilViewModel", "Imagen comprimida: ${compressedBytes.size / 1024} KB")

            return dataUrl

        } catch (e: Exception) {
            android.util.Log.e("PerfilViewModel", "Error al convertir imagen", e)
            return null
        }
    }

    /**
     * Sube la foto en formato Base64.
     */
    fun subirFotoPerfil(context: Context, uri: Uri, token: String) {
        viewModelScope.launch {
            _estadoSubida.value = SubidaEstado.Cargando

            try {
                // 1. Convertir a Base64
                val base64Image = imageToBase64(context, uri)

                if (base64Image == null) {
                    _estadoSubida.value = SubidaEstado.Error("No se pudo procesar la imagen.")
                    return@launch
                }

                // 2. Crear el JSON manualmente
                val jsonBody = JSONObject().apply {
                    put("fotoBase64", base64Image)
                }

                // 3. Hacer la petición
                val response = apiService.actualizarFotoPerfilBase64(
                    token = "Bearer $token",
                    body = okhttp3.RequestBody.create(
                        "application/json".toMediaTypeOrNull(),
                        jsonBody.toString()
                    )
                )

                if (response.isSuccessful) {
                    val body = response.body()?.string()
                    val jsonResponse = JSONObject(body ?: "{}")
                    val fotoUrl = jsonResponse.optString("foto_perfil", "")

                    _estadoSubida.value = SubidaEstado.Exito(fotoUrl)
                    android.util.Log.d("PerfilViewModel", "✅ Foto subida correctamente")
                } else {
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e("PerfilViewModel", "❌ Error: $errorBody")
                    _estadoSubida.value = SubidaEstado.Error(
                        "Error al subir foto: ${response.code()}"
                    )
                }

            } catch (e: Exception) {
                android.util.Log.e("PerfilViewModel", "❌ Excepción", e)
                _estadoSubida.value = SubidaEstado.Error("Error de red: ${e.message}")
            }
        }
    }

    fun resetearEstado() {
        _estadoSubida.value = SubidaEstado.Inicial
    }
}

sealed class SubidaEstado {
    data object Inicial : SubidaEstado()
    data object Cargando : SubidaEstado()
    data class Exito(val fotoUrl: String) : SubidaEstado()
    data class Error(val mensaje: String) : SubidaEstado()
}