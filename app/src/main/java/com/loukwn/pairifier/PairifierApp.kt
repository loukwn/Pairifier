package com.loukwn.pairifier

import android.app.Application
import com.google.firebase.FirebaseApp
import com.loukwn.pairifier.di.appModule
import com.loukwn.pairifier.di.workerModule
import com.loukwn.pairifier.domain.repository.PreferenceRepository
import com.loukwn.pairifier.domain.usecase.SetupServicesUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin

class PairifierApp : Application(), KoinComponent {
    private val preferenceRepository: PreferenceRepository by inject()
    private val setupServicesUseCase: SetupServicesUseCase by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin()
        FirebaseApp.initializeApp(this)
        setupServicesUseCase(forReceiver = preferenceRepository.isReceiver())
    }


    private fun startKoin() {
        startKoin {
            androidLogger()
            androidContext(this@PairifierApp)
            modules(appModule, workerModule)
        }
    }
}
