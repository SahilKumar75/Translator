import com.example.langconverter.data.TranslationService
import com.example.langconverter.model.TranslationResult
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await

class TranslationRepository : TranslationService {

    override suspend fun translateText(text: String, targetLanguage: String): TranslationResult {

        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.CHINESE) // You can modify this as needed
            .setTargetLanguage(targetLanguage)
            .build()

        // Initialize the ML Kit translator
        val translator = Translation.getClient(options)
        return try {
            // Configure the translator options with source and target languages


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