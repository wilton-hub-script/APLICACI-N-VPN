package com.example.ui.dialogs

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.VpnService
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.VpnLock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*

@Composable
fun PermissionsDialog(
    isVpnGranted: Boolean,
    isNotificationGranted: Boolean,
    isOverlayGranted: Boolean,
    onRequestVpn: () -> Unit,
    onRequestNotification: () -> Unit,
    onRequestOverlay: () -> Unit,
    onDismiss: () -> Unit
) {
    val allGranted = isVpnGranted && isNotificationGranted && isOverlayGranted

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(CyberDark)
                .border(2.dp, NeonGreen, RoundedCornerShape(24.dp))
                .padding(22.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "PERMISOS DE LA APLICACIÓN",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Para que Wilt Gamin Pro funcione con todo su poder nativo (VPN, Avisos y FPS Flotante), concede los siguientes permisos:",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Permission Item 1: VPN
                PermissionRow(
                    title = "Permiso de Conexión VPN",
                    desc = "Permite establecer el túnel proxy para acelerar tus juegos.",
                    isGranted = isVpnGranted,
                    onGrantClick = onRequestVpn
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Permission Item 2: Notificaciones
                PermissionRow(
                    title = "Permiso de Notificaciones",
                    desc = "Para recibir los avisos diarios y estado del proxy.",
                    isGranted = isNotificationGranted,
                    onGrantClick = onRequestNotification
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Permission Item 3: Mostrar sobre otras apps (Overlay)
                PermissionRow(
                    title = "Mostrar sobre otras aplicaciones",
                    desc = "Para proyectar el menú HUD flotante con FPS en tiempo real dentro de los juegos.",
                    isGranted = isOverlayGranted,
                    onGrantClick = onRequestOverlay
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (allGranted) "¡LISTO! TODO CONFIGURADO" else "CERRAR",
                        color = CyberBlack,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun PermissionRow(
    title: String,
    desc: String,
    isGranted: Boolean,
    onGrantClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CyberCardBg)
            .border(1.dp, if (isGranted) NeonGreen.copy(alpha = 0.4f) else CyberCardBorder, RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                color = TextSecondary,
                fontSize = 10.5.sp,
                lineHeight = 14.sp
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        if (isGranted) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Concedido",
                tint = NeonGreen,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Button(
                onClick = onGrantClick,
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreenDark),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Text("Permitir", color = NeonGreenBright, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
