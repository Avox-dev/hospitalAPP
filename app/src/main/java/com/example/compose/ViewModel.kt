// ViewModel.kt - Data handling
package com.example.compose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose.data.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val apiService = ApiService()

    private val _apiResult = MutableStateFlow("API 결과 표시 영역")
    val apiResult: StateFlow<String> = _apiResult.asStateFlow()

    fun fetchApiData() {
        viewModelScope.launch {
            apiService.fetchTodoData()
                .onSuccess { result ->
                    _apiResult.value = result
                }
                .onFailure { error ->
                    _apiResult.value = "요청 실패: ${error.message}"
                }
        }
    }
}