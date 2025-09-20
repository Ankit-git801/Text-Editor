package com.celebrare.texteditor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.celebrare.texteditor.model.EditorAction
import com.celebrare.texteditor.model.EditorState
import com.celebrare.texteditor.model.TextElement
import kotlin.math.roundToInt

@Composable
fun TextCanvas(state: EditorState, onAction: (EditorAction) -> Unit, modifier: Modifier = Modifier) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(8.dp))
            .pointerInput(Unit) { detectTapGestures(onTap = { onAction(EditorAction.SelectElement(null)) }) }
    ) {
        val canvasWidthPx = with(LocalDensity.current) { maxWidth.toPx() }
        val canvasHeightPx = with(LocalDensity.current) { maxHeight.toPx() }

        LaunchedEffect(canvasWidthPx, canvasHeightPx) {
            onAction(EditorAction.UpdateCanvasSize(IntSize(canvasWidthPx.roundToInt(), canvasHeightPx.roundToInt())))
        }

        state.textElements.forEach { element ->
            DraggableText(
                element = element,
                isSelected = state.selectedElementId == element.id,
                onAction = onAction,
                canvasWidth = canvasWidthPx,
                canvasHeight = canvasHeightPx
            )
        }
    }
}

@Composable
private fun DraggableText(
    element: TextElement,
    isSelected: Boolean,
    onAction: (EditorAction) -> Unit,
    canvasWidth: Float,
    canvasHeight: Float
) {
    var offset by remember { mutableStateOf(element.position) }
    var composableSize by remember { mutableStateOf(IntSize.Zero) }

    // This is the key fix. This effect runs whenever the alignment or text size changes.
    // It calculates the correct position based on the REAL measured size of the composable.
    LaunchedEffect(element.textAlign, composableSize.width, canvasWidth) {
        if (composableSize.width > 0 && canvasWidth > 0) {
            val textWidth = composableSize.width.toFloat()
            val newX = when (element.textAlign) {
                TextAlign.Start -> 0f
                TextAlign.Center -> (canvasWidth - textWidth) / 2f
                TextAlign.End -> canvasWidth - textWidth
                else -> offset.x // Should not happen
            }
            // Coerce the value to ensure it never goes outside the bounds.
            val clampedX = newX.coerceIn(0f, canvasWidth - textWidth)
            if (offset.x != clampedX) {
                val newOffset = offset.copy(x = clampedX)
                offset = newOffset
                onAction(EditorAction.MoveElement(element.id, newOffset))
                onAction(EditorAction.SaveDrag) // Save this alignment change to history
            }
        }
    }

    LaunchedEffect(element.position) {
        offset = element.position
    }

    Box(
        modifier = Modifier
            .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
            .onSizeChanged { composableSize = it } // This gets the REAL size of the composable
            .pointerInput(element.id) {
                detectTapGestures(
                    onTap = {
                        onAction(EditorAction.SelectElement(element.id))
                        onAction(EditorAction.OpenRenameDialog(element))
                    }
                )
            }
            .pointerInput(element.id) {
                detectDragGestures(
                    onDragStart = { onAction(EditorAction.SelectElement(element.id)) },
                    onDragEnd = { onAction(EditorAction.SaveDrag) }
                ) { change, dragAmount ->
                    change.consume()
                    val newOffset = offset + dragAmount
                    val maxX = canvasWidth - composableSize.width
                    val maxY = canvasHeight - composableSize.height
                    offset = Offset(
                        x = newOffset.x.coerceIn(0f, maxX),
                        y = newOffset.y.coerceIn(0f, maxY)
                    )
                    onAction(EditorAction.MoveElement(element.id, offset))
                }
            }
            .then(if (isSelected) Modifier.border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp)) else Modifier)
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
