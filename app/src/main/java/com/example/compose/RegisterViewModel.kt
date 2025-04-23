// RegisterViewModel.kt
package com.example.compose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose.data.ApiResult
import com.example.compose.data.UserService
import kotlinx.coroutines.delay
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
            _registerState.value = RegisterState.Loading

            val maxRetries = 3
            var retryCount = 0
            var lastException: Exception? = null

            while (retryCount < maxRetries) {
                try {
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
                            return@launch
                        }
                        is ApiResult.Error -> {
                            // 오류 응답 처리
                            lastException = Exception(result.message)
                            // 재시도를 위해 예외 처리로 넘어감
                        }
                    }
                } catch (e: Exception) {
                    lastException = e
                }

                retryCount++
                if (retryCount < maxRetries) {
                    delay(1000L * retryCount)
                }
            }

            // 모든 재시도 실패 후
            _registerState.value = RegisterState.Error("회원가입 중 오류가 발생했습니다: ${lastException?.message}")
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