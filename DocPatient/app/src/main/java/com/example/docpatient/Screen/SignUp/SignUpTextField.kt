package com.example.docpatient.Screen.SignUp

import android.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpTextField(
    modifier: Modifier = Modifier,
    label: String,
    trailing: String = "",
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false // Add flag for password fields
) {
    val uiColor = if (isSystemInDarkTheme()) Color.WHITE else Color.BLACK
    var passwordVisible by remember { mutableStateOf(false) }

    TextField(
        modifier = modifier,
        value = value,
        singleLine = true,
        onValueChange = onValueChange, // Now controlled externally
        label = {
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
                //color = uiColor)
        },
        colors = TextFieldDefaults.colors(
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedPlaceholderColor = MaterialTheme.colorScheme.primary,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
        ),
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None, // Hide/show password
        trailingIcon = {
            if (isPassword) {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.KeyboardArrowUp else Icons.Default.ArrowDropDown,
                        contentDescription = "Toggle password visibility",
                        //tint = uiColor
                    )
                }
            } else if (trailing.isNotEmpty()) {
                TextButton(onClick = { /*TODO*/ }) {
//                    Text(
//                        text = trailing,
//                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
//                        color = uiColor
//                    )
                    Text(
                        text = trailing,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                        //color = uiColor
                    )
                }
            }
        }
    )
}
