package com.loukwn.pairifier

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.loukwn.pairifier.di.appModule
import com.loukwn.pairifier.domain.PreferenceRepository
import com.loukwn.pairifier.util.Logger
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin

class PairifierApp : Application(), KoinComponent {
    private val preferenceRepository: PreferenceRepository by inject()
    override fun onCreate() {
        super.onCreate()

        startKoin()
        FirebaseApp.initializeApp(this)
        initMessaging()
    }

    private fun initMessaging() {
        if (preferenceRepository.isReceiver()) {
            Firebase.messaging.subscribeToTopic("phoneEvents")
                .addOnCompleteListener { task ->
                    var msg = "Subscribed"
                    if (!task.isSuccessful) {
                        msg = "Subscribe failed"
                    }
                    Logger.d("Firebase messaging support: $msg")
                }
        } else {
            Firebase.messaging.unsubscribeFromTopic("phoneEvents")
        }
    }

    private fun startKoin() {
        startKoin {
            androidLogger()
            androidContext(this@PairifierApp)
            modules(appModule)
        }
    }
}