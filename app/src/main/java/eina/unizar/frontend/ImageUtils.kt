package eina.unizar.frontend

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

object ImageUtils {

    private const val TAG = "ImageUtils"
    private const val MAX_IMAGE_SIZE = 512 // Tamaño máximo para el mapa
    private const val ICON_SIZE = 64 // Tamaño final para el ícono del mapa
    private const val JPEG_QUALITY = 85 // Calidad JPEG (0-100)

    /**
     * Procesa una imagen desde URI y la guarda para usar como ícono de vehículo
     */
    fun processImageForVehicle(
        context: Context,
        imageUri: Uri,
        vehiculoId: Int
    ): String? {
        return try {
            Log.d(TAG, "Procesando imagen para vehículo $vehiculoId")

            // 1. Cargar la imagen original
            val originalBitmap = loadBitmapFromUri(context, imageUri) ?: return null
            Log.d(TAG, "Imagen original cargada: ${originalBitmap.width}x${originalBitmap.height}")

            // 2. Corregir orientación si es necesario
            val correctedBitmap = correctImageOrientation(context, imageUri, originalBitmap)

            // 3. Redimensionar para almacenamiento
            val resizedBitmap = resizeBitmap(correctedBitmap, MAX_IMAGE_SIZE)
            Log.d(TAG, "Imagen redimensionada: ${resizedBitmap.width}x${resizedBitmap.height}")

            // 4. Generar ruta de destino
            val iconPreferences = IconPreferences(context)
            val outputPath = iconPreferences.generateImagePath(vehiculoId)

            // 5. Guardar la imagen
            val success = saveBitmapToFile(resizedBitmap, outputPath)

            if (success) {
                Log.d(TAG, "Imagen guardada exitosamente en: $outputPath")
                outputPath
            } else {
                Log.e(TAG, "Error al guardar la imagen")
                null
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error procesando imagen: ${e.message}", e)
            null
        }
    }

    /**
     * Carga un Bitmap desde un URI
     */
    private fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            inputStream?.use { stream ->
                BitmapFactory.decodeStream(stream)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error cargando imagen desde URI: ${e.message}", e)
            null
        }
    }

    /**
     * Corrige la orientación de la imagen basada en datos EXIF
     */
    private fun correctImageOrientation(context: Context, uri: Uri, bitmap: Bitmap): Bitmap {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            inputStream?.use { stream ->
                val exif = ExifInterface(stream)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )

                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
                    else -> bitmap
                }
            } ?: bitmap
        } catch (e: Exception) {
            Log.w(TAG, "No se pudo corregir orientación: ${e.message}")
            bitmap
        }
    }

    /**
     * Rota un bitmap por los grados especificados
     */
    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix().apply {
            postRotate(degrees)
        }

        return Bitmap.createBitmap(
            bitmap, 0, 0,
            bitmap.width, bitmap.height,
            matrix, true
        )
    }

    /**
     * Redimensiona un bitmap manteniendo la relación de aspecto
     */
    fun resizeBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        if (width <= maxSize && height <= maxSize) {
            return bitmap // Ya es suficientemente pequeño
        }

        val ratio = width.toFloat() / height.toFloat()
        val newWidth: Int
        val newHeight: Int

        if (ratio > 1) {
            // Imagen más ancha que alta
            newWidth = maxSize
            newHeight = (maxSize / ratio).toInt()
        } else {
            // Imagen más alta que ancha o cuadrada
            newWidth = (maxSize * ratio).toInt()
            newHeight = maxSize
        }

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    /**
     * Crea un bitmap circular para el ícono del mapa
     */
    fun createCircularIcon(bitmap: Bitmap, size: Int = ICON_SIZE): Bitmap {
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(output)

        val paint = android.graphics.Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.WHITE
        }

        val rect = android.graphics.Rect(0, 0, size, size)
        val rectF = android.graphics.RectF(rect)
        val roundPx = size / 2f

        // Dibujar círculo de fondo
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint)

        // Configurar paint para imagen
        paint.xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN)

        // Escalar imagen al tamaño del círculo
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, size, size, true)
        canvas.drawBitmap(scaledBitmap, rect, rect, paint)

        return output
    }

    /**
     * Guarda un bitmap en un archivo
     */
    private fun saveBitmapToFile(bitmap: Bitmap, filePath: String): Boolean {
        return try {
            val file = File(filePath)

            // Crear directorio padre si no existe
            file.parentFile?.mkdirs()

            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, outputStream)
                outputStream.flush()
            }

            true
        } catch (e: IOException) {
            Log.e(TAG, "Error guardando bitmap: ${e.message}", e)
            false
        }
    }

    /**
     * Elimina un archivo de imagen
     */
    fun deleteImageFile(filePath: String): Boolean {
        return try {
            val file = File(filePath)
            file.delete()
        } catch (e: Exception) {
            Log.e(TAG, "Error eliminando archivo: ${e.message}", e)
            false
        }
    }

    /**
     * Obtiene el tamaño de un archivo en bytes
     */
    fun getFileSize(filePath: String): Long {
        return try {
            File(filePath).length()
        } catch (e: Exception) {
            0L
        }
    }

    /**
     * Formatea el tamaño de archivo para mostrar
     */
    fun formatFileSize(bytes: Long): String {
        return when {
            bytes >= 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
            bytes >= 1024 -> "${bytes / 1024} KB"
            else -> "$bytes B"
        }
    }
}