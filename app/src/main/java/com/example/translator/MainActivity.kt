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
            .background(Color(0xFF1D1E22)) // Background color to cover full screen
            .padding(horizontal = 16.dp, vertical = 32.dp) // Padding to ensure the layout fits well
    ) {
        // Spacing from the top to the title
        Spacer(modifier = Modifier.height(10.dp))

        // Centered screen heading with bold text
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

        // Label for the first input card aligned with the card
        Text(
            text = "Transcript",
            fontSize = 28.sp,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.Start) // Align with the card
                .padding(start = 8.dp, bottom = 8.dp)
        )

        // Translation input field (First card)
        BasicTextField(
            value = textToTranslate,
            onValueChange = { textToTranslate = it },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            textStyle = TextStyle(fontSize = 18.sp, color = Color.White),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(350.dp)
                .height(150.dp)
                .background(Color(0xFF252D34), shape = RoundedCornerShape(16.dp)) // Set card color to #252D34
                .padding(16.dp)
        )

        Spacer(modifier = Modifier.height(26.dp))

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

        Spacer(modifier = Modifier.height(26.dp))

        // Label for the second box (output card) aligned with the card
        Text(
            text = "Translated",
            fontSize = 28.sp,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.Start) // Align with the card
                .padding(start = 8.dp, bottom = 8.dp)
        )

        // Translation result output (Second card)
        Text(
            text = translationResult.translatedText ?: "Translation failed",
            fontSize = 18.sp,
            color = Color(0xFF69D269), // Set text color to #69D269
            fontWeight = FontWeight.Bold, // Bold text
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(350.dp)
                .background(Color(0xFF252D34), shape = RoundedCornerShape(16.dp)) // Set card color to #252D34
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
