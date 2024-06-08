package com.loukwn.pairifier

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import com.loukwn.pairifier.util.PermissionManager
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val uiModel = viewModel.stateFlow.collectAsState().value
            if (uiModel != null) {
                MainScreen(
                    uiModel = uiModel,
                    onAppTypeChanged = viewModel::onAppTypeChanged,
                    onPermissionGrantClicked = ::onPermissionGrantClicked,
                )
            }
        }
    }

    private fun onPermissionGrantClicked(permission: String) {
        lifecycleScope.launch {
            val permissionResult = PermissionManager.requestPermissions(
                activity = this@MainActivity,
                permission = permission
            )

            when (permissionResult) {
                PermissionManager.Result.PermissionGranted -> {
                    viewModel.refreshUi()
                }

                PermissionManager.Result.PermissionDenied -> {
                    Toast.makeText(
                        this@MainActivity,
                        "Permission denied. You might need to enable it in the app settings",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshUi()
    }
}
