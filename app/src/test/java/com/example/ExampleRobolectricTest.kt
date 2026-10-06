package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.KeyManager
import com.example.data.KeyValidationResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Wilt Gamin Pro", appName)
    }

    @Test
    fun `test standard key activation`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val keyManager = KeyManager(context)
        val result = keyManager.activateKey("WILT-GAMIN")
        assertTrue(result is KeyValidationResult.Success)
        assertTrue(keyManager.isActivated())
    }

    @Test
    fun `test vip key activation`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val keyManager = KeyManager(context)
        val result = keyManager.activateKey("WILT-VIP")
        assertTrue(result is KeyValidationResult.Success)
        assertTrue(keyManager.isActivated())
    }

    @Test
    fun `test invalid key fails`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val keyManager = KeyManager(context)
        val result = keyManager.activateKey("INVALID-KEY-123")
        assertTrue(result is KeyValidationResult.Invalid)
    }
}
