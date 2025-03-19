package com.example.droid.gestores

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.droid.PantallaPartida
import com.example.droid.R


class GestorNotificaciones(private val context: Context) {

    private val CHANNEL_ID = "partidaGanada_channel"
    init {
        crearCanalNotificacionVistoria()
    }

    private fun crearCanalNotificacionVistoria() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        { val name = "Canal Victoria"
            val descriptionText = "Canal para notificar victorias"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableLights(true)
                lightColor = Color.GREEN
                enableVibration(true)
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun NotificacionVictoria() {

        val intent = Intent(context, PantallaPartida::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_solo_monochrome48)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.logo_solo))
            .setContentTitle("Felicidades")
            .setContentText("¡Felicidades, has ganado!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            notify(1, builder.build())
        }
    }
}