package com.example.randomstringgenerator.viewmodel

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.randomstringgenerator.data.RandomStringRepository
import com.iav.contestdataprovider.Api.Model.RandomText

import kotlinx.coroutines.launch

class RandomStringViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RandomStringRepository(application.applicationContext)
    var generatedStrings = mutableStateOf<List<RandomText>>(emptyList())
        private set

    var isLoading = mutableStateOf(false)
        private set

    var errorMessage = mutableStateOf("")
        private set

    @RequiresApi(Build.VERSION_CODES.O)
    fun generateRandomString(maxLength: Int) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = ""
            val result = repository.queryRandomString(maxLength)
            if (result != null) {
                // Prepend new string to the list (IAV-3 & IAV-4).
                generatedStrings.value = listOf(result) + generatedStrings.value
            } else {
                errorMessage.value = "Failed to retrieve random string. Please try again."
            }
            isLoading.value = false
        }
    }

    // Deletes a single generated string (IAV-6).
    fun removeString(randomText: RandomText) {
        generatedStrings.value = generatedStrings.value.filter { it != randomText }
    }

    // Clears all generated strings (IAV-5).
    fun clearAllStrings() {
        generatedStrings.value = emptyList()
    }
}
