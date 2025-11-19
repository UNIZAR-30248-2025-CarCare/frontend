package eina.unizar.frontend.services

import android.content.Context
import android.util.Log
import eina.unizar.frontend.models.LogroDesbloqueadoDTO
import eina.unizar.frontend.network.RetrofitClient
import eina.unizar.frontend.notifications.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Servicio para verificar automáticamente el progreso de logros en segundo plano.
 *
 * Se ejecuta periódicamente y notifica al usuario cuando desbloquea nuevos logros.
 */
object LogrosSyncService {

    private var isRunning = false
    private const val SYNC_INTERVAL_MS = 60_000L // 1 minuto

    /**
     * Iniciar sincronización automática de logros.
     *
     * @param context Contexto de la aplicación
     * @param usuarioId ID del usuario
     * @param token Token de autenticación
     * @param onNuevosLogros Callback cuando se desbloquean nuevos logros
     */
    fun iniciarSincronizacion(
        context: Context,
        usuarioId: Int,
        token: String,
        onNuevosLogros: (List<LogroDesbloqueadoDTO>) -> Unit = {}
    ) {
        if (isRunning) {
            Log.d("LogrosSyncService", "Sincronización ya está corriendo")
            return
        }

        isRunning = true
        Log.d("LogrosSyncService", "Iniciando sincronización automática de logros")

        CoroutineScope(Dispatchers.IO).launch {
            while (isRunning) {
                try {
                    verificarProgreso(context, usuarioId, token, onNuevosLogros)
                    delay(SYNC_INTERVAL_MS)
                } catch (e: Exception) {
                    Log.e("LogrosSyncService", "Error en sincronización", e)
                    delay(SYNC_INTERVAL_MS)
                }
            }
        }
    }

    /**
     * Detener sincronización automática.
     */
    fun detenerSincronizacion() {
        isRunning = false
        Log.d("LogrosSyncService", "Sincronización detenida")
    }

    /**
     * Verificar progreso de logros una vez.
     */
    private fun verificarProgreso(
        context: Context,
        usuarioId: Int,
        token: String,
        onNuevosLogros: (List<LogroDesbloqueadoDTO>) -> Unit
    ) {
        Log.d("LogrosSyncService", "🔄 Verificando progreso de logros...")

        RetrofitClient.instance.verificarProgresoLogros("Bearer $token", usuarioId)
            .enqueue(object : Callback<eina.unizar.frontend.models.VerificarProgresoResponse> {
                override fun onResponse(
                    call: Call<eina.unizar.frontend.models.VerificarProgresoResponse>,
                    response: Response<eina.unizar.frontend.models.VerificarProgresoResponse>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        val nuevosLogros = body?.nuevosLogros ?: emptyList()

                        if (nuevosLogros.isNotEmpty()) {
                            Log.d("LogrosSyncService", "🎉 ${nuevosLogros.size} nuevo(s) logro(s) desbloqueado(s)!")

                            // Mostrar notificación por cada logro
                            nuevosLogros.forEach { logro ->
                                mostrarNotificacionLogro(context, logro)
                            }

                            // Callback para actualizar UI
                            onNuevosLogros(nuevosLogros)
                        } else {
                            Log.d("LogrosSyncService", "✓ Sin nuevos logros")
                        }
                    } else {
                        Log.e("LogrosSyncService", "Error ${response.code()}")
                    }
                }

                override fun onFailure(
                    call: Call<eina.unizar.frontend.models.VerificarProgresoResponse>,
                    t: Throwable
                ) {
                    Log.e("LogrosSyncService", "Error de red", t)
                }
            })
    }

    /**
     * Mostrar notificación local cuando se desbloquea un logro.
     */
    private fun mostrarNotificacionLogro(context: Context, logro: LogroDesbloqueadoDTO) {
        NotificationHelper.showAchievementNotification(
            context = context,
            achievementId = logro.id,
            title = "¡Logro Desbloqueado! ${logro.icono}",
            message = "${logro.nombre} (+${logro.puntos} puntos)"
        )
    }
}
