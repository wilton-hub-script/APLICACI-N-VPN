package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenDark

@Composable
fun CyberBackgroundCanvas(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        val strokeColor = NeonGreen.copy(alpha = 0.85f)
        val faintColor = NeonGreen.copy(alpha = 0.25f)
        val circuitStroke = Stroke(width = 3.5f)
        val thinStroke = Stroke(width = 1.5f)

        // Top Left Circuit Brackets
        val topLeftPath = Path().apply {
            moveTo(24f, 180f)
            lineTo(24f, 120f)
            lineTo(48f, 96f)
            lineTo(110f, 96f)
        }
        drawPath(topLeftPath, strokeColor, style = circuitStroke)
        drawCircle(NeonGreen, radius = 4f, center = Offset(110f, 96f))

        // Top Right Circuit Brackets
        val topRightPath = Path().apply {
            moveTo(w - 24f, 180f)
            lineTo(w - 24f, 120f)
            lineTo(w - 48f, 96f)
            lineTo(w - 110f, 96f)
        }
        drawPath(topRightPath, strokeColor, style = circuitStroke)
        drawCircle(NeonGreen, radius = 4f, center = Offset(w - 110f, 96f))

        // Left Side Cyber Rails (as in screenshot)
        val leftRail = Path().apply {
            moveTo(20f, 240f)
            lineTo(20f, h * 0.35f)
            lineTo(38f, h * 0.38f)
            lineTo(38f, h * 0.55f)
            lineTo(20f, h * 0.58f)
            lineTo(20f, h * 0.72f)
            lineTo(36f, h * 0.75f)
            lineTo(36f, h * 0.85f)
            lineTo(56f, h * 0.88f)
            lineTo(56f, h - 140f)
            lineTo(80f, h - 110f)
            lineTo(130f, h - 110f)
            lineTo(150f, h - 80f)
            lineTo(40f, h - 80f)
            lineTo(20f, h - 50f)
        }
        drawPath(leftRail, strokeColor, style = circuitStroke)

        // Right Side Cyber Rails (symmetrical)
        val rightRail = Path().apply {
            moveTo(w - 20f, 240f)
            lineTo(w - 20f, h * 0.35f)
            lineTo(w - 38f, h * 0.38f)
            lineTo(w - 38f, h * 0.55f)
            lineTo(w - 20f, h * 0.58f)
            lineTo(w - 20f, h * 0.72f)
            lineTo(w - 36f, h * 0.75f)
            lineTo(w - 36f, h * 0.85f)
            lineTo(w - 56f, h * 0.88f)
            lineTo(w - 56f, h - 140f)
            lineTo(w - 80f, h - 110f)
            lineTo(w - 130f, h - 110f)
            lineTo(w - 150f, h - 80f)
            lineTo(w - 40f, h - 80f)
            lineTo(w - 20f, h - 50f)
        }
        drawPath(rightRail, strokeColor, style = circuitStroke)

        // Circuit Nodes / Dots
        drawCircle(NeonGreen, radius = 5f, center = Offset(38f, h * 0.38f))
        drawCircle(NeonGreen, radius = 5f, center = Offset(w - 38f, h * 0.38f))
        drawCircle(NeonGreen, radius = 5f, center = Offset(80f, h - 110f))
        drawCircle(NeonGreen, radius = 5f, center = Offset(w - 80f, h - 110f))

        // Center bottom bracket line
        val bottomBracket = Path().apply {
            moveTo(w * 0.35f, h - 45f)
            lineTo(w * 0.40f, h - 25f)
            lineTo(w * 0.60f, h - 25f)
            lineTo(w * 0.65f, h - 45f)
        }
        drawPath(bottomBracket, strokeColor, style = circuitStroke)

        // Ambient cyber glow spots
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(NeonGreen.copy(alpha = 0.12f), Color.Transparent),
                center = Offset(w / 2f, h * 0.68f),
                radius = w * 0.5f
            ),
            radius = w * 0.5f,
            center = Offset(w / 2f, h * 0.68f)
        )
    }
}
