package com.loukwn.pairifier.services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.loukwn.pairifier.util.Logger
import com.loukwn.pairifier.domain.PreferenceRepository
import org.koin.android.ext.android.inject

// NOT USED ATM
class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val preferenceRepository: PreferenceRepository by inject()

    private val NOTIFICATION_CHANNEL_ID = "default_channel_id"
    private val NOTIFICATION_CHANNEL_NAME = "Default Channel"
    private val NOTIFICATION_CHANNEL_DESCRIPTION = "Default notification channel"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (!preferenceRepository.isReceiver()) return
        Logger.d("Received message: data=${remoteMessage.data} - notification=${remoteMessage.notification}")

        // Check if message contains data payload
//        if (remoteMessage.data.isNotEmpty()) {
//            // Get notification data
//            val phoneNumber = remoteMessage.data["phoneNumber"]
//            val contactName = remoteMessage.data["contactName"]
//            val messageBody = "$phoneNumber by ${contactName ?: "unknown"}."
//
//            val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
//                .setContentTitle("New call received")
//                .setContentText(messageBody)
//                .setAutoCancel(true)
//                .build()
//
//            // Create notification channel if necessary
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                val channel = NotificationChannel(
//                    NOTIFICATION_CHANNEL_ID,
//                    NOTIFICATION_CHANNEL_NAME,
//                    NotificationManager.IMPORTANCE_DEFAULT
//                ).apply {
//                    description = NOTIFICATION_CHANNEL_DESCRIPTION
//                }
//                val notificationManager = getSystemService(NotificationManager::class.java)
//                notificationManager?.createNotificationChannel(channel)
//            }
//
//            // Display notification
//            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.notify(0, notification)
//        }
    }
}
