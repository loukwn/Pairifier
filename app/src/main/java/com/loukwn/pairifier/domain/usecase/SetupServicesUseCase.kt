package com.loukwn.pairifier.domain.usecase

import android.content.Context
import android.widget.Toast
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.loukwn.pairifier.services.BatteryCheckWorker
import com.loukwn.pairifier.util.Logger
import org.koin.core.component.KoinComponent
import java.util.concurrent.TimeUnit

class SetupServicesUseCase(private val context: Context) : KoinComponent {

    operator fun invoke(forReceiver: Boolean) {
        if (forReceiver) {
            subscribeToMessages()
            stopBatteryWorker()
        } else {
            unsubscribeFromMessages()
            initBatteryWorker()
        }
    }

    private fun subscribeToMessages() {
        Firebase.messaging.subscribeToTopic(PHONE_EVENTS_MESSAGING_TOPIC)
            .addOnCompleteListener { task ->
                val message = buildString {
                    append("Messages subscription result: ")
                    append(
                        if (task.isSuccessful) {
                            "Subscribed"
                        } else {
                            "Subscribe failed"
                        }
                    )
                }
                log(message)
            }
    }

    private fun unsubscribeFromMessages() {
        Firebase.messaging.unsubscribeFromTopic(PHONE_EVENTS_MESSAGING_TOPIC)
        log("Unsubscribed from messages")
    }

    private fun initBatteryWorker() {
        val workRequest = PeriodicWorkRequest.Builder(
            BatteryCheckWorker::class.java,
            PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
            TimeUnit.MILLISECONDS,
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            BATTERY_CHECKER_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest,
        )

        log("Initialised battery worker")
    }

    private fun stopBatteryWorker() {
        WorkManager.getInstance(context).cancelUniqueWork(BATTERY_CHECKER_WORK_NAME)
        log("Stopped battery worker")
    }

    private fun log(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        Logger.d(message)
    }

    companion object {
        private const val PHONE_EVENTS_MESSAGING_TOPIC = "phoneEvents"
        private const val BATTERY_CHECKER_WORK_NAME = "batteryChecker"
    }
}