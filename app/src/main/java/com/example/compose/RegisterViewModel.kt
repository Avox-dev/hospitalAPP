// RegisterViewModel.kt
package com.example.compose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    // 회원가입 상태를 위한 StateFlow
    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Initial)
    val registerState: StateFlow<RegisterState> = _registerState

    // 회원가입 처리
    fun register(email: String, userId: String, password: String, name: String, phone: String) {
        viewModelScope.launch {
            try {
                _registerState.value = RegisterState.Loading

                // Mock API 호출 (실제로는 여기서 백엔드 API 호출)
                delay(1500)  // 네트워크 요청 시뮬레이션

                // 실제 앱에서는 API 응답에 따라 성공/실패 처리
                // 지금은 항상 성공으로 처리
                _registerState.value = RegisterState.Success("회원가입이 완료되었습니다.")
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error("회원가입 중 오류가 발생했습니다: ${e.message}")
            }
        }
    }

    // 회원가입 상태를 나타내는 sealed class
    sealed class RegisterState {
        object Initial : RegisterState()
        object Loading : RegisterState()
        data class Success(val message: String) : RegisterState()
        data class Error(val message: String) : RegisterState()
    }
}