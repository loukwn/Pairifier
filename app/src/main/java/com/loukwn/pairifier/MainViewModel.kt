package com.loukwn.pairifier

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakewharton.processphoenix.ProcessPhoenix
import com.loukwn.pairifier.domain.PreferenceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@SuppressLint("StaticFieldLeak") // App context
class MainViewModel(
    private val context: Context,
    private val preferencesRepository: PreferenceRepository,
) : ViewModel() {
    private val _stateFlow: MutableStateFlow<UiModel?> = MutableStateFlow(null)
    val stateFlow: StateFlow<UiModel?> = _stateFlow

    private val updateFlow = MutableSharedFlow<Unit>(1)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            updateFlow.onStart { emit(Unit) }.collectLatest {
                _stateFlow.update { model ->
                    val type = if (preferencesRepository.isReceiver()) {
                        AppType.Receiver
                    } else {
                        AppType.Sender
                    }

                    val listPermissions = buildList {
                        val isInVersionThatRequiresNotificationPermission =
                            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU

                        val needsNotification = isInVersionThatRequiresNotificationPermission &&
                                !PermissionModel.Notification.granted(context) &&
                                type == AppType.Receiver

                        if (needsNotification) {
                            add(PermissionModel.Notification)
                        }

                        val needsPhoneState = !PermissionModel.ReadPhoneState.granted(context) &&
                                type == AppType.Sender
                        if (needsPhoneState) {
                            add(PermissionModel.ReadPhoneState)
                        }

                        val needsCallLogs = !PermissionModel.ReadCallLogs.granted(context) &&
                                type == AppType.Sender
                        if (needsCallLogs) {
                            add(PermissionModel.ReadCallLogs)
                        }

                        val needsContacts = !PermissionModel.ReadContacts.granted(context) &&
                                type == AppType.Sender
                        if (needsContacts) {
                            add(PermissionModel.ReadContacts)
                        }
                    }

                    model?.copy(
                        type = type,
                        permissionsNeeded = listPermissions
                    ) ?: UiModel(type, listPermissions)
                }
            }
        }
    }

    fun onAppTypeChanged(appType: AppType) {
        val isReceiver = appType == AppType.Receiver
        preferencesRepository.setIsReceiver(isReceiver = isReceiver)
        ProcessPhoenix.triggerRebirth(context)
    }

    fun refreshUi() {
        updateFlow.tryEmit(Unit)
    }
}

enum class AppType { Sender, Receiver }

enum class PermissionModel(
    val id: String,
    @StringRes val title: Int,
    @StringRes val description: Int,
) {
    Notification(
        id = Manifest.permission.POST_NOTIFICATIONS,
        title = R.string.main_permission_notifications_title,
        description = R.string.main_permission_notifications_description,
    ),
    ReadPhoneState(
        id = Manifest.permission.READ_PHONE_STATE,
        title = R.string.main_permission_phone_state_title,
        description = R.string.main_permission_phone_state_description,
    ),
    ReadCallLogs(
        id = Manifest.permission.READ_CALL_LOG,
        title = R.string.main_permission_call_logs_title,
        description = R.string.main_permission_call_logs_description,
    ),
    ReadContacts(
        id = Manifest.permission.READ_CONTACTS,
        title = R.string.main_permission_contacts_title,
        description = R.string.main_permission_contacts_description,
    ),
}

fun PermissionModel.granted(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(context, this.id) == PackageManager.PERMISSION_GRANTED
}

@Immutable
data class UiModel(
    val type: AppType,
    val permissionsNeeded: List<PermissionModel>,
)
