package com.celebrare.texteditor.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.celebrare.texteditor.R
import com.celebrare.texteditor.model.EditorAction
import com.celebrare.texteditor.model.TextElement
import com.celebrare.texteditor.viewmodel.TextEditorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextEditorScreen(modifier: Modifier = Modifier, viewModel: TextEditorViewModel = viewModel()) {
    val state = viewModel.state

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.app_name), fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White, titleContentColor = Color.Black)
            )
        },
        bottomBar = {
            Column {
                FormattingToolbar(selectedElement = state.textElements.find { it.id == state.selectedElementId }, onAction = viewModel::handleAction)
                Button(onClick = { viewModel.handleAction(EditorAction.ShowAddTextDialog) }, modifier = Modifier.fillMaxWidth().height(60.dp), shape = RoundedCornerShape(0.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0))) {
                    Icon(Icons.Default.Add, "Add Text", tint = Color.Black); Spacer(Modifier.width(8.dp)); Text("Add Text", style = MaterialTheme.typography.titleMedium, color = Color.Black)
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = modifier.padding(paddingValues).padding(horizontal = 16.dp)) {
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.handleAction(EditorAction.Undo) }, enabled = state.canUndo) { Icon(Icons.Default.Undo, "Undo") }
                Spacer(modifier = Modifier.width(24.dp))
                IconButton(onClick = { viewModel.handleAction(EditorAction.Redo) }, enabled = state.canRedo) { Icon(Icons.Default.Redo, "Redo") }
            }
            Box(modifier = Modifier.weight(1f).padding(bottom = 8.dp)) {
                TextCanvas(state = state, onAction = viewModel::handleAction)
            }
        }

        if (state.isAddTextDialogOpen) {
            AddTextDialog(onDismiss = { viewModel.handleAction(EditorAction.HideAddTextDialog) }, onConfirm = { viewModel.handleAction(EditorAction.AddText(it)) })
        }
        if (state.isFontSizeDialogOpen) {
            FontSizeDialog(initialSize = state.textElements.find { it.id == state.selectedElementId }?.fontSize ?: 24, onDismiss = { viewModel.handleAction(EditorAction.CloseFontSizeDialog) }, onConfirm = { newSize ->
                viewModel.handleAction(EditorAction.SelectedElementAction.SetFontSize(newSize))
                viewModel.handleAction(EditorAction.CloseFontSizeDialog)
            })
        }
        state.elementToRename?.let { element ->
            RenameTextDialog(element = element, onConfirm = { newText ->
                viewModel.handleAction(EditorAction.UpdateText(element.id, newText))
                viewModel.handleAction(EditorAction.CloseRenameDialog)
            }, onDismiss = { viewModel.handleAction(EditorAction.CloseRenameDialog) })
        }
    }
}

@Composable
private fun AddTextDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var text by remember { mutableStateOf("") }; AlertDialog(onDismissRequest = onDismiss, title = { Text("Add Your Text") }, text = { OutlinedTextField(value = text, onValueChange = { text = it }, label = { Text("Enter text") }) }, confirmButton = { Button(onClick = { if (text.isNotBlank()) onConfirm(text) }) { Text("Add") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } })
}

@Composable
private fun FontSizeDialog(initialSize: Int, onDismiss: () -> Unit, onConfirm: (Int) -> Unit) {
    var sizeText by remember { mutableStateOf(initialSize.toString()) }; AlertDialog(onDismissRequest = onDismiss, title = { Text("Set Font Size") }, text = { OutlinedTextField(value = sizeText, onValueChange = { sizeText = it.filter { c -> c.isDigit() } }, label = { Text("Size (10-100)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)) }, confirmButton = { Button(onClick = { sizeText.toIntOrNull()?.let(onConfirm) }) { Text("Set") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } })
}

@Composable
private fun RenameTextDialog(element: TextElement, onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    var newText by remember { mutableStateOf(element.text) }; AlertDialog(onDismissRequest = onDismiss, title = { Text("Rename Text") }, text = { OutlinedTextField(value = newText, onValueChange = { newText = it }, label = { Text("Enter new text") }, singleLine = true) }, confirmButton = { Button(onClick = { onConfirm(newText) }) { Text("Rename") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } })
}
