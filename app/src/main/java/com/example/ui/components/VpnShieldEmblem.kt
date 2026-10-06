package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonGreenBright

@Composable
fun VpnShieldEmblem(
    modifier: Modifier = Modifier,
    isConnected: Boolean = false
) {
    Box(
        modifier = modifier.size(width = 80.dp, height = 90.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height

            // Shield Outer Contour Path
            val shieldPath = Path().apply {
                moveTo(w * 0.5f, 2f)
                lineTo(w - 4f, h * 0.15f)
                lineTo(w - 4f, h * 0.55f)
                cubicTo(
                    w - 4f, h * 0.85f,
                    w * 0.5f, h * 0.98f,
                    w * 0.5f, h - 2f
                )
                cubicTo(
                    w * 0.5f, h * 0.98f,
                    4f, h * 0.85f,
                    4f, h * 0.55f
                )
                lineTo(4f, h * 0.15f)
                close()
            }

            // Inner dark fill with green glow
            drawPath(
                path = shieldPath,
                brush = Brush.radialGradient(
                    colors = listOf(
                        if (isConnected) NeonGreen.copy(alpha = 0.35f) else NeonGreen.copy(alpha = 0.18f),
                        Color(0xFF0C1318)
                    ),
                    center = Offset(w * 0.5f, h * 0.4f),
                    radius = w * 0.7f
                )
            )

            // Outer Glowing Stroke
            drawPath(
                path = shieldPath,
                color = if (isConnected) NeonGreenBright else NeonGreen,
                style = Stroke(width = 4.5f)
            )

            // Inner Accent Border
            val innerPath = Path().apply {
                moveTo(w * 0.5f, 10f)
                lineTo(w - 12f, h * 0.18f)
                lineTo(w - 12f, h * 0.52f)
                cubicTo(
                    w - 12f, h * 0.78f,
                    w * 0.5f, h * 0.90f,
                    w * 0.5f, h - 10f
                )
                cubicTo(
                    w * 0.5f, h * 0.90f,
                    12f, h * 0.78f,
                    12f, h * 0.52f
                )
                lineTo(12f, h * 0.18f)
                close()
            }

            drawPath(
                path = innerPath,
                color = NeonGreen.copy(alpha = 0.45f),
                style = Stroke(width = 1.8f)
            )
        }

        // VPN Text
        Text(
            text = "VPN",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.SansSerif,
            letterSpacing = 1.5.sp
        )
    }
}
