package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.data.KeyManager
import com.example.data.PreferencesManager
import com.example.ui.WiltGaminScreen
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.WiltGaminTheme

class MainActivity : ComponentActivity() {

    private lateinit var preferencesManager: PreferencesManager
    private lateinit var keyManager: KeyManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        preferencesManager = PreferencesManager(this)
        keyManager = KeyManager(this)

        setContent {
            WiltGaminTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = CyberBlack
                ) {
                    WiltGaminScreen(
                        keyManager = keyManager,
                        preferencesManager = preferencesManager
                    )
                }
            }
        }
    }
}
