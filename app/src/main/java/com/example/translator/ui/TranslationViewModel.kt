package com.example.translator.ui

import TranslationRepository
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

    // LiveData for recognized text from speech
    private val _recognizedText = MutableLiveData<String>()
    val recognizedText: LiveData<String> get() = _recognizedText

    // Update the recognized text
    fun updateRecognizedText(text: String) {
        _recognizedText.value = text
    }

    // Perform translation
    fun translate(text: String, targetLanguage: String) {
        viewModelScope.launch {
            val result = repository.translateText(text, targetLanguage)
            _translationResult.value = result
        }
    }
}
