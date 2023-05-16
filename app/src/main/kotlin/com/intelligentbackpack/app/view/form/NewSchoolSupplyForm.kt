package com.intelligentbackpack.app.view.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.intelligentbackpack.app.ui.common.BookDetails
import com.intelligentbackpack.app.viewdata.BookView

/**
 * Form for creating a new school supply.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewSchoolSupplyForm(
    rfid: String?,
    isbn: String,
    book: BookView?,
    error: String?,
    onIsbnChange: (isbn: String) -> Unit,
    createSchoolSupply: (isbn: String, rfid: String) -> Unit,
    openCameraDialog: () -> Unit,
    localFocusManager: FocusManager = LocalFocusManager.current,
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically)
    )
    {
        if (rfid != null) {
            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start)
            ) {
                Text(text = "RFID tag found")
                Icon(imageVector = Icons.Outlined.Done, "")
            }

        } else {
            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start)
            ) {
                Text(text = "No RFID tag found")
                Icon(imageVector = Icons.Outlined.ErrorOutline, "")
            }
        }
        OutlinedTextField(
            value = isbn,
            onValueChange = {
                onIsbnChange(it)
                if (it.length == 13) {
                    localFocusManager.clearFocus()
                }
            },
            placeholder = { TextFieldValue("ISBN") },
            label = { Text(text = "ISBN") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = {
                    openCameraDialog()
                }) {
                    Icon(imageVector = Icons.Filled.PhotoCamera, "scan isbn code")
                }
            },
        )
        book?.let {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start)
            ) {
                Text(text = "Book found")
                Icon(imageVector = Icons.Outlined.Done, "")
            }
            BookDetails(it)
        } ?: error?.let {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start)
            ) {
                Text(text = it)
                Icon(imageVector = Icons.Outlined.ErrorOutline, "")
            }
        }
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(
                onClick = { createSchoolSupply(isbn, rfid!!) },
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(top = 10.dp),
                enabled = true,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.background
                ),
                shape = MaterialTheme.shapes.medium,
            )
            {
                Text("Add book copy")
            }
        }
    }
}

@Preview
@Composable
fun NewSchoolSupplyFormPreview() {
    NewSchoolSupplyForm(null, "", book = null, "", { _ -> }, { _, _ -> }, {})
}
