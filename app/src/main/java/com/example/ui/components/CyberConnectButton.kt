package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ripple
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.vpn.VpnStatus
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CyberConnectButton(
    status: VpnStatus,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isConnected = status == VpnStatus.CONNECTED
    val isConnecting = status == VpnStatus.CONNECTING || status == VpnStatus.DISCONNECTING

    // Infinite transition for continuous rotation and pulsing
    val infiniteTransition = rememberInfiniteTransition(label = "cyber_rings")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isConnecting) 1800 else if (isConnected) 6000 else 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring_rotation"
    )

    val counterRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isConnecting) 2200 else 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "counter_rotation"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = if (isConnected) 1.05f else 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = modifier
            .size(280.dp)
            .scale(pulseScale),
        contentAlignment = Alignment.Center
    ) {
        // Outer Radar & Segmented HUD Canvas (Rotating)
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .rotate(rotation)
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxR = size.width / 2f

            // Outer segmented thick arc
            drawArc(
                color = if (isConnected) NeonGreenBright else NeonGreen,
                startAngle = 10f,
                sweepAngle = 70f,
                useCenter = false,
                topLeft = Offset(center.x - maxR + 10f, center.y - maxR + 10f),
                size = Size((maxR - 10f) * 2f, (maxR - 10f) * 2f),
                style = Stroke(width = 9f, cap = StrokeCap.Round)
            )

            drawArc(
                color = if (isConnected) NeonGreenBright else NeonGreen,
                startAngle = 190f,
                sweepAngle = 70f,
                useCenter = false,
                topLeft = Offset(center.x - maxR + 10f, center.y - maxR + 10f),
                size = Size((maxR - 10f) * 2f, (maxR - 10f) * 2f),
                style = Stroke(width = 9f, cap = StrokeCap.Round)
            )

            // Outer thin secondary arcs
            drawArc(
                color = NeonGreen.copy(alpha = 0.45f),
                startAngle = 95f,
                sweepAngle = 40f,
                useCenter = false,
                topLeft = Offset(center.x - maxR + 10f, center.y - maxR + 10f),
                size = Size((maxR - 10f) * 2f, (maxR - 10f) * 2f),
                style = Stroke(width = 3f)
            )

            drawArc(
                color = NeonGreen.copy(alpha = 0.45f),
                startAngle = 280f,
                sweepAngle = 45f,
                useCenter = false,
                topLeft = Offset(center.x - maxR + 10f, center.y - maxR + 10f),
                size = Size((maxR - 10f) * 2f, (maxR - 10f) * 2f),
                style = Stroke(width = 3f)
            )

            // Cyber tick marks around the circle
            val numTicks = 36
            val tickRadius = maxR - 26f
            for (i in 0 until numTicks) {
                val angleRad = Math.toRadians((i * (360.0 / numTicks)))
                val startX = center.x + (tickRadius - 6f) * cos(angleRad).toFloat()
                val startY = center.y + (tickRadius - 6f) * sin(angleRad).toFloat()
                val endX = center.x + tickRadius * cos(angleRad).toFloat()
                val endY = center.y + tickRadius * sin(angleRad).toFloat()

                val tickAlpha = if (i % 3 == 0) 0.8f else 0.25f
                drawLine(
                    color = NeonGreen.copy(alpha = tickAlpha),
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = if (i % 3 == 0) 2.5f else 1.2f
                )
            }
        }

        // Inner Counter-Rotating Ring
        Canvas(
            modifier = Modifier
                .size(220.dp)
                .rotate(counterRotation)
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val r = size.width / 2f - 6f

            drawArc(
                color = NeonGreen.copy(alpha = 0.75f),
                startAngle = 45f,
                sweepAngle = 55f,
                useCenter = false,
                topLeft = Offset(center.x - r, center.y - r),
                size = Size(r * 2f, r * 2f),
                style = Stroke(width = 5f, cap = StrokeCap.Round)
            )

            drawArc(
                color = NeonGreen.copy(alpha = 0.75f),
                startAngle = 225f,
                sweepAngle = 55f,
                useCenter = false,
                topLeft = Offset(center.x - r, center.y - r),
                size = Size(r * 2f, r * 2f),
                style = Stroke(width = 5f, cap = StrokeCap.Round)
            )
        }

        // Center Glowing Button (Interactive)
        Box(
            modifier = Modifier
                .size(175.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = if (isConnected) {
                            listOf(
                                NeonGreenBright.copy(alpha = 0.35f),
                                Color(0xFF032611),
                                CyberBlack
                            )
                        } else {
                            listOf(
                                Color(0xFF0D2517),
                                Color(0xFF08170F),
                                CyberBlack
                            )
                        }
                    )
                )
                .border(
                    width = 4.dp,
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            NeonGreen,
                            NeonGreenBright,
                            NeonGreenDark,
                            NeonGreen
                        )
                    ),
                    shape = CircleShape
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true, color = NeonGreen),
                    onClick = onClick
                )
                .testTag("connect_vpn_button"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                val buttonText = when (status) {
                    VpnStatus.DISCONNECTED -> "TAP TO\nCONNECT"
                    VpnStatus.CONNECTING -> "INICIANDO\nPROXY..."
                    VpnStatus.CONNECTED -> "CONECTADO\n(DESCONECTAR)"
                    VpnStatus.DISCONNECTING -> "CERRANDO..."
                }

                Text(
                    text = buttonText,
                    color = if (isConnected) NeonGreenBright else Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    letterSpacing = 1.2.sp
                )
            }
        }
    }
}
