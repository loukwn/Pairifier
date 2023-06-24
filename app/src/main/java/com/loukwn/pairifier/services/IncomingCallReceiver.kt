package com.loukwn.pairifier.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.telephony.TelephonyManager
import com.loukwn.pairifier.domain.repository.Event
import com.loukwn.pairifier.domain.repository.PreferenceRepository
import com.loukwn.pairifier.domain.repository.SenderRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class IncomingCallReceiver : BroadcastReceiver(), KoinComponent {

    private val preferenceRepository: PreferenceRepository by inject()
    private val senderRepository: SenderRepository by inject()

    override fun onReceive(context: Context, intent: Intent) {
        if (preferenceRepository.isReceiver()) return
        if (intent.action != TelephonyManager.ACTION_PHONE_STATE_CHANGED) return

        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val state = telephonyManager.callState
        val phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

        if (state == TelephonyManager.CALL_STATE_RINGING && phoneNumber != null) {
            val contactName: String? =
                getContactNameFromNumber(context = context, phoneNumber = phoneNumber)

            val event = Event.PhoneCall(
                number = phoneNumber,
                contactName = contactName,
            )

            senderRepository.sendEvent(event)
        }
    }

    private fun getContactNameFromNumber(context: Context, phoneNumber: String): String? {
        val uri = Uri.withAppendedPath(
            /* baseUri = */ ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            /* pathSegment = */ Uri.encode(phoneNumber)
        )
        val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)
        var contactName: String? = null

        context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(0)
            }
        }

        return contactName
    }
}
