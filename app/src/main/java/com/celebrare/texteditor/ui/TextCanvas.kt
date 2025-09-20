package com.celebrare.texteditor.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
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

    // This effect ensures that when alignment changes, the offset is recalculated and clamped.
    LaunchedEffect(element.textAlign, composableSize.width) {
        val textWidth = composableSize.width.toFloat()
        val newX = when (element.textAlign) {
            TextAlign.Start -> 0f
            TextAlign.Center -> (canvasWidth / 2f) - (textWidth / 2f)
            TextAlign.End -> canvasWidth - textWidth
            else -> offset.x
        }
        offset = offset.copy(x = newX.coerceIn(0f, canvasWidth - textWidth))
        onAction(EditorAction.MoveElement(element.id, offset))
    }

    LaunchedEffect(element.position) {
        offset = element.position
    }

    Box(
        modifier = Modifier
            .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
            .onSizeChanged { composableSize = it }
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
