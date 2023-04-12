package com.loukwn.pairifier.data

import com.google.android.gms.tasks.Task
import com.google.firebase.functions.HttpsCallableResult
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.loukwn.pairifier.util.Logger
import com.loukwn.pairifier.domain.Event
import com.loukwn.pairifier.domain.SenderRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SenderRepositoryImpl : SenderRepository {
    override fun sendEvent(event: Event) {
        when (event) {
            is Event.LowBattery -> handleLowBattery()
            is Event.PhoneCall -> handlePhoneCallEvent(event)
        }
    }

    private fun handleLowBattery() {
        callCloudFunction("lowBattery")
    }

    private fun handlePhoneCallEvent(event: Event.PhoneCall) {
        callCloudFunction(
            name = "phoneCall",
            payload = hashMapOf(
                "phoneNumber" to event.number,
                "contactName" to event.contactName,
            ),
        )
    }

    private fun callCloudFunction(name: String, payload: HashMap<String, Any?> = hashMapOf()) {
        val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val time = simpleDateFormat.format(Date())
        val newHashMap = payload.plus("time" to time)

        Logger.d("Calling $name with payload: $newHashMap")

        Firebase.functions
            .getHttpsCallable(name)
            .call(newHashMap)
            .addOnCompleteListener { task: Task<HttpsCallableResult?> ->
                if (task.isSuccessful) {
                    Logger.d("$name called successfully")
                } else {
                    Logger.e("Failed to call $name", task.exception)
                }
            }
    }
}