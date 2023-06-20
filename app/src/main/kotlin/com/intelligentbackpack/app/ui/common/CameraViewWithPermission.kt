package com.intelligentbackpack.app.ui.common

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale
import com.intelligentbackpack.app.sensor.BarcodeAnalyser
import com.intelligentbackpack.app.ui.shape.CutOutShape

@OptIn(ExperimentalPermissionsApi::class)
@Composable
@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
fun CameraViewWithPermission(
    topBar: @Composable () -> Unit,
    message: @Composable () -> Unit,
    barcodeAnalyser: BarcodeAnalyser,
    permissionState: PermissionState,
    onBack: () -> Unit,
    context: Context = LocalContext.current,
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        if (permissionState.status.isGranted) {
            Box {
                PreviewViewComposable(
                    modifier = Modifier.fillMaxSize(),
                    barcodeAnalyser = barcodeAnalyser,
                )
                Surface(
                    shape = CutOutShape(),
                    color = Color.Black.copy(alpha = 0.45f),
                    modifier = Modifier.fillMaxSize(),
                ) { }
                Column(
                    modifier = Modifier
                        .padding(top = 54.dp, start = 32.dp, end = 32.dp, bottom = 54.dp),
                ) {
                    topBar()
                    message()
                }
            }
        } else {
            if (permissionState.status.shouldShowRationale) {
                PermissionsDenied(
                    message = { Text(text = "Permission not available, please enable it in settings") },
                    onBackMessage = { Text(text = "Insert manually") },
                    onBack = onBack,
                    changePermissions = {
                        ContextCompat.startActivity(
                            context,
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts(
                                    "package",
                                    context.packageName,
                                    null,
                                )
                            },
                            null,
                        )
                    },
                ) { Text("Activate permission in settings") }
            } else {
                PermissionsDenied(
                    message = { Text(text = "Can't open camera without permission") },
                    onBackMessage = { Text(text = "Insert manually") },
                    onBack = onBack,
                    changePermissions = {
                        permissionState.launchPermissionRequest()
                    },
                    changePermissionsMessage = { Text(text = "Enable permission") },
                )
            }
        }
    }
}
