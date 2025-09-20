package com.celebrare.texteditor.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.lifecycle.ViewModel
import com.celebrare.texteditor.model.EditorAction
import com.celebrare.texteditor.model.EditorState
import com.celebrare.texteditor.model.TextElement

class TextEditorViewModel : ViewModel() {

    var state by mutableStateOf(EditorState())
        private set

    private val history = mutableListOf<EditorState>()
    private var historyIndex = -1

    init {
        history.add(state.copy())
        historyIndex = 0
    }

    private fun updateState(newState: EditorState, addToHistory: Boolean = true) {
        if (addToHistory) {
            if (historyIndex < history.size - 1) {
                history.subList(historyIndex + 1, history.size).clear()
            }
            history.add(newState.copy(textElements = newState.textElements.map { it.copy() }))
            historyIndex++
        }

        state = newState.copy(
            canUndo = historyIndex > 0,
            canRedo = historyIndex < history.size - 1
        )
    }

    fun handleAction(action: EditorAction) {
        when (action) {
            is EditorAction.AddText -> addText(action.text)
            is EditorAction.UpdateText -> updateText(action.elementId, action.newText)
            is EditorAction.OpenRenameDialog -> updateState(state.copy(elementToRename = action.element), addToHistory = false)
            is EditorAction.CloseRenameDialog -> updateState(state.copy(elementToRename = null), addToHistory = false)
            is EditorAction.OpenFontSizeDialog -> updateState(state.copy(isFontSizeDialogOpen = true), addToHistory = false)
            is EditorAction.CloseFontSizeDialog -> updateState(state.copy(isFontSizeDialogOpen = false), addToHistory = false)
            is EditorAction.MoveElement -> {
                val newElements = state.textElements.map { if (it.id == action.elementId) it.copy(position = action.newPosition) else it }
                updateState(state.copy(textElements = newElements), addToHistory = false)
            }
            is EditorAction.SelectElement -> updateState(state.copy(selectedElementId = action.elementId), addToHistory = false)
            is EditorAction.UpdateCanvasSize -> updateState(state.copy(canvasSize = action.newSize), addToHistory = false)
            is EditorAction.ShowAddTextDialog -> updateState(state.copy(isAddTextDialogOpen = true), addToHistory = false)
            is EditorAction.HideAddTextDialog -> updateState(state.copy(isAddTextDialogOpen = false), addToHistory = false)
            EditorAction.Undo -> undo()
            EditorAction.Redo -> redo()
            EditorAction.SaveDrag -> {
                updateState(state.copy(), addToHistory = true)
            }
            is EditorAction.SelectedElementAction -> applyToSelectedElement(action)
        }
    }

    private fun addText(text: String) {
        val textWidth = 24 * text.length * 0.6f
        val textHeight = 24 * 1.2f
        val initialX = (state.canvasSize.width / 2f) - (textWidth / 2f)
        val initialY = (state.canvasSize.height / 2f) - (textHeight / 2f)
        val newElement = TextElement(text = text, position = Offset(initialX.coerceAtLeast(0f), initialY.coerceAtLeast(0f)))
        val newState = state.copy(textElements = state.textElements + newElement, selectedElementId = newElement.id, isAddTextDialogOpen = false)
        updateState(newState)
    }

    private fun updateText(elementId: String, newText: String) {
        val newElements = state.textElements.map { if (it.id == elementId) it.copy(text = newText) else it }
        updateState(state.copy(textElements = newElements))
    }

    private fun applyToSelectedElement(action: EditorAction.SelectedElementAction) {
        val selectedId = state.selectedElementId ?: return
        val newElements = state.textElements.map { element ->
            if (element.id == selectedId) {
                when (action) {
                    is EditorAction.SelectedElementAction.SetFontSize -> element.copy(fontSize = action.size.coerceIn(10, 100))
                    is EditorAction.SelectedElementAction.IncreaseFontSize -> element.copy(fontSize = (element.fontSize + 2).coerceAtMost(100))
                    is EditorAction.SelectedElementAction.DecreaseFontSize -> element.copy(fontSize = (element.fontSize - 2).coerceAtLeast(10))
                    is EditorAction.SelectedElementAction.ToggleBold -> element.copy(fontWeight = if (element.fontWeight == FontWeight.Bold) FontWeight.Normal else FontWeight.Bold)
                    is EditorAction.SelectedElementAction.ToggleItalic -> element.copy(fontStyle = if (element.fontStyle == FontStyle.Italic) FontStyle.Normal else FontStyle.Italic)
                    is EditorAction.SelectedElementAction.ToggleUnderline -> element.copy(textDecoration = if (element.textDecoration == TextDecoration.Underline) TextDecoration.None else TextDecoration.Underline)
                    is EditorAction.SelectedElementAction.ToggleAlignment -> {
                        val nextAlignment = when (element.textAlign) {
                            TextAlign.Start -> TextAlign.Center
                            TextAlign.Center -> TextAlign.End
                            else -> TextAlign.Start
                        }
                        element.copy(textAlign = nextAlignment)
                    }
                    is EditorAction.SelectedElementAction.ChangeFontFamily -> element.copy(fontFamily = action.fontFamily)
                }
            } else {
                element
            }
        }
        updateState(state.copy(textElements = newElements))
    }

    private fun undo() {
        if (historyIndex > 0) {
            historyIndex--
            val previousState = history[historyIndex]
            updateState(previousState, addToHistory = false)
        }
    }

    private fun redo() {
        if (historyIndex < history.size - 1) {
            historyIndex++
            val nextState = history[historyIndex]
            updateState(nextState, addToHistory = false)
        }
    }
}
