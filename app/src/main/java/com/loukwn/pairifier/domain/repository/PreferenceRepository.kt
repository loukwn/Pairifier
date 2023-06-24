package com.loukwn.pairifier.domain.repository

interface PreferenceRepository {
    fun isReceiver(): Boolean
    fun setIsReceiver(isReceiver: Boolean)
}
