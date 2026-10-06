package com.example.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "wilt_gamin_prefs"
        const val KEY_ACTIVATION_KEY = "activation_key"
        const val KEY_ACTIVATION_TIME = "activation_time"
        const val KEY_VIP_DEVICE_ID = "vip_device_id"
        const val KEY_GAMER_MODE = "gamer_mode"
        const val KEY_FPS_OVERLAY = "fps_overlay"
        const val KEY_SAFE_MODE = "safe_mode"
        const val KEY_DNS_MODE = "dns_mode" // "CLOUDFLARE", "GOOGLE", "WILT_CUSTOM", "ADGUARD"
        const val KEY_SOUND_EFFECTS = "sound_effects"
        const val KEY_GRAPHICS_4K = "graphics_4k"

        const val WHATSAPP_URL = "https://whatsapp.com/channel/0029VbDfhMMHLHQdcrwgBI26"
    }

    var activationKey: String?
        get() = prefs.getString(KEY_ACTIVATION_KEY, null)
        set(value) = prefs.edit().putString(KEY_ACTIVATION_KEY, value).apply()

    var activationTime: Long
        get() = prefs.getLong(KEY_ACTIVATION_TIME, 0L)
        set(value) = prefs.edit().putLong(KEY_ACTIVATION_TIME, value).apply()

    var vipDeviceId: String?
        get() = prefs.getString(KEY_VIP_DEVICE_ID, null)
        set(value) = prefs.edit().putString(KEY_VIP_DEVICE_ID, value).apply()

    var isGamerModeEnabled: Boolean
        get() = prefs.getBoolean(KEY_GAMER_MODE, true)
        set(value) = prefs.edit().putBoolean(KEY_GAMER_MODE, value).apply()

    var isFpsOverlayEnabled: Boolean
        get() = prefs.getBoolean(KEY_FPS_OVERLAY, false)
        set(value) = prefs.edit().putBoolean(KEY_FPS_OVERLAY, value).apply()

    var isSafeModeEnabled: Boolean
        get() = prefs.getBoolean(KEY_SAFE_MODE, false)
        set(value) = prefs.edit().putBoolean(KEY_SAFE_MODE, value).apply()

    var dnsMode: String
        get() = prefs.getString(KEY_DNS_MODE, "WILT_CUSTOM") ?: "WILT_CUSTOM"
        set(value) = prefs.edit().putString(KEY_DNS_MODE, value).apply()

    var isSoundEffectsEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND_EFFECTS, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND_EFFECTS, value).apply()

    var isGraphics4KEnabled: Boolean
        get() = prefs.getBoolean(KEY_GRAPHICS_4K, true)
        set(value) = prefs.edit().putBoolean(KEY_GRAPHICS_4K, value).apply()

    fun clearActivation() {
        prefs.edit().remove(KEY_ACTIVATION_KEY).remove(KEY_ACTIVATION_TIME).apply()
    }
}
