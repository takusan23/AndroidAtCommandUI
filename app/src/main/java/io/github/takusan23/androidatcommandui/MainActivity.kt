package io.github.takusan23.androidatcommandui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.takusan23.androidatcommandui.ui.theme.AndroidAtCommandUITheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AndroidAtCommandUITheme {
                MainScreen()
            }
        }
    }
}

@Composable
private fun MainScreen() {
    val scope = rememberCoroutineScope()
    val outputList = remember { mutableStateOf(emptyList<String>()) }
    val commandText = remember { mutableStateOf("AT") }

    /** AT コマンドを投げる */
    fun executeCommand() {
        scope.launch(Dispatchers.IO) {
            // 出力は outputList で
            Runtime.getRuntime().exec(
                arrayOf("su", "-c", "echo", "-e", """ "${commandText.value}\r" """, ">", "/dev/smd7")
            )
        }
    }

    // AT コマンドの出力を while ループで取り出す
    LaunchedEffect(key1 = Unit) {
        withContext(Dispatchers.IO) {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "cat", "/dev/smd7"))
            try {
                process.inputStream.bufferedReader().use { bufferedReader ->
                    while (isActive) {
                        val readText = bufferedReader.readLine()?.ifEmpty { null } ?: continue
                        outputList.value += readText
                    }
                }
            } finally {
                process.destroy()
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {

            Row(
                modifier = Modifier.padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = commandText.value,
                    onValueChange = { commandText.value = it },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Go,
                        keyboardType = KeyboardType.Ascii
                    ),
                    keyboardActions = KeyboardActions(onGo = { executeCommand() }),
                    label = { Text(text = "AT コマンド") }
                )

                Button(onClick = { executeCommand() }) {
                    Text(text = "実行")
                }
            }

            LazyColumn {
                items(outputList.value) { output ->
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = output
                    )
                    HorizontalDivider(color = LocalContentColor.current.copy(alpha = 0.05f))
                }
            }
        }
    }
}