package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.ui.theme.WADarkBg

@Composable
fun WhatsAppDoodleBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WADarkBg)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val doodleColor = Color(0xFFFFFFFF).copy(alpha = 0.045f)
            val stroke = Stroke(width = 1.6f)

            val spacingX = 85f
            val spacingY = 95f
            val cols = (size.width / spacingX).toInt() + 2
            val rows = (size.height / spacingY).toInt() + 2

            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    val offsetX = c * spacingX + if (r % 2 == 1) spacingX * 0.5f else 0f
                    val offsetY = r * spacingY
                    val doodleType = (r * 7 + c * 3) % 8

                    when (doodleType) {
                        0 -> drawChatBubbleDoodle(offsetX, offsetY, doodleColor, stroke)
                        1 -> drawHeartDoodle(offsetX, offsetY, doodleColor, stroke)
                        2 -> drawMusicNoteDoodle(offsetX, offsetY, doodleColor, stroke)
                        3 -> drawStarDoodle(offsetX, offsetY, doodleColor, stroke)
                        4 -> drawCoffeeCupDoodle(offsetX, offsetY, doodleColor, stroke)
                        5 -> drawSearchDoodle(offsetX, offsetY, doodleColor, stroke)
                        6 -> drawPaperclipDoodle(offsetX, offsetY, doodleColor, stroke)
                        else -> drawCameraDoodle(offsetX, offsetY, doodleColor, stroke)
                    }
                }
            }
        }
        content()
    }
}

private fun DrawScope.drawChatBubbleDoodle(x: Float, y: Float, color: Color, stroke: Stroke) {
    val path = Path().apply {
        moveTo(x - 8f, y - 6f)
        lineTo(x + 8f, y - 6f)
        quadraticTo(x + 12f, y - 6f, x + 12f, y - 2f)
        lineTo(x + 12f, y + 4f)
        quadraticTo(x + 12f, y + 8f, x + 8f, y + 8f)
        lineTo(x - 4f, y + 8f)
        lineTo(x - 8f, y + 12f)
        lineTo(x - 8f, y + 8f)
        quadraticTo(x - 12f, y + 8f, x - 12f, y + 4f)
        lineTo(x - 12f, y - 2f)
        quadraticTo(x - 12f, y - 6f, x - 8f, y - 6f)
        close()
    }
    drawPath(path, color, style = stroke)
}

private fun DrawScope.drawHeartDoodle(x: Float, y: Float, color: Color, stroke: Stroke) {
    val path = Path().apply {
        moveTo(x, y - 2f)
        cubicTo(x - 6f, y - 10f, x - 12f, y - 2f, x, y + 8f)
        cubicTo(x + 12f, y - 2f, x + 6f, y - 10f, x, y - 2f)
        close()
    }
    drawPath(path, color, style = stroke)
}

private fun DrawScope.drawMusicNoteDoodle(x: Float, y: Float, color: Color, stroke: Stroke) {
    drawCircle(color, radius = 3.5f, center = Offset(x - 4f, y + 4f), style = stroke)
    drawCircle(color, radius = 3.5f, center = Offset(x + 5f, y + 2f), style = stroke)
    drawLine(color, start = Offset(x - 1f, y + 4f), end = Offset(x - 1f, y - 7f), strokeWidth = stroke.width)
    drawLine(color, start = Offset(x + 8f, y + 2f), end = Offset(x + 8f, y - 9f), strokeWidth = stroke.width)
    drawLine(color, start = Offset(x - 1f, y - 7f), end = Offset(x + 8f, y - 9f), strokeWidth = stroke.width * 1.5f)
}

private fun DrawScope.drawStarDoodle(x: Float, y: Float, color: Color, stroke: Stroke) {
    drawLine(color, start = Offset(x, y - 7f), end = Offset(x, y + 7f), strokeWidth = stroke.width)
    drawLine(color, start = Offset(x - 7f, y), end = Offset(x + 7f, y), strokeWidth = stroke.width)
    drawLine(color, start = Offset(x - 5f, y - 5f), end = Offset(x + 5f, y + 5f), strokeWidth = stroke.width * 0.8f)
    drawLine(color, start = Offset(x - 5f, y + 5f), end = Offset(x + 5f, y - 5f), strokeWidth = stroke.width * 0.8f)
}

private fun DrawScope.drawCoffeeCupDoodle(x: Float, y: Float, color: Color, stroke: Stroke) {
    val path = Path().apply {
        moveTo(x - 7f, y - 4f)
        lineTo(x + 7f, y - 4f)
        lineTo(x + 5f, y + 6f)
        quadraticTo(x, y + 8f, x - 5f, y + 6f)
        close()
    }
    drawPath(path, color, style = stroke)
    // Handle
    drawArc(
        color = color,
        startAngle = 270f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(x + 4f, y - 2f),
        size = Size(6f, 6f),
        style = stroke
    )
}

private fun DrawScope.drawSearchDoodle(x: Float, y: Float, color: Color, stroke: Stroke) {
    drawCircle(color, radius = 5f, center = Offset(x - 2f, y - 2f), style = stroke)
    drawLine(color, start = Offset(x + 2f, y + 2f), end = Offset(x + 7f, y + 7f), strokeWidth = stroke.width * 1.2f)
}

private fun DrawScope.drawPaperclipDoodle(x: Float, y: Float, color: Color, stroke: Stroke) {
    val path = Path().apply {
        moveTo(x - 4f, y + 5f)
        lineTo(x + 4f, y - 3f)
        arcTo(
            rect = androidx.compose.ui.geometry.Rect(x + 1f, y - 7f, x + 7f, y - 1f),
            startAngleDegrees = 180f,
            sweepAngleDegrees = 180f,
            forceMoveTo = false
        )
        lineTo(x - 2f, y + 3f)
    }
    drawPath(path, color, style = stroke)
}

private fun DrawScope.drawCameraDoodle(x: Float, y: Float, color: Color, stroke: Stroke) {
    drawRoundRect(
        color = color,
        topLeft = Offset(x - 7f, y - 4f),
        size = Size(14f, 10f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f),
        style = stroke
    )
    drawCircle(color, radius = 2.5f, center = Offset(x, y + 1f), style = stroke)
}
