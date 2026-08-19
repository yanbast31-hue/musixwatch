package com.exemple.fauxappel

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.Person

object NotificationHelper {

    const val CHANNEL_ID = "appels_entrants"
    const val NOTIFICATION_ID = 1

    const val ACTION_ANSWER = "com.exemple.fauxappel.ACTION_ANSWER"
    const val ACTION_DECLINE = "com.exemple.fauxappel.ACTION_DECLINE"
    const val ACTION_HANGUP = "com.exemple.fauxappel.ACTION_HANGUP"

    fun creerChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Appels entrants",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal utilisé pour le faux appel entrant"
                enableVibration(true)
            }
            manager.createNotificationChannel(channel)
        }
    }

    fun afficherAppelEntrant(context: Context) {
        creerChannel(context)

        // La personne qui "appelle" (nom affiché sur l'écran d'appel)
        val appelant = Person.Builder()
            .setName("Appel entrant")
            .build()

        // Intent déclenché quand on appuie sur "Répondre"
        val answerIntent = Intent(context, CallActionReceiver::class.java).apply {
            action = ACTION_ANSWER
        }
        val answerPendingIntent = PendingIntent.getBroadcast(
            context, 0, answerIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Intent déclenché quand on appuie sur "Refuser"
        val declineIntent = Intent(context, CallActionReceiver::class.java).apply {
            action = ACTION_DECLINE
        }
        val declinePendingIntent = PendingIntent.getBroadcast(
            context, 1, declineIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // L'écran plein écran qui s'affiche automatiquement (comme un vrai appel)
        val fullScreenIntent = Intent(context, IncomingCallActivity::class.java)
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context, 2, fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.sym_call_incoming)
            .setStyle(
                NotificationCompat.CallStyle.forIncomingCall(
                    appelant, declinePendingIntent, answerPendingIntent
                )
            )
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .build()

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, notification)
    }

    fun annulerNotification(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.cancel(NOTIFICATION_ID)
    }
}
