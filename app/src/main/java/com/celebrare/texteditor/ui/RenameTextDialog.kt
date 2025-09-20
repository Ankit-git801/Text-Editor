package com.celebrare.texteditor.ui

import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.celebrare.texteditor.model.EditorAction
import com.celebrare.texteditor.model.TextElement

@Composable
fun RenameTextDialog(
    element: TextElement,
    onAction: (EditorAction) -> Unit,
    onDismiss: () -> Unit
) {
    var newText by remember { mutableStateOf(element.text) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rename Text") },
        text = {
            OutlinedTextField(
                value = newText,
                onValueChange = { newText = it },
                label = { Text("Enter new text") },
                singleLine = true
            )
        },
        confirmButton = {
            Button(onClick = {
                onAction(EditorAction.UpdateText(element.id, newText))
                onDismiss()
            }) {
                Text("Rename")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
