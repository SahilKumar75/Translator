package com.example.translator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.langconverter.model.TranslationResult
import com.example.translator.ui.TranslationViewModel
import com.example.translator.ui.theme.TranslatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TranslatorTheme {
                // A surface container using the background color from the theme
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
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
            .background(Brush.verticalGradient(colors = listOf(Color(0xFFB2FEFA), Color(0xFF0ED2F7))))
    ) {
        Text(
            text = "Translate Text",
            fontSize = 24.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        BasicTextField(
            value = textToTranslate,
            onValueChange = { textToTranslate = it },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            textStyle = TextStyle(fontSize = 18.sp, color = Color.White),
            modifier = Modifier
                .background(Color(0xFF2C2C2C))
                .fillMaxWidth()
                .padding(16.dp)
                .border(1.dp, Color.Gray)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val targetLanguage = "en" // Example target language
                viewModel.translate(textToTranslate, targetLanguage)
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(colors = listOf(Color(0xFFB2FEFA), Color(0xFF0ED2F7)))),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            Text(text = "Translate", fontSize = 18.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = translationResult.translatedText ?: "Translation failed",
            fontSize = 18.sp,
            color = Color.White,
            modifier = Modifier
                .background(Color(0xFF2C2C2C))
                .fillMaxWidth()
                .padding(16.dp)
                .border(1.dp, Color.Gray)
        )
    }
}
