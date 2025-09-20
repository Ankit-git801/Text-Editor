package com.celebrare.texteditor.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.celebrare.texteditor.model.EditorAction
import com.celebrare.texteditor.viewmodel.TextEditorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextEditorScreen(
    modifier: Modifier = Modifier,
    viewModel: TextEditorViewModel = viewModel()
) {
    val state = viewModel.state

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Assignment", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth()) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        bottomBar = {
            Column {
                FormattingToolbar(
                    selectedElement = state.textElements.find { it.id == state.selectedElementId },
                    onAction = viewModel::handleAction
                )
                Button(
                    onClick = { viewModel.handleAction(EditorAction.ShowAddTextDialog) },
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0))
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Text", tint = Color.Black)
                    Spacer(Modifier.width(8.dp))
                    Text("Add Text", style = MaterialTheme.typography.titleMedium, color = Color.Black)
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            TextCanvas(
                state = state,
                onAction = viewModel::handleAction
            )

            if (state.isAddTextDialogOpen) {
                AddTextDialog(
                    onDismiss = { viewModel.handleAction(EditorAction.HideAddTextDialog) },
                    onConfirm = { text ->
                        viewModel.handleAction(EditorAction.AddText(text))
                    }
                )
            }
        }
    }
}

@Composable
private fun AddTextDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Your Text") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Enter text") }
            )
        },
        confirmButton = {
            Button(onClick = { if (text.isNotBlank()) onConfirm(text) }) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
