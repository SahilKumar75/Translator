package com.example.translator


import android.graphics.Bitmap
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.langconverter.model.TranslationResult
import com.example.translator.ui.TranslationViewModel
import com.example.translator.utils.startVoiceRecognition
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions


@Composable
fun TranslationScreen(
    modifier: Modifier = Modifier,
    viewModel: TranslationViewModel = androidx.lifecycle.viewmodel.compose.viewModel() // Correct initialization
) {
    val textToTranslate by viewModel.recognizedText.observeAsState("")
    val translationResult by viewModel.translationResult.observeAsState(TranslationResult(""))

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var isListening by remember { mutableStateOf(false) }

    // State for dropdown menu
    var expanded by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("hi") } // Default language is Hindi

    val languages = listOf("Hindi", "English", "Chinese")
    val languageCodes = mapOf("Hindi" to "hi", "English" to "en", "Chinese" to "zh")

    // Custom colors
    val customRed = Color(0xFF700201) // Color #700201
    val customYellow = Color(0xFFEEE700) // Color #EEE700

    LaunchedEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.setLanguage(java.util.Locale.getDefault())
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bitmap: Bitmap? ->
            bitmap?.let {
                extractTextFromImage(it, viewModel)
            }
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(customRed) // Applying the custom red color to the background
            .clickable { focusManager.clearFocus() }
            .padding(horizontal = 16.dp, vertical = 32.dp)
    ) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(customRed), // Applying the custom red color to the LazyColumn background
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
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

                Text(
                    text = "Transcript",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                )

                // Text box for input text (Transcript) with custom yellow background
                BasicTextField(
                    value = textToTranslate,
                    onValueChange = { viewModel.updateRecognizedText(it) },
                    textStyle = TextStyle(fontSize = 18.sp, color = Color.Black), // Text color inside the box
                    modifier = Modifier
                        .width(350.dp)
                        .height(150.dp)
                        .background(customYellow, shape = RoundedCornerShape(16.dp)) // Custom yellow background
                        .padding(16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Dropdown for language selection
                Box {
                    Button(
                        onClick = { expanded = true },
                        modifier = Modifier
                            .width(200.dp)
                            .padding(bottom = 16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = customRed)
                    ) {
                        Text("Translate to: ${languages.find { languageCodes[it] == selectedLanguage }}", color = Color.White)
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        languages.forEach { language ->
                            DropdownMenuItem(
                                text = { Text(language) },
                                onClick = {
                                    selectedLanguage = languageCodes[language] ?: "hi"
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Translated Text",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                )

                // Text box for translated text with custom yellow background
                BasicTextField(
                    value = translationResult.translatedText ?: "",
                    onValueChange = {},
                    textStyle = TextStyle(fontSize = 18.sp, color = Color.Black, fontWeight = FontWeight.Bold), // Text color inside the box
                    modifier = Modifier
                        .width(350.dp)
                        .height(150.dp)
                        .background(customYellow, shape = RoundedCornerShape(16.dp)) // Custom yellow background
                        .padding(16.dp)
                )

                Spacer(modifier = Modifier.height(26.dp))

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
                                    isListening = true
                                }
                            } else {
                                isListening = false
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
                        onClick = { cameraLauncher.launch(null) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_camera),
                            contentDescription = "Capture Image",
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
                            painterResource(id = R.drawable.volume),
                            contentDescription = "Speak Translation",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Centered button in the middle of the screen
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            viewModel.translate(textToTranslate, "en", selectedLanguage)
                        },
                        modifier = Modifier
                            .width(150.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = customRed)
                    ) {
                        Text(
                            text = "Translate",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}


fun extractTextFromImage(bitmap: Bitmap, viewModel: TranslationViewModel) {
    val image = InputImage.fromBitmap(bitmap, 0)
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    recognizer.process(image)
        .addOnSuccessListener { visionText ->
            viewModel.updateRecognizedText(visionText.text)
        }
        .addOnFailureListener { e ->
            Log.e("Text Recognition", "Error recognizing text: ", e)
        }
}

@Composable
fun CheckCameraPermission(content: @Composable () -> Unit) {
    val context = LocalContext.current
    var hasPermission by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasPermission = granted
        }
    )

    LaunchedEffect(key1 = true) {
        permissionLauncher.launch(android.Manifest.permission.CAMERA)
    }

    if (hasPermission) {
        content()
    } else {
        Text("Requesting Camera Permission...")
    }
}
