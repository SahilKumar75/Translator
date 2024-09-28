package com.example.translator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.translator.ui.theme.TranslatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the content of MainActivity to display the TranslationScreen with theme and camera permission check
        setContent {
            TranslatorTheme {
                // Check camera permissions, then show TranslationScreen
                CheckCameraPermission {
                    TranslationScreen()
                }
            }
        }
    }
}
