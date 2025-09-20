package com.celebrare.texteditor.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntSize
import java.util.UUID

data class TextElement(
    val id: String = UUID.randomUUID().toString(),
    var text: String,
    var position: Offset,
    var fontSize: Int = 24,
    var color: Color = Color.Black,
    var fontFamily: FontFamily = FontFamily.Default,
    var fontWeight: FontWeight = FontWeight.Normal,
    var fontStyle: FontStyle = FontStyle.Normal,
    var textDecoration: TextDecoration = TextDecoration.None
)

data class EditorState(
    val textElements: List<TextElement> = emptyList(),
    val selectedElementId: String? = null,
    val isAddTextDialogOpen: Boolean = false,
    val canvasSize: IntSize = IntSize.Zero,
    val canUndo: Boolean = false,
    val canRedo: Boolean = false
)

sealed class EditorAction {
    data class AddText(val text: String) : EditorAction()
    data class SelectElement(val elementId: String?) : EditorAction()
    data class MoveElement(val elementId: String, val newPosition: Offset) : EditorAction()
    data class UpdateCanvasSize(val newSize: IntSize) : EditorAction()
    object ShowAddTextDialog : EditorAction()
    object HideAddTextDialog : EditorAction()
    object Undo : EditorAction()
    object Redo : EditorAction()
    object SaveDrag : EditorAction()

    sealed class SelectedElementAction : EditorAction() {
        object IncreaseFontSize : SelectedElementAction()
        object DecreaseFontSize : SelectedElementAction()
        object ToggleBold : SelectedElementAction()
        object ToggleItalic : SelectedElementAction()
        object ToggleUnderline : SelectedElementAction()
        data class ChangeFontFamily(val fontFamily: FontFamily) : SelectedElementAction()
    }
}
