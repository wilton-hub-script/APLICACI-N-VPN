package com.example.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.VpnService
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.KeyManager
import com.example.data.PreferencesManager
import com.example.overlay.FpsOverlayService
import com.example.ui.components.CyberBackgroundCanvas
import com.example.ui.components.CyberConnectButton
import com.example.ui.components.StatusStatsCard
import com.example.ui.components.VpnShieldEmblem
import com.example.ui.dialogs.KeyActivationDialog
import com.example.ui.dialogs.PermissionsDialog
import com.example.ui.dialogs.SettingsSheet
import com.example.ui.theme.*
import com.example.vpn.VpnStateHolder
import com.example.vpn.VpnStatus
import com.example.vpn.WiltVpnService

@Composable
fun WiltGaminScreen(
    keyManager: KeyManager,
    preferencesManager: PreferencesManager,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val liveMetrics by VpnStateHolder.liveMetrics.collectAsStateWithLifecycle()
    val isConnected = liveMetrics.status == VpnStatus.CONNECTED

    var isKeyValid by remember { mutableStateOf(keyManager.isActivated()) }
    var showKeyDialog by remember { mutableStateOf(!isKeyValid) }
    var showSettingsSheet by remember { mutableStateOf(false) }
    var showPermissionsDialog by remember { mutableStateOf(false) }

    // Launcher for VPN Permission
    val vpnPrepareLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val vpnIntent = Intent(context, WiltVpnService::class.java).apply {
                action = WiltVpnService.ACTION_CONNECT
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(vpnIntent)
            } else {
                context.startService(vpnIntent)
            }
        }
    }

    // Launcher for Android 13+ Notification Permission
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    fun handleConnectClick() {
        if (!keyManager.isActivated()) {
            showKeyDialog = true
            return
        }

        if (liveMetrics.status == VpnStatus.CONNECTED) {
            val disconnectIntent = Intent(context, WiltVpnService::class.java).apply {
                action = WiltVpnService.ACTION_DISCONNECT
            }
            context.startService(disconnectIntent)
        } else {
            val prepareIntent = VpnService.prepare(context)
            if (prepareIntent != null) {
                vpnPrepareLauncher.launch(prepareIntent)
            } else {
                val connectIntent = Intent(context, WiltVpnService::class.java).apply {
                    action = WiltVpnService.ACTION_CONNECT
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(connectIntent)
                } else {
                    context.startService(connectIntent)
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBlack)
    ) {
        // Decorative Cyber Circuit traces and accents matching screenshot
        CyberBackgroundCanvas()

        // Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Navigation & Action Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button (Cyber aesthetic)
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(CyberDark.copy(alpha = 0.8f))
                        .clickable {
                            showSettingsSheet = true
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Atrás",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Center Key Status Badge
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(CyberDark.copy(alpha = 0.9f))
                        .border(1.dp, if (isKeyValid) NeonGreenDark else CyberRed, RoundedCornerShape(12.dp))
                        .clickable { showKeyDialog = true }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = "Key",
                        tint = if (isKeyValid) NeonGreen else CyberRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isKeyValid) keyManager.getRemainingDaysDisplay() else "ACTIVAR KEY",
                        color = if (isKeyValid) NeonGreenBright else CyberRed,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Settings Gear Button
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(CyberDark.copy(alpha = 0.8f))
                        .border(1.dp, NeonGreen.copy(alpha = 0.6f), CircleShape)
                        .clickable { showSettingsSheet = true }
                        .testTag("settings_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Ajustes",
                        tint = NeonGreenBright,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // VPN Glowing Shield Emblem
            VpnShieldEmblem(isConnected = isConnected)

            Spacer(modifier = Modifier.height(16.dp))

            // Main Title: WILT GAMIN PRO
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "WILT GAMIN ",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = 1.8.sp
                )
                Text(
                    text = "PRO",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = 1.8.sp,
                    color = CyberGold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Subtitle: PROXY MODE
            Text(
                text = "PROXY MODE",
                color = NeonGreen,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 3.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Stats Card (Matching the OFFLINE / ONLINE card in screenshot)
            StatusStatsCard(
                metrics = liveMetrics,
                onDetailsClick = { showSettingsSheet = true }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Big Center Connect Button (Cyber concentric rings)
            CyberConnectButton(
                status = liveMetrics.status,
                onClick = { handleConnectClick() }
            )

            Spacer(modifier = Modifier.weight(1.2f))

            // Bottom Brand Footnote
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (preferencesManager.isGamerModeEnabled) "MODO GAMER ⚡ ACTIVO" else "MODO ESTÁNDAR",
                    color = if (preferencesManager.isGamerModeEnabled) NeonGreen else TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Wilt Gamin",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // Key Activation Dialog
    if (showKeyDialog) {
        KeyActivationDialog(
            keyManager = keyManager,
            onKeyActivated = {
                isKeyValid = true
                showKeyDialog = false
            },
            onDismissRequest = if (isKeyValid) {
                { showKeyDialog = false }
            } else null
        )
    }

    // Settings Modal Sheet
    if (showSettingsSheet) {
        SettingsSheet(
            preferencesManager = preferencesManager,
            keyManager = keyManager,
            onDismiss = { showSettingsSheet = false },
            onOpenKeyDialog = {
                showSettingsSheet = false
                showKeyDialog = true
            },
            onOpenPermissionsDialog = {
                showSettingsSheet = false
                showPermissionsDialog = true
            }
        )
    }

    // Permissions Dialog
    if (showPermissionsDialog) {
        val hasOverlay = Settings.canDrawOverlays(context)
        val hasNotification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else true
        val hasVpn = VpnService.prepare(context) == null

        PermissionsDialog(
            isVpnGranted = hasVpn,
            isNotificationGranted = hasNotification,
            isOverlayGranted = hasOverlay,
            onRequestVpn = {
                val prepareIntent = VpnService.prepare(context)
                if (prepareIntent != null) {
                    vpnPrepareLauncher.launch(prepareIntent)
                }
            },
            onRequestNotification = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            },
            onRequestOverlay = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:${context.packageName}")
                    )
                    context.startActivity(intent)
                }
            },
            onDismiss = { showPermissionsDialog = false }
        )
    }
}
