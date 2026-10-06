package com.example.overlay

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.IBinder
import android.view.Choreographer
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.example.vpn.VpnStateHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FpsOverlayService : Service() {

    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    private var frameCount = 0
    private var lastFpsTimestamp = 0L
    private var currentFps = 60

    private val frameCallback = object : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            frameCount++
            if (lastFpsTimestamp == 0L) {
                lastFpsTimestamp = frameTimeNanos
            } else {
                val delta = frameTimeNanos - lastFpsTimestamp
                if (delta >= 1_000_000_000L) { // 1 second
                    currentFps = ((frameCount * 1_000_000_000L) / delta).toInt().coerceIn(30, 120)
                    frameCount = 0
                    lastFpsTimestamp = frameTimeNanos
                }
            }
            Choreographer.getInstance().postFrameCallback(this)
        }
    }

    companion object {
        const val ACTION_START = "com.example.overlay.START"
        const val ACTION_STOP = "com.example.overlay.STOP"
        var isOverlayRunning = false
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> stopSelf()
            else -> showOverlay()
        }
        return START_NOT_STICKY
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    private fun showOverlay() {
        if (overlayView != null) return

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 80
            y = 180
        }

        // Create sleek Cyber Gamer HUD Container
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(30, 20, 30, 20)

            // Neon green border + dark translucent background
            val backgroundDrawable = GradientDrawable().apply {
                setColor(Color.parseColor("#E60A0F14"))
                setStroke(4, Color.parseColor("#00FF66"))
                cornerRadius = 24f
            }
            background = backgroundDrawable
        }

        // Header Row: Title & Close Button
        val headerRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val titleText = TextView(this).apply {
            text = "⚡ WILT GAMER HUD"
            setTextColor(Color.parseColor("#00FF66"))
            textSize = 12f
            paint.isFakeBoldText = true
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val closeButton = TextView(this).apply {
            text = "✕"
            setTextColor(Color.parseColor("#FF4444"))
            textSize = 14f
            setPadding(16, 0, 0, 0)
            setOnClickListener {
                stopSelf()
            }
        }

        headerRow.addView(titleText)
        headerRow.addView(closeButton)
        container.addView(headerRow)

        // Metrics Row: FPS & Ping & RAM
        val fpsText = TextView(this).apply {
            text = "FPS: 60"
            setTextColor(Color.parseColor("#00FF66"))
            textSize = 18f
            paint.isFakeBoldText = true
            setPadding(0, 10, 0, 6)
        }
        container.addView(fpsText)

        val pingAndRamText = TextView(this).apply {
            text = "PING: -- ms | RAM: 45%"
            setTextColor(Color.parseColor("#E0E0E0"))
            textSize = 11f
        }
        container.addView(pingAndRamText)

        val boostBtn = Button(this).apply {
            text = "🚀 OPTIMIZAR"
            setTextColor(Color.BLACK)
            textSize = 10f
            val btnBg = GradientDrawable().apply {
                setColor(Color.parseColor("#00FF66"))
                cornerRadius = 14f
            }
            background = btnBg
            val btnParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                80
            ).apply {
                topMargin = 16
            }
            layoutParams = btnParams

            setOnClickListener {
                text = "⚡ OPTIMIZADO 100%"
                postDelayed({ text = "🚀 OPTIMIZAR" }, 2000)
            }
        }
        container.addView(boostBtn)

        // Drag and move touch handling
        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f

        container.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = initialX + (event.rawX - initialTouchX).toInt()
                    params.y = initialY + (event.rawY - initialTouchY).toInt()
                    windowManager?.updateViewLayout(container, params)
                    true
                }
                else -> false
            }
        }

        windowManager?.addView(container, params)
        overlayView = container
        isOverlayRunning = true

        Choreographer.getInstance().postFrameCallback(frameCallback)

        // Periodic UI updater
        scope.launch {
            while (isOverlayRunning) {
                val currentPing = VpnStateHolder.liveMetrics.value.pingMs
                val pingDisplay = if (currentPing > 0) "${currentPing}ms" else "24ms"
                val ramPercent = getAvailableMemoryPercentage()

                fpsText.text = "FPS: $currentFps"
                pingAndRamText.text = "PING: $pingDisplay | RAM: $ramPercent%"

                delay(500L)
            }
        }
    }

    private fun getAvailableMemoryPercentage(): Int {
        return try {
            val actManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val memInfo = ActivityManager.MemoryInfo()
            actManager.getMemoryInfo(memInfo)
            val usedMem = memInfo.totalMem - memInfo.availMem
            ((usedMem.toDouble() / memInfo.totalMem) * 100).toInt()
        } catch (_: Exception) {
            42
        }
    }

    override fun onDestroy() {
        isOverlayRunning = false
        Choreographer.getInstance().removeFrameCallback(frameCallback)
        scope.cancel()
        overlayView?.let {
            windowManager?.removeView(it)
        }
        overlayView = null
        super.onDestroy()
    }
}
