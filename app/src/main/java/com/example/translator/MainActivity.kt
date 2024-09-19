package com.example.translator


import android.speech.tts.TextToSpeech
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.tooling.preview.Preview
import com.example.langconverter.model.TranslationResult
import com.example.translator.ui.TranslationViewModel
import com.example.translator.ui.theme.TranslatorTheme
import androidx.compose.ui.res.painterResource
import com.example.translator.utils.startVoiceRecognition
import kotlinx.coroutines.delay


import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.clickable

@Composable
fun TranslationScreen(
    modifier: Modifier = Modifier,
    viewModel: TranslationViewModel = viewModel()
) {
    val textToTranslate by viewModel.recognizedText.observeAsState("")
    val translationResult by viewModel.translationResult.observeAsState(TranslationResult(""))

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current // Get FocusManager to handle keyboard focus

    var vibrate by remember { mutableStateOf(false) }
    var isListening by remember { mutableStateOf(false) } // Track if currently listening

    // Text-to-Speech instance
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    // Initialize TTS
    LaunchedEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.setLanguage(java.util.Locale.getDefault())
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1D1E22))
            .clickable { focusManager.clearFocus() } // Clear focus when clicking outside the input fields
            .padding(horizontal = 16.dp, vertical = 32.dp)
    ) {
        LazyColumn(
            modifier = modifier.fillMaxSize()
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Translate Text",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(26.dp))

                // Transcript label and input field.
                Text(
                    text = "Transcript",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                )

                BasicTextField(
                    value = textToTranslate,
                    onValueChange = { text -> viewModel.updateRecognizedText(text) },
                    textStyle = TextStyle(fontSize = 18.sp, color = Color.White),
                    modifier = Modifier
                        .width(350.dp)
                        .height(150.dp)
                        .background(Color(0xFF252D34), shape = RoundedCornerShape(16.dp))
                        .padding(16.dp)
                )

                Spacer(modifier = Modifier.height(26.dp))

                // Translation label and result field.
                Text(
                    text = "Translation",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                )

                BasicTextField(
                    value = translationResult.translatedText ?: "",
                    onValueChange = {},
                    textStyle = TextStyle(fontSize = 18.sp, color = Color(0xFF69D269), fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .width(350.dp)
                        .height(150.dp)
                        .background(Color(0xFF252D34), shape = RoundedCornerShape(16.dp))
                        .padding(16.dp)
                )

                Spacer(modifier = Modifier.height(26.dp))

                // Row for microphone and speaker icons side by side.
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    IconButton(
                        onClick = {
                            if (!isListening) {
                                startVoiceRecognition(context) { recognizedText ->
                                    viewModel.updateRecognizedText(recognizedText)

                                    vibrate = true // Set vibrate to true when button is clicked.
                                    isListening = true // Set listening state to true.
                                }
                            } else {
                                isListening = false // Set listening state to false.
                            }
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            painterResource(id = if (isListening) R.drawable.ic_pause else R.drawable.ic_mic),
                            contentDescription = if (isListening) "Pause" else "Mic",
                            tint = Color.White
                        )
                    }

                    IconButton(
                        onClick = {
                            translationResult.translatedText?.let { text ->
                                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
                            }
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Image(
                            painterResource(id = R.drawable.volume), // Replace with your speaker icon resource.
                            contentDescription = "Speak Translation",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(26.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            val targetLanguage = "hi" // Example target language.
                            viewModel.translate(textToTranslate, targetLanguage)
                        },
                        modifier = Modifier.width(150.dp),
                        contentPadding = PaddingValues(vertical = 12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF69D269))
                    ) {
                        Text(
                            text = "Translate",
                            fontSize = 18.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun TranslationScreenPreview() {
    TranslatorTheme {
        TranslationScreen()
    }
}
