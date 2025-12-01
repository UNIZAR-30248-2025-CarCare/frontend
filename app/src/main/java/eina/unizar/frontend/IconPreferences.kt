package eina.unizar.frontend

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File

class IconPreferences(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("vehiculo_icons", Context.MODE_PRIVATE)

    /**
     * Guarda la ruta de la imagen personalizada para un vehículo específico
     */
    fun saveCustomImageForVehicle(vehiculoId: String, imagePath: String) {
        prefs.edit().putString("custom_image_$vehiculoId", imagePath).apply()
    }

    /**
     * Obtiene la ruta de la imagen personalizada de un vehículo
     * Retorna null si no tiene imagen personalizada
     */
    fun getCustomImageForVehicle(vehiculoId: String): String? {
        return prefs.getString("custom_image_$vehiculoId", null)
    }

    /**
     * Elimina la imagen personalizada (vuelve al ícono por defecto)
     */
    fun removeCustomImageForVehicle(vehiculoId: String) {
        val imagePath = getCustomImageForVehicle(vehiculoId)

        // Eliminar archivo físico si existe
        imagePath?.let { path ->
            val file = File(path)
            if (file.exists()) {
                file.delete()
            }
        }

        // Eliminar de SharedPreferences
        prefs.edit().remove("custom_image_$vehiculoId").apply()
    }

    /**
     * Verifica si un vehículo tiene imagen personalizada
     */
    fun hasCustomImage(vehiculoId: String): Boolean {
        val imagePath = getCustomImageForVehicle(vehiculoId)
        return imagePath != null && File(imagePath).exists()
    }

    /**
     * Carga la imagen personalizada como Bitmap
     * Retorna null si no existe o hay error
     */
    fun loadCustomImageBitmap(vehiculoId: String): Bitmap? {
        return try {
            val imagePath = getCustomImageForVehicle(vehiculoId) ?: return null
            val file = File(imagePath)

            if (!file.exists()) return null

            BitmapFactory.decodeFile(imagePath)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Obtiene el ícono por defecto según el tipo de vehículo
     */
    fun getDefaultIconForTipo(tipoVehiculo: TipoVehiculo): Int {
        return when (tipoVehiculo) {
            TipoVehiculo.COCHE -> R.drawable.ic_coche
            TipoVehiculo.MOTO -> R.drawable.ic_moto
            TipoVehiculo.FURGONETA -> R.drawable.ic_furgoneta
            TipoVehiculo.CAMION -> R.drawable.ic_camion
            TipoVehiculo.OTRO -> R.drawable.ic_marker
        }
    }

    /**
     * Método principal para obtener el ícono final de un vehículo
     * Retorna: Bitmap personalizado o null (usar ícono por defecto)
     */
    fun getCustomImageOrNull(vehiculoId: String): Bitmap? {
        return if (hasCustomImage(vehiculoId)) {
            loadCustomImageBitmap(vehiculoId)
        } else {
            null
        }
    }

    /**
     * Obtiene la ruta donde guardar imágenes de vehículos
     */
    fun getVehicleImagesDirectory(): File {
        val dir = File(context.filesDir, "vehicle_images")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    /**
     * Genera una ruta única para guardar una nueva imagen
     */
    fun generateImagePath(vehiculoId: Int): String {
        val timestamp = System.currentTimeMillis()
        val fileName = "vehicle_${vehiculoId}_${timestamp}.jpg"
        return File(getVehicleImagesDirectory(), fileName).absolutePath
    }

    /**
     * Función de debug para listar todas las imágenes guardadas
     */
    fun debugAllCustomImages(): String {
        val all = prefs.all
        val imagePrefs = all.filter { it.key.startsWith("custom_image_") }

        return if (imagePrefs.isEmpty()) {
            "No hay imágenes personalizadas guardadas"
        } else {
            "Imágenes guardadas:\n" + imagePrefs.map { entry ->
                val vehiculoId = entry.key.removePrefix("custom_image_")
                val path = entry.value as String
                val exists = File(path).exists()
                "Vehículo $vehiculoId: $path (existe: $exists)"
            }.joinToString("\n")
        }
    }
}

// Mantener para compatibilidad con íconos predefinidos si los necesitas
data class IconOption(
    val key: String,
    val nombre: String,
    val resId: Int
)