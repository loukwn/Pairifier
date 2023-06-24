package com.loukwn.pairifier.util

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import com.loukwn.pairifier.PermissionModel
import com.loukwn.pairifier.granted
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object PermissionManager {
    suspend fun requestPermissions(
        activity: FragmentActivity,
        permission: String,
    ): Result {
        return when {
            PermissionModel.values().first { it.id == permission }.granted(activity) -> {
                Result.PermissionGranted
            }

            else -> {
                val permissionFragment = PermissionFragment()
                activity.supportFragmentManager.commit {
                    add(permissionFragment, "PermissionFragment")
                }
                val result = suspendCancellableCoroutine { continuation ->
                    permissionFragment.request(permission, continuation)

                    continuation.invokeOnCancellation {
                        if (continuation.isActive) {
                            continuation.resume(Result.PermissionDenied)
                        }
                    }
                }

                permissionFragment.clear()
                activity.supportFragmentManager.commit {
                    remove(permissionFragment)
                }

                result
            }
        }
    }

    enum class Result {
        PermissionGranted, PermissionDenied
    }
}

class PermissionFragment : Fragment() {
    private val permissionActivityResultContract =
        ActivityResultContracts.RequestPermission()

    private var continuation: CancellableContinuation<PermissionManager.Result>? = null
    private var permissionToRequestFor: String = ""

    val callback = { result: PermissionManager.Result ->
        if (continuation?.isActive == true) {
            continuation?.resume(result)
        }
    }

    private val permissionResultLauncher =
        registerForActivityResult(
            permissionActivityResultContract
        ) { granted ->
            if (granted) {
                callback(PermissionManager.Result.PermissionGranted)
            } else {
                callback(PermissionManager.Result.PermissionDenied)
            }
        }

    fun request(
        permission: String,
        continuation: CancellableContinuation<PermissionManager.Result>,
    ) {
        this.continuation = continuation
        this.permissionToRequestFor = permission
    }

    fun clear() {
        permissionResultLauncher.unregister()
        continuation = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionResultLauncher.launch(permissionToRequestFor)
    }
}
