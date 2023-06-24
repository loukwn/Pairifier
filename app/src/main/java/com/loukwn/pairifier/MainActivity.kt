@file:OptIn(ExperimentalMaterial3Api::class)

package com.loukwn.pairifier

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.loukwn.pairifier.ui.theme.PairifierTheme
import com.loukwn.pairifier.util.PermissionManager
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val uiModel = viewModel.stateFlow.collectAsState().value

            PairifierTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (uiModel != null) {
                        Screen(
                            uiModel = uiModel,
                            onAppTypeChanged = viewModel::onAppTypeChanged,
                            onPermissionGrantClicked = ::onPermissionGrantClicked,
                        )
                    }
                }
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

@Composable
private fun Screen(
    uiModel: UiModel,
    onAppTypeChanged: (AppType) -> Unit,
    onPermissionGrantClicked: (String) -> Unit,
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            stringResource(id = R.string.app_name),
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 32.sp),
        )
        Separator()
        AppTypePart(uiModel.type, onAppTypeChanged)
        if (uiModel.permissionsNeeded.isNotEmpty()) {
            Separator()
            PermissionListPart(uiModel.permissionsNeeded, onPermissionGrantClicked)
        }
    }
}

@Composable
fun AppTypePart(
    appType: AppType,
    onAppTypeChanged: (AppType) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(stringResource(id = R.string.main_app_type_title), style = TextStyle(fontSize = 18.sp))
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            val senderSelected = appType == AppType.Sender
            val receiverSelected = !senderSelected

            FilterChip(
                selected = senderSelected,
                onClick = { onAppTypeChanged(AppType.Sender) },
                label = { Text(stringResource(id = R.string.main_sender)) },
                leadingIcon = if (senderSelected) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                }
            )

            Spacer(modifier = Modifier.width(8.dp))

            FilterChip(
                selected = receiverSelected,
                onClick = { onAppTypeChanged(AppType.Receiver) },
                label = { Text(stringResource(id = R.string.main_receiver)) },
                leadingIcon = if (receiverSelected) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "",
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                }
            )
        }
    }
}

@Composable
fun Separator() {
    Column {
        Spacer(modifier = Modifier.height(32.dp))
        Box(
            modifier = Modifier
                .size(40.dp, 1.dp)
                .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun PermissionListPart(
    permissionsNeeded: List<PermissionModel>,
    onPermissionGrantClicked: (String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            stringResource(id = R.string.main_permissions_title),
            style = TextStyle(fontSize = 18.sp)
        )
        permissionsNeeded.forEach { permissionModel ->
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        stringResource(id = permissionModel.title),
                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    )
                    Text(
                        stringResource(id = permissionModel.description),
                        style = TextStyle(fontSize = 14.sp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = { onPermissionGrantClicked(permissionModel.id) }) {
                    Text(stringResource(id = R.string.main_permissions_grant))
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GreetingPreview() {
    PairifierTheme {
        Screen(
            UiModel(
                AppType.Receiver,
                listOf()
            ), {}, {})
    }
}
