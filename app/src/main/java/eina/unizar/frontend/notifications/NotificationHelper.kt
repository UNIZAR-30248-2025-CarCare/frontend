package eina.unizar.frontend.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import eina.unizar.frontend.MainActivity
import eina.unizar.frontend.R

/**
 * Helper estático para crear canales y mostrar notificaciones locales.
 *
 * Responsabilidades:
 * - Crear canales de notificación (Android O+).
 * - Construir y mostrar notificaciones para reservas y mantenimientos.
 * - Respetar las preferencias del usuario antes de mostrar notificaciones.
 * - Incluir PendingIntent que abre MainActivity y deja extras para que la app
 *   pueda navegar a la pantalla correspondiente al pulsar la notificación.
 */
object NotificationHelper {
    const val CHANNEL_RESERVATIONS = "reservations_channel"
    const val CHANNEL_MAINTENANCE = "maintenance_channel"

    const val NOTIFICATION_ID_RESERVATION = 1001
    const val NOTIFICATION_ID_MAINTENANCE = 1002

    const val CHANNEL_ACHIEVEMENTS = "achievements_channel"
    const val NOTIFICATION_ID_ACHIEVEMENT = 2001

    /**
     * Crea los canales de notificación necesarios en Android O+.
     */
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelReservations = NotificationChannel(
                CHANNEL_RESERVATIONS,
                "Recordatorios de Reservas",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de tus próximas reservas"
            }

            val channelMaintenance = NotificationChannel(
                CHANNEL_MAINTENANCE,
                "Alertas de Mantenimiento",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Recordatorios de revisiones y mantenimientos"
            }

            // ========== NUEVO CANAL PARA LOGROS ==========
            val channelAchievements = NotificationChannel(
                CHANNEL_ACHIEVEMENTS,
                "Logros Desbloqueados",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones cuando desbloqueas nuevos logros"
                enableVibration(true)
                enableLights(true)
            }
            // =============================================

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channelReservations)
            notificationManager.createNotificationChannel(channelMaintenance)
            notificationManager.createNotificationChannel(channelAchievements) // ⬅️ CREAR CANAL
        }
    }


    /**
     * Verifica si la app tiene permiso para mostrar notificaciones.
     *
     * @param context Contexto de la aplicación
     * @return true si tiene permiso, false en caso contrario
     */
    private fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // En versiones anteriores a Android 13, el permiso se otorga automáticamente
            true
        }
    }

    /**
     * Muestra una notificación de recordatorio de reserva.
     *
     * - Comprueba preferencias de notificación antes de mostrar.
     * - Verifica permisos de notificación.
     * - Crea un PendingIntent que abre MainActivity con extras:
     *   navigate_to = "reservation_detail" y reservation_id = reservationId
     * - Usa un notificationId único sumando NOTIFICATION_ID_RESERVATION + reservationId.
     */
    fun showReservationNotification(
        context: Context,
        reservationId: Int,
        title: String,
        message: String
    ) {
        // Verificar preferencias del usuario
        if (!NotificationPreferences.areReservationNotificationsEnabled(context)) {
            return
        }

        // Verificar permisos
        if (!hasNotificationPermission(context)) {
            return
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "reservation_detail")
            putExtra("reservation_id", reservationId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            reservationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_RESERVATIONS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(
                NOTIFICATION_ID_RESERVATION + reservationId,
                notification
            )
        } catch (e: SecurityException) {
            // El usuario revocó los permisos después de que se programó la notificación
            e.printStackTrace()
        }
    }

    /**
     * Muestra una notificación de mantenimiento/revisión.
     *
     * - Comprueba preferencias de notificación antes de mostrar.
     * - Verifica permisos de notificación.
     * - Crea un PendingIntent que abre MainActivity con extras:
     *   navigate_to = "maintenance_detail" y maintenance_id = maintenanceId
     * - Usa un notificationId único sumando NOTIFICATION_ID_MAINTENANCE + maintenanceId.
     */
    fun showMaintenanceNotification(
        context: Context,
        maintenanceId: Int,
        title: String,
        message: String
    ) {
        // Verificar preferencias del usuario
        if (!NotificationPreferences.areMaintenanceNotificationsEnabled(context)) {
            return
        }

        // Verificar permisos
        if (!hasNotificationPermission(context)) {
            return
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "maintenance_detail")
            putExtra("maintenance_id", maintenanceId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            maintenanceId + 10000,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_MAINTENANCE)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(
                NOTIFICATION_ID_MAINTENANCE + maintenanceId,
                notification
            )
        } catch (e: SecurityException) {
            // El usuario revocó los permisos después de que se programó la notificación
            e.printStackTrace()
        }
    }

    /**
     * Mostrar notificación de logro desbloqueado.
     *
     * @param context Contexto de la aplicación
     * @param achievementId ID del logro
     * @param title Título de la notificación
     * @param message Mensaje de la notificación
     */
    fun showAchievementNotification(
        context: Context,
        achievementId: Int,
        title: String,
        message: String
    ) {
        // Verificar permisos
        if (!hasNotificationPermission(context)) {
            return
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "logros")
            putExtra("achievement_id", achievementId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            achievementId + 20000, // Offset único para logros
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ACHIEVEMENTS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 500, 200, 500)) // Vibración especial
            .setLights(0xFFD700, 1000, 500) // Luz dorada
            .build()

        try {
            NotificationManagerCompat.from(context).notify(
                NOTIFICATION_ID_ACHIEVEMENT + achievementId,
                notification
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}