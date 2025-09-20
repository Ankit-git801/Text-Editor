package com.celebrare.texteditor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.celebrare.texteditor.model.EditorAction
import com.celebrare.texteditor.model.TextElement

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormattingToolbar(
    selectedElement: TextElement?,
    onAction: (EditorAction) -> Unit
) {
    val isEnabled = selectedElement != null

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 1. Font Family Dropdown
            FontFamilyDropdown(isEnabled, selectedElement, onAction)
            Spacer(modifier = Modifier.width(4.dp))

            // 2. Font Size Controls
            FontSizeControls(isEnabled, selectedElement, onAction)
            Spacer(modifier = Modifier.weight(1f))

            // 3. Style Buttons (Bold, Italic, Underline)
            StyleButtons(isEnabled, selectedElement, onAction)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FontFamilyDropdown(isEnabled: Boolean, selectedElement: TextElement?, onAction: (EditorAction) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val fontFamilies = listOf("Default", "Serif", "Cursive")
    val currentFontName = when (selectedElement?.fontFamily) {
        FontFamily.Serif -> "Serif"
        FontFamily.Cursive -> "Cursive"
        else -> "Font"
    }

    ExposedDropdownMenuBox(
        expanded = isEnabled && expanded,
        onExpandedChange = { if (isEnabled) expanded = !expanded }
    ) {
        TextField(
            value = currentFontName,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.menuAnchor().width(120.dp),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.LightGray,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            enabled = isEnabled
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            fontFamilies.forEach { name ->
                DropdownMenuItem(
                    text = { Text(name, fontFamily = if (name == "Cursive") FontFamily.Cursive else if (name == "Serif") FontFamily.Serif else FontFamily.Default) },
                    onClick = {
                        val fontFamily = when (name) {
                            "Serif" -> FontFamily.Serif
                            "Cursive" -> FontFamily.Cursive
                            else -> FontFamily.Default
                        }
                        onAction(EditorAction.SelectedElementAction.ChangeFontFamily(fontFamily))
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun FontSizeControls(isEnabled: Boolean, selectedElement: TextElement?, onAction: (EditorAction) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(horizontal = 4.dp)
    ) {
        IconButton(onClick = { onAction(EditorAction.SelectedElementAction.DecreaseFontSize) }, enabled = isEnabled) {
            Icon(Icons.Default.Remove, "Decrease size")
        }
        Text(selectedElement?.fontSize?.toString() ?: "10", modifier = Modifier.padding(horizontal = 8.dp))
        IconButton(onClick = { onAction(EditorAction.SelectedElementAction.IncreaseFontSize) }, enabled = isEnabled) {
            Icon(Icons.Default.Add, "Increase size")
        }
    }
}

@Composable
private fun StyleButtons(isEnabled: Boolean, selectedElement: TextElement?, onAction: (EditorAction) -> Unit) {
    Row(
        modifier = Modifier
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(horizontal = 4.dp)
    ) {
        // Bold Button
        StyleButton(
            icon = Icons.Default.FormatBold,
            isSelected = selectedElement?.fontWeight == FontWeight.Bold,
            isEnabled = isEnabled,
            onClick = { onAction(EditorAction.SelectedElementAction.ToggleBold) }
        )
        // Italic Button
        StyleButton(
            icon = Icons.Default.FormatItalic,
            isSelected = selectedElement?.fontStyle == FontStyle.Italic,
            isEnabled = isEnabled,
            onClick = { onAction(EditorAction.SelectedElementAction.ToggleItalic) }
        )
        // Underline Button
        StyleButton(
            icon = Icons.Default.FormatUnderlined,
            isSelected = selectedElement?.textDecoration == TextDecoration.Underline,
            isEnabled = isEnabled,
            onClick = { onAction(EditorAction.SelectedElementAction.ToggleUnderline) }
        )
    }
}

@Composable
private fun StyleButton(icon: androidx.compose.ui.graphics.vector.ImageVector, isSelected: Boolean, isEnabled: Boolean, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        enabled = isEnabled,
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Black,
            disabledContentColor = Color.Gray
        )
    ) {
        Icon(icon, contentDescription = null)
    }
}
