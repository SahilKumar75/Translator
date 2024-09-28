package com.example.translator.ui

import com.example.translator.data.TranslationRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.langconverter.model.TranslationResult
import kotlinx.coroutines.launch



class TranslationViewModel : ViewModel() {

    private val repository = TranslationRepository()

    // LiveData for translation results
    private val _translationResult = MutableLiveData<TranslationResult>()
    val translationResult: LiveData<TranslationResult> get() = _translationResult

    // LiveData for recognized text from speech or user input
    private val _recognizedText = MutableLiveData<String>()
    val recognizedText: LiveData<String> get() = _recognizedText

    // Update the recognized text
    fun updateRecognizedText(text: String) {
        _recognizedText.value = text
    }

    // Perform translation with dynamic source and target languages
    fun translate(text: String, sourceLanguage: String = "en", targetLanguage: String = "hi") {
        viewModelScope.launch {
            val result = repository.translateText(text, sourceLanguage, targetLanguage)
            _translationResult.value = result
        }
    }
}
