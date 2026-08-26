package com.example.tech24.ui.theme

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun CloseConfirmation(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogueTitle: String,
    dialogueText: String,
    icon: ImageVector,
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Alert")
        },
        title = { Text(text = dialogueTitle)},
        text = {
            Text(text = dialogueText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) { Text("End")}
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) { Text("Dismiss")}
        }
    )
}