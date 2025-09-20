package com.celebrare.texteditor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.celebrare.texteditor.model.EditorAction
import com.celebrare.texteditor.model.EditorState
import com.celebrare.texteditor.model.TextElement
import kotlin.math.roundToInt

@Composable
fun TextCanvas(
    state: EditorState,
    onAction: (EditorAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(8.dp))
            .onSizeChanged { onAction(EditorAction.UpdateCanvasSize(it)) }
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onAction(EditorAction.SelectElement(null)) })
            }
    ) {
        state.textElements.forEach { element ->
            DraggableText(
                element = element,
                isSelected = element.id == state.selectedElementId,
                onAction = onAction
            )
        }

        Row(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            IconButton(onClick = { onAction(EditorAction.Undo) }, enabled = state.canUndo) { Icon(Icons.Default.Undo, "Undo") }
            IconButton(onClick = { onAction(EditorAction.Redo) }, enabled = state.canRedo) { Icon(Icons.Default.Redo, "Redo") }
        }
    }
}

@Composable
private fun DraggableText(
    element: TextElement,
    isSelected: Boolean,
    onAction: (EditorAction) -> Unit
) {
    var currentPosition by remember { mutableStateOf(element.position) }

    // Update internal position when the element's position changes from the ViewModel
    LaunchedEffect(element.position) {
        currentPosition = element.position
    }

    Box(
        modifier = Modifier
            .offset { IntOffset(currentPosition.x.roundToInt(), currentPosition.y.roundToInt()) }
            .pointerInput(element.id) {
                detectTapGestures(onTap = { onAction(EditorAction.SelectElement(element.id)) })
            }
            .pointerInput(element.id) {
                detectDragGestures(
                    onDragStart = { onAction(EditorAction.SelectElement(element.id)) },
                    onDragEnd = { onAction(EditorAction.SaveDrag) }
                ) { change, dragAmount ->
                    change.consume()
                    val newPosition = currentPosition + dragAmount
                    currentPosition = newPosition
                    onAction(EditorAction.MoveElement(element.id, newPosition))
                }
            }
            .then(
                if (isSelected) Modifier.border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
                else Modifier
            )
    ) {
        Text(
            text = element.text,
            modifier = Modifier.padding(8.dp),
            color = element.color,
            fontSize = element.fontSize.sp,
            fontFamily = element.fontFamily,
            fontWeight = element.fontWeight,
            fontStyle = element.fontStyle,
            textDecoration = element.textDecoration
        )
    }
}
