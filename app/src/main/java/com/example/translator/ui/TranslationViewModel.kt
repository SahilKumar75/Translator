package com.example.translator.ui

import TranslationRepository
import android.net.Uri
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

    // LiveData for picked image URI
    private val _pickedImageUri = MutableLiveData<Uri?>()
    val pickedImageUri: LiveData<Uri?> get() = _pickedImageUri

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

    // Update picked image URI
    fun onImagePicked(uri: Uri) {
        _pickedImageUri.value = uri
        // You can perform additional processing with the image URI if needed
    }
}
