package com.intelligentbackpack.app

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.intelligentbackpack.app.sensor.NFCTag.Companion.detectTagData
import com.intelligentbackpack.app.ui.navigation.MainNavigation
import com.intelligentbackpack.app.ui.theme.IntelligentBackpackAppTheme
import com.intelligentbackpack.app.view.CreateUser
import com.intelligentbackpack.app.view.Home
import com.intelligentbackpack.app.view.Login
import com.intelligentbackpack.app.view.SchoolSupplyDetails
import com.intelligentbackpack.app.view.UserDetails

class MainActivity : ComponentActivity() {

    private var nfcAdapter: NfcAdapter? = null
    private lateinit var pendingIntent: PendingIntent
    private lateinit var navController: NavHostController

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        nfcAdapter ?: run {
            Toast.makeText(
                this,
                "NO NFC Capabilities",
                Toast.LENGTH_SHORT,
            ).show()
        }
        val intent = Intent(this.applicationContext, this.javaClass)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        pendingIntent = PendingIntent.getActivity(
            this.applicationContext,
            0,
            intent,
            PendingIntent.FLAG_MUTABLE,
        )
        setContent {
            navController = rememberNavController()
            val viewModelStoreOwner = LocalViewModelStoreOwner.current!!
            IntelligentBackpackAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    NavHost(navController, startDestination = MainNavigation.login) {
                        composable(MainNavigation.login) {
                            CompositionLocalProvider(
                                LocalViewModelStoreOwner provides viewModelStoreOwner,
                            ) {
                                Login(navController)
                            }
                        }
                        composable(
                            MainNavigation.createUser,
                            arguments = listOf(navArgument(MainNavigation.createUserParam) { nullable = true }),
                        ) { backStackEntry ->
                            val email = backStackEntry.arguments?.getString(MainNavigation.createUserParam)
                            CompositionLocalProvider(
                                LocalViewModelStoreOwner provides viewModelStoreOwner,
                            ) {
                                CreateUser(
                                    navController = navController,
                                    email = email ?: "",
                                )
                            }
                        }
                        composable(MainNavigation.home) {
                            CompositionLocalProvider(
                                LocalViewModelStoreOwner provides viewModelStoreOwner,
                            ) {
                                Home(navController = navController)
                            }
                        }
                        composable(MainNavigation.user) {
                            CompositionLocalProvider(
                                LocalViewModelStoreOwner provides viewModelStoreOwner,
                            ) {
                                UserDetails(navController = navController)
                            }
                        }
                        composable(
                            MainNavigation.schoolSupply,
                            arguments = listOf(navArgument(MainNavigation.schoolSupplyParam) { nullable = true }),
                        ) { backStackEntry ->
                            val rfid = backStackEntry.arguments?.getString(MainNavigation.schoolSupplyParam)
                            CompositionLocalProvider(
                                LocalViewModelStoreOwner provides viewModelStoreOwner,
                            ) {
                                SchoolSupplyDetails(navController = navController, rfid = rfid)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        intent?.let { resolveIntent(it) }
    }

    @Suppress("DEPRECATION")
    private fun resolveIntent(intent: Intent) {
        val action = intent.action
        if (NfcAdapter.ACTION_TAG_DISCOVERED == action ||
            NfcAdapter.ACTION_TECH_DISCOVERED == action ||
            NfcAdapter.ACTION_NDEF_DISCOVERED == action
        ) {
            val tag: Tag? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
            } else {
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            }
            tag?.let { detectTagData(it) }
                .let {
                    if (navController.currentDestination?.route != MainNavigation.login &&
                        navController.currentDestination?.route != MainNavigation.createUser
                    ) {
                        navController.navigate(MainNavigation.schoolSupply(it?.rfidId)) { launchSingleTop = true }
                    }
                }
        }
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }
}
