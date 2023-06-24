package com.loukwn.pairifier.services

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.loukwn.pairifier.domain.Event
import com.loukwn.pairifier.domain.PreferenceRepository
import com.loukwn.pairifier.domain.SenderRepository
import com.loukwn.pairifier.util.Logger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BatteryCheckWorker(
    private val context: Context,
    workerParameters: WorkerParameters,
) : CoroutineWorker(context, workerParameters), KoinComponent {

    private val preferenceRepository: PreferenceRepository by inject()
    private val senderRepository: SenderRepository by inject()

    init {
        Logger.d("Battery Worker init")
    }

    override suspend fun doWork(): Result {
        if (preferenceRepository.isReceiver()) return Result.success()

        val batteryState = getBatteryState(context)
        if (!batteryState.isCharging && batteryState.level <= BATTERY_LEVEL_LIMIT) {
            senderRepository.sendEvent(Event.LowBattery)
        }

        Logger.d("Battery Worker work $batteryState")

        return Result.success()
    }

    private fun getBatteryState(context: Context): BatteryState {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            context.registerReceiver(null, ifilter)
        }

        val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging: Boolean = status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL

        val level: Float = batteryStatus?.let { intent ->
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            level * 100 / scale.toFloat()
        } ?: 0f

        return BatteryState(
            level = level.toInt(),
            isCharging = isCharging,
        )
    }

    private data class BatteryState(
        val level: Int,
        val isCharging: Boolean,
    )

    companion object {
        private const val BATTERY_LEVEL_LIMIT = 25
    }
}
