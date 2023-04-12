package com.loukwn.pairifier.data

import android.content.SharedPreferences
import com.loukwn.pairifier.domain.PreferenceRepository

class PreferenceRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
) : PreferenceRepository {
    override fun isReceiver(): Boolean = sharedPreferences.getBoolean(KEY_IS_RECEIVER, false)

    override fun setIsReceiver(isReceiver: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean(KEY_IS_RECEIVER, isReceiver)
            apply()
        }
    }

    companion object {
        private const val KEY_IS_RECEIVER = "KEY_IS_RECEIVER"
    }
}