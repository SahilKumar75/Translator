package com.example.translator

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Hide system UI to make the video full-screen
        hideSystemUI()

        // Find the VideoView in the layout
        val videoView = findViewById<VideoView>(R.id.videoView)

        // Set the video URI from the raw folder
        val videoUri = Uri.parse("android.resource://" + packageName + "/" + R.raw.splash_video)
        videoView.setVideoURI(videoUri)

        // Start the video
        videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = false  // Set to true if you want the video to loop
            videoView.start()
        }

        // Listen for when the video finishes playing
        videoView.setOnCompletionListener {
            // Navigate to MainActivity after the video finishes
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()  // Close the splash screen activity
        }
    }

    // Hide the system UI to make the activity truly full-screen
    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
    }
}
