package com.example.docadmin.Screens.Login

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.docadmin.ui.theme.Black


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginTextField(
    label: String,
    trailing: String,
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit
) {

    val uiColor = if (isSystemInDarkTheme()) Color.White else Black
    var text by remember { mutableStateOf("") }

    TextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = uiColor)
        },
        colors = TextFieldDefaults.colors(
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant, // Replaces unfocusedTextFieldText
            focusedPlaceholderColor = MaterialTheme.colorScheme.primary, // Replaces focusedTextFieldText
            unfocusedContainerColor = MaterialTheme.colorScheme.surface, // Replaces textFieldContainer
            focusedContainerColor = MaterialTheme.colorScheme.surface,
        ),
        trailingIcon = {
            TextButton(onClick = { /*TODO*/ }) {
                Text(
                    text = trailing,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                    color = uiColor
                )
            }
        }

    )

}