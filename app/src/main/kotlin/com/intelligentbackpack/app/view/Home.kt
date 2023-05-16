package com.intelligentbackpack.app.view

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Announcement
import androidx.compose.material.icons.outlined.Backpack
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.intelligentbackpack.app.ui.navigation.MainNavigation
import com.intelligentbackpack.app.ui.navigation.NavigationItem
import com.intelligentbackpack.app.viewdata.UserView
import com.intelligentbackpack.app.viewmodel.LoginViewModel
import kotlinx.coroutines.launch


@Composable
fun Home(
    navController: NavHostController,
    loginViewModel: LoginViewModel = viewModel(
        factory = LoginViewModel.Factory
    )
) {
    val user = loginViewModel.user.observeAsState()
    if (user.value == null) {
        navController.navigate(MainNavigation.login)
    }
    val context = LocalContext.current
    val pInfo: PackageInfo = context.packageManager.getPackageInfoCompat(context.packageName, 0)
    val version = pInfo.versionName
    val versionCode = pInfo.longVersionCode
    HomePage(
        navController = navController, user = user.value!!,
        logout = {
            loginViewModel.logout {
                navController.navigate(MainNavigation.login)
            }
        },
        version = version, versionCode = versionCode
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    navController: NavHostController,
    user: UserView,
    logout: () -> Unit,
    version: String,
    versionCode: Long,
) {
    var selectedTab by rememberSaveable { mutableStateOf(0) }
    val tabs = listOf(
        NavigationItem(
            title = "Items",
            icon = Icons.Outlined.Book
        ) {
            //navController.navigate(MainNavigation.items)
        },
        NavigationItem(
            title = "Calendar",
            icon = Icons.Outlined.CalendarToday
        ) {
            //navController.navigate(MainNavigation.calendar)
        },
        NavigationItem(
            title = "Backpack",
            icon = Icons.Outlined.Backpack
        ) {
            //navController.navigate(MainNavigation.backpack)
        },
        NavigationItem(
            title = "Forget",
            icon = Icons.Outlined.Announcement
        ) {
            //navController.navigate(MainNavigation.backpack)
        }
    )
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val items = listOf(
        NavigationItem(
            title = "User info",
            icon = Icons.Default.Person
        ) {
            navController.navigate(MainNavigation.user)
        },
        NavigationItem(
            title = "Logout",
            icon = Icons.Default.Logout
        ) {
            scope.launch {
                logout()
            }
        }
    )
    val selectedItem = remember { mutableStateOf(items[0]) }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(Modifier.height(12.dp))
                    Icon(
                        imageVector = Icons.Rounded.AccountCircle,
                        contentDescription = "Account",
                        modifier = Modifier
                            .height(100.dp)
                            .width(100.dp)
                    )
                    Text(
                        text = user.let { it.name + " " + it.surname },
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(Modifier.height(12.dp))
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth(0.8f),
                        color = Color.Gray,
                        thickness = 1.dp
                    )
                    Spacer(Modifier.height(12.dp))
                    items.forEach { item ->
                        NavigationDrawerItem(
                            icon = { Icon(item.icon, contentDescription = null) },
                            label = { Text(item.title) },
                            selected = item == selectedItem.value,
                            onClick = {
                                item.action()
                                /*scope.launch {
                                    if (item == "Logout") {
                                        logout()
                                    } else {
                                        drawerState.close()
                                        selectedItem.value = item
                                    }
                                }*/
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth(0.8f),
                        color = Color.Gray,
                        thickness = 1.dp
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(text = "Version: $version ($versionCode)")
                    Text(text = "Android version: ${Build.VERSION.RELEASE}")
                    Spacer(Modifier.height(12.dp))
                }
            }
        },
        content = {
            Scaffold(
                topBar = {
                    TopAppBar(
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(imageVector = Icons.Outlined.Menu, contentDescription = "Menu")
                            }
                        },
                        title = { Text("") },
                        backgroundColor = MaterialTheme.colorScheme.primary,
                    )
                },
            ) { contentPadding ->
                Box(
                    modifier = Modifier
                        .padding(contentPadding)
                        .fillMaxSize(),
                    Alignment.BottomStart
                ) {
                    Scaffold(
                        bottomBar = {
                            NavigationBar {
                                tabs.forEachIndexed { index, item ->
                                    NavigationBarItem(
                                        icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                                        label = { Text(item.title) },
                                        selected = selectedTab == index,
                                        onClick = {
                                            selectedTab = index
                                            item.action()
                                        }
                                    )
                                }
                            }
                        }
                    ) { contentPadding ->
                        Box(
                            modifier = Modifier
                                .padding(contentPadding)
                                .fillMaxSize(),
                            Alignment.BottomStart
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val viewModelStoreOwner = LocalViewModelStoreOwner.current!!
                                val itemsNavController = rememberNavController()
                                NavHost(itemsNavController, startDestination = "test") {
                                    composable("test") {
                                        CompositionLocalProvider(
                                            LocalViewModelStoreOwner provides viewModelStoreOwner
                                        ) {

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

fun PackageManager.getPackageInfoCompat(packageName: String, flags: Int = 0): PackageInfo =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(flags.toLong()))
    } else {
        @Suppress("DEPRECATION") getPackageInfo(packageName, flags)
    }

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    val navController = rememberNavController()
    val user = UserView(
        name = "John",
        surname = "Doe",
        email = "JohnDoe@gmail.com",
        password = "Test#1234"
    )
    HomePage(navController, user, {}, "1.0.0", 1)
}