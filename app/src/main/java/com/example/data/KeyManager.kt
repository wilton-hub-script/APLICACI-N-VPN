package com.example.data

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import java.util.concurrent.TimeUnit

sealed class KeyValidationResult {
    data class Success(val keyType: String, val message: String, val remainingMillis: Long) : KeyValidationResult()
    data class Expired(val key: String) : KeyValidationResult()
    data class DeviceMismatch(val message: String) : KeyValidationResult()
    data class Invalid(val message: String) : KeyValidationResult()
}

class KeyManager(private val context: Context) {

    private val prefs = PreferencesManager(context)

    companion object {
        const val KEY_STANDARD = "WILT-GAMIN"
        const val KEY_VIP = "WILT-VIP"
        private const val SEVEN_DAYS_MS = 7L * 24L * 60L * 60L * 1000L
    }

    @SuppressLint("HardwareIds")
    fun getDeviceId(): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "UNKNOWN_DEVICE"
    }

    /**
     * Checks if current key is valid.
     */
    fun checkCurrentKey(): KeyValidationResult {
        val key = prefs.activationKey?.trim()?.uppercase() ?: return KeyValidationResult.Invalid("No hay ninguna Key ingresada.")
        val activationTime = prefs.activationTime
        val currentTime = System.currentTimeMillis()

        if (key == KEY_VIP) {
            val registeredDevice = prefs.vipDeviceId
            val currentDevice = getDeviceId()
            return if (registeredDevice == null || registeredDevice == currentDevice) {
                KeyValidationResult.Success("VIP", "Membresía WILT-VIP Permanente Activa (Dispositivo Registrado)", Long.MAX_VALUE)
            } else {
                KeyValidationResult.DeviceMismatch("Esta clave WILT-VIP ya fue activada en otro dispositivo.")
            }
        }

        if (key == KEY_STANDARD) {
            val elapsed = currentTime - activationTime
            if (elapsed in 0..SEVEN_DAYS_MS) {
                val remaining = SEVEN_DAYS_MS - elapsed
                val days = TimeUnit.MILLISECONDS.toDays(remaining)
                val hours = TimeUnit.MILLISECONDS.toHours(remaining) % 24
                return KeyValidationResult.Success("STANDARD", "Key WILT-GAMIN Activa: Quedan $days días y $hours horas", remaining)
            } else {
                return KeyValidationResult.Expired(key)
            }
        }

        return KeyValidationResult.Invalid("La Key ingresada no es válida.")
    }

    /**
     * Activates a new key entered by the user.
     */
    fun activateKey(rawKey: String): KeyValidationResult {
        val cleaned = rawKey.trim().uppercase()
        val now = System.currentTimeMillis()

        if (cleaned == KEY_VIP) {
            val currentDevice = getDeviceId()
            val existingVipDevice = prefs.vipDeviceId
            if (existingVipDevice != null && existingVipDevice != currentDevice) {
                return KeyValidationResult.DeviceMismatch("Esta clave WILT-VIP ya fue vinculada a otro dispositivo móvil.")
            }
            prefs.vipDeviceId = currentDevice
            prefs.activationKey = KEY_VIP
            prefs.activationTime = now
            return KeyValidationResult.Success("VIP", "¡Felicidades! WILT-VIP activado permanentemente en este dispositivo.", Long.MAX_VALUE)
        }

        if (cleaned == KEY_STANDARD) {
            prefs.activationKey = KEY_STANDARD
            prefs.activationTime = now
            return KeyValidationResult.Success("STANDARD", "¡Key WILT-GAMIN activada con éxito por 7 días!", SEVEN_DAYS_MS)
        }

        return KeyValidationResult.Invalid("Key incorrecta. Consigue la Key oficial en nuestro canal de WhatsApp.")
    }

    fun isActivated(): Boolean {
        return checkCurrentKey() is KeyValidationResult.Success
    }

    fun getRemainingDaysDisplay(): String {
        return when (val result = checkCurrentKey()) {
            is KeyValidationResult.Success -> {
                if (result.keyType == "VIP") "VIP Permanente"
                else {
                    val days = TimeUnit.MILLISECONDS.toDays(result.remainingMillis)
                    val hours = TimeUnit.MILLISECONDS.toHours(result.remainingMillis) % 24
                    "$days días, $hours hrs"
                }
            }
            is KeyValidationResult.Expired -> "Expirada"
            else -> "Inactiva"
        }
    }
}
