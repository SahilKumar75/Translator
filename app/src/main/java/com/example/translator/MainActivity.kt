package com.example.translator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
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
fun TranslationScreen(modifier: Modifier = Modifier, viewModel: TranslationViewModel = viewModel()) {
    var textToTranslate by remember { mutableStateOf("") }
    val translationResult by viewModel.translationResult.observeAsState(TranslationResult(null.toString()))

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFF1D1E22)) // Column background
    ) {
        // Centered screen heading
        Text(
            text = "Translate Text",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp)
        )

        // Label above the first input card
        Text(
            text = "Transcript",
            fontSize = 18.sp,
            color = Color.White,
            modifier = Modifier.align(Alignment.Start) // Align label to the left
        )

        // Translation input field (First card)
        BasicTextField(
            value = textToTranslate,
            onValueChange = { textToTranslate = it },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            textStyle = TextStyle(fontSize = 18.sp, color = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(Color(0xFF252D34), shape = RoundedCornerShape(16.dp)) // Set card color to #252D34
                .padding(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // "Translate" button
        Button(
            onClick = {
                val targetLanguage = "hi" // Example target language
                viewModel.translate(textToTranslate, targetLanguage)
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(150.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF69D269)) // Button color
        ) {
            Text(
                text = "Translate",
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold // Bold text
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Label above the second box (output card)
        Text(
            text = "Translated",
            fontSize = 18.sp,
            color = Color.White,
            modifier = Modifier.align(Alignment.Start) // Align label to the left
        )

        // Translation result output (Second card)
        Text(
            text = translationResult.translatedText ?: "Translation failed",
            fontSize = 18.sp,
            color = Color.White, // Text inside the card is white
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF252D34), shape = RoundedCornerShape(16.dp)) // Set card color to 252D34
                .padding(16.dp)
                .height(150.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TranslationScreenPreview() {
    TranslatorTheme {
        TranslationScreen()
    }
}
