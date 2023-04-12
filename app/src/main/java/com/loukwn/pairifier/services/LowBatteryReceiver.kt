package com.loukwn.pairifier.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.loukwn.pairifier.domain.Event
import com.loukwn.pairifier.domain.SenderRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LowBatteryReceiver : BroadcastReceiver(), KoinComponent {

    private val senderRepository: SenderRepository by inject()

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BATTERY_LOW) return
        senderRepository.sendEvent(Event.LowBattery)
    }
}