package com.celebrare.texteditor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.celebrare.texteditor.ui.TextEditorScreen
import com.celebrare.texteditor.ui.theme.TextEditorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TextEditorTheme {
                TextEditorScreen()
            }
        }
    }
}
