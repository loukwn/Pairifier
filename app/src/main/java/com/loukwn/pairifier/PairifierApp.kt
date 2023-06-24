package com.loukwn.pairifier

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.loukwn.pairifier.di.appModule
import com.loukwn.pairifier.di.workerModule
import com.loukwn.pairifier.domain.PreferenceRepository
import com.loukwn.pairifier.services.BatteryCheckWorker
import com.loukwn.pairifier.util.Logger
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import java.util.concurrent.TimeUnit

class PairifierApp : Application(), KoinComponent {
    private val preferenceRepository: PreferenceRepository by inject()
    override fun onCreate() {
        super.onCreate()

        startKoin()
        FirebaseApp.initializeApp(this)

        if (preferenceRepository.isReceiver()) {
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
                val message = if (task.isSuccessful) {
                    "Subscribed"
                } else {
                    "Subscribe failed"
                }
                Logger.d("Firebase messaging support: $message")
            }
    }

    private fun unsubscribeFromMessages() {
        Firebase.messaging.unsubscribeFromTopic(PHONE_EVENTS_MESSAGING_TOPIC)
    }

    private fun initBatteryWorker() {
        val workRequest = PeriodicWorkRequest.Builder(
            BatteryCheckWorker::class.java,
            PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
            TimeUnit.MILLISECONDS,
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            BATTERY_CHECKER_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest,
        )
    }

    private fun stopBatteryWorker() {
        WorkManager.getInstance(this).cancelUniqueWork(BATTERY_CHECKER_WORK_NAME)
    }

    private fun startKoin() {
        startKoin {
            androidLogger()
            androidContext(this@PairifierApp)
            modules(appModule, workerModule)
        }
    }

    companion object {
        private const val PHONE_EVENTS_MESSAGING_TOPIC = "phoneEvents"
        private const val BATTERY_CHECKER_WORK_NAME = "batteryChecker"
    }
}
