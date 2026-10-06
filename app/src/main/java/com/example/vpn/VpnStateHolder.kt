package com.example.vpn

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class VpnStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    DISCONNECTING
}

data class VpnLiveMetrics(
    val status: VpnStatus = VpnStatus.DISCONNECTED,
    val pingMs: Int = 0,
    val downloadSpeedMb: Double = 0.0,
    val totalBytesTransferred: Long = 0L,
    val connectedDurationSeconds: Long = 0L,
    val activeServer: String = "Wilt Gaming Proxy #1",
    val ipAddress: String = "10.8.0.2"
)

object VpnStateHolder {
    private val _liveMetrics = MutableStateFlow(VpnLiveMetrics())
    val liveMetrics = _liveMetrics.asStateFlow()

    fun updateStatus(status: VpnStatus) {
        _liveMetrics.value = _liveMetrics.value.copy(status = status)
    }

    fun updateMetrics(pingMs: Int, speedMb: Double, totalBytes: Long, durationSec: Long) {
        _liveMetrics.value = _liveMetrics.value.copy(
            pingMs = pingMs,
            downloadSpeedMb = speedMb,
            totalBytesTransferred = totalBytes,
            connectedDurationSeconds = durationSec
        )
    }

    fun reset() {
        _liveMetrics.value = VpnLiveMetrics(status = VpnStatus.DISCONNECTED)
    }
}
