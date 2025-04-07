package com.example.randomstringgenerator
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.randomstringgenerator.ui.theme.RandomStringGeneratorTheme
import com.example.randomstringgenerator.viewmodel.RandomStringViewModel
import com.iav.contestdataprovider.Api.Model.RandomText

class MainActivity : ComponentActivity() {
    private val viewModel: RandomStringViewModel by viewModels()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RandomStringGeneratorTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RandomStringGeneratorScreen(viewModel)
                }
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RandomStringGeneratorScreen(viewModel: RandomStringViewModel) {
    var lengthInput by remember { mutableStateOf("") }
    var inputError by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        // IAV-1: Allow user to enter the desired maximum string length.
        OutlinedTextField(
            value = lengthInput,
            onValueChange = {
                lengthInput = it
                inputError = ""
            },
            label = { Text("Max Length") },
            modifier = Modifier.fillMaxWidth()
        )
        if (inputError.isNotEmpty()) {
            Text(text = inputError, color = MaterialTheme.colorScheme.error)
        }
        Spacer(modifier = Modifier.height(8.dp))

        // IAV-2: Button to trigger the query.
        Button(
            onClick = {
                val maxLength = lengthInput.toIntOrNull()
                if (maxLength == null || maxLength <= 0) {
                    inputError = "Please enter a valid positive number."
                } else {
                    viewModel.generateRandomString(maxLength)
                }
            },
            enabled = !viewModel.isLoading.value,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generate Random String")
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Show a loading indicator if the query is in progress.
        if (viewModel.isLoading.value) {
            CircularProgressIndicator(modifier = Modifier.padding(8.dp))
        }

        // Display any error message from the ViewModel.
        if (viewModel.errorMessage.value.isNotEmpty()) {
            Text(text = viewModel.errorMessage.value, color = MaterialTheme.colorScheme.error)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // IAV-3: Display generated strings with details.
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(viewModel.generatedStrings.value) { randomText ->
                GeneratedStringCard(randomText = randomText, onDelete = {
                    viewModel.removeString(it)
                })
            }
        }

        // IAV-5: Button to clear all generated strings.
        Button(
            onClick = { viewModel.clearAllStrings() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Clear All")
        }
    }
}

@Composable
fun GeneratedStringCard(randomText: RandomText, onDelete: (RandomText) -> Unit) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {
                Text("String: ${randomText.value}")
                Text("Length: ${randomText.length}")
                Text("Created: ${randomText.created}")
            }
            // IAV-6: Icon to delete a single generated string.
            IconButton(onClick = { onDelete(randomText) }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}
