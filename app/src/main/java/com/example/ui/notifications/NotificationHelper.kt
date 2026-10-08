package com.example.ui.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R

object NotificationHelper {

    const val CHANNEL_TASKS_ID = "kinetix_tasks_channel_v2"
    const val CHANNEL_SCHEDULE_ID = "kinetix_schedule_channel_v2"
    const val CHANNEL_MEETINGS_ID = "kinetix_meetings_channel_v2"

    private var activeRingtone: Ringtone? = null

    fun initNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            val tasksChannel = NotificationChannel(
                CHANNEL_TASKS_ID,
                "Alarmas y Recordatorios de Tareas",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alarmas sonoras de tareas y fechas de entrega"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 250, 500, 250, 800)
                setSound(alarmSound, audioAttributes)
            }

            val scheduleChannel = NotificationChannel(
                CHANNEL_SCHEDULE_ID,
                "Horario de Estudio y Trabajo",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alertas sonoras de clases, turnos de trabajo y bloques de horario"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 400, 200, 400)
                setSound(alarmSound, audioAttributes)
            }

            val meetingsChannel = NotificationChannel(
                CHANNEL_MEETINGS_ID,
                "Recordatorios de Reuniones",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones y alertas sonoras de reuniones virtuales y presenciales"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 600, 300, 600)
                setSound(alarmSound, audioAttributes)
            }

            notificationManager.createNotificationChannel(tasksChannel)
            notificationManager.createNotificationChannel(scheduleChannel)
            notificationManager.createNotificationChannel(meetingsChannel)
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun playAlarmSound(context: Context) {
        try {
            stopAlarmSound()
            val alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            activeRingtone = RingtoneManager.getRingtone(context.applicationContext, alertUri)
            activeRingtone?.play()
        } catch (_: Exception) {}
    }

    fun stopAlarmSound() {
        try {
            activeRingtone?.let {
                if (it.isPlaying) it.stop()
            }
            activeRingtone = null
        } catch (_: Exception) {}
    }

    fun isAlarmPlaying(): Boolean {
        return try {
            activeRingtone?.isPlaying == true
        } catch (_: Exception) {
            false
        }
    }

    fun showTaskReminder(
        context: Context,
        title: String,
        message: String,
        ringAlarm: Boolean = false,
        notificationId: Int = (System.currentTimeMillis() % 10000).toInt()
    ) {
        initNotificationChannels(context)

        if (ringAlarm) {
            playAlarmSound(context)
        }

        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_TASKS_ID)
            .setSmallIcon(R.drawable.ic_kinetix_logo)
            .setContentTitle("⏰ Alarma: $title")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setSound(alarmSound)
            .setVibrate(longArrayOf(0, 500, 250, 500, 250, 800))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        try {
            notificationManager.notify(notificationId, builder.build())
        } catch (_: SecurityException) {
            // Permission not granted
        }
    }

    fun showScheduleReminder(
        context: Context,
        title: String,
        message: String,
        ringAlarm: Boolean = false,
        notificationId: Int = (System.currentTimeMillis() % 10000).toInt() + 10000
    ) {
        initNotificationChannels(context)

        if (ringAlarm) {
            playAlarmSound(context)
        }

        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_SCHEDULE_ID)
            .setSmallIcon(R.drawable.ic_kinetix_logo)
            .setContentTitle("🔔 Horario: $title")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setSound(alarmSound)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        try {
            notificationManager.notify(notificationId, builder.build())
        } catch (_: SecurityException) {
            // Permission not granted
        }
    }

    fun showMeetingReminder(
        context: Context,
        title: String,
        time: String,
        isVirtual: Boolean,
        locationOrLink: String,
        ringAlarm: Boolean = true,
        notificationId: Int = (System.currentTimeMillis() % 10000).toInt() + 20000
    ) {
        initNotificationChannels(context)

        if (ringAlarm) {
            playAlarmSound(context)
        }

        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val detailText = if (isVirtual) {
            "Reunión Virtual a las $time. Enlace: ${locationOrLink.ifBlank { "Plataforma no especificada" }}"
        } else {
            "Reunión Presencial a las $time. Lugar: ${locationOrLink.ifBlank { "Lugar acordado" }}"
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_MEETINGS_ID)
            .setSmallIcon(R.drawable.ic_kinetix_logo)
            .setContentTitle("📅 Tienes reunión en 1 hora: $title")
            .setContentText(detailText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(detailText))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setSound(alarmSound)
            .setVibrate(longArrayOf(0, 600, 300, 600))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        try {
            notificationManager.notify(notificationId, builder.build())
        } catch (_: SecurityException) {
            // Permission not granted
        }
    }
}
