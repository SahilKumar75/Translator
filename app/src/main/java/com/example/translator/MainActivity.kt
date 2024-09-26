package com.example.translator

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.langconverter.model.TranslationResult
import com.example.translator.ui.TranslationViewModel
import com.example.translator.ui.theme.TranslatorTheme
import com.example.translator.utils.startVoiceRecognition
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import kotlinx.coroutines.launch
import android.provider.MediaStore
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberImagePainter


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TranslatorTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    TranslationScreen() // Call to your TranslationScreen composable
                }
            }
        }
    }
}

@Composable
fun TranslationScreen(
    modifier: Modifier = Modifier,
    viewModel: TranslationViewModel = viewModel()
) {
    val textToTranslate by viewModel.recognizedText.observeAsState("")
    val translationResult by viewModel.translationResult.observeAsState(TranslationResult(""))

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isListening by remember { mutableStateOf(false) }
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    // Initialize Text-to-Speech
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
            .clickable { /* Clear focus or handle clicks here */ }
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

                // Transcript label and input field
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

                // Translation label and result field
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

                // Row for microphone, speaker, and gallery icons
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    // Microphone Icon
                    IconButton(
                        onClick = {
                            if (!isListening) {
                                startVoiceRecognition(context) { recognizedText ->
                                    viewModel.updateRecognizedText(recognizedText)
                                    isListening = true // Set listening state to true
                                }
                            } else {
                                isListening = false // Set listening state to false
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

                    // Speaker Icon
                    IconButton(
                        onClick = {
                            translationResult.translatedText?.let { text ->
                                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
                            }
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Image(
                            painterResource(id = R.drawable.volume),
                            contentDescription = "Speak Translation",
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Gallery Icon
                    IconButton(
                        onClick = { pickImage(context, viewModel) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            painterResource(id = R.drawable.ic_gallery), // Replace with your gallery icon resource
                            contentDescription = "Select Image",
                            tint = Color.White
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
                            val targetLanguage = "hi" // Example target language
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

// Function to pick an image from the gallery
@Composable
private fun pickImage(
    context: Context,
    viewModel: TranslationViewModel
) {
    // State to hold the URI of the picked image
    val imageUri = remember { mutableStateOf<Uri?>(null) }

    // Launcher to pick an image
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri.value = it
            // You can pass the URI to the ViewModel or perform any action needed with it
            viewModel.onImagePicked(it) // Implement this method in your ViewModel
        }
    }

    // Button to trigger image picking
    Button(onClick = { launcher.launch("image/*") }) {
        Text("Pick Image")
    }

    // Display the selected image if any
    imageUri.value?.let { uri ->
        // Use an Image composable to display the selected image
        Image(
            painter = rememberImagePainter(uri),
            contentDescription = null,
            modifier = Modifier
                .size(128.dp) // You can adjust the size as needed
                .clip(RoundedCornerShape(8.dp)) // Optional: apply rounding
        )
    }
}


// Function to extract text from the selected image using ML Kit
private fun extractTextFromImage(uri: Uri, context: Context, viewModel: TranslationViewModel) {
    val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    val image = InputImage.fromBitmap(bitmap, 0)

    val recognizer = TextRecognition.getClient()
    recognizer.process(image)
        .addOnSuccessListener { visionText ->
            // Handle the recognized text
            viewModel.updateRecognizedText(visionText.text)
        }
        .addOnFailureListener { e ->
            // Handle the error
            e.printStackTrace()
        }
}

@Preview(showBackground = true)
@Composable
fun TranslationScreenPreview() {
    TranslatorTheme {
        TranslationScreen()
    }
}
