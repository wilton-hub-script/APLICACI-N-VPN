package com.example.ui.dialogs

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.KeyManager
import com.example.data.PreferencesManager
import com.example.notifications.DailyNotificationReceiver
import com.example.overlay.FpsOverlayService
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    preferencesManager: PreferencesManager,
    keyManager: KeyManager,
    onDismiss: () -> Unit,
    onOpenKeyDialog: () -> Unit,
    onOpenPermissionsDialog: () -> Unit
) {
    val context = LocalContext.current
    var isGamerMode by remember { mutableStateOf(preferencesManager.isGamerModeEnabled) }
    var isFpsOverlay by remember { mutableStateOf(preferencesManager.isFpsOverlayEnabled) }
    var isSafeMode by remember { mutableStateOf(preferencesManager.isSafeModeEnabled) }
    var dnsMode by remember { mutableStateOf(preferencesManager.dnsMode) }
    var isSoundEnabled by remember { mutableStateOf(preferencesManager.isSoundEffectsEnabled) }
    var isGraphics4K by remember { mutableStateOf(preferencesManager.isGraphics4KEnabled) }

    var showApkDownloadInfo by remember { mutableStateOf(false) }
    var showPoliciesDialog by remember { mutableStateOf(false) }
    var show4KInfoDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = CyberDark,
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = NeonGreen)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AJUSTES WILT GAMIN",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SECTION 1: MODO GAMER & FPS
            SectionHeader(title = "⚡ MODO GAMER ULTRA", color = NeonGreen)

            SettingsCard {
                // Gamer Mode Toggle
                SettingsSwitchRow(
                    title = "Modo Gamer Acelerador",
                    desc = "Optimiza la red con MTU 1400 y paquetes prioritarios para juegos (Free Fire, COD, PUBG).",
                    checked = isGamerMode,
                    icon = Icons.Default.SportsEsports,
                    onCheckedChange = {
                        isGamerMode = it
                        preferencesManager.isGamerModeEnabled = it
                    }
                )

                HorizontalDivider(color = CyberCardBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))

                // Floating FPS Menu Toggle
                SettingsSwitchRow(
                    title = "Menú Flotante de FPS en Pantalla",
                    desc = "Muestra un HUD sobre cualquier juego con FPS en vivo, Ping y RAM liberada.",
                    checked = isFpsOverlay,
                    icon = Icons.Default.Layers,
                    onCheckedChange = { enable ->
                        if (enable) {
                            if (Settings.canDrawOverlays(context)) {
                                isFpsOverlay = true
                                preferencesManager.isFpsOverlayEnabled = true
                                context.startService(Intent(context, FpsOverlayService::class.java))
                            } else {
                                onOpenPermissionsDialog()
                            }
                        } else {
                            isFpsOverlay = false
                            preferencesManager.isFpsOverlayEnabled = false
                            context.stopService(Intent(context, FpsOverlayService::class.java))
                        }
                    }
                )

                HorizontalDivider(color = CyberCardBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))

                // 4K Ultra Quality Graphic Preset
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { show4KInfoDialog = true },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.HighQuality, contentDescription = null, tint = CyberGold, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Calidad 4K / Super Realista", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Perfil de nitidez gráfica y estabilización visual para juegos.", color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                    Switch(
                        checked = isGraphics4K,
                        onCheckedChange = {
                            isGraphics4K = it
                            preferencesManager.isGraphics4KEnabled = it
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CyberBlack,
                            checkedTrackColor = CyberGold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // SECTION 2: MODO PRIVADO Y BLOQUEO DE ANUNCIOS
            SectionHeader(title = "🛡️ MODO PRIVADO / SEGURO", color = Color(0xFF4FC3F7))

            SettingsCard {
                SettingsSwitchRow(
                    title = "Modo Seguro (Bloqueador de Anuncios)",
                    desc = "Filtra dominios publicitarios molestos y rastreadores a nivel DNS.",
                    checked = isSafeMode,
                    icon = Icons.Default.Shield,
                    onCheckedChange = {
                        isSafeMode = it
                        preferencesManager.isSafeModeEnabled = it
                    }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // SECTION 3: MEJOR INTERNET (DNS SELECTION)
            SectionHeader(title = "🌐 AJUSTES DE MEJOR INTERNET", color = NeonGreen)

            SettingsCard {
                Text(
                    text = "Selecciona el servidor DNS optimizado para tu conexión:",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                val dnsOptions = listOf(
                    "WILT_CUSTOM" to "Wilt Gamer Custom (Prioridad Latencia)",
                    "CLOUDFLARE" to "Cloudflare 1.1.1.1 (Ultra Rápido)",
                    "GOOGLE" to "Google DNS 8.8.8.8 (Estabilidad)",
                    "ADGUARD" to "AdGuard DNS (Sin Publicidad)"
                )

                dnsOptions.forEach { (key, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                dnsMode = key
                                preferencesManager.dnsMode = key
                            }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (dnsMode == key),
                            onClick = {
                                dnsMode = key
                                preferencesManager.dnsMode = key
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = NeonGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = label,
                            color = if (dnsMode == key) NeonGreen else Color.White,
                            fontSize = 13.sp,
                            fontWeight = if (dnsMode == key) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // SECTION 4: EFECTOS DE AUDIO & NOTIFICACIONES
            SectionHeader(title = "🔊 AUDIO Y NOTIFICACIONES", color = NeonGreen)

            SettingsCard {
                SettingsSwitchRow(
                    title = "Efectos de Audio Cyber",
                    desc = "Sonido futurista de encendido al conectar y apagado al desconectar.",
                    checked = isSoundEnabled,
                    icon = Icons.Default.VolumeUp,
                    onCheckedChange = {
                        isSoundEnabled = it
                        preferencesManager.isSoundEffectsEnabled = it
                    }
                )

                HorizontalDivider(color = CyberCardBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))

                // Button to test daily notifications
                Button(
                    onClick = {
                        DailyNotificationReceiver.triggerNotification(context)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreenDark),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = NeonGreenBright, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("PROBAR NOTIFICACIÓN DIARIA AHORA", color = NeonGreenBright, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // SECTION 5: ESTADO DE LA KEY Y WHATSAPP
            SectionHeader(title = "🔑 CLAVE DE ACCESO (KEY)", color = CyberGold)

            SettingsCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Clave Activa:", color = TextSecondary, fontSize = 11.sp)
                        Text(
                            text = preferencesManager.activationKey ?: "Sin Clave",
                            color = NeonGreen,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = keyManager.getRemainingDaysDisplay(),
                            color = CyberGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = onOpenKeyDialog,
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCardBorder),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("RENOVAR", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // WhatsApp Button
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(PreferencesManager.WHATSAPP_URL))
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("settings_whatsapp_button")
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "UNIRSE AL CANAL DE WHATSAPP",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // SECTION 6: INFORMACIÓN DE LA APLICACIÓN Y POLÍTICAS
            SectionHeader(title = "ℹ️ INFORMACIÓN Y POLÍTICAS", color = TextSecondary)

            SettingsCard {
                SettingsActionRow(
                    title = "Información de Wilt Gamin Pro",
                    subtitle = "Versión 1.0.0 Nativa (Android)",
                    icon = Icons.Default.Info,
                    onClick = { showApkDownloadInfo = true }
                )

                HorizontalDivider(color = CyberCardBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))

                SettingsActionRow(
                    title = "Políticas de Privacidad y Términos",
                    subtitle = "Cero registro de datos personales, túnel local protegido",
                    icon = Icons.Default.Policy,
                    onClick = { showPoliciesDialog = true }
                )

                HorizontalDivider(color = CyberCardBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))

                SettingsActionRow(
                    title = "Revisar Permisos de la App",
                    subtitle = "VPN, Notificaciones y Overlay",
                    icon = Icons.Default.Security,
                    onClick = onOpenPermissionsDialog
                )
            }
        }
    }

    if (showApkDownloadInfo) {
        AlertDialog(
            onDismissRequest = { showApkDownloadInfo = false },
            containerColor = CyberDark,
            title = { Text("Descarga de APK Nativo", color = NeonGreen, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Esta aplicación está compilada nativamente para Android.\n\nPuedes descargar el archivo APK directamente desde el menú superior de Google AI Studio:\n1. Toca el menú de opciones (tres puntos o ajustes en la esquina superior de la interfaz).\n2. Selecciona 'Export / Download' o 'Generate APK'.\n3. Instala el APK directamente en tu teléfono Android.",
                    color = Color.White,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showApkDownloadInfo = false }) {
                    Text("ENTENDIDO", color = NeonGreen, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (show4KInfoDialog) {
        AlertDialog(
            onDismissRequest = { show4KInfoDialog = false },
            containerColor = CyberDark,
            title = { Text("Modo Gráficos 4K Ultra", color = CyberGold, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "El perfil 4K Ultra ajusta los búferes de renderizado de la GPU para evitar caídas de fotogramas, reduciendo el jitter y optimizando el escalado de resolución en juegos multijugador competitivos.",
                    color = Color.White,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { show4KInfoDialog = false }) {
                    Text("OK", color = CyberGold, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showPoliciesDialog) {
        AlertDialog(
            onDismissRequest = { showPoliciesDialog = false },
            containerColor = CyberDark,
            title = { Text("Políticas de Privacidad", color = NeonGreen, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Wilt Gamin Pro opera bajo una estricta política de NO REGISTRO (No-Logs).\n\n" +
                            "• Tu tráfico es procesado localmente mediante la interfaz VpnService del sistema Android.\n" +
                            "• No se recopilan ni comparten contraseñas, historiales ni datos personales.\n" +
                            "• La conexión se utiliza exclusivamente para acelerar la latencia (ping) en juegos y filtrar publicidad si activas el Modo Seguro.",
                    color = Color.White,
                    fontSize = 12.5.sp,
                    lineHeight = 17.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showPoliciesDialog = false }) {
                    Text("ACEPTAR", color = NeonGreen, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun SectionHeader(title: String, color: Color) {
    Text(
        text = title,
        color = color,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CyberCardBg)
            .border(1.dp, CyberCardBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(content = content)
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    desc: String,
    checked: Boolean,
    icon: ImageVector,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(desc, color = TextSecondary, fontSize = 11.sp, lineHeight = 15.sp)
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = CyberBlack,
                checkedTrackColor = NeonGreen
            )
        )
    }
}

@Composable
private fun SettingsActionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Text(subtitle, color = TextMuted, fontSize = 11.sp)
            }
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
    }
}
