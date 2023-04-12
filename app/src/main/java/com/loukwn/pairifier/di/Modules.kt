package com.loukwn.pairifier.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.loukwn.pairifier.MainViewModel
import com.loukwn.pairifier.data.PreferenceRepositoryImpl
import com.loukwn.pairifier.data.SenderRepositoryImpl
import com.loukwn.pairifier.domain.PreferenceRepository
import com.loukwn.pairifier.domain.SenderRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<SharedPreferences> {
        get<Context>().getSharedPreferences(
            "PairifierPrefs",
            MODE_PRIVATE
        )
    }
    factory<PreferenceRepository> { PreferenceRepositoryImpl(get()) }
    factory<SenderRepository> { SenderRepositoryImpl() }

    viewModel { MainViewModel(get(), get()) }
}