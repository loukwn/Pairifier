package com.loukwn.pairifier.di

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.loukwn.pairifier.MainViewModel
import com.loukwn.pairifier.data.PreferenceRepositoryImpl
import com.loukwn.pairifier.data.SenderRepositoryImpl
import com.loukwn.pairifier.domain.PreferenceRepository
import com.loukwn.pairifier.domain.SenderRepository
import com.loukwn.pairifier.services.BatteryCheckWorker
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val appModule = module {
    single<SharedPreferences> {
        androidContext().getSharedPreferences(
            "PairifierPrefs",
            MODE_PRIVATE
        )
    }
    factory<PreferenceRepository> { PreferenceRepositoryImpl(get()) }
    factory<SenderRepository> { SenderRepositoryImpl() }

    viewModel { MainViewModel(get(), get()) }
}

val workerModule = module {
    worker { BatteryCheckWorker(androidContext(), get()) }
}
