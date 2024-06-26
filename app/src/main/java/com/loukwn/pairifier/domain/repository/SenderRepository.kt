package com.loukwn.pairifier.domain.repository

interface SenderRepository {
    fun sendEvent(event: Event)
}

sealed class Event {
    data class PhoneCall(val number: String, val contactName: String?): Event()
    object LowBattery: Event()
}
