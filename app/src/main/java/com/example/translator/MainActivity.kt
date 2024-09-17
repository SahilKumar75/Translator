package com.example.translator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
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


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TranslatorTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF1D1E22)), // Screen background color
                    contentColor = Color.White
                ) { innerPadding ->
                    TranslationScreen(modifier = Modifier.padding(innerPadding))
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

    var vibrate by remember { mutableStateOf(false) }
    var isListening by remember { mutableStateOf(false) } // Track if currently listening

    // LaunchedEffect to reset vibrate state after a delay
    LaunchedEffect(vibrate) {
        if (vibrate) {
            delay(200) // Delay for 200 milliseconds
            vibrate = false // Reset vibrate state after delay
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1D1E22))
            .padding(horizontal = 16.dp, vertical = 32.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Translate Text",
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.height(26.dp))

        // Text label for the recognized speech input
        Text(
            text = "Transcript",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 8.dp, bottom = 8.dp)
        )

        BasicTextField(
            value = textToTranslate,
            onValueChange = { text -> viewModel.updateRecognizedText(text) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            textStyle = TextStyle(fontSize = 18.sp, color = Color.White),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(350.dp)
                .height(150.dp)
                .background(Color(0xFF252D34), shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        )

        Spacer(modifier = Modifier.height(26.dp))

        // Text label for the translation result
        Text(
            text = "Translation",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 8.dp, bottom = 8.dp)
        )

        BasicTextField(
            value = translationResult.translatedText ?: "",
            onValueChange = {},
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            textStyle = TextStyle(fontSize = 18.sp, color = Color(0xFF69D269), fontWeight = FontWeight.Bold),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(350.dp)
                .height(150.dp)
                .background(Color(0xFF252D34), shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        )

        Spacer(modifier = Modifier.height(26.dp))

        IconButton(
            onClick = {
                if (!isListening) {
                    startVoiceRecognition(context) { recognizedText ->
                        viewModel.updateRecognizedText(recognizedText)
                    }
                    vibrate = true // Set vibrate to true when button is clicked
                    isListening = true // Set listening state to true
                } else {
                    // Stop listening logic here (you may need to implement this in your SpeechRecognizer setup)
                    // For example, you might need to call a method to stop recognition.
                    // This will depend on how you've set up your SpeechRecognizer.
                    isListening = false // Set listening state to false
                }
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(48.dp)
                .graphicsLayer {
                    scaleX = if (vibrate) 1.2f else 1f // Scale up when vibrating
                    scaleY = if (vibrate) 1.2f else 1f
                    alpha = if (vibrate) 0.7f else 1f // Fade out slightly when vibrating
                }
        ) {
            Icon(
                painterResource(id = if (isListening) R.drawable.ic_pause else R.drawable.ic_mic), // Change icon based on state
                contentDescription = if (isListening) "Pause" else "Mic",
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(26.dp))

        Button(
            onClick = {
                val targetLanguage = "hi" // Example target language
                viewModel.translate(textToTranslate, targetLanguage)
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(150.dp),
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



@Preview(showBackground = true)
@Composable
fun TranslationScreenPreview() {
    TranslatorTheme {
        TranslationScreen()
    }
}
