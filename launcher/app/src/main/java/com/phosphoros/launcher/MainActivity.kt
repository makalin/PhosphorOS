package com.phosphoros.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val phosphor = Color(0xFF00FF9C)
            val bg = Color.Black

            MaterialTheme(
                colorScheme = darkColorScheme(
                    background = bg,
                    surface = bg,
                    onBackground = phosphor,
                    onSurface = phosphor,
                    primary = phosphor,
                ),
            ) {
                Surface(modifier = Modifier.fillMaxSize(), color = bg) {
                    val log = remember { mutableStateListOf("phosphoros@android:~$") }
                    var input by remember { mutableStateOf("") }
                    val scrollState = rememberScrollState()

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(bg)
                            .verticalScroll(scrollState)
                            .padding(PaddingValues(16.dp)),
                    ) {
                        Text(
                            text = log.joinToString("\n"),
                            color = phosphor,
                            style = TextStyle(fontSize = 16.sp),
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = ">",
                            color = phosphor,
                            style = TextStyle(fontSize = 16.sp),
                        )

                        BasicTextField(
                            value = input,
                            onValueChange = { input = it },
                            singleLine = true,
                            textStyle = TextStyle(
                                color = phosphor,
                                fontSize = 16.sp,
                            ),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}

