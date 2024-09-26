package com.example.translator.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.speech.RecognizerIntent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

fun startVoiceRecognition(context: Context, onResult: (String) -> Unit) {
    // Create a speech recognizer
    val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

    // Prepare the intent for speech recognition
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US") // You can set the language you want
        putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now")
    }

    // Set the recognition listener
    val listener = object : RecognitionListener {
        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val spokenText = matches?.firstOrNull() ?: ""
            onResult(spokenText)
            speechRecognizer.destroy() // Clean up the recognizer
        }

        override fun onError(error: Int) {
            // Handle errors here
            speechRecognizer.destroy() // Clean up the recognizer on error
        }

        override fun onReadyForSpeech(p0: Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
        override fun onPartialResults(partialResults: Bundle?) {}
        override fun onRmsChanged(rmsdB: Float) {}
    }

    // Check for audio recording permission
    if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.RECORD_AUDIO) !=
        android.content.pm.PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(context as Activity, arrayOf(android.Manifest.permission.RECORD_AUDIO), 1)
    } else {
        speechRecognizer.setRecognitionListener(listener)
        speechRecognizer.startListening(intent)
    }
}
