package com.loukwn.pairifier.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.loukwn.pairifier.domain.repository.PreferenceRepository

class PreferenceRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
) : PreferenceRepository {
    override fun isReceiver(): Boolean = sharedPreferences.getBoolean(KEY_IS_RECEIVER, false)

    override fun setIsReceiver(isReceiver: Boolean) {
        sharedPreferences.edit {
            putBoolean(KEY_IS_RECEIVER, isReceiver)
        }
    }

    companion object {
        private const val KEY_IS_RECEIVER = "KEY_IS_RECEIVER"
    }
}
