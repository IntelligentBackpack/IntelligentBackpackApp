package com.intelligentbackpack.app.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PermissionsDenied(
    message: @Composable () -> Unit,
    onBackMessage: @Composable () -> Unit,
    onBack: () -> Unit,
    changePermissions: () -> Unit,
    changePermissionsMessage: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize(0.8f)
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        message()
        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth(),
            enabled = true,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary,
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            shape = MaterialTheme.shapes.medium,
        ) {
            onBackMessage()
        }
        Button(
            onClick = {
                changePermissions()
            },
            modifier = Modifier
                .fillMaxWidth(),
            enabled = true,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.background,
            ),
            shape = MaterialTheme.shapes.medium,
        ) {
            changePermissionsMessage()
        }
    }
}
