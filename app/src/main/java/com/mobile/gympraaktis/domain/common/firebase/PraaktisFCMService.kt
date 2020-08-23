package com.mobile.gympraaktis.domain.common.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.domain.common.pref.SettingsStorage
import com.mobile.gympraaktis.ui.main.view.MainActivity

class PraaktisFCMService : FirebaseMessagingService() {

    private val settingsStorage by lazy { SettingsStorage.instance }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        sendNotification(remoteMessage.data)
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        settingsStorage.fcmToken = newToken
        settingsStorage.isSentFcmToken = false
    }

    private fun sendNotification(data: Map<String, String>) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("from_notification", true)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

        val reqCode = Math.random() * 10000

        val pendingIntent = PendingIntent.getActivity(
            this, reqCode.toInt() /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        var title = data["type"]
        val body = data["text"]

        when(title) {
            "ReSendFriendRequest" -> {
                title = "Resend friend request"
            }
            "FriendRequest" -> {
                title = "Friend request"
            }
            else -> {
                title = "Friend request accepted"
            }
        }

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Belgian_Hockey",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(reqCode.toInt() /* ID of notification */, notificationBuilder.build())
//        }
    }

}