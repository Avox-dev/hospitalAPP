// RegisterViewModel.kt
package com.example.compose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose.data.ApiResult
import com.example.compose.data.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class RegisterViewModel : ViewModel() {

    // UserService 인스턴스 생성
    private val userService = UserService()

    // 회원가입 상태를 위한 StateFlow
    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Initial)
    val registerState: StateFlow<RegisterState> = _registerState

    // 회원가입 처리
    fun register(email: String, userId: String, password: String, name: String, birthdate: String, phone: String, address: String) {
        viewModelScope.launch {
            try {
                // 로딩 상태로 변경
                _registerState.value = RegisterState.Loading

                // UserService를 통한 회원가입 API 호출
                val result = userService.register(email, userId, password, birthdate, phone, address)

                // 결과 처리
                when (result) {
                    is ApiResult.Success -> {
                        // 성공 응답 처리
                        val responseData = result.data
                        val status = responseData.optString("status")
                        val message = responseData.optString("message")

                        if (status == "success") {
                            _registerState.value = RegisterState.Success(message)
                        } else {
                            _registerState.value = RegisterState.Error(message)
                        }
                    }
                    is ApiResult.Error -> {
                        // 오류 응답 처리
                        _registerState.value = RegisterState.Error(result.message)
                    }
                }
            } catch (e: Exception) {
                // 예외 발생
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