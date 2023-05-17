package com.intelligentbackpack.app.view

import android.Manifest
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.common.Barcode
import com.intelligentbackpack.app.sensor.BarcodeAnalyser
import com.intelligentbackpack.app.ui.common.CameraViewWithPermission
import com.intelligentbackpack.app.ui.common.SchoolSupplyCard
import com.intelligentbackpack.app.viewmodel.BackpackViewModel

/**
 * The content of the backpack screen.
 */
@OptIn(ExperimentalPermissionsApi::class, ExperimentalComposeUiApi::class)
@Composable
fun BackpackContent(
    navController: NavHostController,
    backpackViewModel: BackpackViewModel = viewModel(
        factory = BackpackViewModel.Factory,
    ),
    context: Context = LocalContext.current,
) {
    var openErrorDialog by remember { mutableStateOf(false) }
    var errorDialogMessage by remember { mutableStateOf("") }
    val isBackpackAssociated = backpackViewModel.isBackpackAssociated.observeAsState(false)
    if (openErrorDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text(
                    text = "Error with the " +
                        if (isBackpackAssociated.value) {
                            "disassociation"
                        } else {
                            "association"
                        },
                )
            },
            text = {
                Text(text = errorDialogMessage)
            },
            confirmButton = {
                Button(
                    onClick = {
                        openErrorDialog = false
                    },
                ) {
                    Text("Confirm")
                }
            },
        )
    }
    backpackViewModel.getBackpackAssociated { error ->
        errorDialogMessage = error
        openErrorDialog = true
    }
    if (isBackpackAssociated.value) {
        val backpack = backpackViewModel.backpack.observeAsState(setOf())
        backpackViewModel.subscribe { error ->
            errorDialogMessage = error
            openErrorDialog = true
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp, horizontal = 0.dp),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Backpack",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Button(
                            onClick = {
                                backpackViewModel.disassociateBackpack({
                                    Toast.makeText(context, "Backpack disassociated", Toast.LENGTH_SHORT).show()
                                }, { error ->
                                    errorDialogMessage = error
                                    openErrorDialog = true
                                })
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .padding(top = 10.dp),
                            enabled = true,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.background,
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                            shape = MaterialTheme.shapes.medium,
                        ) {
                            Text("Associate backpack")
                        }
                    }
                }
                items(backpack.value.toList()) {
                    SchoolSupplyCard(
                        navHostController = navController,
                        schoolSupply = it,
                    )
                }
            }
        }
    } else {
        var openCameraDialog by remember { mutableStateOf(false) }
        val permissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
        if (openCameraDialog) {
            Dialog(
                onDismissRequest = { openCameraDialog = false },
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = false,
                    usePlatformDefaultWidth = false,
                ),
            ) {
                CameraViewWithPermission(
                    topBar = {
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            IconButton(onClick = { openCameraDialog = false }) {
                                Icon(
                                    imageVector = Icons.TwoTone.ArrowBack,
                                    contentDescription = null,
                                    tint = Color.White,
                                )
                            }
                            Text(
                                "Scan QR Code",
                                color = Color.White,
                                fontSize = 20.sp,
                            )
                        }
                    },
                    message = {
                        Text(
                            "Place the QR code inside the frame to scan it.",
                            color = Color.White,
                            fontSize = 12.sp,
                        )
                    },
                    barcodeAnalyser = BarcodeAnalyser(
                        onBarcodeFound = {
                            openCameraDialog = false
                            backpackViewModel.associateBackpack(it, success = {
                                Toast.makeText(context, "Backpack associated", Toast.LENGTH_SHORT)
                                    .show()
                            }, error = { error ->
                                errorDialogMessage = error
                                openErrorDialog = true
                            })
                            Toast.makeText(context, "QR code found", Toast.LENGTH_SHORT).show()
                        },
                        options = BarcodeScannerOptions.Builder()
                            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                            .build(),
                    ),
                    permissionState = permissionState,
                    onBack = { openCameraDialog = false },
                )
            }
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        ) {
            Text(text = "No backpack associated")
            Button(
                onClick = {
                    openCameraDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(top = 10.dp),
                enabled = true,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.background,
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                shape = MaterialTheme.shapes.medium,
            ) {
                Text("Associate backpack")
            }
        }
    }
}

@Preview
@Composable
fun BackpackContentPreview() {
    BackpackContent(navController = rememberNavController())
}
