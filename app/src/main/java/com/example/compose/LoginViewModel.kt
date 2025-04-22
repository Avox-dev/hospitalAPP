// LoginViewModel.kt
package com.example.compose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.compose.data.User
import com.example.compose.data.UserRepository
import com.example.compose.data.ApiResult
import com.example.compose.data.UserService
import org.json.JSONObject

class LoginViewModel : ViewModel() {
    private val userRepository = UserRepository.getInstance()
    private val userService = UserService()

    // 로그인 상태를 관리하는 StateFlow
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Initial)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    // 로그인 함수
    fun login(userId: String, password: String) {
        viewModelScope.launch {
            try {
                // 로딩 상태로 변경
                _loginState.value = LoginState.Loading

                // UserService를 통한 로그인 API 호출
                val result = userService.login(userId, password)

                // 결과 처리
                when (result) {
                    is ApiResult.Success -> {
                        // 로그인 성공 시 사용자 정보 처리
                        val responseData = result.data
                        val status = responseData.optString("status")
                        val message = responseData.optString("message")

                        if (status == "success") {
                            // 사용자 데이터 추출
                            val userData = responseData.optJSONObject("data")
                            val sessionId = responseData.optString("session", "")
                            if (userData != null) {
                                val username = userData.optString("username")
                                val email = userData.optString("email")
                                val phone = userData.optString("phone", "")
                                val birthdate = userData.optString("birthdate", "")
                                val address = userData.optString("address", "")

                                // 사용자 정보 저장
                                userRepository.setCurrentUser(
                                    User(
                                        userId = username,
                                        userName = username,
                                        email = email,
                                        phone = phone,
                                        birthdate = birthdate,
                                        address = address
                                    )
                                )
                                // 세션 ID 저장
                                userRepository.setSessionId(sessionId)

                                _loginState.value = LoginState.Success(message)
                            } else {
                                _loginState.value = LoginState.Error("사용자 정보를 불러오는데 실패했습니다.")
                            }
                        } else {
                            _loginState.value = LoginState.Error(message)
                        }
                    }
                    is ApiResult.Error -> {
                        // 로그인 실패
                        _loginState.value = LoginState.Error(result.message)
                    }
                }
            } catch (e: Exception) {
                // 예외 발생
                _loginState.value = LoginState.Error("로그인 중 오류가 발생했습니다: ${e.message}")
            }
        }
    }


    // 로그인 상태를 나타내는 sealed class
    sealed class LoginState {
        object Initial : LoginState()
        object Loading : LoginState()
        data class Success(val message: String) : LoginState()
        data class Error(val message: String) : LoginState()
    }
}