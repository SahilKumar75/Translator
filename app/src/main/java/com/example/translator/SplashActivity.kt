package com.example.translator

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.translator.MainActivity

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SplashScreenContent() // Just shows the image
        }

        // Delay for 2 seconds before navigating to MainActivity
        lifecycleScope.launch {
            delay(2000L) // Splash screen delay
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish() // Close SplashActivity
        }
    }
}

@Composable
fun SplashScreenContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Display the splash screen image (e.g., your app's logo)
        Image(
            painter = painterResource(id = R.drawable.app_logo), // Replace with your image resource
            contentDescription = "Splash Logo",
            modifier = Modifier.size(200.dp), // Adjust size accordingly
            contentScale = ContentScale.Fit
        )
    }
}
