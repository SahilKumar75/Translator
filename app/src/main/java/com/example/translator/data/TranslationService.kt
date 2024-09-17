package com.example.langconverter.data


import com.example.langconverter.model.TranslationResult

interface TranslationService {
    suspend fun translateText(text: String, targetLanguage: String): TranslationResult
}
