package com.celebrare.texteditor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.celebrare.texteditor.model.EditorAction
import com.celebrare.texteditor.model.TextElement
import com.celebrare.texteditor.ui.theme.InconsolataFamily
import com.celebrare.texteditor.ui.theme.LobsterFamily
import com.celebrare.texteditor.ui.theme.MerriweatherFamily

@Composable
fun FormattingToolbar(selectedElement: TextElement?, onAction: (EditorAction) -> Unit) {
    val isEnabled = selectedElement != null
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FontFamilyDropdown(isEnabled, selectedElement, onAction)
            FontSizeControls(isEnabled, selectedElement, onAction)
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            StyleButtons(isEnabled, selectedElement, onAction)
        }
    }
}

@Composable
private fun FontFamilyDropdown(isEnabled: Boolean, selectedElement: TextElement?, onAction: (EditorAction) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val fontFamilies = mapOf(
        "Default" to FontFamily.Default, "Lobster" to LobsterFamily,
        "Merriweather" to MerriweatherFamily, "Inconsolata" to InconsolataFamily
    )
    val currentFontName = fontFamilies.entries.find { it.value == selectedElement?.fontFamily }?.key ?: "Font"

    Box {
        Row(
            modifier = Modifier
                .width(140.dp)
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.background)
                .clickable(enabled = isEnabled) { expanded = true }
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(currentFontName, color = MaterialTheme.colorScheme.onBackground)
            Icon(Icons.Default.ArrowDropDown, "Dropdown", tint = MaterialTheme.colorScheme.onBackground)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            fontFamilies.forEach { (name, fontFamily) ->
                DropdownMenuItem(
                    text = { Text(name, fontFamily = fontFamily) },
                    onClick = {
                        onAction(EditorAction.SelectedElementAction.ChangeFontFamily(fontFamily))
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun RowScope.FontSizeControls(isEnabled: Boolean, selectedElement: TextElement?, onAction: (EditorAction) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 4.dp).weight(1f)
    ) {
        IconButton(onClick = { onAction(EditorAction.SelectedElementAction.DecreaseFontSize) }, enabled = isEnabled) { Icon(Icons.Default.Remove, "Decrease") }
        Text(selectedElement?.fontSize?.toString() ?: "10", modifier = Modifier.weight(1f).clickable(enabled = isEnabled) { onAction(EditorAction.OpenFontSizeDialog) }, textAlign = TextAlign.Center)
        IconButton(onClick = { onAction(EditorAction.SelectedElementAction.IncreaseFontSize) }, enabled = isEnabled) { Icon(Icons.Default.Add, "Increase") }
    }
}

@Composable
private fun StyleButtons(isEnabled: Boolean, selectedElement: TextElement?, onAction: (EditorAction) -> Unit) {
    Row(
        modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.background).padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StyleButton(Icons.Default.FormatBold, selectedElement?.fontWeight == FontWeight.Bold, isEnabled) { onAction(EditorAction.SelectedElementAction.ToggleBold) }
        StyleButton(Icons.Default.FormatItalic, selectedElement?.fontStyle == FontStyle.Italic, isEnabled) { onAction(EditorAction.SelectedElementAction.ToggleItalic) }
        StyleButton(Icons.Default.FormatUnderlined, selectedElement?.textDecoration == TextDecoration.Underline, isEnabled) { onAction(EditorAction.SelectedElementAction.ToggleUnderline) }
        val alignIcon = when (selectedElement?.textAlign) {
            TextAlign.Center -> Icons.Default.FormatAlignCenter
            TextAlign.End -> Icons.Default.FormatAlignRight
            else -> Icons.Default.FormatAlignLeft
        }
        StyleButton(alignIcon, true, isEnabled) { onAction(EditorAction.SelectedElementAction.ToggleAlignment) }
    }
}

@Composable
private fun StyleButton(icon: androidx.compose.ui.graphics.vector.ImageVector, isSelected: Boolean, isEnabled: Boolean, onClick: () -> Unit) {
    FilledTonalIconButton(
        onClick = onClick,
        enabled = isEnabled,
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground
        )
    ) {
        Icon(icon, null)
    }
}
