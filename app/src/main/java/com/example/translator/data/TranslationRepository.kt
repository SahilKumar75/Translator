package com.example.translator.data

import com.example.langconverter.data.TranslationService
import com.example.langconverter.model.TranslationResult
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await

class TranslationRepository : TranslationService {

    // Add sourceLanguageCode and targetLanguageCode as function parameters
    override suspend fun translateText(text: String, sourceLanguageCode: String, targetLanguageCode: String): TranslationResult {

        // Convert sourceLanguage and targetLanguage to ML Kit TranslateLanguage format
        val sourceLanguage = TranslateLanguage.fromLanguageTag(sourceLanguageCode)
        val targetLanguage = TranslateLanguage.fromLanguageTag(targetLanguageCode)

        // Ensure that both sourceLanguage and targetLanguage are valid
        if (sourceLanguage == null || targetLanguage == null) {
            return TranslationResult("Invalid language code.")
        }

        // Dynamically set the source and target languages
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLanguage)  // Source language dynamically set
            .setTargetLanguage(targetLanguage)  // Target language dynamically set
            .build()

        // Initialize the ML Kit translator
        val translator = Translation.getClient(options)

        return try {
            // Download language models if necessary
            translator.downloadModelIfNeeded().await()

            // Perform the translation
            val translatedText = translator.translate(text).await()

            // Return the result as successful
            TranslationResult(translatedText ?: "Translation failed")
        } catch (e: Exception) {
            // Handle any errors during translation
            TranslationResult("Error: ${e.localizedMessage}")
        }
    }
}
