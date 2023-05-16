package com.intelligentbackpack.app.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.provider.Settings
import android.widget.Toast
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.common.Barcode
import com.intelligentbackpack.app.sensor.BarcodeAnalyser
import com.intelligentbackpack.app.ui.common.CameraViewWithPermission
import com.intelligentbackpack.app.ui.navigation.MainNavigation
import com.intelligentbackpack.app.view.form.NewSchoolSupplyForm
import com.intelligentbackpack.app.view.form.SchoolSupplyDetailsForm
import com.intelligentbackpack.app.viewdata.BookView
import com.intelligentbackpack.app.viewmodel.SchoolSupplyViewModel

@Composable
@ExperimentalGetImage
fun SchoolSupplyDetails(
    navController: NavHostController,
    rfid: String?,
    schoolSupplyViewModel: SchoolSupplyViewModel = viewModel(
        factory = SchoolSupplyViewModel.Factory
    ),
    context: Context = LocalContext.current
) {
    val nfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(context)
    nfcAdapter?.let {
        if (!it.isEnabled) {
            Toast.makeText(
                context,
                "Please activate NFC and press Back to return to the application!",
                Toast.LENGTH_LONG
            ).show()
            startActivity(context, Intent(Settings.ACTION_NFC_SETTINGS), null)
        }
        rfid?.let {
            val schoolSupplyView = schoolSupplyViewModel.schoolSupply.observeAsState()
            schoolSupplyViewModel.getSchoolSupply(rfid) {}
            schoolSupplyView.value?.let { schoolSupply ->
                SchoolSupplyDetailsForm(
                    schoolSupply,
                    onBack = {
                        navController.navigate(MainNavigation.home)
                    })
            } ?: SchoolSupplyDetailsPage(navController = navController, rfidTag = rfid)
        } ?: SchoolSupplyDetailsPage(navController = navController, rfidTag = null)
    } ?: run {
        Toast.makeText(context, "This device doesn't support NFC.", Toast.LENGTH_LONG).show()
        navController.navigate(MainNavigation.home)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class, ExperimentalComposeUiApi::class)
@Composable
@ExperimentalGetImage
fun SchoolSupplyDetailsPage(
    navController: NavHostController,
    rfidTag: String?,
    schoolSupplyViewModel: SchoolSupplyViewModel = viewModel(
        factory = SchoolSupplyViewModel.Factory,
    ),
    context: Context = LocalContext.current,
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        val rfid by rememberSaveable { mutableStateOf(rfidTag) }
        var isbn by rememberSaveable { mutableStateOf("") }
        var book: BookView? by rememberSaveable { mutableStateOf(null) }
        var openCameraDialog by remember { mutableStateOf(false) }
        var bookError by remember { mutableStateOf("") }
        val permissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
        val getBook = { isbnGet: String ->
            schoolSupplyViewModel.getBook(isbnGet, {
                book = it
                bookError = ""
            }, {
                bookError = it
            })
        }

        if (openCameraDialog) {
            Dialog(
                onDismissRequest = { openCameraDialog = false },
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = false,
                    usePlatformDefaultWidth = false
                )
            ) {
                CameraViewWithPermission(
                    topBar = {
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { openCameraDialog = false }) {
                                Icon(
                                    imageVector = Icons.TwoTone.ArrowBack,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                            Text(
                                "Scan ISBN",
                                color = Color.White,
                                fontSize = 20.sp
                            )
                        }
                    },
                    message = {
                        Text(
                            "Place the barcode inside the frame to scan it.",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    },
                    barcodeAnalyser = BarcodeAnalyser(
                        onBarcodeFound = {
                            isbn = it
                            getBook(it)
                            openCameraDialog = false
                            Toast.makeText(context, "Barcode found", Toast.LENGTH_SHORT).show()

                        },
                        options = BarcodeScannerOptions.Builder()
                            .setBarcodeFormats(Barcode.FORMAT_EAN_13)
                            .build()
                    ),
                    permissionState = permissionState,
                    onBack = { openCameraDialog = false },
                )
            }

        }
        TopAppBar(
            title = { Text("Add book copy") },
            navigationIcon = {
                IconButton(onClick = {
                    navController.navigate(MainNavigation.home)
                }) {
                    Icon(imageVector = Icons.Filled.ArrowBack, "Back", tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
        )
        NewSchoolSupplyForm(
            rfid = rfid,
            isbn = isbn,
            book = book,
            error = bookError,
            onIsbnChange = {
                isbn = it
                getBook(it)
            },
            createSchoolSupply = { _, _ ->/* TODO */ },
            openCameraDialog = {
                openCameraDialog = true
            }
        )
    }
}

@Preview
@Composable
@androidx.annotation.OptIn(ExperimentalGetImage::class)
fun SchoolSupplyDetailsPreview() {
    SchoolSupplyDetailsPage(navController = rememberNavController(), rfidTag = null)
}
