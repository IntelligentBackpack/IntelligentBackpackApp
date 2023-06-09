package com.intelligentbackpack.app.view.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.intelligentbackpack.app.ui.common.BookDetails
import com.intelligentbackpack.app.viewdata.BookView

/**
 * Form for creating a new school supply.
 *
 * @param rfid RFID tag of the school supply.
 * @param isbn ISBN of the book.
 * @param book Book associated with the school supply.
 * @param error Error message to display.
 * @param onIsbnChange Callback for when the ISBN changes.
 * @param createSchoolSupply Callback for when the school supply is created.
 * @param openCameraDialog Callback for when the camera dialog should be opened.
 * @param localFocusManager Focus manager.
 */
@Composable
fun NewSchoolSupplyForm(
    rfid: String?,
    isbn: String,
    book: BookView?,
    error: String?,
    onIsbnChange: (isbn: String) -> Unit,
    createSchoolSupply: (book: BookView?, rfid: String?) -> Unit,
    openCameraDialog: () -> Unit,
    localFocusManager: FocusManager = LocalFocusManager.current,
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
    ) {
        if (rfid != null) {
            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start),
            ) {
                Text(text = "RFID tag found")
                Icon(imageVector = Icons.Outlined.Done, "")
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start),
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
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
            ),
        )
        book?.let {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start),
            ) {
                Text(text = "Book found")
                Icon(imageVector = Icons.Outlined.Done, "")
            }
            BookDetails(book = it)
        } ?: error?.let {
            if (it.isNotBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start),
                ) {
                    Text(text = it)
                    Icon(imageVector = Icons.Outlined.ErrorOutline, "")
                }
            }
        }
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(
                onClick = { createSchoolSupply(book, rfid) },
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(top = 10.dp),
                enabled = true,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.background,
                ),
                shape = MaterialTheme.shapes.medium,
            ) {
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
