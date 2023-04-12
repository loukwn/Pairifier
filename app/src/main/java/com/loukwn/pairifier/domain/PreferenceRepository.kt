package com.loukwn.pairifier.domain

interface PreferenceRepository {
    fun isReceiver(): Boolean
    fun setIsReceiver(isReceiver: Boolean)
}