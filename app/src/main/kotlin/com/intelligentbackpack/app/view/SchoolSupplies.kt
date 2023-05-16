package com.intelligentbackpack.app.view

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.intelligentbackpack.app.ui.common.SchoolSupplyCard
import com.intelligentbackpack.app.ui.navigation.MainNavigation
import com.intelligentbackpack.app.viewmodel.SchoolSupplyViewModel

@Composable
fun SchoolSupplies(
    navController: NavHostController,
    schoolSupplyViewModel: SchoolSupplyViewModel = viewModel(
        factory = SchoolSupplyViewModel.Factory,
    )
) {
    val context = LocalContext.current
    val schoolSupplies = schoolSupplyViewModel.schoolSupplies.observeAsState(emptySet())
    schoolSupplyViewModel.getSchoolSupplies {
        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp, horizontal = 0.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
        ) {
            items(schoolSupplies.value.toList()) {
                SchoolSupplyCard(
                    navHostController = navController,
                    schoolSupply = it,
                    modifier = Modifier.fillMaxSize(0.9f)
                )
            }
        }
        FloatingActionButton(
            modifier = Modifier
                .padding(all = 16.dp)
                .align(alignment = Alignment.BottomEnd),
            shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 50)),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.background,
            onClick = {
                navController.navigate(MainNavigation.schoolSupply(null))
            }
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
        }
    }
}

@Preview
@Composable
fun SchoolSuppliesPreview() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        SchoolSupplies(navController = rememberNavController())
    }
}